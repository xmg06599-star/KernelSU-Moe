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
 * 首页立绘 + 全App背景图的存储中心。
 *
 * 图片都不打包进 APK，而是用户自己在「设置 → 主题设置」里导入，
 * 缩放到最长边 1440 后存进应用私有目录，路径记在 SettingsRepository 里。
 */
object MoeHeroStore {
    /** 首页 Hero 卡立绘；空串表示没设置（回落到纯渐变 + 樱花装饰）。 */
    var path by mutableStateOf("")
        private set

    /** 全 App 页面背景图；空串表示没设置。 */
    var bgPath by mutableStateOf("")
        private set

    /** Hero 卡排版：0 = 侧图，1 = 满铺 */
    var layout by mutableStateOf(0)
        private set

    /** 卡片不透明度 */
    var cardAlpha by mutableStateOf(0.55f)
        private set

    /** 背景模糊（dp） */
    var bgBlur by mutableStateOf(14f)
        private set

    /** 自定义主页标题（空 = 默认台词） */
    var title by mutableStateOf("")
        private set

    /** 自定义主页副标题 */
    var subtitle by mutableStateOf("")
        private set

    fun refresh() {
        val repo = SettingsRepositoryImpl()
        path = repo.moeHeroPath
        bgPath = repo.moeBgPath
        layout = repo.moeHeroLayout
        cardAlpha = repo.moeCardAlpha
        bgBlur = repo.moeBgBlur
        title = repo.moeTitle
        subtitle = repo.moeSubtitle
    }

    fun updateAppearance(title: String, subtitle: String, cardAlpha: Float, bgBlur: Float) {
        val repo = SettingsRepositoryImpl()
        repo.moeTitle = title
        repo.moeSubtitle = subtitle
        repo.moeCardAlpha = cardAlpha
        repo.moeBgBlur = bgBlur
        this.title = title
        this.subtitle = subtitle
        this.cardAlpha = cardAlpha
        this.bgBlur = bgBlur
    }

    fun updateLayout(value: Int) {
        SettingsRepositoryImpl().moeHeroLayout = value
        layout = value
    }

    fun clear() {
        SettingsRepositoryImpl().moeHeroPath = ""
        path = ""
    }

    fun clearBackground() {
        SettingsRepositoryImpl().moeBgPath = ""
        bgPath = ""
    }

    suspend fun importImage(context: Context, uri: Uri): Boolean = withContext(Dispatchers.IO) {
        saveImage(context, uri, "hero.png")?.let { file ->
            SettingsRepositoryImpl().moeHeroPath = file
            true
        } ?: false
    }

    suspend fun importBackground(context: Context, uri: Uri): Boolean = withContext(Dispatchers.IO) {
        saveImage(context, uri, "bg.png")?.let { file ->
            SettingsRepositoryImpl().moeBgPath = file
            true
        } ?: false
    }

    /** 解码 + 缩放到最长边 1440 + 存成 PNG，返回绝对路径；失败返回 null。 */
    private fun saveImage(context: Context, uri: Uri, fileName: String): String? {
        return try {
            val decoded = context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream)
            } ?: return null

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
            val target = File(dir, fileName)
            FileOutputStream(target).use { out ->
                scaled.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            scaled.recycle()
            target.absolutePath
        } catch (_: Throwable) {
            null
        }
    }
}

/** 读 [MoeHeroStore.path] 对应的立绘，路径变了会自动重新加载。 */
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
