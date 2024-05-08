package com.ariqa.storyapp.view.main

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ariqa.storyapp.R
import com.ariqa.storyapp.ViewModelFactory
import com.ariqa.storyapp.data.response.ListStoryItem
import com.ariqa.storyapp.databinding.ActivityMainBinding
import com.ariqa.storyapp.view.login.LoginActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this) { user ->
            if (user.token.isNotEmpty() && user.token != "") {
                token = user.token
                setupView()
                setupAction(token)

            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
//        playAnimation()
    }

    private fun setupAction(token: String) {
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.settings -> {
                    viewModel.logout()
                    true
                }

                else -> false
            }
        }

        lifecycleScope.launch {
            viewModel.getAllStoriesItem.collectLatest {
                setAllStoriesList(it)
            }
        }
        viewModel.getAllStories(token)
    }

    private fun setAllStoriesList(items: List<ListStoryItem>) {
        val adapter = AllStoriesAdapter()
        adapter.submitList(items)
        with(binding) {
            rvListStories.layoutManager = LinearLayoutManager(this@MainActivity)
            rvListStories.adapter = adapter
            rvListStories.setHasFixedSize(true)
        }
        if (items.isNotEmpty()) {
            Snackbar.make(binding.root, "Result ${items.size}", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

//    private fun playAnimation() {
//        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
//            duration = 6000
//            repeatCount = ObjectAnimator.INFINITE
//            repeatMode = ObjectAnimator.REVERSE
//        }.start()
//
//        //textView
//        val nameText = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
//        val messageText = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(100)
//        //button
//        val logout = ObjectAnimator.ofFloat(binding.logoutButton, View.ALPHA, 1f).setDuration(100)
//
//        val together = AnimatorSet().apply {
//            playTogether(logout)
//        }
//
//        AnimatorSet().apply {
//            playSequentially(nameText, messageText, together)
//            start()
//        }
//    }

}