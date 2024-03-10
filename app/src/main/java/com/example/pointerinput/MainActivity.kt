package com.example.pointerinput

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.pointerinput.ui.theme.PointerInputTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PointerInputTheme {
                    ImageVerticalGrid(feedItem = TestData.testItemsList)
            }
        }
    }
}

//Every kind of physical object Interaction with app called uses Pointer
//1-Composable inbuilt support - onClick
//2-gesture modifiers : Applied to composable -> Clickable , draggable , scrollable etc
//3-Low level, using pointerInput -> detectTabGesture , detectDragGesture etc...


@Composable
fun ImageVerticalGrid(feedItem: List<TestDataItem>) {
    var selectedItem by rememberSaveable {
        mutableStateOf<TestDataItem?>(null)
    }

    var selectedImageItem by rememberSaveable {
        mutableStateOf<TestDataItem?>(null)
    }
    LazyVerticalGrid(columns = GridCells.Fixed(3)) {
        items(items = TestData.testItemsList, key = {
            it.id
        }) { feed ->
            Image(
                painter = rememberAsyncImagePainter(model = feed.url),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(1.dp)
                    .height(140.dp)
                    .wrapContentWidth()
                    .pointerInput(Unit) {
                        //For capturing all Events
                        awaitEachGesture {
                            //Consuming means we are using it we are letting it happen
                            awaitFirstDown().consume()
                            waitForUpOrCancellation().let {
                                it?.consume()
                                selectedItem = feed
                            }
                        }
                    }
            )
        }
    }
    if (selectedItem != null){
        FullScreenImage(feedItem = selectedItem){
            selectedItem = null
        }
    }
}

@Composable
fun FullScreenImage(feedItem: TestDataItem?,onDismiss : () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.DarkGray.copy(alpha = 0.75f))
                .clickable {
                    onDismiss()
                }
        ) {
        }
        Image(
            painter = rememberAsyncImagePainter(model = feedItem?.url),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}