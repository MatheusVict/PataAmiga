package com.example.pawfriend

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pawfriend.apiJsons.ListPostsPets
import com.example.pawfriend.databinding.PostAdapterBinding

class PostPetsAdapter(
    private val listPostsPets: List<ListPostsPets>,
    private val idPostSelect: (Long) -> Unit

) : RecyclerView.Adapter<PostPetsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            PostAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val postSelect = listPostsPets[position]

        holder.binding.postName.text = postSelect.name
        holder.binding.postLocation.text = postSelect.petLocation
        holder.binding.postRace.text = postSelect.race
        holder.binding.postSex.text = postSelect.sex
        holder.binding.postSpecie.text = postSelect.specie
        postSelect.postPic.let {
           holder.binding.postPic.setImageBitmap(decodeBase64ToBitMap(it))
        }

        holder.itemView.setOnClickListener { idPostSelect(postSelect.id) }
    }


    override fun getItemCount() = listPostsPets.size

    class MyViewHolder(val binding: PostAdapterBinding) : RecyclerView.ViewHolder(binding.root)

    private fun decodeBase64ToBitMap(base64Code: String): Bitmap {

        val withoutSpaces = removeSpaces(base64Code)

        base64Code.let {
            val imageBytes = Base64.decode(it, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        }
    }

    private fun removeSpaces(input: String): String {
        return if (input.contains("\\s".toRegex())) {
            input.replace("\\s+".toRegex(), "")
        } else input
    }
}