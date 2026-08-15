package com.example.demo.activity.paging

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.demo.R
import com.example.demo.adapter.ViewHolderImpl
import com.example.demo.databinding.ItemDemoBinding

internal class ItemPagingAdapter :
    PagingDataAdapter<ItemData, ViewHolderImpl>(object : DiffUtil.ItemCallback<ItemData>() {
        override fun areItemsTheSame(oldItem: ItemData, newItem: ItemData): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ItemData, newItem: ItemData): Boolean {
            return oldItem == newItem
        }
    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderImpl {
        return ViewHolderImpl.create(parent, R.layout.item_demo)
    }

    override fun onBindViewHolder(holder: ViewHolderImpl, position: Int) {
        val binding = ItemDemoBinding.bind(holder.itemView)
        val item = getItem(position)

        binding.text.text = item?.name
    }
}