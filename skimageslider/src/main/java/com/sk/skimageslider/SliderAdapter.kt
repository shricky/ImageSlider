package com.sk.skimageslider

import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.net.URL

class SliderAdapter(
    private val items: List<SliderItem>,
    private val errorImage: Int,
    private val onItemClick: ((SliderItem) -> Unit)?
) : RecyclerView.Adapter<SliderAdapter.SliderViewHolder>()
{

    inner class SliderViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.sliderImage)
        val title: TextView = view.findViewById(R.id.sliderTitle)
        val desc: TextView = view.findViewById(R.id.sliderDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_slider, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        val item = items[position]

        // Load image from different sources
        when (item.imageUri) {
            is Int -> holder.image.setImageResource(item.imageUri)
            is String -> {
                if (item.imageUri.startsWith("http")) {
                    // Load from network (basic)
                    Thread {
                        try {
                            val input = URL(item.imageUri).openStream()
                            val bmp = BitmapFactory.decodeStream(input)
                            holder.image.post { holder.image.setImageBitmap(bmp) }
                        } catch (e: Exception) {
                            holder.image.post { holder.image.setImageResource(errorImage) }
                        }
                    }.start()
                } else {
                    val file = File(item.imageUri)
                    if (file.exists()) {
                        holder.image.setImageBitmap(BitmapFactory.decodeFile(file.absolutePath))
                    } else {
                        holder.image.setImageResource(errorImage)
                    }
                }
            }
            is Uri -> {
                try {
                    holder.image.setImageURI(item.imageUri)
                } catch (e: Exception) {
                    holder.image.setImageResource(errorImage)
                }
            }
            is File -> {
                if (item.imageUri.exists()) {
                    holder.image.setImageBitmap(BitmapFactory.decodeFile(item.imageUri.absolutePath))
                } else {
                    holder.image.setImageResource(errorImage)
                }
            }
            else -> holder.image.setImageResource(errorImage)
        }

        holder.title.text = item.title ?: ""
        holder.desc.text = item.description ?: ""

        holder.itemView.setOnClickListener { onItemClick?.invoke(item) }
    }

    override fun getItemCount() = items.size
}