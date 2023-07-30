package com.designlife.justdo.common.domain.converters

import com.designlife.justdo.common.data.room.dao.TodoDao
import com.designlife.justdo.common.domain.calendar.IDateGenerator
import com.designlife.justdo.common.domain.entities.Todo

object TodoConverters {

    fun getTodoEntity(todo: Todo) : com.designlife.justdo.common.data.entities.Todo{
        return com.designlife.justdo.common.data.entities.Todo(
            title = todo.title,
            date = IDateGenerator.getEpochFromDate(todo.date),
            note = todo.note,
            isRepeated = todo.isRepeated,
            isCompleted = todo.isCompleted,
            categoryId = todo.categoryId
        )
    }

    fun getTodo(todo: com.designlife.justdo.common.data.entities.Todo) : Todo{
        return Todo(
            title = todo.title,
            date = IDateGenerator.getDateFromEpoch(todo.date),
            note = todo.note,
            isRepeated = todo.isRepeated,
            isCompleted = todo.isCompleted,
            categoryId = todo.categoryId
        )
    }

}