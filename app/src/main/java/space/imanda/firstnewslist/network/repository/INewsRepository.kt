package space.imanda.firstnewslist.network.repository

import space.imanda.firstnewslist.data.model.NewsResponse
import space.imanda.firstnewslist.network.state.NetworkState

interface INewsRepository {

    suspend fun getNews(countryCode: String, pageNumber: Int, category: String): NetworkState<NewsResponse>

    suspend fun searchNews(searchQuery: String, pageNumber: Int): NetworkState<NewsResponse>

}