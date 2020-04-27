package com.amit.android.marsplayimageupload.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.amit.android.marsplayimageupload.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_preview.*
import uk.co.senab.photoview.PhotoViewAttacher

class PreviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)
        val imageUrl = intent!!.extras!!.getString("image_url")
        if (imageUrl != null) {
            try {
                Glide.with(this).load(imageUrl).into(imageView)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        PhotoViewAttacher(imageView).update()
    }
}
