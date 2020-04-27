package com.amit.android.marsplayimageupload.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.app.Activity
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.amit.android.marsplayimageupload.viewModel.AppViewModel
import com.amit.android.marsplayimageupload.R
import com.amit.android.marsplayimageupload.model.AppRepository
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private var viewModel: AppViewModel? = null
    private var myList: ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        uploadImageBtn.setOnClickListener { uploadImage() }
        viewModel = ViewModelProviders.of(this).get(AppViewModel::class.java)
        setObservers()
        initRecyclerView()

    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = ImageListAdapter(myList, this)
    }

    override fun onResume() {
        super.onResume()
        Log.d("asdf", "on resume")
        viewModel!!.getImageList()

    }

    private fun setObservers() {
        viewModel!!.getImageListData().observe(this, Observer<List<String>> {
            // update UI
            if (it.isNotEmpty()) {
                warningText?.visibility = View.GONE
                myList.clear()
                for (element in it) {
                    Log.d("asdf", "image url $element")
                    myList.add(element)
                }
                recyclerView!!.adapter!!.notifyDataSetChanged()
            }
        })
        viewModel!!.getProcessManagerData().observe(this, Observer {
            if (it.equals(AppRepository.IMAGE_NOT_FOUND)) {
                warningText?.visibility = View.VISIBLE
            }
        })
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun uploadImage() {
        if (allPermissionsGranted()) {
            openCameraOrGallery()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun openCameraOrGallery() {
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.bottom_sheet)
        dialog.show()
        val gallery: TextView = dialog.findViewById<TextView>(R.id.gallery)!!
        gallery.setOnClickListener {
            openGallery()
            dialog.dismiss()
        }
        val cameraX: TextView = dialog.findViewById<TextView>(R.id.camera)!!
        cameraX.setOnClickListener {
            openCameraX()
            dialog.dismiss()
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(
                intent,
                "Select Image from here..."
            ), 102
        )
    }

    private fun openCameraX() {
        startActivity(Intent(this, CameraActivity::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === PICK_IMAGE_REQUEST
            && resultCode === Activity.RESULT_OK
            && data != null
            && data.data != null
        ) {
            val filePath = data.data
            val intent = Intent(this, UploadActivity::class.java)
            intent.putExtra("image_uri", filePath.toString())
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                openCameraOrGallery()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                uploadImage()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 101
        private const val PICK_IMAGE_REQUEST = 102
    }
}
