package edu.cs371m.reddit.api

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.text.clearSpans
import com.google.gson.annotations.SerializedName

data class RedditPost (
    @SerializedName("name")
    val key: String,
    @SerializedName("title")
    val title: SpannableString,
    @SerializedName("score")
    val score: Int,
    @SerializedName("author")
    val author: String,
    @SerializedName("num_comments")
    val commentCount: Int,
    @SerializedName("thumbnail")
    val thumbnailURL: String,
    @SerializedName("url")
    val imageURL: String,
    @SerializedName("selftext")
    val selfText : SpannableString?,
    @SerializedName("is_video")
    val isVideo : Boolean,
    // Useful for subreddits
    @SerializedName("display_name")
    val displayName: SpannableString?,
    @SerializedName("icon_img")
    val iconURL: String,
    @SerializedName("public_description")
    val publicDescription: SpannableString?
) {
    companion object {
        // NB: This only highlights the first match
        private fun setSpan(fulltext: SpannableString, subtext: String): Boolean {
            if (subtext.isEmpty()) return true
            val i = fulltext.indexOf(subtext, ignoreCase = true)
            if (i == -1) return false
            fulltext.setSpan(
                ForegroundColorSpan(Color.BLUE), i, i + subtext.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return true
        }
        // XXX Failed experiment.  Seems to always return true
        fun spannableStringsEqual(a: SpannableString?, b: SpannableString?): Boolean {
            if(a == null && b == null) return true
            if(a == null && b != null) return false
            if(a != null && b == null) return false
            val spA = a!!.getSpans<Any>(0, a.length, Any::class.java)
            val spB = b!!.getSpans<Any>(0, b.length, Any::class.java)
            return a.toString() == b.toString()
                    &&
                    spA.contentDeepEquals(spB)
        }
    }
    private fun removeAllCurrentSpans(){
        // Erase all spans
        title.clearSpans()
        selfText?.clearSpans()
        displayName?.clearSpans()
        publicDescription?.clearSpans()
    }
    // Given a search string, look for it in the RedditPost.  If found,
    // highlight it and return true, otherwise return false.
    fun searchFor(searchTerm: String): Boolean {
        // XXX Write me, search both regular posts and subreddit listings
        removeAllCurrentSpans()
        var titleFound = false
        var selfTextFound = false

        if (title.contains(searchTerm, ignoreCase = true)) {

            titleFound = setSpan(title, searchTerm)
        }
        if (selfText?.contains(searchTerm, ignoreCase = true)!!) {
            selfTextFound = setSpan(selfText, searchTerm)
        }


        return titleFound || selfTextFound
    }

    fun searchForSub(searchTerm: String): Boolean {
        removeAllCurrentSpans()
        var displayNameFound = false
        var pubDesFound = false
        if(displayName?.contains(searchTerm, ignoreCase = true)!!) {
            displayNameFound = setSpan(displayName, searchTerm)
        }
        if(publicDescription?.contains(searchTerm, ignoreCase = true)!!){
            pubDesFound = setSpan(publicDescription, searchTerm)
        }
        return displayNameFound || pubDesFound

    }

    // NB: This changes the behavior of lists of RedditPosts.  I want posts fetched
    // at two different times to compare as equal.  By default, they will be different
    // objects with different hash codes.
    override fun equals(other: Any?) : Boolean =
        if (other is RedditPost) {
            key == other.key
        } else {
            false
        }
}
