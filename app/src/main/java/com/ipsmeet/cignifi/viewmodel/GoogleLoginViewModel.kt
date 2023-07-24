package com.ipsmeet.cignifi.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes

class GoogleLoginViewModel : ViewModel() {

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInResult: BeginSignInRequest

    fun signInWithGoogle(context: Context) {
        oneTapClient = Identity.getSignInClient(context)
        signInResult = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId("7*******************************************b.apps.googleusercontent.com")
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            ).build()

        oneTapClient.beginSignIn(signInResult)
    }

    fun beginSignIn(activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>) {
        oneTapClient.beginSignIn(signInResult)
            .addOnSuccessListener {
                try {
                    val intentSenderRequest = IntentSenderRequest.Builder(it.pendingIntent.intentSender).build()
                    activityResultLauncher.launch(intentSenderRequest)
                }
                catch (e: IntentSender.SendIntentException) {
                    Log.e("One-Tap Failed", e.message.toString())
                }
            }
            .addOnFailureListener { e ->
                Log.d("Error", e.message.toString())
            }
    }

    fun handleSignInResult(data: Intent?, resultCode: Int): SignInCredential? {
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
        return null
    }

}