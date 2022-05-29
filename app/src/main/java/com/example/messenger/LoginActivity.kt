package com.example.messenger

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text

class LoginActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val login: TextView = findViewById(R.id.login_edittext_login)
        val password: TextView = findViewById(R.id.password_edittext_login)
        val loginButton: Button = findViewById(R.id.login_button_login)


        //FirebaseAuth.getInstance().signInWithEmailAndPassword(login.text.toString(), password.text.toString())
            //.addOnCompleteListener(){

            //}
            //.addOnFailureListener(){

            //}

    }

}