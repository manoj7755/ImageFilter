package com.example.question

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {
    lateinit var imageUri: Uri
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var imageView: ImageView
    private lateinit var selectImageButton: Button


    // Handle the selected image in onActivityResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            // Get the selected image's URI
             imageUri = data.data!!
            // Do something with the image
            Glide.with(this).load(imageUri).centerCrop().into(imageView)

            val editImageButton = findViewById<Button>(R.id.Edit_image)

            editImageButton.setOnClickListener {
                val intent = Intent(this, MainActivity2::class.java)
                intent.putExtra("image_uri", imageUri.toString())
                startActivity(intent)
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.image_view)
        val select_img = findViewById<Button>(R.id.select_image_button)
        select_img.setOnClickListener {
            // Create an intent to open the gallery and select an image
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }


    }
}