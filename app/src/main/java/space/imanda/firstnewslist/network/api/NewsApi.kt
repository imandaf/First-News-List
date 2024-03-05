package space.imanda.firstnewslist.network.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import space.imanda.firstnewslist.data.model.NewsResponse
import space.imanda.firstnewslist.utils.Constants.Companion.API_KEY
import space.imanda.firstnewslist.utils.Constants.Companion.COUNTRY_CODE
import space.imanda.firstnewslist.utils.Constants.Companion.QUERY_PER_PAGE
import space.imanda.firstnewslist.utils.Constants.Companion.TECHNOLOGY

interface NewsApi {

    @GET("v2/top-headlines")
    suspend fun getNews(
        @Query("country")
        countryCode: String = COUNTRY_CODE,
        @Query("page")
        pageNumber: Int = 1,
        @Query("category")
        category: String = TECHNOLOGY,
        @Query("pageSize")
        pageSize: Int = QUERY_PER_PAGE,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

    @GET("v2/everything")
    suspend fun searchNews(
        @Query("q")
        searchQuery: String,
        @Query("page")
        pageNumber: Int = 1,
        @Query("pageSize")
        pageSize: Int = QUERY_PER_PAGE,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>


}