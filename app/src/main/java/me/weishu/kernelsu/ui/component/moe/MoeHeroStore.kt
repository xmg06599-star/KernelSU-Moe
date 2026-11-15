package me.weishu.kernelsu.ui.component.moe

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.weishu.kernelsu.data.repository.SettingsRepositoryImpl
import java.io.File
import java.io.FileOutputStream

/**
 * 首页立绘的存储中心。
 *
 * 图片不打包进 APK，而是用户第一次在「设置 → 主页立绘」里挑一张，
 * 缩放到最长边 1440 后存进应用私有目录，路径记在 SettingsRepository 里。
 */
object MoeHeroStore {
    /** 当前立绘的绝对路径；空串表示没设置（回落到纯渐变 + 樱花装饰）。 */
    var path by mutableStateOf("")
        private set

    /** 0 = 侧图，1 = 满铺 */
    var layout by mutableStateOf(0)
        private set

    fun refresh() {
        val repo = SettingsRepositoryImpl()
        path = repo.moeHeroPath
        layout = repo.moeHeroLayout
    }

    fun updateLayout(value: Int) {
        SettingsRepositoryImpl().moeHeroLayout = value
        layout = value
    }

    fun clear() {
        SettingsRepositoryImpl().moeHeroPath = ""
        path = ""
    }

    /**
     * 把 [uri] 指向的图片压到最长边 1440，存为 files/moe/hero.png。
     * @return 是否成功
     */
    suspend fun importImage(context: Context, uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val decoded = context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream)
            } ?: return@withContext false

            val longest = maxOf(decoded.width, decoded.height)
            val scaled = if (longest > 1440) {
                val ratio = 1440f / longest
                val w = (decoded.width * ratio).toInt().coerceAtLeast(1)
                val h = (decoded.height * ratio).toInt().coerceAtLeast(1)
                Bitmap.createScaledBitmap(decoded, w, h, true).also { decoded.recycle() }
            } else {
                decoded
            }

            val dir = File(context.filesDir, "moe").apply { mkdirs() }
            val target = File(dir, "hero.png")
            FileOutputStream(target).use { out ->
                scaled.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            scaled.recycle()

            SettingsRepositoryImpl().moeHeroPath = target.absolutePath
            true
        } catch (_: Throwable) {
            false
        }
    }
}

/** 读 [MoeHeroStore.path] 对应的图片，路径变了会自动重新加载。 */
@Composable
fun rememberMoeHeroPainter(): Painter? {
    val path = MoeHeroStore.path
    return remember(path) {
        if (path.isEmpty()) return@remember null
        runCatching {
            val options = BitmapFactory.Options().apply { inSampleSize = 2 }
            BitmapFactory.decodeFile(path, options)?.let { BitmapPainter(it.asImageBitmap()) }
        }.getOrNull()
    }
}
