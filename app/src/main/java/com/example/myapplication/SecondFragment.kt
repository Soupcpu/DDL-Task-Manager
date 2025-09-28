package com.example.myapplication

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.example.myapplication.R
import com.example.myapplication.data.Priority
import com.example.myapplication.data.Task
import com.example.myapplication.databinding.FragmentSecondBinding
import com.example.myapplication.utils.KeyboardUtils
import com.example.myapplication.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    private val taskId: Long by lazy { arguments?.getLong("taskId", -1L) ?: -1L }
    private val taskViewModel: TaskViewModel by viewModels()

    private var selectedDateTime: Calendar = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    private var currentTask: Task? = null
    private var selectedPriority: Priority = Priority.MEDIUM
    private val priorityButtons by lazy {
        listOf(
            binding.btnPriorityLow,
            binding.btnPriorityMedium,
            binding.btnPriorityHigh,
            binding.btnPriorityUrgent
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPriorityButtons()
        setupDateTimePickers()
        setupClickListeners()
        setupKeyboardHiding()

        // 检查是否是编辑模式
        if (taskId != -1L) {
            loadTaskForEditing(taskId)
        } else {
            setupForNewTask()
        }

        updateDateTimeDisplay()
    }

    private fun setupForNewTask() {
        binding.tvPageTitle.text = "新建任务"
        binding.tvPageSubtitle.text = "设置任务的详细信息和截止时间"
        currentTask = null
    }

    private fun loadTaskForEditing(taskId: Long) {
        binding.tvPageTitle.text = "编辑任务"
        binding.tvPageSubtitle.text = "修改任务信息和设置"

        // 这里需要从数据库获取任务，简化处理
        // 在实际应用中，你需要在TaskViewModel中添加获取单个任务的LiveData方法
        val task = taskViewModel.getTaskById(taskId)
        if (task != null) {
            currentTask = task
            populateTaskData(task)
        }
    }

    private fun populateTaskData(task: Task) {
        binding.etTaskTitle.setText(task.title)
        binding.etTaskDescription.setText(task.description)
        binding.etCategory.setText(task.category)
        binding.switchReminder.isChecked = task.reminderEnabled

        selectedDateTime.time = task.dueDate
        selectedPriority = task.priority

        updatePrioritySelection()
        updateDateTimeDisplay()
    }

    private fun setupPriorityButtons() {
        val priorities = Priority.values()

        priorityButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                selectedPriority = priorities[index]
                updatePrioritySelection()
            }
        }

        // 设置默认选中
        updatePrioritySelection()
    }

    private fun updatePrioritySelection() {
        priorityButtons.forEachIndexed { index, button ->
            val isSelected = Priority.values()[index] == selectedPriority

            if (isSelected) {
                button.setBackgroundColor(ContextCompat.getColor(requireContext(),
                    when (selectedPriority) {
                        Priority.LOW -> R.color.priority_low
                        Priority.MEDIUM -> R.color.priority_medium
                        Priority.HIGH -> R.color.priority_high
                        Priority.URGENT -> R.color.priority_urgent
                    }
                ))
                button.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            } else {
                button.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
                button.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
                button.strokeColor = ContextCompat.getColorStateList(requireContext(), R.color.card_stroke_light)
            }
        }
    }

    private fun setupDateTimePickers() {
        // 日期时间选择器的逻辑在setupKeyboardHiding()中实现
    }

    private fun updateDateTimeDisplay() {
        val dateFormat = SimpleDateFormat("M月d日", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        binding.tvSelectedDate.text = dateFormat.format(selectedDateTime.time)
        binding.tvSelectedTime.text = timeFormat.format(selectedDateTime.time)
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            KeyboardUtils.hideKeyboard(this)
            findNavController().navigateUp()
        }

        binding.btnCancel.setOnClickListener {
            KeyboardUtils.hideKeyboard(this)
            findNavController().navigateUp()
        }

        binding.btnSaveTask.setOnClickListener {
            KeyboardUtils.hideKeyboard(this)
            saveTask()
        }
    }

    private fun setupKeyboardHiding() {
        // 创建隐藏键盘的通用函数
        val hideKeyboardAndClearFocus = {
            KeyboardUtils.hideKeyboard(this)
            binding.etTaskTitle.clearFocus()
            binding.etTaskDescription.clearFocus()
            binding.etCategory.clearFocus()
        }

        // 设置根视图点击监听
        binding.linearLayoutMain.setOnClickListener {
            hideKeyboardAndClearFocus()
        }

        // 为描述输入框设置IME监听
        binding.etTaskDescription.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                KeyboardUtils.hideKeyboard(this)
                binding.etTaskDescription.clearFocus()
                true
            } else {
                false
            }
        }

        // 为优先级按钮添加键盘隐藏
        priorityButtons.forEach { button ->
            button.setOnTouchListener { _, _ ->
                hideKeyboardAndClearFocus()
                false
            }
        }

        // 为日期时间选择按钮添加键盘隐藏和选择器
        binding.btnSelectDate.setOnClickListener {
            hideKeyboardAndClearFocus()

            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    selectedDateTime.set(year, month, day)
                    updateDateTimeDisplay()
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.btnSelectTime.setOnClickListener {
            hideKeyboardAndClearFocus()

            TimePickerDialog(
                requireContext(),
                { _, hour, minute ->
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hour)
                    selectedDateTime.set(Calendar.MINUTE, minute)
                    updateDateTimeDisplay()
                },
                selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

    private fun saveTask() {
        val title = binding.etTaskTitle.text.toString().trim()
        val description = binding.etTaskDescription.text.toString().trim()
        val category = binding.etCategory.text.toString().trim()

        if (title.isEmpty()) {
            Toast.makeText(requireContext(), "请输入任务标题", Toast.LENGTH_SHORT).show()
            binding.etTaskTitle.requestFocus()
            return
        }

        val reminderEnabled = binding.switchReminder.isChecked

        if (currentTask != null) {
            // 编辑模式：更新现有任务
            val updatedTask = currentTask!!.copy(
                title = title,
                description = description,
                dueDate = selectedDateTime.time,
                priority = selectedPriority,
                category = category.ifEmpty { "其他" },
                reminderEnabled = reminderEnabled
            )
            taskViewModel.updateTask(updatedTask)
            Toast.makeText(requireContext(), "任务已更新 ✓", Toast.LENGTH_SHORT).show()
        } else {
            // 新建模式：创建新任务
            val newTask = Task(
                title = title,
                description = description,
                dueDate = selectedDateTime.time,
                priority = selectedPriority,
                category = category.ifEmpty { "其他" },
                reminderEnabled = reminderEnabled
            )
            taskViewModel.insertTask(newTask)
            Toast.makeText(requireContext(), "任务已创建 ✓", Toast.LENGTH_SHORT).show()
        }

        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}