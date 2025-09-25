package com.example.newsapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState

class GenericPagingSource<T: Any>(
    private val fetchPage: suspend (page: Int) -> List<T>
) : PagingSource<Int, T>() {

    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPos ->
            val anchorPage = state.closestPageToPosition(anchorPos)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val page = params.key ?: STARTING_PAGE_INDEX

        return try {
            val items = fetchPage(page)
            val nextKey = if (items.size < params.loadSize) null else page + 1
            LoadResult.Page(
                data = items,
                prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}