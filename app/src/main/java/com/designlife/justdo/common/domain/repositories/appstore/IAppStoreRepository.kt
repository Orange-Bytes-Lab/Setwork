package com.designlife.justdo.common.domain.repositories.appstore

import android.util.Log
import androidx.datastore.core.DataStore
import com.designlife.justdo.common.data.datastore.AppStore
import com.designlife.justdo.common.domain.entities.SettingPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class IAppStoreRepository(
    private val appStore : DataStore<AppStore>
) : AppStoreRepository {
    val TAG : String = "APP_STORE"
    override suspend fun getTodoId(): Int {
        return appStore.data.firstOrNull()?.todoId ?: -1
    }

    override suspend fun updateTodoId(todoId: Int) {
        try {
            appStore.updateData {
                it.copy(
                    todoId = todoId
                )
            }
        }catch (e : Exception){
            Log.e(TAG, "updateTodoId: App Store Repository : ${e.message}")
        }
    }

    override suspend fun getSettingPreferences(): SettingPreference? {
        val data = appStore.data.firstOrNull()
        data?.let {
            return SettingPreference(
                defaultScreen = it.selectedScreen,
                appTheme = it.selectedAppTheme,
                fontSize = it.selectedFontSize,
                listItemHeight = it.selectedListItemHeight
            )
        }
        return null
    }

    suspend fun observerSettingPreferences(): Flow<AppStore> {
        return appStore.data
    }


    override suspend fun updateSettingPreferences(settingPreference: SettingPreference) {
        try {
            appStore.updateData {
                it.copy(
                    selectedScreen = settingPreference.defaultScreen,
                    selectedAppTheme = settingPreference.appTheme,
                    selectedFontSize = settingPreference.fontSize,
                    selectedListItemHeight = settingPreference.listItemHeight
                )
            }
        }catch (e : Exception){
            Log.e(TAG, "updateSettingPreferences: App Store Repository : ${e.message}")
        }
    }
}