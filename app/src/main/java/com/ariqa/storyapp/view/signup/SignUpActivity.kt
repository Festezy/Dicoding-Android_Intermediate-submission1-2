package com.ariqa.storyapp.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ariqa.storyapp.ViewModelFactory
import com.ariqa.storyapp.data.Result
import com.ariqa.storyapp.databinding.ActivitySignUpBinding
import com.ariqa.storyapp.view.login.LoginActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {
    private val viewModel by viewModels<SignUpViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            showLoading(true)
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            lifecycleScope.launch {
                viewModel.postSignup(name, email, password).observe(this@SignUpActivity) { result ->
                    when (result) {
                        is Result.Loading -> {

                        }

                        is Result.Error -> {
                            showToast(result.error)
                            Log.d("SignupActivity", "SignupActivity Error: ${result.error}")
                        }

                        is Result.Success -> {
                            showToast(result.data.message)
                            showLoading(false)
                            Log.d(
                                "SignupActivity",
                                "SignupActivity success: ${result.data.message}"
                            )
                            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
                            finish()
                        }
                    }
                }
            }
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

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        //button
        val daftar = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)
        //textView
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val nameText = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val emailText =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val passwordText =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        //editText
        val nameEditText =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val emailEditText =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordEditText =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)

        val together = AnimatorSet().apply {
            playTogether(daftar)
        }

        AnimatorSet().apply {
            playSequentially(
                title, nameText, nameEditText, emailText, emailEditText,
                passwordText, passwordEditText, together
            )
            start()
        }
    }
}
