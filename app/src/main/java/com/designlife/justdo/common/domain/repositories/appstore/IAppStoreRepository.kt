package com.designlife.justdo.common.domain.repositories.appstore

import android.util.Log
import androidx.datastore.core.DataStore
import com.designlife.justdo.common.data.datastore.AppStore
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
}