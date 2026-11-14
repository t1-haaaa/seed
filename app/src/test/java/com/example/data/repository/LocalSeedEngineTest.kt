package com.example.data.repository

import com.example.data.model.MinecraftEdition
import com.example.data.model.MinecraftVersionCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalSeedEngineTest {

  private val engine = LocalSeedEngine()

  @Test
  fun `suggestSeeds returns 3 unique seeds never repeating excludedSeeds`() {
    val excluded = mutableListOf<String>()

    repeat(10) {
      val batch =
        engine.suggestSeeds(
          wishes = "رسبون في جبل مع موارد كثيرة وقرية",
          quickTags = listOf("رسبون في جبل", "موارد كثيرة"),
          edition = MinecraftEdition.BOTH,
          version = "1.21.4",
          excludedSeeds = excluded,
        )

      assertEquals(3, batch.size)
      val batchValues = batch.map { it.seedValue }
      assertEquals(3, batchValues.toSet().size)
      for (seed in batchValues) {
        assertFalse("Seed $seed was repeated from excluded list", seed in excluded)
        assertTrue(seed.toLongOrNull() != null)
      }
      excluded.addAll(batchValues)
    }

    assertEquals(30, excluded.toSet().size)
  }

  @Test
  fun `suggestSeeds works across all legacy and custom Minecraft versions`() {
    val versionsToCheck =
      listOf(
        MinecraftVersionCatalog.ALL_VERSIONS,
        "1.21.5",
        "1.20.4",
        "1.16.5",
        "1.12.2",
        "1.8.9",
        "1.0",
        "1.21.60",
        "25w10a",
      )

    for (ver in versionsToCheck) {
      val batch =
        engine.suggestSeeds(
          wishes = "أزهار الكرز مع كهف أميثيست وقرية وموارد",
          quickTags = listOf("موارد كثيرة وألماس", "قرية قريبة"),
          edition = MinecraftEdition.BOTH,
          version = ver,
          excludedSeeds = emptyList(),
        )
      assertEquals(3, batch.size)
      assertTrue(batch.all { it.minecraftVersion == ver && it.title.isNotBlank() })
      // Legacy versions (< 1.20) must not recommend Cherry Grove in spawnBiome
      val minor = MinecraftVersionCatalog.parseMinorVersion(ver)
      if (minor != null && minor < 20) {
        assertTrue(batch.none { it.spawnBiome.contains("Cherry Grove", ignoreCase = true) })
      }
    }
  }

  @Test
  fun `version parser handles ALL_VERSIONS, classic, Bedrock, and snapshots`() {
    assertNull(MinecraftVersionCatalog.parseMinorVersion(MinecraftVersionCatalog.ALL_VERSIONS))
    assertEquals(21, MinecraftVersionCatalog.parseMinorVersion("1.21.60"))
    assertEquals(12, MinecraftVersionCatalog.parseMinorVersion("1.12.2"))
    assertEquals(8, MinecraftVersionCatalog.parseMinorVersion("1.8.9"))
    assertEquals(0, MinecraftVersionCatalog.parseMinorVersion("1.0"))
    assertEquals(21, MinecraftVersionCatalog.parseMinorVersion("25w10a"))
    assertEquals(
      MinecraftVersionCatalog.ALL_VERSIONS,
      MinecraftVersionCatalog.formatDisplay(MinecraftVersionCatalog.ALL_VERSIONS),
    )
    assertEquals("v1.12.2", MinecraftVersionCatalog.formatDisplay("1.12.2"))
  }

  @Test
  fun `suggestSeeds works seamlessly with empty wishes`() {
    val batch =
      engine.suggestSeeds(
        wishes = "",
        quickTags = emptyList(),
        edition = MinecraftEdition.JAVA,
        version = MinecraftVersionCatalog.ALL_VERSIONS,
        excludedSeeds = emptyList(),
      )

    assertEquals(3, batch.size)
    assertTrue(batch.all { it.title.isNotBlank() && it.summary.isNotBlank() })
  }
}
