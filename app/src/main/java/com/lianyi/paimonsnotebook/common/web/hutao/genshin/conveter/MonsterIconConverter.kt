package com.lianyi.paimonsnotebook.common.web.hutao.genshin.conveter

import com.lianyi.paimonsnotebook.common.web.HutaoEndpoints

object MonsterIconConverter {
    fun iconNameToUrl(name: String) = HutaoEndpoints.staticFile(
        "MonsterIcon",
        "${name}.png"
    )
}