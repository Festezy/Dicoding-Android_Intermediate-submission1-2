package com.ariqa.storyapp.view.addmedia

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.ariqa.storyapp.R
import com.ariqa.storyapp.ViewModelFactory
import com.ariqa.storyapp.data.Result
import com.ariqa.storyapp.databinding.ActivityAddStoryBinding
import com.ariqa.storyapp.helper.getImageUri
import com.ariqa.storyapp.helper.reduceFileImage
import com.ariqa.storyapp.helper.uriToFile
import com.ariqa.storyapp.view.addmedia.CameraActivity.Companion.CAMERAX_RESULT
import com.ariqa.storyapp.view.main.MainActivity
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding

    private val viewModel by viewModels<AddStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var currentImageUri: Uri? = null
    private var isCameraX: Boolean = true

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[PERMISSION_READ_EXTERNAL_STORAGE] ?: false -> {
                    // Access Media for Android 12 LOWER
                    startGallery()
                }

                permissions[PERMISSION_READ_MEDIA_IMAGES] ?: false -> {
                    // Access Media for Android 13 HiGHER
                    startGallery()
                }

                permissions[PERMISSION_CAMERA] ?: false -> {
                    if (isCameraX) startCameraX()
                    if (!isCameraX) startCamera()
                }

                else -> {
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
    }

    private fun setupAction() {
        binding.apply {
            galleryButton.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (!checkPermission(PERMISSION_READ_MEDIA_IMAGES)
                        && !checkPermission(PERMISSION_READ_EXTERNAL_STORAGE)) {
                        requestPermissionLauncher.launch(
                            arrayOf(PERMISSION_READ_MEDIA_IMAGES)
                        )
                    } else {
                        startGallery()
                    }
                } else {
                    if (!checkPermission(PERMISSION_READ_EXTERNAL_STORAGE)
                        && !checkPermission(PERMISSION_READ_MEDIA_IMAGES)) {
                        requestPermissionLauncher.launch(
                            arrayOf(PERMISSION_READ_EXTERNAL_STORAGE)
                        )

                    } else {
                        startGallery()
                    }
                }


            }
            cameraButton.setOnClickListener {
                isCameraX = false
                if (!checkPermission(PERMISSION_CAMERA)) {
                    requestPermissionLauncher.launch(
                        arrayOf(PERMISSION_CAMERA)
                    )
                } else {

                    startCamera()
                }

            }
            cameraXButton.setOnClickListener {
                isCameraX
                if (!checkPermission(PERMISSION_CAMERA)) {
                    requestPermissionLauncher.launch(
                        arrayOf(PERMISSION_CAMERA)
                    )
                } else {
                    startCameraX()

                }

            }
            uploadButton.setOnClickListener { uploadImage() }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentImageUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            showImage()
        }
    }

    private fun uploadImage() {
        showLoading(true)
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.textDescriptions.text.toString()

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            lifecycleScope.launch {
                viewModel.uploadImage(multipartBody, requestBody)
                    .observe(this@AddStoryActivity) { result ->
                        when (result) {
                            is Result.Loading -> {
                                showLoading(true)
                            }

                            is Result.Error -> {
                                showToast(result.error)
                                Log.d("AddStoryActivity", "uploadImage error: ${result.error}")
                            }

                            is Result.Success -> {
                                showToast(result.data.message)
                                Log.d("GetStory", "uploadImage: ${result.data}")
                                showLoading(false)
                                val intent = Intent(this@AddStoryActivity, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            }
                        }
                    }
            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val PERMISSION_CAMERA = Manifest.permission.CAMERA
        private const val PERMISSION_READ_EXTERNAL_STORAGE =
            Manifest.permission.READ_EXTERNAL_STORAGE
        private const val PERMISSION_READ_MEDIA_IMAGES = Manifest.permission.READ_MEDIA_IMAGES
    }

}