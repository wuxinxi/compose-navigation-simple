package cn.xxstudy.navigation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cn.xxstudy.navigation.core.Navigator
import cn.xxstudy.navigation.routes.HomeDetailNavKey

@Composable
fun HomeScreen(navigator: Navigator) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Home Content",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.clickable{
                navigator.navigate(HomeDetailNavKey)
            }
        )
    }
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