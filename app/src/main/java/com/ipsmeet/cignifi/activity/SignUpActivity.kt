package com.ipsmeet.cignifi.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ipsmeet.cignifi.databinding.ActivitySignUpBinding
import com.ipsmeet.cignifi.viewmodel.AppleLoginViewModel
import com.ipsmeet.cignifi.viewmodel.GoogleLoginViewModel
import com.ipsmeet.cignifi.viewmodel.TwitterLoginViewModel

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private lateinit var googleLoginViewModel: GoogleLoginViewModel
    private lateinit var twitterLoginViewModel: TwitterLoginViewModel
    private lateinit var appleLoginViewModel: AppleLoginViewModel

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()

        sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        //  GOOGLE-VIEW-MODEL
        googleLoginViewModel = ViewModelProvider(this)[GoogleLoginViewModel::class.java]
        googleLoginViewModel.signInWithGoogle(this)

        //  TWITTER-VIEW-MODEL
        twitterLoginViewModel = ViewModelProvider(this)[TwitterLoginViewModel::class.java]

        //  APPLE-VIEW-MODEL
        appleLoginViewModel = ViewModelProvider(this)[AppleLoginViewModel::class.java]

        //  GOOGLE-SIGN-IN BUTTON
        binding.cardVGoogle.setOnClickListener {
            googleLoginViewModel.beginSignIn(googleSignInLauncher)
        }

        //  TWITTER-SIGN-IN BUTTON
        binding.cardVTwitter.setOnClickListener {
            twitterLoginViewModel.signInWithTwitter(this)
        }

        twitterLoginViewModel.twitterLoginResultLive.observe(this) {
            if (it.success) {
                startActivity(
                    Intent(this, HomeActivity::class.java)
                )
                finish()
            }
            else {
                Log.e("Failed to auth", "Null")
            }
        }

        //  APPLE-SIGN-IN BUTTON
        binding.cardVApple.setOnClickListener {
            appleLoginViewModel.signInWithApple(this, appleSignLauncher)
        }
    }

    private val googleSignInLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            val credential = googleLoginViewModel.handleSignInResult(it.data, it.resultCode)
            if (credential.toString() != "null" || credential.toString() != "") {
                editor.putString("username", credential?.displayName)
                editor.putBoolean("googleSignIn", true)
                editor.apply()
                startActivity(
                    Intent(this, HomeActivity::class.java)
                )
                finish()
            }
        }

    private val appleSignLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val credential = appleLoginViewModel.handleSignInResult(it.data, it.resultCode)
            if (credential.toString() != "null") {
                startActivity(
                    Intent(this, HomeActivity::class.java)
                        .putExtra("name", credential?.displayName)
                )
                editor.putString("username", credential?.displayName)
                editor.apply()
                finish()
            }
        }
}