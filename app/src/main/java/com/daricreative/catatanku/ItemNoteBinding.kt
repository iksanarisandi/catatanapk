package com.daricreative.catatanku

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.daricreative.catatanku.R

class ItemNoteBinding(private val rootView: View) {
    val tvTitle: TextView = rootView.findViewById(R.id.tvTitle)
    val tvContent: TextView = rootView.findViewById(R.id.tvContent)
    val tvDate: TextView = rootView.findViewById(R.id.tvDate)
    val root: View = rootView

    companion object {
        fun inflate(inflater: LayoutInflater, parent: ViewGroup, attachToParent: Boolean): ItemNoteBinding {
            return ItemNoteBinding(inflater.inflate(R.layout.item_note, parent, attachToParent))
        }
    }
}
