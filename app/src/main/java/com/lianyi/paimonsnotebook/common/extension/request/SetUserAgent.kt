package com.lianyi.paimonsnotebook.common.extension.request

import okhttp3.Request

fun Request.Builder.setUserAgent(value: String) = this.addHeader("User-Agent", value)