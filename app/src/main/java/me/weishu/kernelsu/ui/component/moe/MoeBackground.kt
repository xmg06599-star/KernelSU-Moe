package me.weishu.kernelsu.ui.component.moe

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale

/** 读 [MoeHeroStore.bgPath] 对应的背景图。 */
@Composable
fun rememberMoeBackgroundPainter(): Painter? {
    // 优先用「背景图」，没设就用「主页立绘」同一张——虚化后色调天然统一
    val path = MoeHeroStore.bgPath.ifEmpty { MoeHeroStore.path }
    return remember(path) {
        if (path.isEmpty()) return@remember null
        runCatching {
            val options = BitmapFactory.Options().apply { inSampleSize = 2 }
            BitmapFactory.decodeFile(path, options)?.let { BitmapPainter(it.asImageBitmap()) }
        }.getOrNull()
    }
}

/**
 * 页面背景层：设置过背景图时铺一张裁切图 + 一层半透明白，让上面的卡片/文字依然清楚。
 * 没设置时完全不绘制，行为和原版一致。
 */
@Composable
fun MoePageBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val painter = rememberMoeBackgroundPainter()
    Box(modifier = modifier) {
        if (painter != null) {
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(14.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.55f))
            )
        }
        content()
    }
}
