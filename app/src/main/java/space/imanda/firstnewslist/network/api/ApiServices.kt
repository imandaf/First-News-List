package space.imanda.firstnewslist.network.api

import retrofit2.Response
import space.imanda.firstnewslist.data.model.NewsResponse

interface ApiServices {
    suspend fun getNews(countryCode: String, pageNumber: Int, category: String): Response<NewsResponse>
    suspend fun searchNews(query: String, pageNumber: Int): Response<NewsResponse>
}
