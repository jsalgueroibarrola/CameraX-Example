package com.jakurudev.cameraxexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CameraEnhance
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jakurudev.cameraxexample.ui.theme.CameraXExampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CameraXExampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CameraTabs()
                }
            }
        }
    }
}

@Composable
fun CameraTabs() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf(
        Pair("Controller", Icons.Filled.Camera),
        Pair("Provider", Icons.Filled.CameraEnhance),
        Pair("Gallery", Icons.Filled.Collections)
    )

    Column {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabTitles.forEachIndexed { index, item ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    icon = { Icon(imageVector = item.second, contentDescription = "") },
                    text = { Text(text = item.first, modifier = Modifier.padding(16.dp)) }
                )
            }
        }
        when (selectedTabIndex) {
            0 -> CameraControllerTab()
            1 -> CameraProviderTab()
            2 -> GalleryTab()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CameraXExampleTheme {
        CameraTabs()
    }
}