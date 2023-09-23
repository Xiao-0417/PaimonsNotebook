package com.lianyi.paimonsnotebook.ui.screen.home.components.card.gacha_record

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.lianyi.paimonsnotebook.common.components.media.Indicator
import com.lianyi.paimonsnotebook.common.database.gacha.data.GachaRecordOverview
import com.lianyi.paimonsnotebook.common.extension.modifier.radius.radius
import com.lianyi.paimonsnotebook.common.util.compose.provider.NoOverscrollEffectThemeProvides
import com.lianyi.paimonsnotebook.ui.theme.*

@OptIn(ExperimentalPagerApi::class)
@Composable
internal fun GachaRecordCardGroup(
    gachaRecordOverviewList: List<GachaRecordOverview>,
    startIndex: Int = 0,
) {
    val state = rememberPagerState(startIndex)

    val currentIndex = state.currentPage

    Column(
        modifier = Modifier
            .radius(8.dp)
            .background(CardBackGroundColor)
            .padding(0.dp, 12.dp)
    ) {
        NoOverscrollEffectThemeProvides {
            HorizontalPager(count = gachaRecordOverviewList.size, state = state) {
                Row(modifier = Modifier.padding(12.dp, 0.dp)) {
                    GachaRecordCard(item = gachaRecordOverviewList[it])
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Indicator(
            currentPageIndex = currentIndex,
            count = state.pageCount,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            activeColor = Primary_2,
            disableColor = Primary_8
        )

    }
}