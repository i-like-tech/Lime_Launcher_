package com.example.limelauncher

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.example.limelauncher.ui.theme.LimeLauncherTheme


data class AppInfo(
    val label: CharSequence,
    val icon: Drawable,
    val packageName: String,
    val activityName: String
)

// Correct structure
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LimeLauncherTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(

                            painter = painterResource(id = R.drawable.iphone_16_pro_max_65),
                            contentDescription = "Background Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        AppGridScreen(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}




@Composable
fun AppGridScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val packageManager = context.packageManager
    var apps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }

    LaunchedEffect(Unit) {
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolvedApps: List<ResolveInfo> = packageManager.queryIntentActivities(mainIntent, PackageManager.MATCH_DEFAULT_ONLY)

        apps = resolvedApps.mapNotNull { resolveInfo ->
            resolveInfo.activityInfo?.let { activityInfo ->
                AppInfo(
                    label = resolveInfo.loadLabel(packageManager),
                    icon = resolveInfo.loadIcon(packageManager),
                    packageName = activityInfo.packageName,
                    activityName = activityInfo.name
                )
            }
        }.sortedBy { it.label.toString().lowercase() }
    }


    val numberOfColumns = 4

    LazyVerticalGrid(
        columns = GridCells.Fixed(numberOfColumns),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(apps) { app ->
            AppGridItem(appInfo = app) {
                val launchIntent = packageManager.getLaunchIntentForPackage(app.packageName)
                if (launchIntent != null) {
                    context.startActivity(launchIntent)
                }
            }
        }
    }
}

@Composable
fun AppGridItem(appInfo: AppInfo, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            bitmap = appInfo.icon.toBitmap().asImageBitmap(),
            contentDescription = appInfo.label.toString(),
            modifier = Modifier
                .fillMaxSize(0.7f)
                .aspectRatio(1f)
        )
        Text(
            text = appInfo.label.toString(),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LimeLauncherTheme {

        val context = LocalContext.current
        val mockApp = AppInfo(
            label = "Sample App Very Long Name",
            icon = context.packageManager.getApplicationIcon(context.packageName),
            packageName = "com.example.sample",
            activityName = "com.example.sample.MainActivity"
        )
        val mockApps = List(8) { mockApp.copy(label = "App ${it + 1}") }


        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.padding(8.dp),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(mockApps) { app ->
                AppGridItem(appInfo = app, onClick = {})
            }
        }
    }
}