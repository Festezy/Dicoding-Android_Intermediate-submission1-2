package com.ariqa.storyapp

import com.ariqa.storyapp.data.response.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val storyItem = ListStoryItem(
                "https://story-api.dicoding.dev/images/stories/photos-1715879953261_a1698915b4900dbc097a.jpg",
                "2024-05-16T17:19:13.262Z",
            "ariq2",
            "test1",
            0.0,
            "story-$i",
            0.0
            )
            items.add(storyItem)
        }
        return items
    }

}