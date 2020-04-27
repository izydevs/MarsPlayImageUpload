package com.amit.android.marsplayimageupload.view

import android.app.Activity
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.theartofdev.edmodo.cropper.CropImage
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.amit.android.marsplayimageupload.viewModel.AppViewModel
import com.amit.android.marsplayimageupload.R
import com.amit.android.marsplayimageupload.model.AppRepository
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_upload.*


class UploadActivity : AppCompatActivity() {

    private var viewModel: AppViewModel? = null
    private var filePath: Uri? = null
    private var dialog : ProgressDialog?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)
        val imageUri = Uri.parse(intent.extras!!.getString("image_uri"))
        CropImage.activity(imageUri).start(this);
        upload?.setOnClickListener {
            uploadImageToServer()
            dialog = ProgressDialog.show(this, "Image   Uploading", "Please wait...", true)

        }

        viewModel = ViewModelProviders.of(this).get(AppViewModel::class.java)
        setObservers()

    }

    private fun setObservers() {
        viewModel?.getProcessManagerData()?.observe(this, Observer<String> {
            Log.d("asdf","getProcessManagerData $it")
            if (it.equals(AppRepository.IMAGE_UPLOAD_SUCCESS)){
                if (dialog!=null && dialog!!.isShowing)
                    dialog?.dismiss()
                Toast.makeText(this,"Image Upload Successfully",Toast.LENGTH_LONG).show()
                finish()
            }else{
                if (dialog!=null && dialog!!.isShowing)
                    dialog?.dismiss()
                Toast.makeText(this,"Something went wrong",Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun uploadImageToServer() {
        viewModel!!.uploadImage(filePath)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                try {
                    filePath = result.uri
                    Glide.with(this).load(filePath).into(imageView)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                finish()
            }else{
                finish()
            }
        }else{
            finish()
        }
    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }
}
