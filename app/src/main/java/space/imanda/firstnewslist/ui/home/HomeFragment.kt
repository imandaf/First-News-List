package space.imanda.firstnewslist.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import space.imanda.firstnewslist.R
import space.imanda.firstnewslist.databinding.FragmentHomeBinding
import space.imanda.firstnewslist.network.state.NetworkState
import space.imanda.firstnewslist.ui.adapter.NewsAdapter
import space.imanda.firstnewslist.ui.main.MainActivity
import space.imanda.firstnewslist.ui.main.MainViewModel
import space.imanda.firstnewslist.utils.Constants
import space.imanda.firstnewslist.utils.Constants.Companion.QUERY_PER_PAGE
import space.imanda.firstnewslist.utils.EndlessRecyclerOnScrollListener

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var onScrollListener: EndlessRecyclerOnScrollListener
    lateinit var mainViewModel: MainViewModel
    private lateinit var newsAdapter: NewsAdapter
    val countryCode = Constants.COUNTRY_CODE

    private var newsCategory: String = Constants.TECHNOLOGY

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = (activity as MainActivity).mainViewModel
        setupUI()
        setupRecyclerView()
        setupObservers()
    }

    private fun setupUI() {
        binding.itemErrorMessage.btnRetry.setOnClickListener {
            mainViewModel.getNews(countryCode, newsCategory)
            hideErrorMessage()
        }

        onScrollListener = object : EndlessRecyclerOnScrollListener(QUERY_PER_PAGE) {
            override fun onLoadMore() {
                mainViewModel.getNews(countryCode, newsCategory)
            }
        }

        val refreshListener = SwipeRefreshLayout.OnRefreshListener {

            try {
                binding.swipeRefreshLayout.isRefreshing = false
                mainViewModel.getNews(countryCode, newsCategory)
            } catch (exception: Exception) {
                exception.printStackTrace()
            }

        }
        binding.swipeRefreshLayout.setOnRefreshListener(refreshListener)
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(onScrollListener)
        }
        newsAdapter.setOnItemClickListener { news ->
            val bundle = Bundle().apply {
                putSerializable("news", news)
            }
            findNavController().navigate(
                R.id.action_homeFragment_to_detailFragment,
                bundle
            )
        }
    }

    private fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            mainViewModel.newsResponse.collect { response ->
                when (response) {
                    is NetworkState.Success -> {
                        hideProgressBar()
                        hideErrorMessage()
                        response.data?.let { newResponse ->

                            newsAdapter.differ.submitList(newResponse.articles.toList())
                            mainViewModel.totalPage =
                                newResponse.totalResults / QUERY_PER_PAGE + 1
                            onScrollListener.isLastPage =
                                mainViewModel.getNewsPage == mainViewModel.totalPage + 1
                            hideBottomPadding()
                        }
                    }

                    is NetworkState.Loading -> {
                        showProgressBar()
                    }

                    is NetworkState.Error -> {
                        hideProgressBar()
                        response.message?.let {
                            showErrorMessage(response.message)
                        }
                    }

                    else -> {}
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            mainViewModel.errorMessage.collect { value ->
                if (value.isNotEmpty()) {
                    Toast.makeText(activity, value, Toast.LENGTH_LONG).show()
                }
                mainViewModel.hideErrorToast()
            }
        }
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showErrorMessage(message: String) {
        binding.itemErrorMessage.errorCard.visibility = View.VISIBLE
        binding.itemErrorMessage.tvErrorMessage.text = message
        onScrollListener.isError = true
    }

    private fun hideErrorMessage() {
        binding.itemErrorMessage.errorCard.visibility = View.GONE
        onScrollListener.isError = false
    }

    private fun hideBottomPadding() {
        if (onScrollListener.isLastPage) {
            binding.rvNews.setPadding(0, 0, 0, 0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}