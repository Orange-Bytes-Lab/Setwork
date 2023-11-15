package com.designlife.justdo.common.domain.entities

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

data class Note(
    val noteId : Long = 0L,
    val title : String = "",
    val content : String = "",
    val emoji : String = "📓",
    val categoryId : Long = 0L,
    val coverImage : ByteArray? = null,
    val createdTime: Date = Date(System.currentTimeMillis()),
    val lastModified : Date = Date(System.currentTimeMillis()),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Note

        if (coverImage != null) {
            if (other.coverImage == null) return false
            if (!coverImage.contentEquals(other.coverImage)) return false
        } else if (other.coverImage != null) return false

        return true
    }

    override fun hashCode(): Int {
        return coverImage?.contentHashCode() ?: 0
    }
}
