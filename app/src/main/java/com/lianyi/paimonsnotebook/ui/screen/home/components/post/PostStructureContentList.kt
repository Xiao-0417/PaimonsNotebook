package com.lianyi.paimonsnotebook.ui.screen.home.components.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.lianyi.paimonsnotebook.R
import com.lianyi.paimonsnotebook.common.components.media.NetworkImage
import com.lianyi.paimonsnotebook.common.components.placeholder.ImageLoadingPlaceholder
import com.lianyi.paimonsnotebook.common.components.placeholder.TextPlaceholder
import com.lianyi.paimonsnotebook.common.components.spacer.NavigationPaddingSpacer
import com.lianyi.paimonsnotebook.common.components.text.DividerText
import com.lianyi.paimonsnotebook.common.components.text.FoldTextContent
import com.lianyi.paimonsnotebook.common.components.text.TitleText
import com.lianyi.paimonsnotebook.common.database.disk_cache.entity.DiskCache
import com.lianyi.paimonsnotebook.common.extension.modifier.radius.radius
import com.lianyi.paimonsnotebook.common.util.html.RichTextParser
import com.lianyi.paimonsnotebook.common.util.time.TimeStampType
import com.lianyi.paimonsnotebook.common.util.time.TimeHelper
import com.lianyi.paimonsnotebook.common.web.hoyolab.bbs.post.PostFullData
import com.lianyi.paimonsnotebook.common.web.hoyolab.bbs.post.PostStructuredContent
import com.lianyi.paimonsnotebook.common.web.hoyolab.bbs.post.StructuredBackupText
import com.lianyi.paimonsnotebook.common.web.hoyolab.bbs.post.StructuredContentType
import com.lianyi.paimonsnotebook.ui.theme.Black
import com.lianyi.paimonsnotebook.ui.theme.CardBackGroundColor_Gray_Dark
import com.lianyi.paimonsnotebook.ui.theme.Font_Normal
import com.lianyi.paimonsnotebook.ui.theme.Info
import com.lianyi.paimonsnotebook.ui.theme.LinkColor

/*
* 文章结构内容列表
* 结构解析还是有一些小问题,不修了,凑合用
* */
@OptIn(ExperimentalLayoutApi::class, ExperimentalComposeUiApi::class)
@Composable
internal fun PostStructureContentList(
    postFull: PostFullData,
    fontSize: TextUnit = 12.sp,
) {
    //处理结构内容,将文字与链接整合到一起
    val structuredContent = remember(postFull.post.post.post_id) {
        RichTextParser.parseGroup(postFull.post.post.structured_content)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .pointerInteropFilter {
                println("x = ${it.x} y = ${it.y}")
                false
            },
        contentPadding = PaddingValues(12.dp, 6.dp)
    ) {

        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                TitleText(text = postFull.post.post.subject, fontSize = 18.sp)

                DividerText(
                    text = "文章发表:${
                        TimeHelper.getTime(
                            postFull.post.post.created_at.toLong() * 1000L,
                            TimeStampType.MM_DD
                        ).replace(" ", "-")
                    }", modifier = Modifier
                        .padding(0.dp, 8.dp)
                        .fillMaxWidth()
                )
            }
        }


        items(structuredContent) { item ->
            when (item.first) {
                StructuredContentType.BackupText -> {
                    val lottery = item.second.first().insert.backupText?.lottery
                    if (lottery != null) {
                        PostBackupTextLottery(
                            lottery.toast ?: ""
                        )
                    }
                }

                StructuredContentType.Text -> {
                    TextBuildAnnotatedSpannableString(data = item.second, fontSize = fontSize)
                }

                StructuredContentType.Vod -> {
                    TextPlaceholder("Vod")
                }

                StructuredContentType.Image -> {
                    val data = item.second.first()
                    NetworkImage(
                        url = data.insert.url ?: "",
                        modifier = Modifier
                            .padding(0.dp, 8.dp)
                            .apply {
                                val height = data.attributes?.height
                                val width = data.attributes?.width
                                with(LocalDensity.current) {
                                    this@apply.width(width?.toDp() ?: 0.dp)
                                    this@apply.height(height?.toDp() ?: 0.dp)
                                }
                            }
                            .clip(RoundedCornerShape(4.dp))
                            .clickable {
//                                imageFullScreen = true
                            },
                        contentScale = ContentScale.FillWidth,
                        diskCache = DiskCache(
                            url = item.second.first().insert.url ?: "",
                            name = "文章详情图片",
                            description = "阅读文章时加载的配图"
                        ),
                        placeholder = {
                            ImageLoadingPlaceholder()
                        }
                    )
                }

                StructuredContentType.LinkCard -> {
                    val linkCard = item.second.first().insert.linkCard
                    if (linkCard != null) {
                        PostLinkCard(item = linkCard) {

                        }
                    }
                }

                StructuredContentType.Fold -> {
                    val fold = item.second.first().insert.backupText?.fold
                    if (fold != null) {
                        FoldTextContent(modifier = Modifier.padding(0.dp, 6.dp),
                            titleSlot = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_fold_content_icon),
                                    contentDescription = null,
                                    tint = Black,
                                    modifier = Modifier
                                        .size(18.dp)
                                        .graphicsLayer {
                                            rotationY = 180f
                                        }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                TextBuildAnnotatedSpannableString(fold.getTitle()) {
                                    Text(
                                        text = it,
                                        fontSize = fontSize,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        ) {
                            Box(modifier = Modifier.padding(8.dp, 0.dp)) {
                                TextBuildAnnotatedSpannableString(fold.getContent()) {
                                    Text(
                                        text = it,
                                        fontSize = fontSize,
                                        lineHeight = fontSize * 1.4444f
                                    )
                                }
                            }
                        }
                    }
                }

                else -> {}
            }
        }

        item {

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                postFull.post.topics.forEach {
                    BoxWithConstraints {
                        Text(
                            text = it.name, fontSize = 10.sp, color = Font_Normal,
                            modifier = Modifier
                                .radius(2.dp)
                                .background(CardBackGroundColor_Gray_Dark)
                                .padding(4.dp, 2.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.heightIn(6.dp))
        }

        item {
            Row(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_page_view),
                        contentDescription = null,
                        tint = Info,
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "阅读量${postFull.post.stat.view_num}",
                        fontSize = 12.sp,
                        color = Info
                    )
                }
            }
        }

        item {
            NavigationPaddingSpacer()
        }
    }
}

