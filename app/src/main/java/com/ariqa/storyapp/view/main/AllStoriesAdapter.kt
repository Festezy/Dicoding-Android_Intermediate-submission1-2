package com.ariqa.storyapp.view.main

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ariqa.storyapp.data.response.ListStoryItem
import com.ariqa.storyapp.databinding.ItemGetAllstoriesBinding

class AllStoriesAdapter: ListAdapter<ListStoryItem, AllStoriesAdapter.MyViewHolder>(DIFF_CALLBACK) {
    class MyViewHolder(private val binding: ItemGetAllstoriesBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(listStories: ListStoryItem) {
            binding.photoUrl.load(listStories.photoUrl)
            binding.textName.text = "${listStories.name}"
            binding.textDescriptions.text = "${listStories.description}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemGetAllstoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val items = getItem(position)
        holder.bind(items)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}