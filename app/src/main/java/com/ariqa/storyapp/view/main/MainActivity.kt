package com.ariqa.storyapp.view.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ariqa.storyapp.R
import com.ariqa.storyapp.ViewModelFactory
import com.ariqa.storyapp.data.Result
import com.ariqa.storyapp.data.response.ListStoryItem
import com.ariqa.storyapp.databinding.ActivityMainBinding
import com.ariqa.storyapp.view.addmedia.AddStoryActivity
import com.ariqa.storyapp.view.login.LoginActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        showLoading(true)
        viewModel.getSession().observe(this) { user ->
            if (user.token.isNotEmpty() && user.token != "") {
                setupView()
                setupAction()
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun setupAction() {
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.logout -> {
                    viewModel.logout()
                    true
                }

                else -> false
            }
        }

        binding.addPhoto.setOnClickListener {
            startActivity(Intent(this@MainActivity, AddStoryActivity::class.java))
        }

        lifecycleScope.launch {
            viewModel.getStory().observe(this@MainActivity){ result ->
                when(result){
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Error -> {
                        showToast(result.error)
                    }
                    is Result.Success -> {
                        Log.d("MainActivity", "Main: ${result.data}")
                        setAllStoriesList(result.data.toList())
                        showLoading(false)
                    }
                }
            }
        }
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
            showToast("Result ${items.size}")
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

        //observer
        viewModel.isLoading.observe(this@MainActivity) {
            showLoading(it)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }

}