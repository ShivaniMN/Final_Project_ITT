package com.example.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUp : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    private val email:TextInputEditText
    get() = findViewById(R.id.etEmailAddress)

    private val phone: TextInputEditText
    get() = findViewById(R.id.etPhoneNumber)

    private val password: TextInputEditText
    get() = findViewById(R.id.etPassword)

    private val confirmPassword: TextInputEditText
    get() = findViewById(R.id.etConfirmPassword)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        supportActionBar?.hide()
        firebaseAuth = FirebaseAuth.getInstance()
        val signup = findViewById<Button>(R.id.signUp)
        emailFocusListener()
        phoneFocusListener()
        passwordFocusListener()
        confirmPasswordFocusListener()
        signup.setOnClickListener {
            signUpUser()
        }
    }

    private fun signUpUser() {
        if(email.text?.isBlank() == true || password.text?.isBlank() == true || password.text?.isBlank() == true || confirmPassword.text?.isBlank() == true){
            Toast.makeText(this, R.string.toast_valid_Email, Toast.LENGTH_SHORT).show()
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener(this){
                if(it.isSuccessful){
                    Toast.makeText(this,R.string.login_success, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    Toast.makeText(this,R.string.error, Toast.LENGTH_SHORT).show()
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
        if(password.length() < 8){
            return getString(R.string.minimum_8_characters)
        }
        if(!password.text?.matches(".*[A-Z].*".toRegex())!!){
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

    private fun phoneFocusListener(){
        etPhoneNumber.setOnFocusChangeListener{_, b ->
            if(!b) {
                phoneContainer.helperText = validPhone()
            }
        }
    }

    private fun validPhone(): String?{
        if(!phone.text?.matches(".*[0-9].*".toRegex())!!) {
            return getString(R.string.only_numbers)
        }
        if(phone.length() != 10 ){
            return getString(R.string.exactly_10_digits)
        }
        return null
    }

    private  fun confirmPasswordFocusListener(){
        etConfirmPassword.setOnFocusChangeListener{_, b->
            if(!b){
                confirmPasswordContainer.helperText = validConfirmPassword()
            }
        }
    }

    private fun validConfirmPassword(): String?{
        if(password.text.toString() != confirmPassword.text.toString()){
            return getString(R.string.incorrect_password)
        }
        return null
    }

}