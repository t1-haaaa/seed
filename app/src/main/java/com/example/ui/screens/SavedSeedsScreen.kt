package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.MinecraftVersionCatalog
import com.example.data.model.SavedSeed
import com.example.ui.theme.SeedMonoTextStyle
import com.example.ui.viewmodel.VaultUiState

private enum class VaultFilter(val label: String) {
  ALL("الكل"),
  FAVORITES("المفضلة"),
  JAVA("Java"),
  BEDROCK("Bedrock"),
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SavedSeedsScreen(
  vaultState: VaultUiState,
  onToggleFavorite: (SavedSeed) -> Unit,
  onDeleteSeed: (String) -> Unit,
  onOpenMap: (SavedSeed) -> Unit,
  onCopiedSeed: (String) -> Unit,
  onGoToExplorer: () -> Unit,
  onRequestSignIn: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val clipboardManager = LocalClipboardManager.current
  var searchQuery by remember { mutableStateOf("") }
  var activeFilter by remember { mutableStateOf(VaultFilter.ALL) }

  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.TopCenter,
  ) {
    when (vaultState) {
      is VaultUiState.RequiresAuth -> {
        Box(
          modifier = Modifier.fillMaxSize().padding(20.dp),
          contentAlignment = Alignment.Center,
        ) {
          Card(
            colors =
              CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(22.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
            modifier = Modifier.widthIn(max = 520.dp).fillMaxWidth(),
          ) {
            Column(
              modifier = Modifier.fillMaxWidth().padding(22.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              Image(
                painter = painterResource(id = R.drawable.img_empty_vault),
                contentDescription = "خزنة البذور تتطلب تسجيل الدخول",
                contentScale = ContentScale.Crop,
                modifier =
                  Modifier.fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(16.dp)),
              )
              Text(
                text = "حفظ البذور والمفضلة يتطلب تسجيل الدخول",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
              )
              Text(
                text =
                  "سجّل الدخول بحساب Google لتتمكن من حفظ بذورك المفضلة وتنظيمها ومزامنتها عبر السحابة.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
              )
              Button(
                onClick = onRequestSignIn,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().testTag("vault_sign_in_button"),
              ) {
                Text("تسجيل الدخول بحساب Google")
              }
              OutlinedButton(
                onClick = onGoToExplorer,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
              ) {
                Icon(
                  imageVector = Icons.Filled.Explore,
                  contentDescription = null,
                  modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("العودة إلى مكتشف السيدات")
              }
            }
          }
        }
      }

      is VaultUiState.Loading -> {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
      }

      is VaultUiState.Error -> {
        Box(
          modifier = Modifier.fillMaxSize().padding(24.dp),
          contentAlignment = Alignment.Center,
        ) {
          Card(
            colors =
              CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
            shape = RoundedCornerShape(18.dp),
          ) {
            Text(
              text = vaultState.message,
              style = MaterialTheme.typography.bodyLarge,
              color = MaterialTheme.colorScheme.onErrorContainer,
              modifier = Modifier.padding(20.dp),
            )
          }
        }
      }

      is VaultUiState.Success -> {
        val filteredSeeds =
          remember(vaultState.seeds, searchQuery, activeFilter) {
            vaultState.seeds.filter { seed ->
              val matchesFilter =
                when (activeFilter) {
                  VaultFilter.ALL -> true
                  VaultFilter.FAVORITES -> seed.isFavorite
                  VaultFilter.JAVA -> seed.edition == "java" || seed.edition == "both"
                  VaultFilter.BEDROCK -> seed.edition == "bedrock" || seed.edition == "both"
                }
              val q = searchQuery.trim()
              val matchesQuery =
                q.isEmpty() ||
                  seed.title.contains(q, ignoreCase = true) ||
                  seed.seedValue.contains(q, ignoreCase = true) ||
                  seed.spawnBiome.contains(q, ignoreCase = true) ||
                  seed.minecraftVersion.contains(q, ignoreCase = true) ||
                  seed.userWishPrompt.contains(q, ignoreCase = true)
              matchesFilter && matchesQuery
            }
          }

        LazyColumn(
          modifier = Modifier.widthIn(max = 720.dp).fillMaxSize(),
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
          item {
            OutlinedTextField(
              value = searchQuery,
              onValueChange = { searchQuery = it },
              placeholder = { Text("ابحث بالاسم أو البيوم أو رقم السيد...") },
              leadingIcon = {
                Icon(imageVector = Icons.Filled.Search, contentDescription = null)
              },
              singleLine = true,
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier.fillMaxWidth().testTag("vault_search_input"),
            )
          }

          item {
            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              VaultFilter.entries.forEach { filter ->
                FilterChip(
                  selected = activeFilter == filter,
                  onClick = { activeFilter = filter },
                  label = { Text(filter.label) },
                  modifier = Modifier.testTag("vault_filter_${filter.name.lowercase()}"),
                )
              }
            }
          }

          if (filteredSeeds.isEmpty()) {
            item {
              Card(
                colors =
                  CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                  ),
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier.fillMaxWidth(),
              ) {
                Column(
                  modifier = Modifier.fillMaxWidth().padding(22.dp),
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                  Image(
                    painter = painterResource(id = R.drawable.img_empty_vault),
                    contentDescription = "خزنة البذور فارغة",
                    contentScale = ContentScale.Crop,
                    modifier =
                      Modifier.fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp)),
                  )
                  Text(
                    text =
                      if (vaultState.seeds.isEmpty()) "خزنتك السحابية فارغة حالياً"
                      else "لا توجد بذور مطابقة لبحثك",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                  )
                  Text(
                    text =
                      "احفظ البذور التي تعجبك من مكتشف السيدات لتبقى متوفرة دائماً في حسابك.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                  )
                  if (vaultState.seeds.isEmpty()) {
                    Button(
                      onClick = onGoToExplorer,
                      shape = RoundedCornerShape(14.dp),
                      modifier = Modifier.testTag("go_to_explorer_button"),
                    ) {
                      Icon(
                        imageVector = Icons.Filled.Explore,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                      )
                      Spacer(modifier = Modifier.width(8.dp))
                      Text("استكشف بذوراً الآن")
                    }
                  }
                }
              }
            }
          } else {
            items(filteredSeeds, key = { it.id }) { seed ->
              SavedSeedCard(
                seed = seed,
                onCopy = {
                  clipboardManager.setText(AnnotatedString(seed.seedValue))
                  onCopiedSeed(seed.seedValue)
                },
                onToggleFavorite = { onToggleFavorite(seed) },
                onOpenMap = { onOpenMap(seed) },
                onDelete = { onDeleteSeed(seed.id) },
              )
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SavedSeedCard(
  seed: SavedSeed,
  onCopy: () -> Unit,
  onToggleFavorite: () -> Unit,
  onOpenMap: () -> Unit,
  onDelete: () -> Unit,
) {
  Card(
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    shape = RoundedCornerShape(20.dp),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
    modifier = Modifier.fillMaxWidth().testTag("vault_card_${seed.id}"),
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = seed.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
          )
          Text(
            text =
              "${seed.edition.uppercase()} • ${MinecraftVersionCatalog.formatDisplay(seed.minecraftVersion)}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary,
          )
        }
        Row {
          IconButton(
            onClick = onToggleFavorite,
            modifier = Modifier.testTag("fav_seed_${seed.id}"),
          ) {
            Icon(
              imageVector =
                if (seed.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
              contentDescription = "تبديل المفضلة",
              tint =
                if (seed.isFavorite) MaterialTheme.colorScheme.secondary
                else MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
          IconButton(
            onClick = onDelete,
            modifier = Modifier.testTag("delete_seed_${seed.id}"),
          ) {
            Icon(
              imageVector = Icons.Filled.DeleteOutline,
              contentDescription = "حذف السيد",
              tint = MaterialTheme.colorScheme.error,
            )
          }
        }
      }

      Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
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
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
          ) {
            Icon(
              imageVector = Icons.Filled.ContentCopy,
              contentDescription = "نسخ السيد",
              modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("نسخ")
          }
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Filled.LocationOn,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.tertiary,
          modifier = Modifier.size(16.dp),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = seed.spawnBiome,
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.width(8.dp))
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
          Text(
            text = seed.spawnCoordinates,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }

      Text(
        text = seed.summary,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )

      if (seed.highlights.isNotEmpty()) {
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          seed.highlights.forEach { tag ->
            Surface(
              color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
              shape = RoundedCornerShape(50),
            ) {
              Text(
                text = tag,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              )
            }
          }
        }
      }

      OutlinedButton(
        onClick = onOpenMap,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Icon(
          imageVector = Icons.Filled.Map,
          contentDescription = null,
          modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text("عرض خريطة العالم")
      }
    }
  }
}
