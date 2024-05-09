package com.ariqa.storyapp.view.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.ariqa.storyapp.databinding.ActivityDetailstoriesBinding

class DetailstoriesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailstoriesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailstoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
    }

    private fun setupAction() {
        val photoUrl = intent.getStringExtra(EXTRA_PHOTO_URL)
        val name = intent.getStringExtra(EXTRA_NAME)
        val description = intent.getStringExtra(EXTRA_DESC)

        binding.apply {
            photoUrlImageView.load(photoUrl)
            textName.text = name
            textDescriptions.text = description
        }
    }


    companion object{
        const val EXTRA_PHOTO_URL = "extra_photourl"
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_DESC = "extra_description"
    }
}