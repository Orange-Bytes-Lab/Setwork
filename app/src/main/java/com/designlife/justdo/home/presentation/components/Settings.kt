package com.designlife.justdo.home.presentation.components


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp
import com.designlife.justdo.common.utils.entity.SettingItem
import com.designlife.justdo.ui.theme.ButtonHighLightPrimary
import com.designlife.justdo.ui.theme.SettingHeaderStyle
import com.designlife.justdo.ui.theme.SettingPageHeaderStyle

@Composable
fun Settings(
    iconList : List<SettingItem>,
    pickerState : Boolean,
    onDefaultScreenEvent : () -> Unit,
    onAppThemeEvent : () -> Unit,
    onFontSizeEvent : () -> Unit,
    onListHeightEvent : () -> Unit,
    onImportEvent : () -> Unit,
    onExportEvent : () -> Unit,
    onHelpEvent : () -> Unit,
    onFeedbackEvent : () -> Unit,
    onSettingItemClick : () -> Unit
) {

    Box(modifier = Modifier
                .fillMaxSize()
                .alpha(if(pickerState) 0.7F else 1F)
                .blur(radius =if(pickerState) 7.dp else 0.dp))
    {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                modifier = Modifier.padding(start = 6.dp),
                text = "Settings",
                style = SettingPageHeaderStyle
            )
            LazyColumn(
                modifier = Modifier
                    .padding(start = 6.dp)
                    .fillMaxSize()
            ){
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    DividerLiner()
                    Spacer(modifier = Modifier.height(15.dp))
                    SettingHeader(headerTitle = "General")
                    Spacer(modifier = Modifier.height(15.dp))
                    SettingItemComponent(drawableIcon = iconList[0].icon, title = iconList[0].title) {
                        onDefaultScreenEvent.invoke()
                        onSettingItemClick()
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    SettingItemComponent(drawableIcon = iconList[1].icon, title = iconList[1].title) {
                        onAppThemeEvent.invoke()
                        onSettingItemClick()
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    SettingItemComponent(drawableIcon = iconList[2].icon, title = iconList[2].title ) {
                        onFontSizeEvent.invoke()
                        onSettingItemClick()
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    SettingItemComponent(drawableIcon = iconList[3].icon, title = iconList[3].title) {
                        onListHeightEvent.invoke()
                        onSettingItemClick()
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    DividerLiner()
                    Spacer(modifier = Modifier.height(15.dp))
                    SettingHeader(headerTitle = "Backup")
                    Spacer(modifier = Modifier.height(15.dp))
                    SettingItemComponent(drawableIcon = iconList[4].icon, title = iconList[4].title) {
                        onImportEvent.invoke()
                        onSettingItemClick()
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    SettingItemComponent(drawableIcon = iconList[5].icon, title = iconList[5].title) {
                        onExportEvent.invoke()
                        onSettingItemClick()
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    DividerLiner()
                    Spacer(modifier = Modifier.height(15.dp))
                    SettingHeader(headerTitle = "More")
                    Spacer(modifier = Modifier.height(15.dp))
                    SettingItemComponent(drawableIcon = iconList[6].icon, title = iconList[6].title) {
                        onHelpEvent.invoke()
                        onSettingItemClick()
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    SettingItemComponent(drawableIcon = iconList[7].icon, title = iconList[7].title) {
                        onFeedbackEvent.invoke()
                        onSettingItemClick()
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    SettingHeader(headerTitle = "VERSION 1.0.0")
                }
            }
        }
    }


}

@Composable
fun SettingHeader(
    headerTitle : String
) {
   Text(text = headerTitle.uppercase(), style = SettingHeaderStyle)
}

@Composable
fun DividerLiner() {
    Spacer(modifier = Modifier
        .height(0.2.dp)
        .fillMaxWidth()
        .background(color = ButtonHighLightPrimary))
}

