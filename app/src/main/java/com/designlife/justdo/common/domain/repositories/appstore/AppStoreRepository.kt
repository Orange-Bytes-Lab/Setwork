package com.designlife.justdo.common.domain.repositories.appstore

import com.designlife.justdo.common.domain.entities.SettingPreference

interface AppStoreRepository {
    suspend fun getTodoId() : Int
    suspend fun updateTodoId(todoId : Int)

    suspend fun getSettingPreferences() : SettingPreference?
    suspend fun updateSettingPreferences(settingPreference: SettingPreference)

}