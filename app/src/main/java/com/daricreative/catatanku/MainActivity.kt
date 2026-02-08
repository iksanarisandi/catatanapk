package com.daricreative.catatanku

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daricreative.catatanku.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var database: AppDatabase
    private lateinit var rvNotes: RecyclerView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var emptyState: LinearLayout
    private lateinit var adapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = AppDatabase.getDatabase(this)

        initViews()
        setupRecyclerView()
        observeNotes()
    }

    private fun initViews() {
        rvNotes = findViewById(R.id.rvNotes)
        fabAdd = findViewById(R.id.fabAdd)
        emptyState = findViewById(R.id.emptyState)

        fabAdd.setOnClickListener {
            showNoteDialog(null)
        }
    }

    private fun setupRecyclerView() {
        adapter = NoteAdapter(
            notes = emptyList(),
            onItemClick = { note -> showNoteDialog(note) },
            onItemLongClick = { note ->
                showDeleteDialog(note)
                true
            }
        )
        rvNotes.layoutManager = LinearLayoutManager(this)
        rvNotes.adapter = adapter
    }

    private fun observeNotes() {
        lifecycleScope.launch {
            database.noteDao().getAllNotes().collect { notes ->
                adapter.updateNotes(notes)
                updateEmptyState(notes.isEmpty())
            }
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        emptyState.visibility = if (isEmpty) {
            rvNotes.visibility = android.view.View.GONE
            android.view.View.VISIBLE
        } else {
            android.view.View.GONE
        }
    }

    private fun showNoteDialog(note: NoteEntity?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_note, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.etTitle)
        val etContent = dialogView.findViewById<EditText>(R.id.etContent)

        if (note != null) {
            etTitle.setText(note.title)
            etContent.setText(note.content)
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val dialogTitle = TextView(this).apply {
            text = if (note == null) getString(R.string.add_note) else getString(R.string.edit_note)
            textSize = 20f
            setPadding(40, 40, 40, 20)
            setTextColor(resources.getColor(R.color.text_primary))
        }

        dialog.setCustomTitle(dialogTitle)

        val btnSave = Button(this).apply {
            text = getString(R.string.save)
            setBackgroundColor(resources.getColor(R.color.primary))
            setTextColor(resources.getColor(android.R.color.white))
            setOnClickListener {
                val title = etTitle.text.toString().trim()
                val content = etContent.text.toString().trim()

                if (title.isEmpty() && content.isEmpty()) {
                    etTitle.error = "Judul atau konten harus diisi"
                    return@setOnClickListener
                }

                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        if (note == null) {
                            database.noteDao().insert(
                                NoteEntity(
                                    title = title,
                                    content = content
                                )
                            )
                        } else {
                            database.noteDao().update(
                                note.copy(
                                    title = title,
                                    content = content,
                                    updatedAt = System.currentTimeMillis()
                                )
                            )
                        }
                    }
                    dialog.dismiss()
                }
            }
        }

        val btnCancel = Button(this).apply {
            text = getString(R.string.cancel)
            setBackgroundColor(resources.getColor(android.R.color.transparent))
            setTextColor(resources.getColor(R.color.text_secondary))
            setOnClickListener { dialog.dismiss() }
        }

        val buttonContainer = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(40, 20, 40, 40)
            gravity = android.view.Gravity.END
            addView(btnCancel)
            addView(btnSave)
        }

        dialog.setView(dialogView)
        dialog.setView(buttonContainer)

        dialog.show()
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun showDeleteDialog(note: NoteEntity) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_note))
            .setMessage(getString(R.string.confirm_delete))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        database.noteDao().delete(note)
                    }
                }
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }
}
