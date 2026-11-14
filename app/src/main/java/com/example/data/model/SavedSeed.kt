package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

enum class MinecraftEdition(val apiValue: String, val arabicLabel: String) {
  JAVA("java", "Java Edition"),
  BEDROCK("bedrock", "Bedrock Edition"),
  BOTH("both", "Java & Bedrock"),
}

data class SuggestedSeed(
  val seedValue: String,
  val title: String,
  val edition: MinecraftEdition,
  val minecraftVersion: String,
  val spawnBiome: String,
  val spawnCoordinates: String,
  val summary: String,
  val highlights: List<String>,
  val imagePrompt: String,
)

@IgnoreExtraProperties
data class SavedSeed(
  @DocumentId val id: String = "",
  val userId: String = "",
  val seedValue: String = "",
  val title: String = "",
  val edition: String = MinecraftEdition.JAVA.apiValue,
  val minecraftVersion: String = "1.21.4",
  val spawnBiome: String = "",
  val spawnCoordinates: String = "",
  val summary: String = "",
  val userWishPrompt: String = "",
  val highlights: List<String> = emptyList(),
  @get:PropertyName("isFavorite")
  @field:PropertyName("isFavorite")
  val isFavorite: Boolean = true,
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null,
)
