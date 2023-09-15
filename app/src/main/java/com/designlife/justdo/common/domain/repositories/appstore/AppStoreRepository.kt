package com.designlife.justdo.common.domain.repositories.appstore

interface AppStoreRepository {
    suspend fun getTodoId() : Int
    suspend fun updateTodoId(todoId : Int)
}