package me.weishu.kernelsu.ui.component.moe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 「打扮本小姐」：改主页台词、卡片不透明度、背景模糊。
 * 数值实时写进 MoeHeroStore，确认后全局生效。
 */
@Composable
fun MoeAppearanceDialog(
    show: Boolean,
    onDismiss: () -> Unit,
) {
    if (!show) return

    var title by remember { mutableStateOf(MoeHeroStore.title) }
    var subtitle by remember { mutableStateOf(MoeHeroStore.subtitle) }
    var cardAlpha by remember { mutableFloatStateOf(MoeHeroStore.cardAlpha) }
    var bgBlur by remember { mutableFloatStateOf(MoeHeroStore.bgBlur) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("打扮本小姐") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "主页台词（留空则用默认台词）",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    singleLine = true,
                    placeholder = { Text("哼，KernelSU 才不是给你玩的") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = subtitle,
                    onValueChange = { subtitle = it },
                    singleLine = true,
                    placeholder = { Text("权限已经交到你手上了……别乱用啊！") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    "卡片不透明度 ${(cardAlpha * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Slider(
                    value = cardAlpha,
                    onValueChange = { cardAlpha = it },
                    valueRange = 0.30f..1.0f
                )

                Text(
                    "背景模糊 ${bgBlur.toInt()}dp（0 = 不模糊）",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Slider(
                    value = bgBlur,
                    onValueChange = { bgBlur = it },
                    valueRange = 0f..40f
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                MoeHeroStore.updateAppearance(
                    title = title.trim(),
                    subtitle = subtitle.trim(),
                    cardAlpha = cardAlpha,
                    bgBlur = bgBlur,
                )
                onDismiss()
            }) { Text("好了") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("算了") }
        },
    )
}
