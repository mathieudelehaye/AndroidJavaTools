package com.android.java.androidjavatools.controller.tabview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import com.google.accompanist.pager.HorizontalPager
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.android.java.androidjavatools.R
import com.google.accompanist.pager.ExperimentalPagerApi

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column {
                messageCard(Message("Android", "Jetpack Compose"))

                HorizontalPager(
                    modifier = Modifier.fillMaxWidth(),
                    count = 10
                ) { page ->
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .background(Color.Blue)
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    ) {
                        Text(text = page.toString())
                    }
                }
            }
        }
    }

    @Composable
    fun messageCard(msg: Message) {
        // Add padding around our message
        Row(modifier = Modifier.padding(all = 8.dp)) {
            Image(
                painter = painterResource(R.drawable.beauty01),
                contentDescription = "Contact profile picture",
                modifier = Modifier
                    // Set image size to 40 dp
                    .size(40.dp)
                    // Clip image to be shaped as a circle
                    .clip(CircleShape)
            )

            // Add a horizontal space between the image and the column
            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(text = msg.author)
                // Add a vertical space between the author and message texts
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = msg.body)
            }
        }
    }
}

data class Message(val author: String, val body: String)