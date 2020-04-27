package com.amit.android.marsplayimageupload.model

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*
import com.google.firebase.database.*
import kotlin.collections.ArrayList


class AppRepository() {

    private var firebaseStroge: FirebaseStorage? = null
    private var reference: StorageReference? = null
    private var database: FirebaseDatabase? = null
    private var databaseRef: DatabaseReference? = null
    private var list: MutableLiveData<List<String>> = MutableLiveData()
    private var processManagerData: MutableLiveData<String> = MutableLiveData()

    fun getUploadedImageListData(): LiveData<List<String>> = list

    fun getUploadedImageList() {
        database = FirebaseDatabase.getInstance()
        databaseRef = database!!.reference

        val query: Query = databaseRef!!.child("MarsPlay/Image")
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val post = dataSnapshot.value
                val imageList: ArrayList<String> = ArrayList()
                for (data: DataSnapshot in dataSnapshot.getChildren()) {
                    Log.d("asdf", "database-> ${data.value.toString()} ")
                    imageList.add(data.value.toString())
                }
                list.postValue(imageList)
                Log.d("asdf", "database ${post.toString()} ")
                if (post.toString()== null)
                    processManagerData.postValue(IMAGE_NOT_FOUND)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("asdf", "loadPost:onCancelled", databaseError.toException())
            }
        }
        query.addValueEventListener(postListener)
    }

    fun uploadImage(filePath: Uri?) {
        firebaseStroge = FirebaseStorage.getInstance()
        reference = firebaseStroge!!.reference

        if (filePath != null) {
            val ref: StorageReference = reference!!.child("images/" + UUID.randomUUID().toString())
            ref.putFile(filePath)
                .addOnSuccessListener {
                    // Image uploaded successfully
                    Log.d("asdf", "image uploaded")
                    ref.downloadUrl.addOnCompleteListener {
                        Log.d("asdf", "image url ${it.result.toString()}")
                        saveUploadImageUrl(it.result.toString())

                    }
                }

                .addOnFailureListener {
                    // Error, Image not uploaded
                    Log.d("asdf", "something went wrong")
                    processManagerData.postValue(IMAGE_UPLOAD_FAILURE)
                }
                .addOnProgressListener { taskSnapshot ->
                    // Progress Listener for loading
                    // percentage on the dialog box
                    val progress =
                        (((100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount))
                    Log.d("asdf", "$progress image uploading")
                }
        }

    }

    fun saveUploadImageUrl(uploadImageUrl: String?) {
        database = FirebaseDatabase.getInstance()
        databaseRef = database!!.reference
        databaseRef!!.child("MarsPlay/Image").child(System.currentTimeMillis().toString())
            .setValue(uploadImageUrl)

        Log.d("asdf", "image uploaded")
        processManagerData.postValue("success")
    }

    fun getProcessManagerData(): LiveData<String> = processManagerData


    companion object{
        const val IMAGE_UPLOAD_SUCCESS : String = "success"
        const val IMAGE_UPLOAD_FAILURE : String = "fail"
        const val IMAGE_NOT_FOUND : String = "image not found"
    }
}