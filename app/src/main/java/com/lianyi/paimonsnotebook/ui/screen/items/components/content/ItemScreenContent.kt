package com.lianyi.paimonsnotebook.ui.screen.items.components.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.lianyi.paimonsnotebook.R
import com.lianyi.paimonsnotebook.common.components.media.FullScreenImage
import com.lianyi.paimonsnotebook.common.components.spacer.NavigationPaddingSpacer
import com.lianyi.paimonsnotebook.common.util.enums.ListLayoutStyle
import com.lianyi.paimonsnotebook.ui.screen.items.components.item.base.ItemBaseInfo
import com.lianyi.paimonsnotebook.ui.screen.items.components.item.list_card.ItemGridListCard
import com.lianyi.paimonsnotebook.ui.screen.items.components.item.list_card.ItemListCard
import com.lianyi.paimonsnotebook.ui.screen.items.components.layout.ItemInformationCardLayout
import com.lianyi.paimonsnotebook.ui.screen.items.components.layout.ItemInformationContentLayout
import com.lianyi.paimonsnotebook.ui.screen.items.components.search.ItemFilterContent
import com.lianyi.paimonsnotebook.ui.screen.items.components.widget.ItemActionButton
import com.lianyi.paimonsnotebook.ui.screen.items.components.widget.ItemScreenTopBar
import com.lianyi.paimonsnotebook.ui.screen.items.components.widget.ItemTabLayout
import com.lianyi.paimonsnotebook.ui.screen.items.data.ItemListCardData
import com.lianyi.paimonsnotebook.ui.screen.items.util.ItemFilterType
import com.lianyi.paimonsnotebook.ui.screen.items.viewmodel.filter.ItemFilterViewModel

/*
* 物品界面内容
* */
@Composable
internal fun <T> ItemScreenContent(
    backgroundImgUrl: String,
    listButtonText: String,
    baseInfoName: String,
    baseInfoStarCount: Int,
    baseInfoIconUrl: String,
    tabs: Array<String>,
    enabledItemShadow:Boolean = false,
    itemBackgroundResId: Int = -1,
    itemFilterViewModel: ItemFilterViewModel<T>,
    onClickListButton: () -> Unit,
    itemImageContentScale:ContentScale = ContentScale.Crop,
    getListItemDataContent: (T, ItemFilterType, Boolean) -> String,
    getItemListCardData: (T) -> ItemListCardData,
    informationContentSlot: @Composable (T) -> Unit,
    onClickListItemCard: (T) -> Unit,
    cardContent: @Composable (Int) -> Unit
) {
    var showFullScreenImg by remember {
        mutableStateOf(false)
    }

    ItemInformationContentLayout(
        imgUrl = backgroundImgUrl,
        enabledItemShadow = enabledItemShadow,
        itemBackgroundResId = itemBackgroundResId,
        itemImageContentScale = itemImageContentScale
    ) {
        val lazyListState =
            rememberLazyListState()

        ItemScreenTopBar(
            onClickListButton = onClickListButton,
            lazyListState = lazyListState,
            text = listButtonText
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyListState
        ) {

            item {
                Spacer(modifier = Modifier.height(this@ItemInformationContentLayout.maxHeight * .6f))
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 0.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    //todo 此处是未来新功能按钮的占位(如果有的话)
                    Spacer(modifier = Modifier.width(1.dp))

                    ItemActionButton(
                        iconResId = R.drawable.ic_arrow_expand
                    ) {
                        showFullScreenImg = true
                    }
                }
            }

            item {
                ItemInformationCardLayout {
                    ItemBaseInfo(
                        name = baseInfoName,
                        starCount = baseInfoStarCount,
                        iconUrl = baseInfoIconUrl
                    )

                    var currentTabIndex by remember {
                        mutableIntStateOf(0)
                    }

                    ItemTabLayout(
                        tabs = tabs,
                        currentIndex = currentTabIndex,
                        onClick = {
                            currentTabIndex = it
                        }
                    )

                    cardContent.invoke(currentTabIndex)
                }
            }

            item {
                NavigationPaddingSpacer()
            }
        }

        ItemFilterContent(
            itemFilterViewModel = itemFilterViewModel,
            itemSlot = { data, layoutStyle, type ->

                val dataContent = remember(data) {
                    getListItemDataContent(data, type, layoutStyle == ListLayoutStyle.ListVertical)
                }

                when (layoutStyle) {
                    ListLayoutStyle.ListVertical -> {
                        ItemListCard(
                            data = data,
                            dataContent = dataContent,
                            onClick = onClickListItemCard,
                            itemListCardData = getItemListCardData.invoke(data),
                            informationContentSlot = {
                                informationContentSlot.invoke(data)
                            }
                        )
                    }

                    ListLayoutStyle.GridVertical -> {
                        ItemGridListCard(
                            data = data,
                            dataContent = dataContent,
                            onClick = onClickListItemCard,
                            itemListCardData = getItemListCardData.invoke(data),
                        )
                    }

                    else -> {}
                }
            }
        )

        if (showFullScreenImg) {
            FullScreenImage(
                url = backgroundImgUrl,
            ) {
                showFullScreenImg = false
            }
        }
    }
}