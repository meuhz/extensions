package com.example.demo.activity.paging

import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.example.demo.R
import com.example.demo.adapter.ViewHolderImpl
import com.example.demo.databinding.HolderLoadStateBinding

class FooterLoadStateAdapter(private val retryCallback: View.OnClickListener? = null) :
    LoadStateAdapter<ViewHolderImpl>() {
    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolderImpl {
        return ViewHolderImpl.create(parent, R.layout.holder_load_state)
    }

    override fun onBindViewHolder(holder: ViewHolderImpl, loadState: LoadState) {
        val binding = HolderLoadStateBinding.bind(holder.itemView)

        if (loadState is LoadState.Error) {
            binding.errorMsg.text = loadState.error.message
            binding.errorMsg.visibility = View.VISIBLE
            binding.retryButton.visibility = View.VISIBLE
            binding.retryButton.setOnClickListener(retryCallback)
        } else {
            binding.errorMsg.visibility = View.GONE
            binding.retryButton.visibility = View.GONE
        }
        if (loadState is LoadState.Loading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}