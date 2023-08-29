package com.designlife.justdo.common.utils

import android.content.Context
import com.designlife.justdo.common.data.room.dao.AppDatabase
import com.designlife.justdo.common.domain.repeat.RepeatRepository
import com.designlife.justdo.common.domain.repositories.CategoryRepository
import com.designlife.justdo.common.domain.repositories.TodoRepository
import java.util.Date

object AppServiceLocator {
    private var categoryRepository : CategoryRepository? = null
    private var todoRepository : TodoRepository? = null
    private var repeatRepository : RepeatRepository? = null


    public fun provideCategoryRepository(context : Context) : CategoryRepository{
       return categoryRepository ?: createCategoryRepository(context);
    }

    private fun createCategoryRepository(context: Context): CategoryRepository {
        val categoryDao = AppDatabase.getDatabase(context).categoryDao()
        categoryRepository = CategoryRepository(categoryDao)
        return categoryRepository!!
    }

    public fun provideTodoRepository(context : Context) : TodoRepository{
        return todoRepository ?: createTodoRepository(context)
    }

    private fun createTodoRepository(context: Context): TodoRepository {
        val todoDao = AppDatabase.getDatabase(context).todoDao()
        todoRepository = TodoRepository(todoDao)
        return todoRepository!!
    }

    public fun provideRepeatRepository() : RepeatRepository{
        return repeatRepository ?: createRepeatRepository()
    }

    private fun createRepeatRepository(): RepeatRepository {
        repeatRepository = RepeatRepository()
        return repeatRepository!!
    }
}