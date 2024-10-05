package com.example.todolist

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var taskList: ArrayList<String>
    private lateinit var taskIds: ArrayList<Int>
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        databaseHelper = DatabaseHelper(this)
        val taskEditText = findViewById<EditText>(R.id.taskEditText)
        val addTaskButton = findViewById<Button>(R.id.addTaskButton)
        val taskListView = findViewById<ListView>(R.id.taskListView)

        taskList = ArrayList()
        taskIds = ArrayList()

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, taskList)
        taskListView.adapter = adapter

        loadTasks()

        addTaskButton.setOnClickListener {
            val task = taskEditText.text.toString()
            if (task.isNotEmpty()) {
                val id = databaseHelper.insertTask(task)
                if (id > -1) {
                    taskList.add(task)
                    taskIds.add(id.toInt())
                    adapter.notifyDataSetChanged()
                    taskEditText.text.clear()
                    Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error adding task", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Enter a task", Toast.LENGTH_SHORT).show()
            }
        }

        taskListView.setOnItemClickListener { _, _, position, _ ->
            val id = taskIds[position]
            val result = databaseHelper.deleteTask(id)
            if (result > 0) {
                taskList.removeAt(position)
                taskIds.removeAt(position)
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error deleting task", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadTasks() {
        val cursor = databaseHelper.getAllTasks()
        if (cursor.moveToFirst()) {
            do {
                val task =
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK))
                val id =
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))
                taskList.add(task)
                taskIds.add(id)
            } while (cursor.moveToNext())
        }
        cursor.close()

    }
}