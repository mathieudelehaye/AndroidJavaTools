package com.android.java.androidjavatools.controller.tabview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.java.androidjavatools.R
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val images = intArrayOf(R.drawable.beauty01, R.drawable.beauty02, R.drawable.beauty03,
            R.drawable.beauty04, R.drawable.beauty05)

        setContent {
            Column {
                Spacer(modifier = Modifier.height(130.dp))
                infinitePage(images)
                Spacer(modifier = Modifier.height(40.dp))
                infinitePage(images)
                Spacer(modifier = Modifier.height(40.dp))
                infinitePage(images)
            }
        }
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    fun infinitePage(images: IntArray) {
        // Add padding around our message
        HorizontalPager(
            count = Int.MAX_VALUE,
            state = rememberPagerState(
                initialPage = Int.MAX_VALUE / 2
            )
        ) { page ->
            Box(
                modifier = Modifier
                    .background(Color.White)
                    .border(width = 2.dp, Color.DarkGray)
                    .width(251.dp)
                    .height(180.dp)
            ) {
                Image(
                    contentDescription = "Contact profile picture",
                    painter = painterResource(images[page % 5]),
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                    .align(Alignment.Center)
                )
            }
        }
    }
}