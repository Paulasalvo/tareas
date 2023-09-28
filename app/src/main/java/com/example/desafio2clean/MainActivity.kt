package com.example.desafio2clean

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.desafio2clean.data.TaskDao
import com.example.desafio2clean.data.TaskDatabase
import com.example.desafio2clean.databinding.ActivityMainBinding
import com.example.desafio2clean.databinding.AddTaskBinding
import com.example.desafio2clean.model.TaskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: TaskListAdapter

    private lateinit var database: TaskDatabase
    private lateinit var taskDao: TaskDao



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setUpViews()
        observeAllTasks()
    }

    private fun setUpViews() {
        binding.rvTaskList.layoutManager = LinearLayoutManager(this)
        adapter = TaskListAdapter()
        binding.rvTaskList.adapter = adapter
        adapter.onClickEdit = {
            taskUIDataHolder -> showUpdateDialog(taskUIDataHolder)
        }
    }

    private fun showUpdateDialog (taskUIDataHolder: TaskUIDataHolder){
        val addTaskBinding = AddTaskBinding.inflate(layoutInflater)
        addTaskBinding.titleInputLayout.editText?.setText(taskUIDataHolder.text)
        addTaskBinding.descriptionInputLayout.editText?.setText(taskUIDataHolder.description)
        val dialogBuilder = AlertDialog
            .Builder(this)
            .setTitle("Editar Tarea")
            .setView(addTaskBinding.root)
            .setNegativeButton("Cerrar") { dialog: DialogInterface, _: Int -> dialog.dismiss() }
            .setPositiveButton("Guardar") { dialog: DialogInterface, _: Int ->
                val text = addTaskBinding.taskTitleInput.text.toString()
                val description = addTaskBinding.taskDescriptionInput.text.toString()
                if (text.isNotEmpty()) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val editTask = TaskEntity(
                            id=taskUIDataHolder.id,
                            title = text,
                            description = description,
                            createdAt = System.currentTimeMillis()
                        )
                        taskDao.updateTask(editTask)
                        dialog.dismiss()
                    }
                }

            }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()

    }
    private fun observeAllTasks() {
        lifecycleScope.launch(Dispatchers.IO) {
            database = TaskDatabase.getDatabase(this@MainActivity)
            taskDao = database.taskDao()
            taskDao.getAllTasks().collect() { taskEntityList ->
                val taskUiDataList = taskEntityList.map { taskEntity ->
                    TaskUIDataHolder(taskEntity.id, taskEntity.title, taskEntity.description)
                }
                withContext(Dispatchers.Main){
                    adapter.updateData(taskUiDataList)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when (item.itemId) {
            R.id.add -> addTask()
            R.id.remove_all -> removeAll()
        }
        return true
    }

    private fun addTask() {
        val addTaskBinding = AddTaskBinding.inflate(layoutInflater)
        val taskText = addTaskBinding.taskTitleInput
        val descriptionText = addTaskBinding.taskDescriptionInput
        val dialogBuilder = AlertDialog
            .Builder(this)
            .setTitle("Agrega una Tarea")
            .setView(addTaskBinding.root)
            .setNegativeButton("Cerrar") { dialog: DialogInterface, _: Int -> dialog.dismiss() }
            .setPositiveButton("Agregar") { dialog: DialogInterface, _: Int ->
                val text = taskText.text.toString()
                val description = descriptionText.text.toString()
                if (taskText.text?.isNotEmpty()!!) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val newTask = TaskEntity(
                            title = text,
                            description = description,
                            createdAt = System.currentTimeMillis()
                        )
                        taskDao.insertTask(newTask)
                        dialog.dismiss()
                    }
                }
            }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    private fun removeAll() {
        val dialog = AlertDialog
            .Builder(this)
            .setTitle("Borrar Todo")
            .setMessage("¿Desea Borrar todas las tareas?")
            .setNegativeButton("Cerrar") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            .setPositiveButton("Aceptar") { dialog: DialogInterface, _: Int ->
                lifecycleScope.launch(Dispatchers.IO) {
                    taskDao.deleteAllTasks()
                }
            }
        dialog.show()
    }
}