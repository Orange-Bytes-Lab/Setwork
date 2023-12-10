package com.designlife.justdo.settings.presentation.events

import com.designlife.justdo.common.utils.enums.GeneralSettingView

sealed class SettingEvents{
    data class OnPickerToggle(val toggleValue : Boolean) : SettingEvents()
    data class OnGeneralSettingViewChange(val generalSettingView: GeneralSettingView) : SettingEvents()
    data class OnPickerItemClick(val index: Int) : SettingEvents()
}
