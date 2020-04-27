package com.amit.android.marsplayimageupload.view

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amit.android.marsplayimageupload.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.image_item_list.view.*

class ImageListAdapter(val myList : ArrayList<String>, val context: Context): RecyclerView.Adapter<ImageListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.image_item_list,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = myList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(myList[position]).placeholder(R.drawable.image_loading).into(holder.imageView!!)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, PreviewActivity::class.java)
            intent.putExtra("image_url",myList[position])
            context.startActivity(intent)
        }
    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val imageView = view.image_item
    }

}