package com.ariqa.storyapp.view.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.ariqa.storyapp.R
import com.ariqa.storyapp.ViewModelFactory
import com.ariqa.storyapp.data.response.ListStoryItem
import com.ariqa.storyapp.databinding.ActivityMainBinding
import com.ariqa.storyapp.view.adapter.LoadingStateAdapter
import com.ariqa.storyapp.view.adapter.StoriesPagingAdapter
import com.ariqa.storyapp.view.addmedia.AddStoryActivity
import com.ariqa.storyapp.view.login.LoginActivity
import com.ariqa.storyapp.view.maps.MapsActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

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

        showLoading(true)
        runBlocking { delay(500L) }
        setupView()
        setupAction()
    }

    private fun setupAction() {
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.MapIcon -> {
                    startActivity(Intent(this@MainActivity, MapsActivity::class.java))
                    true
                }
                R.id.logout -> {
                    showLoading(true)
                    viewModel.logout()
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    showLoading(false)
                    true
                }
                else -> false
            }
        }

        binding.addPhoto.setOnClickListener {
            startActivity(Intent(this@MainActivity, AddStoryActivity::class.java))
        }
    }

    private fun setAllStoriesList(items: PagingData<ListStoryItem>) {
        val adapter = StoriesPagingAdapter()
        with(binding) {
            rvListStories.layoutManager = LinearLayoutManager(this@MainActivity)
            rvListStories.adapter = adapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    adapter.retry()
                }
            )
            rvListStories.setHasFixedSize(true)
            adapter.submitData(lifecycle, items)
        }
    }

    private fun setupView() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

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
        viewModel.stories.observe(this@MainActivity) {
            setAllStoriesList(it)
        }
        showLoading(false)
    }

//    private fun showToast(message: String) {
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }

}