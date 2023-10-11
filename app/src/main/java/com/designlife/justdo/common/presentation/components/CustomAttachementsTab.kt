package com.designlife.justdo.common.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.designlife.justdo.R
import com.designlife.justdo.common.domain.entities.Category
import com.designlife.justdo.common.utils.camelCase
import com.designlife.justdo.ui.theme.AttachmentTabItemTextStyle

@Composable
fun CustomAttachementsTab(
    hasCover : Boolean,
    onGalleryEvent : () -> Unit,
    categoryList : List<Category>,
    selectedCategoryIndex : Int,
    onCategoryEvent : (index : Int) -> Unit,
    addCategoryEvent : () -> Unit
) {
    var dropDownState by remember {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier.wrapContentSize(),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.wrapContentSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (hasCover){
                AttachementTabItem(icon = R.drawable.ic_gallery, itemTitle = "Gallery") {
                    onGalleryEvent()
                }
                Spacer(modifier = Modifier.width(25.dp))
            }
            AttachementTabItem(icon = R.drawable.ic_folder, itemTitle = if (selectedCategoryIndex == -1) "Category" else categoryList[selectedCategoryIndex].name.camelCase(),isCategory = true) {
                dropDownState = true
            }
        }
        if (dropDownState){
            DropdownMenu(
                modifier = Modifier.background(color = Color.White, shape = RoundedCornerShape(10.dp)),
                expanded = dropDownState,
                onDismissRequest = { dropDownState = false }
            ) {
                categoryList.forEachIndexed {index,category ->
                    DropdownMenuItem(
                        onClick = {
                            onCategoryEvent(index)
                            dropDownState = false
                        }
                    ){
                        Row(
                            modifier = Modifier.wrapContentWidth()
                        ) {
                            Text("${category.emoji} ${category.name.camelCase()}")
                        }
                    }
                }
                DropdownMenuItem(
                    onClick = {
                        addCategoryEvent()
                    }
                ){
                    Row(
                        modifier = Modifier.wrapContentWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🗃 New category")
                    }
                }
            }
        }
    }
}

@Composable
fun AttachementTabItem(
    @DrawableRes icon : Int,
    itemTitle : String,
    isCategory : Boolean = false,
    isDeckCategory : Boolean = false,
    onEvent : () -> Unit
) {
    Row(
        modifier = Modifier
            .wrapContentSize()
            .clickable {
                onEvent()
            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isDeckCategory){
            Icon(modifier = Modifier.size(width = 10.dp, height = 15.dp), painter = painterResource(id = icon), contentDescription = "Tab Icon")
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = itemTitle,
            style = AttachmentTabItemTextStyle
        )
        if (isCategory){
            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Drop Down")
        }
    }
}