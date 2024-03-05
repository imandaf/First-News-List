package space.imanda.firstnewslist.network.repository

import org.json.JSONObject
import space.imanda.firstnewslist.data.model.NewsResponse
import space.imanda.firstnewslist.network.api.ApiServices
import space.imanda.firstnewslist.network.state.NetworkState
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val remoteDataSource: ApiServices
) : INewsRepository {

    override suspend fun getNews(
        countryCode: String,
        pageNumber: Int,
        category: String
    ): NetworkState<NewsResponse> {
        return try {

            val response = remoteDataSource.getNews(countryCode, pageNumber, category)
            val result = response.body()

            if (response.isSuccessful && result != null) {
                NetworkState.Success(result)
            } else {
                val initialData = response.errorBody()?.string()
                val jsonObject = JSONObject(initialData)
                val messageData = jsonObject.getString("message")
                NetworkState.Error(messageData ?: "An error occurred")
            }
        } catch (exception: Exception) {
            NetworkState.Error("Error occurred ${exception.localizedMessage}")
        }
    }

    override suspend fun searchNews(
        searchQuery: String,
        pageNumber: Int
    ): NetworkState<NewsResponse> {

        return try {
            val response = remoteDataSource.searchNews(searchQuery, pageNumber)
            val result = response.body()
            if (response.isSuccessful && result != null) {
                NetworkState.Success(result)
            } else {
                val initialData = response.errorBody()?.string()
                val jsonObject = JSONObject(initialData)
                val messageData = jsonObject.getString("message")
                NetworkState.Error(messageData ?: "An error occurred")
            }
        } catch (exception: Exception) {
            NetworkState.Error("Error occurred ${exception.localizedMessage}")
        }

    }

}