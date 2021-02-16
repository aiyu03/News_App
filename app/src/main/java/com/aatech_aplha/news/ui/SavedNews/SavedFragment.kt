package com.aatech_aplha.news.ui.SavedNews

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aatech_aplha.news.Database.NewsSaved
import com.aatech_aplha.news.R
import com.aatech_aplha.news.data.News
import com.aatech_aplha.news.databinding.FragmentSavedBinding
import com.aatech_aplha.news.ui.Description.NewsDescription
import com.aatech_aplha.news.ui.TopHeading.TopHeadingFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialElevationScale
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SavedFragment : Fragment(R.layout.fragment_saved), SavedNewsAdapter.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialElevationScale(/* growing= */ false)
        reenterTransition = MaterialElevationScale(/* growing= */ true)
    }

    private val viewModel: SavedFragmentViewModel by viewModels()

    private var _binding: FragmentSavedBinding? = null
    private val binding: FragmentSavedBinding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSavedBinding.bind(view)
        val newsAdapter = SavedNewsAdapter(this)

        binding.apply {
            saveNewsView.apply {
                adapter = newsAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }
        viewModel.savedNews.observe(viewLifecycleOwner) {
            newsAdapter.submitList(it)
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.savedEvent.collect { event ->
                Snackbar.make(binding.content, "Deleted", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onShareClick(news: NewsSaved) {
        viewModel.deleteNews(news)
    }

    override fun onItemClick(news: NewsSaved, view: View) {
        val news = News(
            author = news.author,
            title = news.title,
            description = news.description,
            url = news.url,
            urlToImage = news.urlToImage,
            publishedAt = news.publishedAt,
            content = news.content,
            source = News.NewsSource(
                id = news.source.id,
                name = news.source.name
            )
        )
        val intent = Intent(requireContext(), NewsDescription::class.java)
        val options = ActivityOptions.makeSceneTransitionAnimation(
            requireActivity(),
            view,
            "ayaan"
        )
        intent.putExtra(TopHeadingFragment.EXTRA_NEWS, news)
        startActivity(intent, options.toBundle())
    }
}