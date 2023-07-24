package com.ipsmeet.cignifi.activity

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.ipsmeet.cignifi.R
import com.ipsmeet.cignifi.databinding.ActivitySignInBinding
import com.ipsmeet.cignifi.viewmodel.AppleLoginViewModel
import com.ipsmeet.cignifi.viewmodel.GoogleLoginViewModel
import com.ipsmeet.cignifi.viewmodel.TwitterLoginViewModel

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding

    private lateinit var googleLoginViewModel: GoogleLoginViewModel
    private lateinit var twitterLoginViewModel: TwitterLoginViewModel
    private lateinit var appleLoginViewModel: AppleLoginViewModel

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
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

        val spannableString = SpannableString(getText(R.string.txt_no_account))

        val clickablePart = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(
                    Intent(this@SignInActivity, SignUpActivity::class.java)
                )
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }

        spannableString.apply {
            setSpan(clickablePart, 23, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(
                ForegroundColorSpan(ContextCompat.getColor(this@SignInActivity, R.color.cignifi_blue)),
                23, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        binding.txtNoAccount.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
        }

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

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if(user != null ){
            startActivity(Intent(this,HomeActivity::class.java))
            finish()
        }

        val isSignedIn = sharedPreferences.getBoolean("googleSignIn", false)
        if (isSignedIn) {
            startActivity(Intent(this,HomeActivity::class.java))
            finish()
        }
    }
}