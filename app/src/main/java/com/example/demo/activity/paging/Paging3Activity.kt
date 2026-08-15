package com.example.demo.activity.paging

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.demo.activity.BaseActivity
import com.example.demo.databinding.ActivityPaging3Binding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class Paging3Activity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityPaging3Binding.inflate(layoutInflater)
        setContentView(binding.getRoot())
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        val pagingAdapter = ItemPagingAdapter()
        val footerAdapter = FooterLoadStateAdapter { pagingAdapter.retry() }
        val concatAdapter = pagingAdapter.withLoadStateFooter(footerAdapter)
        binding.recyclerview.setAdapter(concatAdapter)

        binding.swipeRefreshLayout.setOnRefreshListener {
            pagingAdapter.refresh()
        }

        pagingAdapter.addLoadStateListener {
            binding.swipeRefreshLayout.isRefreshing = it.refresh is LoadState.Loading
        }

        val viewModel by viewModels<PagingViewModel>()
        val flow = Pager(
            config = PagingConfig(10),
            pagingSourceFactory = { DemoPagingSource("Page") }
        ).flow.cachedIn(viewModel.viewModelScope)

        lifecycleScope.launch {
            flow.collectLatest { pagingAdapter.submitData(it) }
        }
    }
}