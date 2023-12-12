package com.designlife.justdo.settings.presentation.entity

import com.designlife.justdo.settings.presentation.enums.LoaderState

data class LoaderStatus(
    val title : String,
    val loaderState : LoaderState,
    val message : String
)
