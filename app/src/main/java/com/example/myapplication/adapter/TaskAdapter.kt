package com.example.myapplication.adapter

import android.animation.ObjectAnimator
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.example.myapplication.R
import com.example.myapplication.data.Priority
import com.example.myapplication.data.Task
import com.example.myapplication.databinding.ItemTaskBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

class TaskAdapter(
    private val onTaskClick: (Task) -> Unit,
    private val onTaskCompletionChanged: (Task, Boolean) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            with(binding) {
                // 添加卡片进入动画
                addCardAnimation()

                tvTaskTitle.text = task.title
                tvTaskDescription.text = task.description
                tvCategory.text = task.category

                // Set localized priority text and icon
                val (priorityText, priorityIcon) = when (task.priority) {
                    Priority.LOW -> Pair(root.context.getString(R.string.priority_low), R.drawable.ic_priority_low)
                    Priority.MEDIUM -> Pair(root.context.getString(R.string.priority_medium), R.drawable.ic_priority_medium)
                    Priority.HIGH -> Pair(root.context.getString(R.string.priority_high), R.drawable.ic_priority_high)
                    Priority.URGENT -> Pair(root.context.getString(R.string.priority_urgent), R.drawable.ic_priority_urgent)
                }
                tvPriority.text = priorityText
                ivPriorityIcon.setImageResource(priorityIcon)

                // Format due date and time with better formatting
                val dateFormat = SimpleDateFormat("M月d日", Locale.getDefault())
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val formattedDate = dateFormat.format(task.dueDate)
                val formattedTime = timeFormat.format(task.dueDate)

                tvDueDate.text = "截止：$formattedDate $formattedTime"

                // Calculate time remaining and format nicely
                val now = Calendar.getInstance().time
                val timeDifferenceMs = task.dueDate.time - now.time
                val oneDayMs = 24 * 60 * 60 * 1000L
                val oneHourMs = 60 * 60 * 1000L
                val threeDaysMs = 3 * oneDayMs

                // Format time remaining text
                val timeRemainingText = when {
                    timeDifferenceMs < 0 -> "已逾期"
                    timeDifferenceMs < oneHourMs -> "不到1小时"
                    timeDifferenceMs < oneDayMs -> "还剩${(timeDifferenceMs / oneHourMs).toInt()}小时"
                    timeDifferenceMs < threeDaysMs -> "还剩${(timeDifferenceMs / oneDayMs).toInt()}天"
                    else -> "还剩${(timeDifferenceMs / oneDayMs).toInt()}天"
                }
                tvTimeRemaining.text = timeRemainingText

                // Apply time-based color coding with enhanced visual feedback
                val (textColor, timeBackgroundColor, iconRes, cardStrokeColor) = when {
                    timeDifferenceMs < 0 -> {
                        // Overdue - critical state
                        Quadruple(R.color.text_time_overdue, R.color.time_bg_overdue, R.drawable.ic_time_overdue, R.color.text_time_overdue)
                    }
                    timeDifferenceMs < oneDayMs -> {
                        // Less than 1 day - urgent
                        Quadruple(R.color.text_time_urgent, R.color.time_bg_urgent, R.drawable.ic_time_urgent, R.color.text_time_urgent)
                    }
                    timeDifferenceMs < threeDaysMs -> {
                        // 1-3 days - warning
                        Quadruple(R.color.text_time_warning, R.color.time_bg_warning, R.drawable.ic_time_warning, R.color.text_time_warning)
                    }
                    else -> {
                        // More than 3 days - safe
                        Quadruple(R.color.text_time_safe, R.color.time_bg_safe, R.drawable.ic_time_safe, R.color.text_time_safe)
                    }
                }

                // Apply enhanced visual styling
                tvDueDate.setTextColor(ContextCompat.getColor(root.context, textColor))
                ivTimeIcon.setImageResource(iconRes)
                ivTimeIcon.setColorFilter(ContextCompat.getColor(root.context, textColor))

                // Update time remaining background
                tvTimeRemaining.background = ContextCompat.getDrawable(root.context, R.drawable.time_remaining_background)
                tvTimeRemaining.backgroundTintList = ContextCompat.getColorStateList(root.context, timeBackgroundColor)

                // Apply subtle card stroke color based on urgency
                (root as? MaterialCardView)?.let { cardView ->
                    cardView.strokeColor = ContextCompat.getColor(root.context, cardStrokeColor)
                    cardView.strokeWidth = if (timeDifferenceMs < oneDayMs) 3 else 1
                }

                // Set priority styling with better visual hierarchy
                val priorityColor = when (task.priority) {
                    Priority.LOW -> R.color.priority_low
                    Priority.MEDIUM -> R.color.priority_medium
                    Priority.HIGH -> R.color.priority_high
                    Priority.URGENT -> R.color.priority_urgent
                }
                priorityBadge.backgroundTintList = ContextCompat.getColorStateList(root.context, priorityColor)

                // Calculate and display progress (mockup - you can integrate real progress logic)
                val mockProgress = calculateTaskProgress(task)
                progressBar.progress = mockProgress
                tvProgress.text = "$mockProgress%"

                // Set completion state
                cbTaskCompleted.isChecked = task.isCompleted
                updateCompletionStyle(task.isCompleted)

                // Enhanced click listeners with haptic feedback
                cbTaskCompleted.setOnCheckedChangeListener { _, isChecked ->
                    // Add completion animation
                    animateTaskCompletion(isChecked)
                    onTaskCompletionChanged(task, isChecked)
                    updateCompletionStyle(isChecked)
                }

                root.setOnClickListener {
                    // Add click animation
                    animateCardClick()
                    onTaskClick(task)
                }
            }
        }

        private fun updateCompletionStyle(isCompleted: Boolean) {
            with(binding) {
                if (isCompleted) {
                    tvTaskTitle.paintFlags = tvTaskTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    tvTaskDescription.paintFlags = tvTaskDescription.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    root.alpha = 0.65f
                    // Disable progress elements for completed tasks
                    progressBar.alpha = 0.3f
                    tvProgress.alpha = 0.3f
                } else {
                    tvTaskTitle.paintFlags = tvTaskTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    tvTaskDescription.paintFlags = tvTaskDescription.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    root.alpha = 1.0f
                    progressBar.alpha = 1.0f
                    tvProgress.alpha = 1.0f
                }
            }
        }

        private fun addCardAnimation() {
            val scaleX = ObjectAnimator.ofFloat(binding.root, "scaleX", 0.95f, 1.0f)
            val scaleY = ObjectAnimator.ofFloat(binding.root, "scaleY", 0.95f, 1.0f)
            val alpha = ObjectAnimator.ofFloat(binding.root, "alpha", 0.7f, 1.0f)

            scaleX.duration = 300
            scaleY.duration = 300
            alpha.duration = 300

            scaleX.interpolator = AccelerateDecelerateInterpolator()
            scaleY.interpolator = AccelerateDecelerateInterpolator()
            alpha.interpolator = AccelerateDecelerateInterpolator()

            scaleX.start()
            scaleY.start()
            alpha.start()
        }

        private fun animateCardClick() {
            val scaleDown = ObjectAnimator.ofFloat(binding.root, "scaleX", 1.0f, 0.98f)
            val scaleUp = ObjectAnimator.ofFloat(binding.root, "scaleX", 0.98f, 1.0f)

            scaleDown.duration = 100
            scaleUp.duration = 100

            scaleDown.start()
            Handler(Looper.getMainLooper()).postDelayed({
                scaleUp.start()
            }, 100)
        }

        private fun animateTaskCompletion(isCompleted: Boolean) {
            val targetScale = if (isCompleted) 1.1f else 1.0f
            val scaleAnimation = ObjectAnimator.ofFloat(binding.cbTaskCompleted, "scaleX", targetScale)
            val scaleYAnimation = ObjectAnimator.ofFloat(binding.cbTaskCompleted, "scaleY", targetScale)

            scaleAnimation.duration = 200
            scaleYAnimation.duration = 200

            scaleAnimation.start()
            scaleYAnimation.start()

            Handler(Looper.getMainLooper()).postDelayed({
                val resetX = ObjectAnimator.ofFloat(binding.cbTaskCompleted, "scaleX", 1.0f)
                val resetY = ObjectAnimator.ofFloat(binding.cbTaskCompleted, "scaleY", 1.0f)
                resetX.duration = 200
                resetY.duration = 200
                resetX.start()
                resetY.start()
            }, 200)
        }

        private fun calculateTaskProgress(task: Task): Int {
            // Mock progress calculation - replace with your actual logic
            val now = Calendar.getInstance().time
            val createdTime = task.dueDate.time - (7 * 24 * 60 * 60 * 1000) // Assume created a week before due date
            val totalTime = task.dueDate.time - createdTime
            val elapsedTime = now.time - createdTime

            return max(0, ((elapsedTime.toFloat() / totalTime.toFloat()) * 100).toInt()).coerceAtMost(100)
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }

    // Helper data class for multiple return values
    private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}