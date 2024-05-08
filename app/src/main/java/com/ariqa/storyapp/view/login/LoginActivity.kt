package com.ariqa.storyapp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.ariqa.storyapp.ViewModelFactory
import com.ariqa.storyapp.data.preference.UserModel
import com.ariqa.storyapp.databinding.ActivityLoginBinding
import com.ariqa.storyapp.view.main.MainActivity
import com.ariqa.storyapp.view.signup.SignUpActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            lifecycleScope.launch {
                viewModel.login(email, password)
                viewModel.getToken.collect { token ->
                    if (token != null) {
                        viewModel.saveSession(UserModel(email, token))
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish() // Tutup LoginActivity agar tidak bisa kembali dengan tombol kembali
                    } else {
                        showToast("Failed to get token")
                    }
                }
            }

//            lifecycleScope.launch {
//                viewModel.login(email, password)
//                viewModel.getToken.collect() { value ->
//                    if (value != null) {
//                        token = value
//                    }
//                    viewModel.saveSession(UserModel(email, token))
//                }
//
////                    viewModel.saveSession(UserModel(email, token))
//                viewModel.responseMessage.collectLatest {
//                    showToast(it!!)
//                }
//            }

            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        }
    }

    private fun showToast(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
}