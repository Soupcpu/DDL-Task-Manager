package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.example.myapplication.adapter.TaskAdapter
import com.example.myapplication.databinding.FragmentFirstBinding
import com.example.myapplication.viewmodel.TaskViewModel
import com.example.myapplication.data.Task

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private val taskViewModel: TaskViewModel by viewModels()
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeTasks()
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            onTaskClick = { task ->
                // Navigate to edit task with task ID
                val bundle = Bundle().apply {
                    putLong("taskId", task.id)
                }
                findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundle)
            },
            onTaskCompletionChanged = { task, isCompleted ->
                taskViewModel.updateTaskCompletion(task.id, isCompleted)
                // Show completion feedback
                showTaskCompletionFeedback(task, isCompleted)
            }
        )

        binding.recyclerViewTasks.apply {
            adapter = taskAdapter
            layoutManager = LinearLayoutManager(requireContext())

            // Enhanced animations
            itemAnimator = DefaultItemAnimator().apply {
                addDuration = 300
                removeDuration = 300
                moveDuration = 300
                changeDuration = 300
            }

            // Add item spacing
            addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            ).apply {
                setDrawable(resources.getDrawable(android.R.color.transparent, null))
            })
        }

        // Setup swipe to delete gesture
        setupSwipeToDelete()
    }

    private fun setupSwipeToDelete() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val task = taskAdapter.currentList[position]

                // Delete task with undo option
                taskViewModel.deleteTask(task)

                Snackbar.make(binding.root, "任务已删除", Snackbar.LENGTH_LONG)
                    .setAction("撤销") {
                        taskViewModel.insertTask(task)
                    }
                    .show()
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.recyclerViewTasks)
    }

    private fun showTaskCompletionFeedback(task: Task, isCompleted: Boolean) {
        val message = if (isCompleted) {
            "✅ 任务 '${task.title}' 已完成！"
        } else {
            "📝 任务 '${task.title}' 标记为未完成"
        }

        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(resources.getColor(if (isCompleted) android.R.color.holo_green_light else android.R.color.holo_orange_light, null))
            .show()
    }

    private fun observeTasks() {
        taskViewModel.incompleteTasks.observe(viewLifecycleOwner) { tasks ->
            // Sort tasks by urgency and due date for better UX
            val sortedTasks = tasks.sortedWith(compareBy<Task> { it.isCompleted }
                .thenBy {
                    val now = System.currentTimeMillis()
                    val timeDiff = it.dueDate.time - now
                    when {
                        timeDiff < 0 -> 0 // Overdue - highest priority
                        timeDiff < 24 * 60 * 60 * 1000 -> 1 // < 1 day
                        timeDiff < 3 * 24 * 60 * 60 * 1000 -> 2 // < 3 days
                        else -> 3 // > 3 days
                    }
                }
                .thenBy { it.priority.ordinal }
                .thenBy { it.dueDate }
            )

            taskAdapter.submitList(sortedTasks) {
                // Scroll to top when list updates (smooth scroll)
                if (sortedTasks.isNotEmpty()) {
                    binding.recyclerViewTasks.smoothScrollToPosition(0)
                }
            }

            // Enhanced empty state with animation
            if (tasks.isEmpty()) {
                binding.recyclerViewTasks.animate()
                    .alpha(0f)
                    .setDuration(200)
                    .withEndAction {
                        binding.recyclerViewTasks.visibility = View.GONE
                        binding.tvEmptyState.apply {
                            visibility = View.VISIBLE
                            alpha = 0f
                            animate().alpha(1f).setDuration(300).start()
                        }
                    }
            } else {
                binding.tvEmptyState.animate()
                    .alpha(0f)
                    .setDuration(200)
                    .withEndAction {
                        binding.tvEmptyState.visibility = View.GONE
                        binding.recyclerViewTasks.apply {
                            visibility = View.VISIBLE
                            alpha = 0f
                            animate().alpha(1f).setDuration(300).start()
                        }
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}