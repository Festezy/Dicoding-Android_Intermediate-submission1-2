package com.ariqa.storyapp.view.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ariqa.storyapp.data.response.ListStoryItem
import com.ariqa.storyapp.databinding.ItemGetAllstoriesBinding
import com.ariqa.storyapp.view.detail.DetailstoriesActivity

class StoriesAdapter: ListAdapter<ListStoryItem, StoriesAdapter.MyViewHolder>(DIFF_CALLBACK) {
    class MyViewHolder(private val binding: ItemGetAllstoriesBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(listStories: ListStoryItem) {
            binding.apply {
                photoUrl.load(listStories.photoUrl)
                textName.text = "${listStories.name}"
                textDescriptions.text = "${listStories.description}"

                root.setOnClickListener {
                    Intent(root.context, DetailstoriesActivity::class.java).also {
                        it.putExtra(DetailstoriesActivity.EXTRA_PHOTO_URL, listStories.photoUrl)
                        it.putExtra(DetailstoriesActivity.EXTRA_NAME, listStories.name)
                        it.putExtra(DetailstoriesActivity.EXTRA_DESC, listStories.description)

                        val optionsCompat: ActivityOptionsCompat =
                            ActivityOptionsCompat.makeSceneTransitionAnimation(
                                itemView.context as Activity,
                                Pair(photoUrl, "photo"),
                                Pair(textName, "name"),
                                Pair(textDescriptions, "description"),
                            )

                        root.context.startActivity(it, optionsCompat.toBundle())
                    }
                }
            }

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