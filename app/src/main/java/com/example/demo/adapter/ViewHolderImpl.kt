package com.example.demo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

class ViewHolderImpl(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun create(parent: ViewGroup, @LayoutRes resource: Int): ViewHolderImpl {
            return ViewHolderImpl(
                LayoutInflater.from(parent.context).inflate(resource, parent, false)
            )
        }
    }
}