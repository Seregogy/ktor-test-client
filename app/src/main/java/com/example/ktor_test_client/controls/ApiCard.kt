package com.example.ktor_test_client.controls

import android.graphics.Rect
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Vertices
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview(showBackground = true)
@Composable
fun apiCard() {
    Card(
        modifier = Modifier
            .height(350.dp)
            .width(350.dp),
        shape = RectangleShape
    ) {
        Box(
            modifier = Modifier
                .padding(15.dp)
        ) {
            Column {
                Text(
                    text = "User fetch",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.W700
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "get"
                    )

                    Text(
                        text = "http://192.168.1.64:8080"
                    )
                }
            }
        }
    }
}