package com.aatech_aplha.news.ui.AllNews

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.aatech_aplha.news.Database.NewsSaved
import com.aatech_aplha.news.R
import com.aatech_aplha.news.data.News
import com.aatech_aplha.news.databinding.FragmentHomeScreenBinding
import com.aatech_aplha.news.ui.Description.NewsDescription
import com.aatech_aplha.news.ui.TopHeading.Adapter
import com.aatech_aplha.news.ui.TopHeading.NewsLoadStateAdapter
import com.aatech_aplha.news.ui.TopHeading.TopHeadingFragment
import com.aatech_aplha.news.util.onQueryTextChanged
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialElevationScale
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllNews : Fragment(R.layout.fragment_home_screen), Adapter.ClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialElevationScale(/* growing= */ false)
        reenterTransition = MaterialElevationScale(/* growing= */ true)
    }

    private val viewModel: AllNewsViewModel by viewModels()

    private lateinit var searchView: SearchView
    private var _binding: FragmentHomeScreenBinding? = null
    private val binding: FragmentHomeScreenBinding get() = _binding!!
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeScreenBinding.bind(view)
        val allNewsAdapter = Adapter(this)
        binding.apply {
            mainView.apply {
                adapter = allNewsAdapter.withLoadStateHeaderAndFooter(
                    header = NewsLoadStateAdapter { allNewsAdapter.retry() },
                    footer = NewsLoadStateAdapter { allNewsAdapter.retry() }
                )
                layoutManager = LinearLayoutManager(requireContext())
            }
            swipeLayout.setOnRefreshListener {
                allNewsAdapter.refresh()
            }
        }

        lifecycleScope.launch {
            allNewsAdapter.loadStateFlow.collectLatest {
                binding.progressBar.isVisible = it.refresh is LoadState.Loading
                binding.noData.isVisible = it.refresh !is LoadState.Loading
                binding.noData.isVisible = it.append.endOfPaginationReached
            }
        }
        viewModel.isConnected.observe(viewLifecycleOwner) {
            if (!it) {
                allNewsAdapter.retry()
            }
        }

        viewModel.news.observe(viewLifecycleOwner) { news ->
            allNewsAdapter.submitData(viewLifecycleOwner.lifecycle, news)
            binding.swipeLayout.isRefreshing = false
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.newsEventFlow.collect { event ->
                when (event) {
                    is AllNewsViewModel.NewsEvent.NewsAdded -> {
                        Snackbar.make(binding.content, "Added", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                    is AllNewsViewModel.NewsEvent.NewsNotAdded -> {
                        Snackbar.make(binding.content, "Already Added", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
        setHasOptionsMenu(true)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        searchView.setOnQueryTextListener(null)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView
        searchView.onQueryTextChanged(searchView) {
            binding.mainView.scrollToPosition(0)
            viewModel.searchQuery.value = if (it.isBlank()) AllNewsViewModel.DEFAULT_QUERY else it
        }
    }

    override fun onClickListener(news: News, view: View) {
        val intent = Intent(requireContext(), NewsDescription::class.java)
        val options = ActivityOptions.makeSceneTransitionAnimation(
            requireActivity(),
            view,
            "ayaan"
        )
        intent.putExtra(TopHeadingFragment.EXTRA_NEWS, news)
        startActivity(intent, options.toBundle())
    }

    override fun onShareClick(news: News) {
        val share = Intent()
        share.action = Intent.ACTION_SEND
        share.putExtra(Intent.EXTRA_SUBJECT, "Share News")
        val sb = StringBuilder()
        sb.append(news.title + "\n\n" + news.description + "\n \nLink :" + news.url)
        share.putExtra(
            Intent.EXTRA_TEXT, sb.toString()
        )
        share.type = "text/plain"
        startActivity(share)
    }

    override fun onSaveNewsClick(news: News) {
        val newsSaved = NewsSaved(
            author = news.author,
            title = news.title,
            description = news.description,
            url = news.url,
            urlToImage = news.urlToImage,
            publishedAt = news.publishedAt,
            content = news.content,
            source = NewsSaved.NewsSource(
                id = news.source.id,
                name = news.source.name
            )
        )
        viewModel.addNews(newsSaved)
    }
}