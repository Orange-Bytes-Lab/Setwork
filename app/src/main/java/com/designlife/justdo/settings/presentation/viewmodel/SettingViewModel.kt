package com.designlife.justdo.settings.presentation.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.designlife.justdo.common.domain.repositories.appstore.AppStoreRepository
import com.designlife.justdo.common.utils.enums.AppFontSize
import com.designlife.justdo.common.utils.enums.AppListHeight
import com.designlife.justdo.common.utils.enums.AppTheme
import com.designlife.justdo.common.utils.enums.GeneralSettingView
import com.designlife.justdo.common.utils.enums.ViewType
import com.designlife.justdo.settings.presentation.events.SettingEvents

class SettingViewModel(
    private val appStoreRepository: AppStoreRepository
) : ViewModel() {


    private val _pickerVisibility : MutableState<Boolean> = mutableStateOf(false)
    val pickerVisibility : State<Boolean> = _pickerVisibility

    private val _defaultScreen : MutableState<ViewType> = mutableStateOf(ViewType.TASK)
    val defaultScreen : State<ViewType> = _defaultScreen

    private val _appTheme : MutableState<AppTheme> = mutableStateOf(AppTheme.LIGHT)
    val appTheme : State<AppTheme> = _appTheme

    private val _fontSize : MutableState<AppFontSize> = mutableStateOf(AppFontSize.MEDIUM)
    val fontSize : State<AppFontSize> = _fontSize

    private val _listHeight : MutableState<AppListHeight> = mutableStateOf(AppListHeight.MEDIUM)
    val listHeight : State<AppListHeight> = _listHeight

    private val _selectedPickerView : MutableState<GeneralSettingView> = mutableStateOf(GeneralSettingView.DEFAULT_SCREEN)
    val selectedPickerView : State<GeneralSettingView> = _selectedPickerView

    private val _pickerItemList : MutableState<List<String>> = mutableStateOf(emptyList())
    val pickerItemList : State<List<String>> = _pickerItemList

    private val screenList : List<String> = listOf(
        "Task Screen",
        "Note Screen",
        "Deck Screen"
    )

    private val appThemeList : List<String> = listOf(
        "Light",
        "Dark",
    )

    private val fontSizeList: List<String> = listOf(
        "Small",
        "Medium",
        "Large",
    )

    private val lineHeightList: List<String> = listOf(
        "Half",
        "One",
        "One & Half",
    )

    fun onEvent(event : SettingEvents){
        when(event){
            is SettingEvents.OnPickerToggle -> {
                _pickerVisibility.value = event.toggleValue
            }
            is SettingEvents.OnGeneralSettingViewChange -> {
                _selectedPickerView.value = event.generalSettingView
                _pickerItemList.value = when(event.generalSettingView){
                    GeneralSettingView.DEFAULT_SCREEN -> screenList
                    GeneralSettingView.APP_THEME -> appThemeList
                    GeneralSettingView.FONT_SIZE -> fontSizeList
                    GeneralSettingView.LIST_HEIGHT -> lineHeightList
                }
            }
            is SettingEvents.OnPickerItemClick -> {
                when(_selectedPickerView.value){
                    GeneralSettingView.DEFAULT_SCREEN -> {
                        _defaultScreen.value = when(event.index){
                            0 -> ViewType.TASK
                            1 -> ViewType.NOTE
                            2 -> ViewType.DECK
                            else -> ViewType.TASK
                        }
                    }
                    GeneralSettingView.APP_THEME -> {
                        _appTheme.value = when(event.index){
                            0 -> AppTheme.LIGHT
                            1 -> AppTheme.DARK
                            else -> AppTheme.LIGHT
                        }
                    }
                    GeneralSettingView.FONT_SIZE -> {
                        _fontSize.value = when(event.index){
                            0 -> AppFontSize.SMALL
                            1 -> AppFontSize.MEDIUM
                            2 -> AppFontSize.LARGE
                            else -> AppFontSize.MEDIUM
                        }
                    }
                    GeneralSettingView.LIST_HEIGHT -> {
                        _listHeight.value = when(event.index){
                            0 -> AppListHeight.SMALL
                            1 -> AppListHeight.MEDIUM
                            2 -> AppListHeight.LARGE
                            else -> AppListHeight.MEDIUM
                        }
                    }
                }
            }
        }
    }

}