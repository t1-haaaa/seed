package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.MinecraftEdition
import com.example.data.model.MinecraftVersionCatalog
import com.example.data.model.SuggestedSeed
import com.example.ui.theme.SeedMonoTextStyle
import com.example.ui.viewmodel.ExplorerFormState
import com.example.ui.viewmodel.SeedSuggestUiState

private val quickWishOptions =
  listOf(
    "موارد كثيرة وألماس",
    "رسبون في جبل",
    "قرية قريبة",
    "كهف أميثيست عميق",
    "غابة أزهار الكرز",
    "بوابة نذر محطمة",
    "جزيرة نادرة",
    "قلعة قديمة",
  )

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SeedExplorerScreen(
  formState: ExplorerFormState,
  suggestState: SeedSuggestUiState,
  savedSeedValues: Set<String>,
  onWishesChange: (String) -> Unit,
  onToggleTag: (String) -> Unit,
  onSelectEdition: (MinecraftEdition) -> Unit,
  onSelectVersion: (String) -> Unit,
  onGenerateSeeds: () -> Unit,
  onSaveSeed: (SuggestedSeed) -> Unit,
  onOpenMap: (SuggestedSeed) -> Unit,
  onCopiedSeed: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val clipboardManager = LocalClipboardManager.current
  var showAllVersionsSheet by remember { mutableStateOf(false) }

  if (showAllVersionsSheet) {
    AllVersionsBottomSheet(
      currentVersion = formState.selectedVersion,
      onSelectVersion = { ver ->
        onSelectVersion(ver)
        showAllVersionsSheet = false
      },
      onDismiss = { showAllVersionsSheet = false },
    )
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
        HeroWorldCard()
      }

      item {
        Card(
          colors =
            CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.surface
            ),
          shape = RoundedCornerShape(22.dp),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Filled.Explore,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp),
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "صف عالمك المثالي",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
              )
            }

            OutlinedTextField(
              value = formState.wishesText,
              onValueChange = onWishesChange,
              label = { Text("ما الذي تتمناه في هذا العالم؟") },
              placeholder = {
                Text("مثال: رسبون على قمة جبل ثلجي مع كهف ألماس وقرية قريبة...")
              },
              minLines = 3,
              maxLines = 5,
              shape = RoundedCornerShape(16.dp),
              colors =
                OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = MaterialTheme.colorScheme.primary,
                  unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                ),
              modifier = Modifier.fillMaxWidth().testTag("wish_input"),
            )

            Text(
              text = "إضافات سريعة",
              style = MaterialTheme.typography.titleSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              quickWishOptions.forEach { tag ->
                val selected = tag in formState.selectedTags
                FilterChip(
                  selected = selected,
                  onClick = { onToggleTag(tag) },
                  label = { Text(tag, style = MaterialTheme.typography.labelMedium) },
                  leadingIcon =
                    if (selected) {
                      {
                        Icon(
                          imageVector = Icons.Filled.Check,
                          contentDescription = null,
                          modifier = Modifier.size(16.dp),
                        )
                      }
                    } else null,
                  colors =
                    FilterChipDefaults.filterChipColors(
                      selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                      selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                  modifier = Modifier.testTag("wish_chip_$tag"),
                )
              }
            }

            Text(
              text = "النسخة والإصدار",
              style = MaterialTheme.typography.titleSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              MinecraftEdition.entries.forEach { edition ->
                val selected = formState.selectedEdition == edition
                FilterChip(
                  selected = selected,
                  onClick = { onSelectEdition(edition) },
                  label = { Text(edition.arabicLabel) },
                  colors =
                    FilterChipDefaults.filterChipColors(
                      selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                      selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    ),
                  modifier = Modifier.testTag("edition_${edition.apiValue}"),
                )
              }
            }

            val displayedQuickVersions =
              remember(formState.selectedVersion) {
                if (formState.selectedVersion in MinecraftVersionCatalog.quickPresetVersions) {
                  MinecraftVersionCatalog.quickPresetVersions
                } else {
                  listOf(
                    MinecraftVersionCatalog.ALL_VERSIONS,
                    formState.selectedVersion,
                  ) +
                    MinecraftVersionCatalog.quickPresetVersions.filter {
                      it != MinecraftVersionCatalog.ALL_VERSIONS
                    }
                }
              }

            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              displayedQuickVersions.forEach { ver ->
                val selected = formState.selectedVersion == ver
                FilterChip(
                  selected = selected,
                  onClick = { onSelectVersion(ver) },
                  label = {
                    Text(
                      text = MinecraftVersionCatalog.formatDisplay(ver),
                      style = MaterialTheme.typography.labelMedium,
                    )
                  },
                  modifier = Modifier.testTag("version_$ver"),
                )
              }
              AssistChip(
                onClick = { showAllVersionsSheet = true },
                label = { Text("جميع الإصدارات (1.0–1.21+) / مخصص") },
                leadingIcon = {
                  Icon(
                    imageVector = Icons.Filled.Tune,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                  )
                },
                modifier = Modifier.testTag("all_versions_picker_button"),
              )
            }

            Button(
              onClick = onGenerateSeeds,
              shape = RoundedCornerShape(16.dp),
              colors =
                ButtonDefaults.buttonColors(
                  containerColor = MaterialTheme.colorScheme.primary,
                  contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
              modifier =
                Modifier.fillMaxWidth()
                  .height(54.dp)
                  .testTag("generate_seeds_button"),
            ) {
              Icon(
                imageVector =
                  if (suggestState is SeedSuggestUiState.Success) Icons.Filled.Refresh
                  else Icons.Filled.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text =
                  if (suggestState is SeedSuggestUiState.Success)
                    "اقترح 3 سيدات جديدة ومختلفة"
                  else "اقترح لي سيدات عالمي",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
              )
            }

            if (formState.excludedSeeds.isNotEmpty()) {
              Text(
                text =
                  "وضع التنوع بلا حدود: تم استبعاد ${formState.excludedSeeds.size} سيد سابق لضمان عدم التكرار.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
              )
            }
          }
        }
      }

      when (suggestState) {
        is SeedSuggestUiState.Idle -> {
          item {
            Card(
              colors =
                CardDefaults.cardColors(
                  containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                ),
              shape = RoundedCornerShape(18.dp),
              modifier = Modifier.fillMaxWidth(),
            ) {
              Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Icon(
                  imageVector = Icons.Filled.Terrain,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.secondary,
                  modifier = Modifier.size(28.dp),
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                  text =
                    "اكتب رغباتك أو اختر وسوماً من الأعلى، ليمنحك المحرك المحلي فوراً وبلا حدود 3 بذور متجددة وفريدة في كل مرة.",
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
            }
          }
        }

        is SeedSuggestUiState.Success -> {
          item {
            Text(
              text = "البذور المقترحة لك (${suggestState.seeds.size})",
              style = MaterialTheme.typography.titleLarge,
              color = MaterialTheme.colorScheme.onBackground,
            )
          }
          items(suggestState.seeds, key = { it.seedValue }) { seed ->
            val isSaved = seed.seedValue in savedSeedValues
            val isSaving = formState.savingSeedValue == seed.seedValue
            SuggestedSeedCard(
              seed = seed,
              isSaved = isSaved,
              isSaving = isSaving,
              onCopy = {
                clipboardManager.setText(AnnotatedString(seed.seedValue))
                onCopiedSeed(seed.seedValue)
              },
              onSave = { onSaveSeed(seed) },
              onOpenMap = { onOpenMap(seed) },
            )
          }
        }
      }
    }
  }
}

