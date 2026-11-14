package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.data.model.MinecraftVersionCatalog

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AllVersionsBottomSheet(
  currentVersion: String,
  onSelectVersion: (String) -> Unit,
  onDismiss: () -> Unit,
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var customVersionText by remember { mutableStateOf("") }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
  ) {
    LazyColumn(
      modifier = Modifier.fillMaxWidth(),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      item {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(
            text = "جميع إصدارات ماينكرافت (1.0 – 1.21+)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
          )
          Text(
            text =
              "اختر أي إصدار من القائمة أدناه أو اكتب رقم إصدار مخصص (Java / Bedrock / Snapshots).",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }

      item {
        FilterChip(
          selected = currentVersion == MinecraftVersionCatalog.ALL_VERSIONS,
          onClick = { onSelectVersion(MinecraftVersionCatalog.ALL_VERSIONS) },
          label = { Text("كل الإصدارات (بذور شاملة لجميع التحديثات)") },
          leadingIcon =
            if (currentVersion == MinecraftVersionCatalog.ALL_VERSIONS) {
              {
                Icon(
                  imageVector = Icons.Filled.Check,
                  contentDescription = null,
                )
              }
            } else null,
          colors =
            FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
          modifier = Modifier.testTag("sheet_version_all"),
        )
      }

      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            OutlinedTextField(
              value = customVersionText,
              onValueChange = { if (it.length <= 24) customVersionText = it },
              label = { Text("إصدار مخصص") },
              placeholder = { Text("1.21.60 / 25w10a") },
              singleLine = true,
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier.weight(1f).testTag("custom_version_input"),
            )
          }
          Button(
            onClick = {
              val clean = customVersionText.trim()
              if (clean.isNotEmpty()) {
                onSelectVersion(clean)
              }
            },
            enabled = customVersionText.trim().isNotEmpty(),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.height(52.dp).testTag("apply_custom_version_button"),
          ) {
            Text("تطبيق")
          }
        }
      }

      items(MinecraftVersionCatalog.eras) { era ->
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
          Text(
            text = era.title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
          )
          Text(
            text = era.subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            era.versions.forEach { ver ->
              val selected = currentVersion == ver
              FilterChip(
                selected = selected,
                onClick = { onSelectVersion(ver) },
                label = { Text("v$ver") },
                colors =
                  FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                  ),
                modifier = Modifier.testTag("sheet_version_$ver"),
              )
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(20.dp))
        Spacer(modifier = Modifier.width(1.dp))
      }
    }
  }
}
