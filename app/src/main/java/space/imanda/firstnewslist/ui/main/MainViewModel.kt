package space.imanda.firstnewslist.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import space.imanda.firstnewslist.data.model.NewsResponse
import space.imanda.firstnewslist.network.repository.INewsRepository
import space.imanda.firstnewslist.network.state.NetworkState
import space.imanda.firstnewslist.utils.Constants
import space.imanda.firstnewslist.utils.NetworkHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: INewsRepository,
    private val networkHelper: NetworkHelper
): ViewModel() {

    private val TAG = "MainViewModel"
    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String>
        get() = _errorMessage

    private val _newsResponse = MutableStateFlow<NetworkState<NewsResponse>>(NetworkState.Empty())
    val newsResponse: StateFlow<NetworkState<NewsResponse>>
        get() = _newsResponse

    private var getNewsResponse: NewsResponse? = null
    var getNewsPage = 1
    var totalPage = 1

    init {
        getNews(Constants.COUNTRY_CODE, Constants.TECHNOLOGY)
        Log.i(TAG, "init getNews")
    }

    fun getNews(countryCode: String, category: String) {

        if (getNewsPage <= totalPage) {
            if (networkHelper.isNetworkConnected()) {
                viewModelScope.launch {
                    _newsResponse.value = NetworkState.Loading()

                    when (val response = repository.getNews(countryCode, getNewsPage, category)) {
                        is NetworkState.Success -> {
                            _newsResponse.value = handleGetNewsResponse(response)
                        }
                        is NetworkState.Error -> {
                            _newsResponse.value = NetworkState.Error(response.message ?: "Error")
                        }
                        else -> {}
                    }

                }
            } else {
                _errorMessage.value = "No internet available"
            }
        }
    }

    private fun handleGetNewsResponse(response: NetworkState<NewsResponse>): NetworkState<NewsResponse> {
        response.data?.let { resultResponse ->
            if (getNewsResponse == null) {
                getNewsPage = 2
                getNewsResponse = resultResponse
            } else {
                getNewsPage++
                val oldArticles = getNewsResponse?.articles
                val newArticles = resultResponse.articles
                oldArticles?.addAll(newArticles)
            }
            //Conversion
            getNewsResponse?.let {
                convertPublishedDate(it)
            }
            return NetworkState.Success(getNewsResponse ?: resultResponse)
        }
        return NetworkState.Error("No data found")
    }

    private fun convertPublishedDate(currentResponse: NewsResponse) {
        currentResponse.articles.map { article ->
            article.publishedAt?.let {
                article.publishedAt = formatDate(it)
            }
        }
    }

    fun formatDate(strCurrentDate: String): String {
        var convertedDate = ""
        try {
            if (strCurrentDate.isNotEmpty() && strCurrentDate.contains("T")) {
                val local = Locale("US")
                var format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", local)
                val newDate: Date? = format.parse(strCurrentDate)

                format = SimpleDateFormat("MMM dd, yyyy hh:mm a", local)
                newDate?.let {
                    convertedDate = format.format(it)
                }
            } else {
                convertedDate = strCurrentDate
            }
        } catch (e: Exception) {
            e.message?.let {
                Log.e(TAG, it)
            }
            convertedDate = strCurrentDate
        }
        return convertedDate
    }

    fun hideErrorToast() {
        _errorMessage.value = ""
    }

}