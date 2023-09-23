package com.lianyi.paimonsnotebook.common.web.hoyolab.takumi.event.calculate

import com.lianyi.paimonsnotebook.common.data.hoyolab.user.User
import com.lianyi.paimonsnotebook.common.extension.request.setReferer
import com.lianyi.paimonsnotebook.common.extension.request.setUser
import com.lianyi.paimonsnotebook.common.util.json.JSON
import com.lianyi.paimonsnotebook.common.util.request.buildRequest
import com.lianyi.paimonsnotebook.common.util.request.emptyOkHttpClient
import com.lianyi.paimonsnotebook.common.util.request.getAsJson
import com.lianyi.paimonsnotebook.common.web.ApiEndpoints
import com.lianyi.paimonsnotebook.common.web.hoyolab.cookie.CookieHelper
import okhttp3.RequestBody.Companion.toRequestBody

class CalculateClient {

    suspend fun getCalculateCompute(user: User, avatarPromotionDelta: AvatarPromotionDelta) =
        buildRequest {
            url(ApiEndpoints.CalculateCompute)

            setUser(user.userEntity,CookieHelper.Type.CookieToken)
            setReferer(ApiEndpoints.WebStaticMihoyoReferer)

            post(JSON.stringify(avatarPromotionDelta).toRequestBody())
        }.getAsJson<Consumption>(emptyOkHttpClient)
}