@Composable
private fun HeroWorldCard() {
  Box(
    modifier =
      Modifier.fillMaxWidth()
        .height(144.dp)
        .clip(RoundedCornerShape(22.dp))
  ) {
    Image(
      painter = painterResource(id = R.drawable.img_hero_world),
      contentDescription = "بانر استكشاف عوالم ماينكرافت",
      contentScale = ContentScale.Crop,
      modifier = Modifier.fillMaxSize(),
    )
    Box(
      modifier =
        Modifier.fillMaxSize()
          .background(
            Brush.horizontalGradient(
              colors =
                listOf(
                  MaterialTheme.colorScheme.background.copy(alpha = 0.88f),
                  MaterialTheme.colorScheme.background.copy(alpha = 0.35f),
                )
            )
          )
    )
    Column(
      modifier = Modifier.align(Alignment.CenterStart).padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(50),
      ) {
        Text(
          text = "يعمل بدون قيود • بذور متجددة دائماً",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSecondaryContainer,
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
      }
      Text(
        text = "مقترح سيدات ماينكرافت",
        style = MaterialTheme.typography.headlineSmall,
        color = Color.White,
        fontWeight = FontWeight.Bold,
      )
      Text(
        text = "اكتب تفاصيل عالمك واحصل على بذور مختلفة في كل مرة",
        style = MaterialTheme.typography.bodySmall,
        color = Color.White.copy(alpha = 0.9f),
      )
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SuggestedSeedCard(
  seed: SuggestedSeed,
  isSaved: Boolean,
  isSaving: Boolean,
  onCopy: () -> Unit,
  onSave: () -> Unit,
  onOpenMap: () -> Unit,
) {
  Card(
    colors =
      CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    shape = RoundedCornerShape(20.dp),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
    modifier = Modifier.fillMaxWidth().testTag("seed_card_${seed.seedValue}"),
  ) {
    Column(
      modifier = Modifier.padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = seed.title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text =
              "${seed.edition.arabicLabel} • ${MinecraftVersionCatalog.formatDisplay(seed.minecraftVersion)}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary,
          )
        }
        IconButton(
          onClick = onSave,
          enabled = !isSaved && !isSaving,
          modifier = Modifier.testTag("save_seed_${seed.seedValue}"),
        ) {
          if (isSaving) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
          } else {
            Icon(
              imageVector =
                if (isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
              contentDescription = if (isSaved) "محفوظ في الخزنة" else "حفظ السيد",
              tint =
                if (isSaved) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }
      }

      // Monospace LTR Seed box with 1-tap copy
      Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Text(
              text = seed.seedValue,
              style = SeedMonoTextStyle,
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.weight(1f),
            )
          }
          FilledTonalButton(
            onClick = onCopy,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.testTag("copy_seed_${seed.seedValue}"),
          ) {
            Icon(
              imageVector = Icons.Filled.ContentCopy,
              contentDescription = "نسخ السيد",
              modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("نسخ", style = MaterialTheme.typography.labelMedium)
          }
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(
          imageVector = Icons.Filled.LocationOn,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.tertiary,
          modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = seed.spawnBiome,
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.width(10.dp))
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
          Text(
            text = seed.spawnCoordinates,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }

      Text(
        text = seed.summary,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )

      AnimatedVisibility(visible = seed.highlights.isNotEmpty()) {
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          seed.highlights.forEach { tag ->
            Surface(
              color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
              shape = RoundedCornerShape(50),
            ) {
              Text(
                text = tag,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
              )
            }
          }
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        OutlinedButton(
          onClick = onOpenMap,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.weight(1f).testTag("open_map_${seed.seedValue}"),
        ) {
          Icon(
            imageVector = Icons.Filled.Map,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text("خريطة التضاريس")
        }

        FilledTonalButton(
          onClick = onSave,
          enabled = !isSaved && !isSaving,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.weight(1f),
        ) {
          Icon(
            imageVector = if (isSaved) Icons.Filled.Check else Icons.Filled.BookmarkBorder,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(if (isSaved) "محفوظ" else "حفظ بالخزنة")
        }
      }
    }
  }
}
