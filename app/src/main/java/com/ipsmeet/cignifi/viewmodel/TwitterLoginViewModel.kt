package com.ipsmeet.cignifi.viewmodel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider

class TwitterLoginViewModel: ViewModel() {

    private val twitterLoginResult = MutableLiveData<LoginResult>()
    val twitterLoginResultLive: LiveData<LoginResult> = twitterLoginResult

    fun signInWithTwitter(activity: Activity) {
        val provider = OAuthProvider.newBuilder("twitter.com")

        val pendingResultTask = FirebaseAuth.getInstance().pendingAuthResult
        if (pendingResultTask != null) {
            pendingResultTask
                .addOnSuccessListener {
                    Log.d("AuthResult1.user?.displayName", it.user?.displayName.toString())
                    Log.d("AuthResult1.additionalUserInfo?.username", it.additionalUserInfo?.username.toString())
                    Log.d("AuthResult1.credential!!.provider", it.credential!!.provider)

                    twitterLoginResult.value = LoginResult(true)
                }
                .addOnFailureListener {
                    twitterLoginResult.value = LoginResult(false)
                    Log.e("AuthResult1 failed", it.message.toString())
                }
        }
        else {
            FirebaseAuth.getInstance()
                .startActivityForSignInWithProvider(activity, provider.build())
                .addOnSuccessListener {
                    Log.d("AuthResult2.user?.displayName", it.user?.displayName.toString())
                    Log.d("AuthResult2.additionalUserInfo?.username", it.additionalUserInfo?.username.toString())
                    Log.d("AuthResult2.credential!!.provider", it.credential!!.provider)

                    twitterLoginResult.value = LoginResult(true)
                }
                .addOnFailureListener {
                    // Handle failure.
                    twitterLoginResult.value = LoginResult(false)
                    Log.e("AuthResult2 failed", it.message.toString())
                }
        }
    }

    data class LoginResult(
        val success: Boolean
    )
}