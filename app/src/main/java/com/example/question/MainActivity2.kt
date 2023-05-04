package com.example.question

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.core.math.MathUtils.clamp

import java.io.InputStream


class MainActivity2 : AppCompatActivity() {
    lateinit var imageView: ImageView
    private var mImageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        imageView = findViewById(R.id.image_view_edited)
        mImageUri = Uri.parse(intent.getStringExtra("image_uri"))
        imageView.setImageURI(mImageUri)
        mImageUri = Uri.parse(intent.getStringExtra("image_uri"))


        Glide.with(this)
            .load(mImageUri)
            .into(imageView)
        var inputStream: InputStream? = null

        // Get the input stream for the Uri
        inputStream = this.contentResolver.openInputStream(mImageUri!!)
        // Decode the input stream into a bitmap
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.RGB_565
        options.inSampleSize = 2 // Sample size of 2 to reduce memory usage
        val bitmap = BitmapFactory.decodeStream(inputStream, null, options)


        // finding Id of Button
        findViewById<Button>(R.id.Brightness_Bth).setOnClickListener {
            bitmap?.let { applyBrightness(bitmap, 20) }
        }
        findViewById<Button>(R.id.Highlights).setOnClickListener {
            bitmap?.let { applyHighlights(bitmap, 20f) }
        }
        findViewById<Button>(R.id.Contrast_Btn).setOnClickListener {
            bitmap?.let { applyContrast(bitmap, 12f) }
        }
        findViewById<Button>(R.id.WhiteBalance).setOnClickListener {
            bitmap?.let { applyWhiteBalance(bitmap, Color.RED, Color.GREEN, Color.BLUE) }
        }
        findViewById<Button>(R.id.saveToGallery).setOnClickListener {
            bitmap?.let { saveToGallery(bitmap, this, "EdtedImage", "Image") }
        }


        bitmap?.let { applyBrightness(it, 20) }


    }


    // Apply brightness to the image
    fun applyBrightness(bitmap: Bitmap, brightness: Int) {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                val pixel = bitmap.getPixel(x, y)
                val r = Color.red(pixel) + brightness
                val g = Color.green(pixel) + brightness
                val b = Color.blue(pixel) + brightness
                val color = Color.rgb(
                    clamp(r, 0, 255),
                    clamp(g, 0, 255),
                    clamp(b, 0, 255)
                )
                output.setPixel(x, y, color)
            }
        }
        Glide.with(this).load(output).into(imageView)
    }

    // Apply contrast to the image
    fun applyContrast(bitmap: Bitmap, contrast: Float) {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val factor = (259 * (contrast + 255)) / (255 * (259 - contrast))
        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                val pixel = bitmap.getPixel(x, y)
                val r = factor * (Color.red(pixel) - 128) + 128
                val g = factor * (Color.green(pixel) - 128) + 128
                val b = factor * (Color.blue(pixel) - 128) + 128
                val color = Color.rgb(
                    clamp(r.toInt(), 0, 255),
                    clamp(g.toInt(), 0, 255),
                    clamp(b.toInt(), 0, 255)
                )
                output.setPixel(x, y, color)
            }
        }
        Glide.with(this).load(output).into(imageView)
    }

    // Apply highlights to the image
    fun applyHighlights(bitmap: Bitmap, highlights: Float) {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val threshold = 255 - highlights
        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                val pixel = bitmap.getPixel(x, y)
                val r = if (Color.red(pixel) > threshold) 255 else Color.red(pixel)
                val g = if (Color.green(pixel) > threshold) 255 else Color.green(pixel)
                val b = if (Color.blue(pixel) > threshold) 255 else Color.blue(pixel)
                val color = Color.rgb(r, g, b)
                output.setPixel(x, y, color)
            }
        }
        Glide.with(this).load(output).into(imageView)
    }

    // Apply white balance to the image
    fun applyWhiteBalance(bitmap: Bitmap, red: Int, green: Int, blue: Int) {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                val pixel = bitmap.getPixel(x, y)
                val r = Color.red(pixel) * red / 255
                val g = Color.green(pixel) * green / 255
                val b = Color.blue(pixel) * blue / 255
                val color = Color.rgb(
                    clamp(r, 0, 255),
                    clamp(g, 0, 255),
                    clamp(b, 0, 255)
                )
                output.setPixel(x, y, color)
            }
        }
        Glide.with(this).load(output).into(imageView)
    }

    // Save the bitmap to the gallery
    fun saveToGallery(bitmap: Bitmap, context: Context, title: String, description: String) {
        // Create a new image in the gallery
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, title)
        values.put(MediaStore.Images.Media.DESCRIPTION, description)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        val uri =
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        // Write the bitmap to the output stream
        val outputStream = context.contentResolver.openOutputStream(uri!!)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        outputStream?.close()

        // Show a message to the user
        Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show()
    }

}