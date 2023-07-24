package com.ipsmeet.cignifi.viewmodel

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes

class AppleLoginViewModel : ViewModel() {

    private lateinit var oneTapClient: SignInClient

    fun signInWithApple(activity: Activity, appleSignLauncher: ActivityResultLauncher<Intent>) {
        oneTapClient = Identity.getSignInClient(activity)

        val providers = arrayListOf(AuthUI.IdpConfig.AppleBuilder().build())

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        appleSignLauncher.launch(signInIntent)
    }

    fun handleSignInResult(data: Intent?, resultCode: Int): SignInCredential? {
        val response = IdpResponse.fromResultIntent(data)
        if (resultCode == Activity.RESULT_OK) {
            try {
                return oneTapClient.getSignInCredentialFromIntent(data)
            } catch (e: ApiException) {
                when (e.statusCode) {
                    CommonStatusCodes.CANCELED -> {
                        Log.d("Dialog closed", "One-Tap closed")
                    }
                }
            }
        }
        else {
            // Sign-in failed
            return if (response == null) {
                // User pressed back button
                null
            }
            else {
                // Retrieve and handle the error
                val error = response.error
                null
            }
        }
        return null
    }


}