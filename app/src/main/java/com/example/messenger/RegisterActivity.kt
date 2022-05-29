package com.example.messenger

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.media.Image
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val registerButton: Button = findViewById(R.id.register_button_register)
        val alreadyhaveaccount: TextView = findViewById(R.id.have_account_textview)
        val selectphotobutton: Button = findViewById(R.id.selectphoto_button_register)


        registerButton.setOnClickListener {
            performRegister()
        }

        alreadyhaveaccount.setOnClickListener{

            val intent = Intent(this, LoginActivity::class.java)

            startActivity(intent)
        }


        selectphotobutton.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"

            startActivityForResult(intent, 0)
        }


    }
    var photoFilePath: Uri? = null


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val selectphotobutton: Button = findViewById(R.id.selectphoto_button_register)
        val selectphotoimage: ImageView = findViewById(R.id.selectphoto_imageview_register)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){

            photoFilePath = data.data
            val inputStream = photoFilePath?.let { contentResolver.openInputStream(it) }
            val drawable = Drawable.createFromStream(inputStream, photoFilePath.toString())
            selectphotobutton.background = drawable

            selectphotoimage.setImageDrawable(drawable)

            selectphotobutton.alpha = 0f

        }
    }

    private fun performRegister() {
        val email: TextView = findViewById(R.id.email_edittext_register)
        val password: TextView = findViewById(R.id.password_edittext_register)



        if (email.text.toString().isEmpty() || password.text.toString().isEmpty()) {
            Toast.makeText(this, "Please enter email/password", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                uploadImageToStorage()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()

            }
    }
    private fun uploadImageToStorage(){

        //if(photoFilePath == null) return
        Log.d("RegisterActivity", "2 Photo path is ${photoFilePath}")

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(photoFilePath!!)
            .addOnSuccessListener { it ->
                Log.d("RegisterActivity", "Succesfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {

                    saveUserToFirebaseDataBase(it.toString())
                }
            }

    }
    private fun saveUserToFirebaseDataBase(profileImageUrl: String) {

        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance("https://messenger-ecb45-default-rtdb.europe-west1.firebasedatabase.app/").getReference("/users/$uid")
        val login: TextView = findViewById(R.id.login_edittext_register)
        Log.d("RegisterActivity", "profileimageurl is $profileImageUrl")
        Log.d("RegisterActivity", "login is is ${login.text}")
        Log.d("RegisterActivity", "uid is $uid")


        val user = User(uid, login.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Saving user to firebase database")
            }
    }
}

//class User(val username:String,val login:String, val profileImageUrl: String)