package com.jakurudev.cameraxexample

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun GalleryTab() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(ImagesSingleton.images.isNotEmpty()){
            LazyRow(
                modifier = Modifier
                    .height(600.dp)
                    .padding(16.dp)
            ) {
                items(count = ImagesSingleton.images.size, key = { it.hashCode() }) { i ->
                    Image(
                        bitmap = ImagesSingleton.images[i].asImageBitmap(),
                        contentDescription = "Loaded image",
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
        else{
            Text(text = "Realice una foto para verla aquí")
        }
    }
}