/*
* 富文本构造组件
* */
@Composable
private fun TextBuildAnnotatedSpannableString(
    data: List<PostStructuredContent>,
    fontSize: TextUnit,
    block: (String) -> Unit = {},
) {
    //防止空集合
    if (data.isEmpty()) return
    //防止无意义的换行
    if (data.size == 1 && (data.first().insert.content == null || data.first().insert.content == " \n" || data.first().insert.content == "\n")) return

    val text = buildAnnotatedString {
        data.forEach { item ->
            val textColor =
                if (item.attributes?.color != null) {
                    Color((item.attributes.color).toColorInt())
                } else if (item.type == StructuredContentType.Link) {
                    LinkColor
                } else {
                    Black
                }
            val content = item.insert.content

            withStyle(
                style = SpanStyle(
                    fontSize = fontSize,
                    color = textColor,
                    fontWeight = if (item.attributes?.bold == true) FontWeight.SemiBold else FontWeight.Normal
                ),
            ) {

                if (item.type == StructuredContentType.Link) {
                    val link = item.attributes?.link ?: ""
                    val end = this.length - 1 + (content?.length ?: 0)
                    addStringAnnotation("Link", link, this.length, end)
                }

                append(content)
            }
        }
    }

    //根据连续文本的最后一个的对齐属性设置对齐方式
    val attrAlign = data.last().attributes?.align
    val textAlign = remember(attrAlign) {
        if (attrAlign == "center") {
            TextAlign.Center
        } else {
            TextAlign.Start
        }
    }

    ClickableText(
        text = text,
        onClick = { index ->
            val annotatedString = text.getStringAnnotations("Link", 0, text.length)

            annotatedString.forEach {
                if (index in (it.start..it.end)) {
                    block(it.item)
                    return@forEach
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        style = TextStyle(
            lineHeight = fontSize * 1.4444f,
            textAlign = textAlign
        )
    )
}

@Composable
private fun TextBuildAnnotatedSpannableString(
    data: List<StructuredBackupText.StructuredText>,
    textSlot: @Composable (text: AnnotatedString) -> Unit,
) {
    if (data.isEmpty()) return

    val text = buildAnnotatedString {
        data.forEachIndexed { index, item ->
            val color = if (item.attributes?.color != null) {
                Color((item.attributes.color).toColorInt())
            } else {
                Black
            }
            withStyle(
                style = SpanStyle(
                    color = color
                ),
            ) {
                append(if (index != data.lastIndex) item.insert else item.insert?.removeSuffix("\n"))
            }
        }
    }
    textSlot(text)
}