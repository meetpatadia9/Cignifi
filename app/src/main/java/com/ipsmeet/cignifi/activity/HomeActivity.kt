package com.ipsmeet.cignifi.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ipsmeet.cignifi.R
import com.ipsmeet.cignifi.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    lateinit var sharedPreferences: SharedPreferences
    lateinit var editore: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE)
        editore = sharedPreferences.edit()

        val firebaseName = FirebaseAuth.getInstance().currentUser?.displayName
        if (firebaseName.toString() != "null") {
            binding.txtWelcome.text = getString(R.string.welcome_msg, firebaseName)
            /** OR
            binding.txtWelcome.text = String.format(getString(R.string.welcome_msg), firebaseName)
            */
        }
        else {
            binding.txtWelcome.text = getString(R.string.welcome_msg, sharedPreferences.getString("username", ""))
        }


        binding.btnSignOut.setOnClickListener {
            Firebase.auth.signOut()

            editore.putBoolean("googleSignIn", false)
            editore.apply()

            startActivity(
                Intent(this, SignInActivity::class.java)
            )
            finish()
        }
    }
}