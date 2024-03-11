package com.example.pointerinput

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageVerticalGrid(feedItem: List<TestDataItem>) {
    var selectedItem by rememberSaveable {
        mutableStateOf<TestDataItem?>(null)
    }

    var longPressSelectedImageItem by rememberSaveable {
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
                    //TODO Part one
//                    .pointerInput(Unit) {
//                        //For capturing all Events
//                        awaitEachGesture {
//                            /** //TODO FIRST WAY
//                            //Consuming means we are using it we are letting it happen
//                            awaitFirstDown().consume()
//                            waitForUpOrCancellation().let {
//                                it?.consume()
//                                selectedItem = feed
//                            }
//                            **/
//                            //TODO SECOND WAY
//                            awaitFirstDown().let {
//                                it.consume()
//                                selectedItem = feed
//                            }
//                        }
//                    }
                    //TODO Mid Level Detecting some tap gestures
                    //Gestures Rec-organisers, we can use when we need to get and use the offset of the taps
//                    .pointerInput(Unit) {
//                        detectTapGestures(
//                            onTap = {
//                                selectedItem = feed
//                            },
//                            onPress = {
//
//                            },
//                            onDoubleTap = {
//
//                            },
//                            onLongPress = {
//
//                            }
//                        )
//                    }
                    //TODO High level
//                    .clickable { }
                    .combinedClickable(
                        onClick = {
                            selectedItem = feed
                        },
                        onDoubleClick = {},
                        onLongClick = {
                            longPressSelectedImageItem = feed
                        },
                    )
            )
        }
    }
    if (selectedItem != null) {
        FullScreenImage(feedItem = selectedItem) {
            selectedItem = null
        }
    }
    if (longPressSelectedImageItem != null){
        BottomSheet(feedItem = longPressSelectedImageItem){
            longPressSelectedImageItem = null
        }
    }
}

@Composable
fun FullScreenImage(feedItem: TestDataItem?, onDismiss: () -> Unit) {
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
        var zoomed by remember { mutableStateOf(false) }
        var zoomOffset by remember { mutableStateOf(Offset.Zero) }
        Image(
            painter = rememberAsyncImagePainter(model = feedItem?.url),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            val newOffset = Offset((it.x - (size.width / 2)).coerceIn(-size.width / 2f , size.width / 2f),0f)
                            zoomOffset  = if (zoomed) Offset.Zero else newOffset
                            zoomed = !zoomed
                        }
                    )
                }
                .graphicsLayer {
                    scaleX = if (zoomed) 2f else 1f
                    scaleY = if (zoomed) 2f else 1f
                    translationX = zoomOffset.x
                    translationY = zoomOffset.y
                }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(feedItem : TestDataItem?, onDismissRequest: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = {
            onDismissRequest()
        }
    ) {
        ListItem(headlineContent = {
            Text(text = "share")
        },
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share"
                )
            }
        )
        ListItem(headlineContent = {
            Text(text = "Remove")
        },
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Share"
                )
            }
        )
    }
}