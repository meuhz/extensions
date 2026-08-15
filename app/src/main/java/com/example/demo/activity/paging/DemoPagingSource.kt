package com.example.demo.activity.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class DemoPagingSource(private val mQuery: String) : PagingSource<Int, ItemData>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ItemData> {
        return withContext(Dispatchers.IO) {
            try {
                val page = params.key ?: 1
                val items = ArrayList<ItemData>()
                for (i in 0..9) {
                    items.add(ItemData("$mQuery $page: $i"))
                }
                if (page > 1) Thread.sleep(2000)
                LoadResult.Page(items, null, if (page == 5) null else page + 1)
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ItemData>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val anchorPage = state.closestPageToPosition(anchorPosition) ?: return null

        val prevKey = anchorPage.prevKey
        if (prevKey != null) {
            return prevKey + 1
        }

        val nextKey = anchorPage.nextKey
        if (nextKey != null) {
            return nextKey - 1
        }
        return null
    }
}