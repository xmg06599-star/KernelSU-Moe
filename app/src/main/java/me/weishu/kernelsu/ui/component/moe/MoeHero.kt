package me.weishu.kernelsu.ui.component.moe

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 萌化主题配色。与主题系统解耦：即使系统开了 Monet 动态取色，Hero 区也保持固定的樱粉-薰衣草-天空蓝渐变。
 */
object MoePalette {
    val Pink = Color(0xFFFF8FB1)
    val Lavender = Color(0xFFB49BFF)
    val Sky = Color(0xFF7FD4F5)

    val Ink = Color(0xFF4A3E6B)
    val InkSoft = Color(0xFF7C6E9E)

    val Paper1 = Color(0xFFFFD8E6)
    val Paper2 = Color(0xFFEBDCFF)
    val Paper3 = Color(0xFFD4F0FF)

    val Glass = Color(0xCCFFFFFF)

    /** 满铺版底部压暗用的深色 */
    val Scrim = Color(0xFF1B1233)
}

/** Hero 卡里立绘的排法。 */
enum class MoeHeroLayout {
    /** 图贴右侧，文字在左侧纸面上。 */
    SIDE,

    /** 图铺满整卡，底部压暗，文字压在图上。 */
    FULL;

    companion object {
        fun fromValue(value: Int): MoeHeroLayout = if (value == 1) FULL else SIDE
    }
}

/**
 * 首页顶部 Hero 卡：渐变纸张 + 樱花飘落 + 状态标签 + 一句台词。
 *
 * [art] 是可选的立绘：在「设置 → 主题设置 → 主页立绘」里直接选图即可（存于应用私有目录）。
 * [layout] 控制图的排法，默认 [MoeHeroLayout.SIDE]；[MoeHeroLayout.FULL] 是满铺海报感。
 * 没有图时两种排版都会退回纯渐变 + 樱花装饰版。
 */
@Composable
fun MoeHeroCard(
    title: String,
    subtitle: String,
    tags: List<String>,
    modifier: Modifier = Modifier,
    art: Painter? = null,
    layout: MoeHeroLayout = MoeHeroLayout.SIDE,
) {
    val fullBleed = layout == MoeHeroLayout.FULL && art != null
    val titleColor = if (fullBleed) Color.White else MoePalette.Ink
    val subtitleColor = if (fullBleed) Color.White.copy(alpha = 0.88f) else MoePalette.InkSoft

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(176.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    listOf(MoePalette.Paper1, MoePalette.Paper2, MoePalette.Paper3)
                )
            )
    ) {
        // 左上角柔光
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        0.0f to Color.White.copy(alpha = 0.55f),
                        0.65f to Color.Transparent
                    )
                )
        )

        // 飘落的樱花
        MoeHeroPetals(modifier = Modifier.fillMaxSize())

        if (art != null) {
            if (fullBleed) {
                Image(
                    painter = art,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // 底部压暗，保证白字可读
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                0.30f to Color.Transparent,
                                1.0f to MoePalette.Scrim.copy(alpha = 0.70f)
                            )
                        )
                )
            } else {
                Image(
                    painter = art,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .fillMaxWidth(0.62f)
                )
                // 让立绘左侧与纸面自然融合，避免硬边
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                0.0f to MoePalette.Paper1,
                                0.30f to MoePalette.Paper1.copy(alpha = 0.85f),
                                0.55f to Color.Transparent
                            )
                        )
                )
            }
        }

        // 右下角水印字（满铺时让位给图片）
        if (!fullBleed) {
            Text(
                text = "K",
                fontSize = 150.sp,
                fontWeight = FontWeight.Black,
                color = Color.White.copy(alpha = 0.18f),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 18.dp, y = 34.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                tags.forEach { tag ->
                    Text(
                        text = tag,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MoePalette.InkSoft,
                        modifier = Modifier
                            .clip(RoundedCornerShape(percent = 50))
                            .background(MoePalette.Glass)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Column(
                modifier = if (fullBleed) Modifier.fillMaxWidth() else Modifier.fillMaxWidth(0.72f)
            ) {
                Text(
                    text = title,
                    fontSize = 19.sp,
                    lineHeight = 27.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = titleColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = subtitleColor
                )
            }
        }
    }
}

/** 几片旋转的樱花瓣，纯 Canvas 绘制，不需要图片资源。 */
@Composable
private fun MoeHeroPetals(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val petal = Color.White.copy(alpha = 0.60f)
        // fx, fy, 旋转角度, 花瓣宽度
        val petals = listOf(
            floatArrayOf(0.86f, 0.16f, 18f, 9f),
            floatArrayOf(0.70f, 0.34f, -24f, 7f),
            floatArrayOf(0.94f, 0.58f, 42f, 6f),
            floatArrayOf(0.62f, 0.12f, 8f, 5f),
            floatArrayOf(0.80f, 0.84f, -38f, 8f),
            floatArrayOf(0.55f, 0.66f, 26f, 5f),
        )
        petals.forEach { p ->
            val cx = size.width * p[0]
            val cy = size.height * p[1]
            val w = p[3]
            val h = w * 1.9f
            rotate(degrees = p[2], pivot = Offset(cx, cy)) {
                drawOval(
                    color = petal,
                    topLeft = Offset(cx - w / 2f, cy - h / 2f),
                    size = Size(w, h)
                )
            }
        }
    }
}
