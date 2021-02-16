package com.aatech_aplha.news.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.aatech_aplha.news.R
import com.aatech_aplha.news.databinding.ActivityMainBinding
import com.aatech_aplha.news.ui.AllNews.AllNews
import com.aatech_aplha.news.ui.SavedNews.SavedFragment
import com.aatech_aplha.news.ui.TopHeading.TopHeadingFragment
import com.aatech_aplha.news.ui.TopHeading.TopHeadingViewModel
import com.aatech_aplha.news.util.settingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: TopHeadingViewModel by viewModels()
    private val newsFragment = TopHeadingFragment()
    private val allNews = AllNews()
    private val savedFragment = SavedFragment()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        title = "Home"
        settingFragment(supportFragmentManager, allNews, "1")

        binding.menu.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.newsFragment -> {
                    settingFragment(supportFragmentManager, newsFragment, "2")
                    title = "Top Headings"
                }
                R.id.allNews -> {
                    title = "Home"
                    settingFragment(supportFragmentManager, allNews, "1")
                }
                R.id.searchFragment -> {
                    title = "Saved"
                    settingFragment(supportFragmentManager, savedFragment, "3")
                }
            }
            true
        }

        viewModel.isConnected.observe(this) {
            binding.no.isVisible = it

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}