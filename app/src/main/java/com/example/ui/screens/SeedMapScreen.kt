package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.ui.theme.SeedMonoTextStyle
import kotlin.math.abs
import kotlin.math.floor
import kotlin.random.Random

private enum class MapBiome(val arabicName: String, val color: Color) {
  DEEP_OCEAN("محيط عميق", Color(0xFF153A5B)),
  SHALLOW_WATER("بحر وأنهار", Color(0xFF2874A6)),
  BEACH_DESERT("رمال وصحراء", Color(0xFFE5C16C)),
  PLAINS("سهول خصبة", Color(0xFF66BB6A)),
  FOREST("غابات كثيفة", Color(0xFF2E7D32)),
  CHERRY_GROVE("أزهار الكرز", Color(0xFFF48FB1)),
  MOUNTAIN("جبال صخرية", Color(0xFF78909C)),
  SNOW_PEAKS("قمم ثلجية", Color(0xFFECEFF1)),
}

private data class MapLandmark(
  val name: String,
  val relX: Float,
  val relY: Float,
  val blockCoords: String,
  val pinColor: Color,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SeedMapScreen(
  seedText: String,
  onSeedTextChange: (String) -> Unit,
  onCopiedSeed: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val clipboardManager = LocalClipboardManager.current
  var radiusBlocks by remember { mutableIntStateOf(1000) }
  val numericSeed =
    remember(seedText) {
      val trimmed = seedText.trim()
      trimmed.toLongOrNull() ?: trimmed.hashCode().toLong()
    }

  val landmarks =
    remember(numericSeed, radiusBlocks) {
      generateLandmarks(numericSeed, radiusBlocks)
    }

  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.TopCenter,
  ) {
    LazyColumn(
      modifier = Modifier.widthIn(max = 720.dp).fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          shape = RoundedCornerShape(22.dp),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Filled.Map,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp),
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "خريطة التضاريس التفاعلية (بدون حدود)",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
              )
            }

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
              OutlinedTextField(
                value = seedText,
                onValueChange = onSeedTextChange,
                label = { Text("رقم السيد (Seed)") },
                textStyle = SeedMonoTextStyle,
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().testTag("map_seed_input"),
              )
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              Button(
                onClick = {
                  val next = Random(System.nanoTime()).nextLong(-8_999_999_999_999_999L, 8_999_999_999_999_999L)
                  onSeedTextChange(next.toString())
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1f).testTag("map_random_seed_button"),
              ) {
                Icon(
                  imageVector = Icons.Filled.Casino,
                  contentDescription = null,
                  modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("سيد عشوائي جديد")
              }

              FilledTonalButton(
                onClick = {
                  if (seedText.isNotBlank()) {
                    clipboardManager.setText(AnnotatedString(seedText.trim()))
                    onCopiedSeed(seedText.trim())
                  }
                },
                shape = RoundedCornerShape(14.dp),
              ) {
                Icon(
                  imageVector = Icons.Filled.ContentCopy,
                  contentDescription = "نسخ السيد",
                  modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("نسخ")
              }
            }

            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              listOf(500, 1000, 2000).forEach { r ->
                FilterChip(
                  selected = radiusBlocks == r,
                  onClick = { radiusBlocks = r },
                  label = { Text("نطاق ±$r بلوك") },
                )
              }
            }
          }
        }
      }

      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          shape = RoundedCornerShape(22.dp),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            ProceduralSeedCanvas(
              seed = numericSeed,
              radiusBlocks = radiusBlocks,
              landmarks = landmarks,
              gridColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.14f),
              modifier =
                Modifier.fillMaxWidth()
                  .aspectRatio(1f)
                  .clip(RoundedCornerShape(16.dp))
                  .testTag("seed_biome_canvas"),
            )

            Text(
              text = "المعالم المتوقعة حول نقطة البداية",
              style = MaterialTheme.typography.titleSmall,
              color = MaterialTheme.colorScheme.onSurface,
              fontWeight = FontWeight.Bold,
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              landmarks.forEach { lm ->
                Surface(
                  color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier.fillMaxWidth(),
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Surface(
                        color = lm.pinColor,
                        shape = CircleShape,
                        modifier = Modifier.size(12.dp),
                      ) {}
                      Spacer(modifier = Modifier.width(10.dp))
                      Text(
                        text = lm.name,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                      )
                    }
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                      Text(
                        text = lm.blockCoords,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                      )
                    }
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "دليل الألوان البيومية",
              style = MaterialTheme.typography.labelLarge,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(10.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              MapBiome.entries.forEach { biome ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Surface(
                    color = biome.color,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.size(14.dp),
                  ) {}
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = biome.arabicName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ProceduralSeedCanvas(
  seed: Long,
  radiusBlocks: Int,
  landmarks: List<MapLandmark>,
  gridColor: Color,
  modifier: Modifier = Modifier,
) {
  val gridSize = 48
  val scale = radiusBlocks / 450f

  val biomeGrid =
    remember(seed, radiusBlocks) {
      Array(gridSize) { gy ->
        Array(gridSize) { gx ->
          val wx = ((gx - gridSize / 2f) / (gridSize / 2f)) * scale
          val wz = ((gy - gridSize / 2f) / (gridSize / 2f)) * scale
          sampleBiomeAt(seed, wx, wz)
        }
      }
    }

  Canvas(modifier = modifier) {
    val cellW = size.width / gridSize
    val cellH = size.height / gridSize

    for (gy in 0 until gridSize) {
      for (gx in 0 until gridSize) {
        drawRect(
          color = biomeGrid[gy][gx].color,
          topLeft = Offset(gx * cellW, gy * cellH),
          size = Size(cellW + 0.8f, cellH + 0.8f),
        )
      }
    }

    // Subtle chunk grid lines
    val step = 8
    for (i in step until gridSize step step) {
      val x = i * cellW
      val y = i * cellH
      drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
      drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
    }

    // Center Spawn crosshair
    val cx = size.width / 2f
    val cy = size.height / 2f
    drawCircle(
      color = Color.White,
      radius = 10f,
      center = Offset(cx, cy),
      style = Stroke(width = 3f),
    )

    // Landmarks
    landmarks.forEach { lm ->
      val px = lm.relX * size.width
      val py = lm.relY * size.height
      drawCircle(color = Color.Black.copy(alpha = 0.5f), radius = 12f, center = Offset(px, py))
      drawCircle(color = lm.pinColor, radius = 8f, center = Offset(px, py))
      drawCircle(
        color = Color.White,
        radius = 8f,
        center = Offset(px, py),
        style = Stroke(width = 2f),
      )
    }
  }
}

private fun sampleBiomeAt(seed: Long, x: Float, z: Float): MapBiome {
  val elev = fbmNoise(seed, x * 0.9f, z * 0.9f)
  val moisture = fbmNoise(seed xor 0x5DEECE66DL, x * 0.75f + 17.3f, z * 0.75f - 9.1f)
  val temp = fbmNoise(seed xor 0x2545F4914F6CDD1DL, x * 0.65f - 11.7f, z * 0.65f + 23.4f)

  return when {
    elev < 0.28f -> MapBiome.DEEP_OCEAN
    elev < 0.37f -> MapBiome.SHALLOW_WATER
    elev > 0.79f -> MapBiome.SNOW_PEAKS
    elev > 0.66f -> MapBiome.MOUNTAIN
    temp > 0.64f && moisture < 0.45f -> MapBiome.BEACH_DESERT
    moisture > 0.68f && elev > 0.50f -> MapBiome.CHERRY_GROVE
    moisture > 0.52f -> MapBiome.FOREST
    else -> MapBiome.PLAINS
  }
}

private fun fbmNoise(seed: Long, x: Float, y: Float): Float {
  var total = 0f
  var amp = 0.55f
  var freq = 1f
  var norm = 0f
  for (octave in 0 until 3) {
    total += valueNoise(seed + octave * 1013L, x * freq, y * freq) * amp
    norm += amp
    amp *= 0.5f
    freq *= 2.05f
  }
  return (total / norm).coerceIn(0f, 1f)
}

private fun valueNoise(seed: Long, x: Float, y: Float): Float {
  val ix = floor(x).toInt()
  val iy = floor(y).toInt()
  val fx = x - ix
  val fy = y - iy
  val u = fx * fx * (3f - 2f * fx)
  val v = fy * fy * (3f - 2f * fy)

  val n00 = hash2D(seed, ix, iy)
  val n10 = hash2D(seed, ix + 1, iy)
  val n01 = hash2D(seed, ix, iy + 1)
  val n11 = hash2D(seed, ix + 1, iy + 1)

  val nx0 = n00 + u * (n10 - n00)
  val nx1 = n01 + u * (n11 - n01)
  return nx0 + v * (nx1 - nx0)
}

private fun hash2D(seed: Long, x: Int, y: Int): Float {
  var h = seed xor (x.toLong() * 374761393L) xor (y.toLong() * 668265263L)
  h = (h xor (h ushr 13)) * 1274126177L
  h = h xor (h ushr 16)
  return (abs(h) % 10_000L) / 10_000f
}

private fun generateLandmarks(seed: Long, radius: Int): List<MapLandmark> {
  val rng = Random(seed)
  val entries =
    listOf(
      "نقطة البداية (Spawn)" to Color(0xFFFFFFFF),
      "قرية قريبة" to Color(0xFFFBBF24),
      "كهف غني بالموارد" to Color(0xFF34D399),
      "بوابة نذر محطمة" to Color(0xFFA78BFA),
      "مجمع أميثيست / آثار" to Color(0xFFF472B6),
    )
  return entries.mapIndexed { idx, (label, color) ->
    if (idx == 0) {
      MapLandmark(label, 0.5f, 0.5f, "X: 0, Z: 0", color)
    } else {
      val rx = rng.nextFloat() * 0.72f + 0.14f
      val ry = rng.nextFloat() * 0.72f + 0.14f
      val bx = ((rx - 0.5f) * 2f * radius).toInt()
      val bz = ((ry - 0.5f) * 2f * radius).toInt()
      MapLandmark(label, rx, ry, "X: $bx, Z: $bz", color)
    }
  }
}
