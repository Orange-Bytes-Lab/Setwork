package com.designlife.justdo.settings.presentation.events

import com.designlife.justdo.settings.presentation.enums.AppBackup
import com.designlife.justdo.settings.presentation.enums.GeneralSettingView

sealed class SettingEvents{
    data class OnPickerToggle(val toggleValue : Boolean) : SettingEvents()
    data class OnGeneralSettingViewChange(val generalSettingView: GeneralSettingView) : SettingEvents()
    data class OnPickerItemClick(val index: Int) : SettingEvents()
    data class OnLoaderToggle(val toggleValue : Boolean) : SettingEvents()
    data class OnBackupSettingViewChange(val backupSetting: AppBackup) : SettingEvents()
}
