package com.example.data.model

data class MinecraftEra(
  val title: String,
  val subtitle: String,
  val versions: List<String>,
)

object MinecraftVersionCatalog {
  const val ALL_VERSIONS = "كل الإصدارات"

  val quickPresetVersions =
    listOf(
      ALL_VERSIONS,
      "1.21.5",
      "1.21.4",
      "1.20.4",
      "1.18.2",
      "1.16.5",
      "1.12.2",
      "1.8.9",
    )

  val eras: List<MinecraftEra> =
    listOf(
      MinecraftEra(
        title = "تحديثات 1.21 (Tricky Trials & The Garden Awakens)",
        subtitle = "الحديقة الشاحبة، غرف المحاكمة، وتوافق تام بين Java و Bedrock",
        versions = listOf("1.21.5", "1.21.4", "1.21.3", "1.21.1", "1.21", "1.21.60", "1.21.50"),
      ),
      MinecraftEra(
        title = "تحديث 1.20 (Trails & Tales)",
        subtitle = "غابات أزهار الكرز، الآثار المدفونة، والجمال",
        versions = listOf("1.20.6", "1.20.4", "1.20.2", "1.20.1", "1.20", "1.20.80"),
      ),
      MinecraftEra(
        title = "تحديث 1.19 (The Wild Update)",
        subtitle = "الأعماق الداكنة، المدن القديمة، والمانغروف",
        versions = listOf("1.19.4", "1.19.3", "1.19.2", "1.19"),
      ),
      MinecraftEra(
        title = "تحديثات 1.17 – 1.18 (Caves & Cliffs)",
        subtitle = "الجبال الشاهقة، كهوف اللوش، الأميثيست، وارتفاع العالم الجديد",
        versions = listOf("1.18.2", "1.18.1", "1.18", "1.17.1", "1.17"),
      ),
      MinecraftEra(
        title = "تحديثات 1.14 – 1.16 (Village & Nether)",
        subtitle = "النذر المحدث، القرى الجديدة، والبوابات المحطمة",
        versions = listOf("1.16.5", "1.16.4", "1.16.1", "1.15.2", "1.14.4"),
      ),
      MinecraftEra(
        title = "الإصدارات الكلاسيكية والمودات (1.7 – 1.13)",
        subtitle = "إصدارات المودات والقتال الشهيرة وتحديث المحيطات",
        versions = listOf("1.13.2", "1.12.2", "1.11.2", "1.10.2", "1.9.4", "1.8.9", "1.7.10"),
      ),
      MinecraftEra(
        title = "الإصدارات القديمة الأولى (1.0 – 1.6)",
        subtitle = "الحنين إلى التضاريس الكلاسيكية والبدايات الأولى",
        versions = listOf("1.6.4", "1.5.2", "1.4.7", "1.2.5", "1.0"),
      ),
    )

  /**
   * Parses major-minor feature level (e.g. "1.21.4" -> 21, "1.12.2" -> 12, "1.8.9" -> 8).
   * Returns null when [version] represents ALL_VERSIONS.
   */
  fun parseMinorVersion(version: String): Int? {
    val trimmed = version.trim()
    if (trimmed.isEmpty() || trimmed == ALL_VERSIONS) return null

    // Handle Java snapshots like "25w10a" / "24w45a" -> modern 1.21+
    if (Regex("^\\d{2}w\\d{2}[a-z]?$", RegexOption.IGNORE_CASE).matches(trimmed)) {
      val year = trimmed.take(2).toIntOrNull() ?: return 21
      return if (year >= 24) 21 else 20
    }

    val match = Regex("^1\\.(\\d+)").find(trimmed)
    return match?.groupValues?.getOrNull(1)?.toIntOrNull()
  }

  fun formatDisplay(version: String): String {
    val trimmed = version.trim()
    if (trimmed.isEmpty() || trimmed == ALL_VERSIONS) return ALL_VERSIONS
    return if (trimmed.first().isDigit()) "v$trimmed" else trimmed
  }
}
