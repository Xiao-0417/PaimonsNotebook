package com.lianyi.paimonsnotebook.common.util.time

import java.text.SimpleDateFormat
import java.util.Locale

object TimeHelper {
    val locale: Locale by lazy {
        Locale.CHINA
    }

    private val YY_MM_DD_HH_MM_SS by lazy {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale)
    }
    private val YY_MM_DD_HH_MM by lazy {
        SimpleDateFormat("yyyy-MM-dd HH:mm", locale)
    }
    private val YY_MM_DD_HH by lazy {
        SimpleDateFormat("yyyy-MM-dd HH", locale)
    }
    private val YY_MM_DD by lazy {
        SimpleDateFormat("yyyy-MM-dd", locale)
    }
    private val YY_MM by lazy {
        SimpleDateFormat("yyyy-MM", locale)
    }
    private val YY by lazy {
        SimpleDateFormat("yyyy", locale)
    }

    private val MM_DD_HH_MM_SS by lazy {
        SimpleDateFormat("MM-dd HH:mm:ss", locale)
    }
    private val DD_HH_MM_SS by lazy {
        SimpleDateFormat("dd HH:mm:ss", locale)
    }
    private val HH_MM_SS by lazy {
        SimpleDateFormat("HH:mm:ss", locale)
    }
    private val HH_MM by lazy {
        SimpleDateFormat("HH:mm", locale)
    }
    private val MM_SS by lazy {
        SimpleDateFormat("mm:ss", locale)
    }
    private val MM_DD by lazy {
        SimpleDateFormat("MM dd", locale)
    }
    private val MM_DD_HH_MM by lazy {
        SimpleDateFormat("MM DD HH:mm", locale)
    }
    private val MM_DD_HH by lazy {
        SimpleDateFormat("MM DD HH", locale)
    }
    private val DD_HH_MM by lazy {
        SimpleDateFormat("DD mm:ss", locale)
    }
    private val DD_HH by lazy {
        SimpleDateFormat("DD HH", locale)
    }

    fun getTime(
        timeStamp: Long,
        timeStampType: TimeStampType = TimeStampType.YYYY_MM_DD_HH_MM_SS,
    ): String {
        val sdf = when (timeStampType) {
            TimeStampType.YYYY_MM_DD_HH_MM_SS -> YY_MM_DD_HH_MM_SS
            TimeStampType.YYYY_MM_DD_HH_MM -> YY_MM_DD_HH_MM
            TimeStampType.YYYY_MM_DD_HH -> YY_MM_DD_HH
            TimeStampType.YYYY_MM_DD -> YY_MM_DD
            TimeStampType.YYYY_MM -> YY_MM
            TimeStampType.YYYY -> YY
            TimeStampType.MM_DD_HH_MM_SS -> MM_DD_HH_MM_SS
            TimeStampType.DD_HH_MM_SS -> DD_HH_MM_SS
            TimeStampType.HH_MM_SS -> HH_MM_SS
            TimeStampType.MM_SS -> MM_SS
            TimeStampType.HH_MM -> HH_MM
            TimeStampType.MM_DD -> MM_DD
            TimeStampType.MM_DD_HH -> MM_DD_HH
            TimeStampType.MM_DD_HH_MM -> MM_DD_HH_MM
            TimeStampType.DD_HH_MM -> DD_HH_MM
            TimeStampType.DD_HH -> DD_HH
        }
        return sdf.format(timeStamp)
    }


    fun getTime(
        timeStamp: Long,
        targetFormat: String,
    ): String {
        return try {
            val sdf = SimpleDateFormat(targetFormat, locale)
            sdf.format(timeStamp)
        } catch (e: Exception) {
            "targetFormat参数错误"
        }
    }

    fun getWeekName(week: Int) =
        when (week) {
            1 -> "周一"
            2 -> "周二"
            3 -> "周三"
            4 -> "周四"
            5 -> "周五"
            6 -> "周六"
            7 -> "周日"
            else -> ""
        }

    /*
    * 用于将时间戳转换为时长的字符串
    * timeStamp:时间戳
    *
    * */
    fun timeParse(timeStamp: Int, timeParseType: TimeParseType = TimeParseType.MM_SS): String {
        val hour = numberFormatter(timeStamp / 1000 / 3600)
        val minute = numberFormatter(timeStamp / 1000 / 60)
        val second = numberFormatter(timeStamp / 1000 % 60)

        return when (timeParseType) {
            TimeParseType.HH_MM_SS -> {
                "${hour}:${minute}:${second}"
            }

            TimeParseType.MM_SS -> {
                "${minute}:${second}"
            }

            TimeParseType.SS -> {
                second
            }
        }
    }

    fun timeStampParseToTextDayAndHour(timeStamp: Long): String {
        val day = timeStamp / 86400000L
        val hour = timeStamp % 86400000L / 3600000L

        return if (day > 0) {
            "${day}天${hour}小时"
        } else {
            if (hour > 0) {
                "${hour}小时"
            } else {
                "不到一小时"
            }
        }

    }

    //获取恢复时间
    //timeStamp需要乘以1000转成与系统时间一致的时间戳
    fun getRecoverTime(second: Long, currentTimeStamp: Long = System.currentTimeMillis()): String {
        val finishTimeStamp = currentTimeStamp + (second * 1000L)

        val formatString = HH_MM.format(finishTimeStamp)

        val todayDataString = YY_MM_DD.format(currentTimeStamp)
        val todayLimitTimeStamp =
            YY_MM_DD_HH_MM_SS.parse("$todayDataString 23:59:59")?.time
                ?: 0L

        val diffValue = (finishTimeStamp - todayLimitTimeStamp)

        val days = if (diffValue > 0) {
            diffValue / 86400000L + 1
        } else {
            0
        }

        val dayString = when (days) {
            0L -> "今天"
            1L -> "明天"
            2L -> "后天"
            else -> "${days}天后"
        }

        return "$dayString $formatString"
    }


    private fun numberFormatter(number: Int): String = if (number < 10) "0${number}" else "$number"
}