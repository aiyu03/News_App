package com.aatech_aplha.news.ui.Description

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.palette.graphics.Palette
import com.aatech_aplha.news.R
import com.aatech_aplha.news.data.News
import com.aatech_aplha.news.databinding.ActivityNewsDescriptionBinding
import com.aatech_aplha.news.ui.TopHeading.TopHeadingFragment
import com.bumptech.glide.Glide
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class NewsDescription : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementEnterTransition = MaterialContainerTransform().apply {
            addTarget(R.id.content)
            duration = 500L
        }
        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(R.id.content)
            duration = 350L
        }
        super.onCreate(savedInstanceState)
        val binding =
            ActivityNewsDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        title = "Detail"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        val news: News = intent.getSerializableExtra(TopHeadingFragment.EXTRA_NEWS) as News
        binding.apply {
            Glide.with(this@NewsDescription)
                .asBitmap()
                .load(news.urlToImage)
                .centerCrop()
                .error(R.drawable.ic_no_photo)
                .into(icon)

            title.setExpandedTitleColor(resources.getColor(android.R.color.transparent))
            title.setCollapsedTitleTextColor(Color.rgb(0, 0, 0))
            loadView.apply {
                loadUrl(news.url)
                settings.javaScriptEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.domStorageEnabled = true
                settings.loadsImagesAutomatically = true

                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        view.loadUrl(url)
                        return true
                    }
                }
                webChromeClient = object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView, newProgress: Int) {
                        if (newProgress == 100) {
                            binding.loading.visibility = View.GONE
                        }
                        super.onProgressChanged(view, newProgress)
                    }
                }
            }

        }
        GlobalScope.launch {
            if (news.urlToImage != null) {
                try {
                    val bitmap: Bitmap = Glide
                        .with(this@NewsDescription)
                        .asBitmap()
                        .load(news.urlToImage)
                        .error(R.drawable.ic_load)
                        .submit()
                        .get()

                    Palette.from(bitmap).generate {
                        if (it != null) {
                            binding.title.setContentScrimColor(it.getMutedColor(R.attr.colorPrimary))
                            val window: Window = window
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                            window.statusBarColor = it.getMutedColor(R.attr.colorPrimary)
                        }
                    }
                } catch (e: Exception) {
                    Log.d("Error", "${e.message}")
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finishAfterTransition()
        }
        return super.onOptionsItemSelected(item)
    }


}