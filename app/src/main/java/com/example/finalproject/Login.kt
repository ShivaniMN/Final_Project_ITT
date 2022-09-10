package com.example.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*

class Login : AppCompatActivity() {

    lateinit var firebaseAuth: FirebaseAuth

    private val email: TextInputEditText
    get() = findViewById(R.id.etEmailAddress)

    private val password: TextInputEditText
    get() = findViewById(R.id.etPassword)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        firebaseAuth = FirebaseAuth.getInstance()
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnSignUp = findViewById<TextView>(R.id.btnSignUp)
        emailFocusListener()
        passwordFocusListener()
        btnLogin.setOnClickListener{
            login()
        }

        btnSignUp.setOnClickListener{
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun login(){
        if(email.text?.isEmpty() == true || password.text?.isEmpty() == true){
            Toast.makeText(this, R.string.toast_valid_Email, Toast.LENGTH_SHORT).show()
            return
        }

        firebaseAuth.signInWithEmailAndPassword(email.text.toString(), password.text.toString()).addOnCompleteListener(this){
            if(it.isSuccessful){
                Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this, R.string.authentication_Failure, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun emailFocusListener(){
        etEmailAddress.setOnFocusChangeListener { _, b ->
            if (!b) {
                emailContainer.helperText = validEmail()
            }
        }
    }

    private fun validEmail():String?{
        if (email.text?.isBlank() == true){
            Toast.makeText(this, R.string.toast_valid_Email, Toast.LENGTH_SHORT).show()
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()){
            return getString(R.string.enter_valid_email)
        }
        return null
    }

    private fun passwordFocusListener(){
        etPassword.setOnFocusChangeListener{ _, b ->
            if(!b) {
                passwordContainer.helperText = validPassword()
            }
        }
    }

    private fun validPassword(): String?{
        if (password.text?.isBlank() == true){
            Toast.makeText(this, R.string.toast_valid_Email, Toast.LENGTH_SHORT).show()
        }
        if(password.length() < 8){
            return getString(R.string.minimum_8_characters)
        }
        if(!password.text?.matches(".*[A-Z].*".toRegex())!!) {
            return getString(R.string.atleast_1_uppercase)
        }
        if(!password.text?.matches(".*[a-z].*".toRegex())!!){
            return getString(R.string.atleast_1_lowercase)
        }
        if(!password.text?.matches(".*[@#\$%^&+=].*".toRegex())!!){
            return getString(R.string.special_characters)
        }

        return null
    }
}