package com.example.desafio2clean

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.desafio2clean.databinding.TaskItemBinding


class TaskListAdapter : RecyclerView.Adapter<TaskViewHolder>(){


    var taskList = mutableListOf<TaskUIDataHolder>()

    lateinit var onClickEdit:(TaskUIDataHolder)->Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = TaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    override fun onBindViewHolder(taskViewHolder: TaskViewHolder, position: Int) {
        val data = taskList[position]
        taskViewHolder.binding.taskText.text = data.text
        taskViewHolder.binding.taskText.setOnClickListener{
            onClickEdit(data)
        }
    }

    fun updateData(items: List<TaskUIDataHolder>) {
        taskList.clear()
        taskList.addAll(items)
        notifyDataSetChanged()
    }
}