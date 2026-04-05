package cn.xxstudy.navigation.screen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cn.xxstudy.navigation.core.Navigator
import cn.xxstudy.navigation.routes.HomeDetailNavKey
import kotlin.random.Random

@Composable
fun HomeScreen(navigator: Navigator) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        var name by remember { mutableStateOf("ZH") }

        Text(
            text = "Home Content",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.clickable {
                navigator.navigate(HomeDetailNavKey)
            }
        )

        Text(name)

        CustomText()
        Button(onClick = {
            name = Random.nextDouble().toString()
        }) {
            Text("CHANGE")
        }

    }
}

@Composable
fun CustomText() {
    Log.d("ComposeTest", "CustomText recomposed")
    Text("Hello")
}


@Composable
fun HomeDetailScreen(navigator: Navigator) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(
            text = "Home Detail",
            style = MaterialTheme.typography.headlineMedium
        )

        Button(onClick = { navigator.goBack() }) {
            Text(text = "Go to Home")
        }


    }
}