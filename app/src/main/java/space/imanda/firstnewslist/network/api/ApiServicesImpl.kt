package space.imanda.firstnewslist.network.api

import retrofit2.Response
import space.imanda.firstnewslist.data.model.NewsResponse
import space.imanda.firstnewslist.network.api.NewsApi
import javax.inject.Inject

class ApiServicesImpl @Inject constructor(private val newsApi: NewsApi): ApiServices {

    override suspend fun getNews(countryCode: String, pageNumber: Int, category: String): Response<NewsResponse> =
        newsApi.getNews(countryCode, pageNumber, category)

    override suspend fun searchNews(query: String, pageNumber: Int): Response<NewsResponse> =
        newsApi.searchNews(query, pageNumber)

}