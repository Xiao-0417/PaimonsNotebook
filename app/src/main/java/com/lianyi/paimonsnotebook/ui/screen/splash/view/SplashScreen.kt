package com.lianyi.paimonsnotebook.ui.screen.splash.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.lianyi.paimonsnotebook.common.components.loading.ContentLoadingPlaceholder
import com.lianyi.paimonsnotebook.common.core.base.BaseActivity
import com.lianyi.paimonsnotebook.common.util.data_store.PreferenceKeys
import com.lianyi.paimonsnotebook.common.util.data_store.dataStoreValues
import com.lianyi.paimonsnotebook.common.util.data_store.datastorePf
import com.lianyi.paimonsnotebook.ui.screen.guide.view.GuideScreen
import com.lianyi.paimonsnotebook.ui.screen.home.util.HomeHelper
import com.lianyi.paimonsnotebook.ui.screen.home.view.HomeScreen
import com.lianyi.paimonsnotebook.ui.screen.splash.viewmodel.SplashScreenViewModel
import com.lianyi.paimonsnotebook.ui.theme.BackGroundColor
import com.lianyi.paimonsnotebook.ui.theme.PaimonsNotebookTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashScreen : BaseActivity(false) {

    val viewModel by lazy {
        ViewModelProvider(this)[SplashScreenViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //todo 用户协议
//        lifecycleScope.launch {
//            val userAgree = datastorePf.data.first()[PreferenceKeys.AgreeUserAgreement] ?: false
//            if (!userAgree) {
//                HomeHelper.goActivity(
//                    GuideScreen::class.java,
//                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                )
//            }
//        }

        viewModel.init {
            goHomeScreen()
        }

        setContent {
            PaimonsNotebookTheme {
                if (viewModel.showLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(BackGroundColor),
                        contentAlignment = Alignment.Center
                    ) {
                        ContentLoadingPlaceholder(text = "正在下载所需的元数据...")
                    }
                }
            }
        }

    }

    private fun goHomeScreen() {
        startActivity(Intent(this@SplashScreen, HomeScreen::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

}