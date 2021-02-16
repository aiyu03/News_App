package com.aatech_aplha.news.ui.TopHeading

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.aatech_aplha.news.Database.NewsSaved
import com.aatech_aplha.news.R
import com.aatech_aplha.news.data.News
import com.aatech_aplha.news.databinding.FragmentTopHeadingBinding
import com.aatech_aplha.news.ui.Description.NewsDescription
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialElevationScale
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TopHeadingFragment : Fragment(R.layout.fragment_top_heading), Adapter.ClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialElevationScale(/* growing= */ false)
        reenterTransition = MaterialElevationScale(/* growing= */ true)
    }

    private val viewModel: TopHeadingViewModel by viewModels()
    private var _binding: FragmentTopHeadingBinding? = null
    private val binding get() = _binding!!
    private val TAG = "NewsFragment"

    companion object {
        const val EXTRA_NEWS = "com.aatech_aplha.news.ui.NewsFragment_EXTRA_NEWS"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTopHeadingBinding.bind(view)
        val mainAdapter = Adapter(this)

        binding.apply {
            showNews.apply {
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                adapter = mainAdapter.withLoadStateHeaderAndFooter(
                    header = NewsLoadStateAdapter { mainAdapter.retry() },
                    footer = NewsLoadStateAdapter { mainAdapter.retry() }
                )
            }

            swipeLayout.setOnRefreshListener {
                mainAdapter.refresh()
            }
        }
        viewModel.isConnected.observe(viewLifecycleOwner) {
            if (!it) {
                mainAdapter.retry()
            }
        }

        lifecycleScope.launch {
            mainAdapter.loadStateFlow.collectLatest {
                binding.progressBar.isVisible = it.refresh is LoadState.Loading
            }
        }
        viewModel.news.observe(viewLifecycleOwner) {
            mainAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            binding.swipeLayout.isRefreshing = false
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.newsEventFlow.collect { event ->
                when (event) {
                    is TopHeadingViewModel.NewsEvent.NewsAdded -> {
                        Snackbar.make(binding.content, "Added", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                    is TopHeadingViewModel.NewsEvent.NewsNotAdded -> {
                        Snackbar.make(binding.content, "Already Added", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClickListener(news: News, view: View) {
        val intent = Intent(requireContext(), NewsDescription::class.java)
        val options = ActivityOptions.makeSceneTransitionAnimation(
            requireActivity(),
            view,
            "ayaan"
        )
        intent.putExtra(EXTRA_NEWS, news)
        startActivity(intent, options.toBundle())
    }

    override fun onShareClick(news: News) {
        val share = Intent()
        share.action = Intent.ACTION_SEND
        share.putExtra(Intent.EXTRA_SUBJECT, "Share News")
        val sb = StringBuilder()
        sb.append(news.title + "\n" + news.description + "\n \nLink :" + news.url)
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