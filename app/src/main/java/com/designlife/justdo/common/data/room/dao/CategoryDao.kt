package com.designlife.justdo.common.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.designlife.justdo.common.data.entities.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Transaction
    @Insert
    suspend fun insertCategory(category: Category) : Long

    @Transaction
    @Query("SELECT * FROM CATEGORY")
    fun getAllCategories() : Flow<List<Category>>
}