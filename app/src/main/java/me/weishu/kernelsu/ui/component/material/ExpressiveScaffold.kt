package me.weishu.kernelsu.ui.component.material

import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import me.weishu.kernelsu.ui.component.moe.MoePageBackground
import me.weishu.kernelsu.ui.util.rememberBlurBackdrop
import top.yukonga.miuix.kmp.blur.LayerBackdrop
import top.yukonga.miuix.kmp.blur.layerBackdrop

/** 页面级 backdrop：卡片用它采样「身后的内容」并实时模糊，做出液态玻璃。 */
val LocalMoeBackdrop = staticCompositionLocalOf<LayerBackdrop?> { null }

@Composable
fun ExpressiveScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = Color.Transparent,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    content: @Composable (PaddingValues) -> Unit,
) {
    val moeBackdrop = rememberBlurBackdrop(true)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(if (moeBackdrop != null) Modifier.layerBackdrop(moeBackdrop) else Modifier)
    ) {
        MoePageBackground(modifier = Modifier.fillMaxSize()) { }
        CompositionLocalProvider(LocalMoeBackdrop provides moeBackdrop) {
            Scaffold(
            modifier = modifier,
            topBar = {
                Box {
                    topBar()
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    0.0f to MaterialTheme.colorScheme.surface.copy(alpha = 0.58f),
                                    0.65f to MaterialTheme.colorScheme.surface.copy(alpha = 0.30f),
                                    1.0f to Color.Transparent
                                )
                            )
                    )
                }
            },
            bottomBar = bottomBar,
            snackbarHost = snackbarHost,
            floatingActionButton = floatingActionButton,
            floatingActionButtonPosition = floatingActionButtonPosition,
            containerColor = containerColor,
            contentColor = contentColor,
            contentWindowInsets = contentWindowInsets,
            content = content,
            )
        }
    }
}

@Composable
fun expressiveTopAppBarColors(
    containerColor: Color = Color.Transparent,
    scrolledContainerColor: Color = containerColor,
): TopAppBarColors = TopAppBarDefaults.topAppBarColors(
    containerColor = containerColor,
    scrolledContainerColor = scrolledContainerColor,
    titleContentColor = MaterialTheme.colorScheme.onSurface,
    actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
)
