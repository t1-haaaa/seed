package com.example.data.repository

import com.example.data.model.MinecraftEdition
import com.example.data.model.MinecraftVersionCatalog
import com.example.data.model.SuggestedSeed
import kotlin.math.abs
import kotlin.random.Random

enum class SeedTrait(val arabicLabel: String, val keywords: List<String>) {
  MOUNTAIN(
    "رسبون جبلي",
    listOf("جبل", "جبال", "قمم", "قمة", "ثلج", "مرتفع", "جرف", "mountain", "peak", "snow", "cliff"),
  ),
  RESOURCES(
    "وفرة الموارد",
    listOf(
      "موارد",
      "مورد",
      "ألماس",
      "الماس",
      "حديد",
      "ذهب",
      "زمرد",
      "فحم",
      "نحاس",
      "مناجم",
      "منجم",
      "خام",
      "كثيرة",
      "غني",
      "diamond",
      "iron",
      "ore",
      "resource",
      "mine",
      "emerald",
    ),
  ),
  VILLAGE(
    "قرية قريبة",
    listOf("قرية", "قرى", "قرويين", "حداد", "بيوت", "مزرعة", "village", "blacksmith", "outpost"),
  ),
  CAVE(
    "كهوف وأميثيست",
    listOf("كهف", "كهوف", "أميثيست", "اميثيست", "كريستال", "لوش", "عميق", "cave", "lush", "amethyst", "dripstone"),
  ),
  CHERRY(
    "أزهار الكرز",
    listOf("كرز", "أزهار", "ازهار", "وردي", "cherry", "blossom", "pink"),
  ),
  ISLAND(
    "جزيرة وبحار",
    listOf("جزيرة", "جزر", "بحر", "محيط", "ماء", "سفينة", "شعب", "مرجان", "فطر", "island", "ocean", "mushroom"),
  ),
  ANCIENT_CITY(
    "آثار وقلعة قديمة",
    listOf("قلعة", "مدينة", "قديمة", "معبد", "حصن", "واردين", "نهاية", "ancient", "city", "stronghold", "temple", "warden"),
  ),
  NETHER_PORTAL(
    "بوابة نذر ولافا",
    listOf("نذر", "بوابة", "لافا", "أوبسيديان", "اوبسيديان", "portal", "nether", "lava", "obsidian"),
  ),
  FOREST(
    "غابات وأنهار",
    listOf("غابة", "غابات", "شجر", "خشب", "نهر", "وادي", "أدغال", "ادغال", "forest", "river", "valley", "jungle"),
  ),
  DESERT(
    "صحراء ومعبد صحراوي",
    listOf("صحراء", "صحراوي", "هرم", "رمال", "بادية", "desert", "pyramid", "badlands", "mesa"),
  ),
}

private data class CuratedSeedEntry(
  val seedValue: String,
  val title: String,
  val edition: MinecraftEdition,
  val minMinor: Int,
  val spawnBiome: String,
  val spawnCoordinates: String,
  val summary: String,
  val highlights: List<String>,
  val traits: Set<SeedTrait>,
)

class LocalSeedEngine {

  private val curatedCatalog =
    listOf(
      CuratedSeedEntry(
        seedValue = "-6842190574312088",
        title = "قمم الزمرد ومناجم الألماس العميقة",
        edition = MinecraftEdition.BOTH,
        minMinor = 18,
        spawnBiome = "قمم متجمدة (Jagged Peaks)",
        spawnCoordinates = "X: 32, Y: 146, Z: -64",
        summary =
          "تبدأ رحلتك فوق سلسلة جبال شاهقة تطل على وادٍ أخضر خصيب؛ أسفل المنحدر الغربي توجد شبكة كهوف مترابطة غنية بعروق الحديد والفحم مع هبوط سريع إلى طبقة الألماس (-54) وقرية جبلية قريبة.",
        highlights = listOf("رسبون فوق جبل", "وفرة الألماس والحديد", "قرية قريبة", "كهف عميق"),
        traits = setOf(SeedTrait.MOUNTAIN, SeedTrait.RESOURCES, SeedTrait.VILLAGE, SeedTrait.CAVE),
      ),
      CuratedSeedEntry(
        seedValue = "409812736459012",
        title = "الوادي المخفي وبوابة النذر الماسية",
        edition = MinecraftEdition.JAVA,
        minMinor = 20,
        spawnBiome = "غابة الكرز الجبلية (Cherry Grove)",
        spawnCoordinates = "X: -112, Y: 118, Z: 88",
        summary =
          "دائرة طبيعية من جبال أزهار الكرز تحيط ببحيرة صافية، مع بوابة نذر محطمة على بعد 90 بلوك من البداية وبداخل صندوقها أدوات ذهبية وأوبسيديان، بالإضافة إلى منجم مكشوف في الجدار الصخري.",
        highlights = listOf("أزهار الكرز", "بوابة نذر قريبة", "موارد مكشوفة", "تضاريس جبلية"),
        traits = setOf(SeedTrait.CHERRY, SeedTrait.MOUNTAIN, SeedTrait.NETHER_PORTAL, SeedTrait.RESOURCES),
      ),
      CuratedSeedEntry(
        seedValue = "-198472615039481",
        title = "كهوف الأميثيست وقلاع الأعماق",
        edition = MinecraftEdition.BEDROCK,
        minMinor = 19,
        spawnBiome = "تلال الرياح الصخرية (Windswept Hills)",
        spawnCoordinates = "X: 164, Y: 108, Z: -140",
        summary =
          "رسبون على هضبة صخرية تتخللها فجوات تؤدي مباشرة إلى كهف لوش مضيء مليء بالكريستال (Amethyst Geode) وعروق الذهب والنحاس بكثافة عالية، وتحت الجبل الرئيسي تمتد مدينة قديمة ضخمة.",
        highlights = listOf("أميثيست مزدوج", "موارد كثيرة", "مدينة قديمة", "كهف لوش"),
        traits = setOf(SeedTrait.CAVE, SeedTrait.RESOURCES, SeedTrait.ANCIENT_CITY, SeedTrait.MOUNTAIN),
      ),
      CuratedSeedEntry(
        seedValue = "7739104826519304",
        title = "أرخبيل الحدادين والمحيط الدافئ",
        edition = MinecraftEdition.BOTH,
        minMinor = 14,
        spawnBiome = "شواطئ الجزيرة المرجانية (Warm Ocean)",
        spawnCoordinates = "X: 16, Y: 68, Z: 24",
        summary =
          "جزيرة متوسطة الحجم تضم قرية ساحلية مع حداد يوفر سبائك حديد وخبزاً من البداية، تحيط بها سفينة غارقة على عمق قريب فيها خريطة كنز وموارد بحرية نادرة.",
        highlights = listOf("جزيرة بداية", "قرية حدادين", "سفينة غارقة", "موارد سريعة"),
        traits = setOf(SeedTrait.ISLAND, SeedTrait.VILLAGE, SeedTrait.RESOURCES),
      ),
      CuratedSeedEntry(
        seedValue = "-5129038471625409",
        title = "مرتفعات الثلج والقرى الثلاث",
        edition = MinecraftEdition.JAVA,
        minMinor = 18,
        spawnBiome = "منحدرات ثلجية (Snowy Slopes)",
        spawnCoordinates = "X: -48, Y: 132, Z: 192",
        summary =
          "ثلاث قرى متقاربة في السهل الممتد أسفل الجبل الثلجي، مع وفرة في الحيوانات والأخشاب، وتقاطع أنهار عميقة يكشف طبقات الحديد والفحم مباشرة على السطح.",
        highlights = listOf("3 قرى متجاورة", "رسبون جبلي ثلجي", "أنهار وموارد", "مناسب للبقاء"),
        traits = setOf(SeedTrait.MOUNTAIN, SeedTrait.VILLAGE, SeedTrait.RESOURCES, SeedTrait.FOREST),
      ),
      CuratedSeedEntry(
        seedValue = "839201746512890",
        title = "وادي الأنهار والغابة الكثيفة",
        edition = MinecraftEdition.BEDROCK,
        minMinor = 16,
        spawnBiome = "غابة البتولا القديمة (Old Growth Forest)",
        spawnCoordinates = "X: 80, Y: 76, Z: -112",
        summary =
          "نهر عريض يشق غابة غنية بالأخشاب والأنهار المتفرعة مع وادٍ سحيق (Ravine) قريب يجمع بين اللافا والماء لتشكيل أوبسيديان طبيعي وعروق ألماس في قاع الوادي.",
        highlights = listOf("خشب وفير", "وادي سحيق بالأوبسيديان", "أنهار متصلة", "بداية مريحة"),
        traits = setOf(SeedTrait.FOREST, SeedTrait.RESOURCES, SeedTrait.NETHER_PORTAL),
      ),
      CuratedSeedEntry(
        seedValue = "-9041827365412098",
        title = "واحة الصحراء ومعبد الكنوز",
        edition = MinecraftEdition.BOTH,
        minMinor = 7,
        spawnBiome = "صحراء وأراضي وعرة (Desert & Badlands)",
        spawnCoordinates = "X: 204, Y: 72, Z: 60",
        summary =
          "تبدأ قرب التقاء الصحراء بأراضي الميسا الغنية بالذهب السطحي؛ على بعد خطوات يظهر معبد صحراوي فيه صناديق مليئة بالكتب المسحورة والذهب، وتجاوره قرية صحراوية.",
        highlights = listOf("ذهب سطحي بكثرة", "معبد صحراوي", "قرية واحة", "متوافق مع الكلاسيك"),
        traits = setOf(SeedTrait.DESERT, SeedTrait.RESOURCES, SeedTrait.VILLAGE, SeedTrait.ANCIENT_CITY),
      ),
      CuratedSeedEntry(
        seedValue = "2468135790246813",
        title = "قمة البركان وبوابة النذر العملاقة",
        edition = MinecraftEdition.JAVA,
        minMinor = 16,
        spawnBiome = "قمم صخرية (Stony Peaks)",
        spawnCoordinates = "X: -72, Y: 141, Z: -88",
        summary =
          "جبل صخري شاهق تتدفق منه شلالات لافا ومياه متجاورة مع بوابة نذر محطمة على ارتفاع الجبل، ومغارة عميقة تنحدر إلى بحيرة ألماس وريدستون.",
        highlights = listOf("قمة جبلية", "بوابة نذر ولافا", "مناجم عميقة", "أحجار كريمة"),
        traits = setOf(SeedTrait.MOUNTAIN, SeedTrait.NETHER_PORTAL, SeedTrait.RESOURCES, SeedTrait.CAVE),
      ),
      CuratedSeedEntry(
        seedValue = "-3349182746501927",
        title = "جزيرة الفطر الساحرة ومغارة الكريستال",
        edition = MinecraftEdition.BOTH,
        minMinor = 17,
        spawnBiome = "حقول الفطر (Mushroom Fields)",
        spawnCoordinates = "X: 0, Y: 74, Z: 0",
        summary =
          "جزيرة فطر آمنة تماماً لا ترسبن فيها الوحوش ليلاً، أسفلها كهف مائي مضيء يحتوي على بلورات أميثيست وخام حديد وألماس، ومعبر بحري قريب نحو البر الرئيسي.",
        highlights = listOf("جزيرة آمنة ليلاً", "كهف أميثيست تحتها", "نادر جداً", "موارد تحت الأرض"),
        traits = setOf(SeedTrait.ISLAND, SeedTrait.CAVE, SeedTrait.RESOURCES),
      ),
      CuratedSeedEntry(
        seedValue = "6152438970124567",
        title = "هضبة أزهار الكرز والقرية المعلقة",
        edition = MinecraftEdition.BEDROCK,
        minMinor = 20,
        spawnBiome = "غابة الكرز (Cherry Grove)",
        spawnCoordinates = "X: 128, Y: 124, Z: 48",
        summary =
          "تتوزع بيوت القرية على حافة جبل مغطى بأزهار الكرز الوردية مع إطلالة بانورامية على وادي الأنهار، ويخترق الجبل نفق طبيعي مليء بالفحم والحديد.",
        highlights = listOf("قرية على جبل الكرز", "مناظر خلابة", "نفق موارد", "بناء إبداعي"),
        traits = setOf(SeedTrait.CHERRY, SeedTrait.VILLAGE, SeedTrait.MOUNTAIN, SeedTrait.RESOURCES),
      ),
      CuratedSeedEntry(
        seedValue = "-758493021654987",
        title = "معقل النهاية السريع وغرف المحاكمة",
        edition = MinecraftEdition.JAVA,
        minMinor = 21,
        spawnBiome = "سهول وتلال خضراء (Meadow)",
        spawnCoordinates = "X: -24, Y: 96, Z: 144",
        summary =
          "ممتاز لعشاق الإنجاز السريع والمغامرة؛ على بعد مسافة قصيرة تقع قرية بجانب فجوة عميقة تقود إلى غرف المحاكمة (Trial Chambers) وحصن النذر وبوابة النهاية.",
        highlights = listOf("غرف المحاكمة 1.21+", "موارد قتالية", "قرية قريبة", "تقدم سريع"),
        traits = setOf(SeedTrait.ANCIENT_CITY, SeedTrait.VILLAGE, SeedTrait.CAVE, SeedTrait.RESOURCES),
      ),
      CuratedSeedEntry(
        seedValue = "9182736450192834",
        title = "عش الجبال والغابة المطيرة",
        edition = MinecraftEdition.BOTH,
        minMinor = 14,
        spawnBiome = "أدغال الخيزران (Bamboo Jungle)",
        spawnCoordinates = "X: 60, Y: 88, Z: -180",
        summary =
          "أدغال كثيفة بالخيزران والكاكاو والبطيخ مع معبد أدغال مخفي بين الأشجار، وخلفه ترتفع جبال حجرية غنية بالزمرد والحديد.",
        highlights = listOf("أدغال وخيزران", "معبد أدغال", "زمرد في الجبال", "تنوع بيومي"),
        traits = setOf(SeedTrait.FOREST, SeedTrait.ANCIENT_CITY, SeedTrait.MOUNTAIN, SeedTrait.RESOURCES),
      ),
      CuratedSeedEntry(
        seedValue = "1029384756102938",
        title = "التلال الكلاسيكية ومنجم الحداد (1.7 – 1.12)",
        edition = MinecraftEdition.JAVA,
        minMinor = 0,
        spawnBiome = "تلال خضراء وقرية حدادين (Extreme Hills & Plains)",
        spawnCoordinates = "X: 48, Y: 84, Z: -36",
        summary =
          "بذرة كلاسيكية متوافقة مع الإصدارات القديمة وإصدارات المودات الشهيرة؛ توفر قرية حداد قريبة مع كهف مكشوف يصل سريعاً إلى طبقة الألماس (Y: 11) وبركة لافا جاهزة لبوابة النذر.",
        highlights = listOf("مثالي للإصدارات الكلاسيكية", "قرية حداد", "ألماس Y: 11", "لافا قريبة"),
        traits = setOf(SeedTrait.MOUNTAIN, SeedTrait.VILLAGE, SeedTrait.RESOURCES, SeedTrait.NETHER_PORTAL),
      ),
      CuratedSeedEntry(
        seedValue = "-4488221199337755",
        title = "وادي القرى والألماس السريع (1.8 – 1.16)",
        edition = MinecraftEdition.BOTH,
        minMinor = 0,
        spawnBiome = "سهول الغابات والأنهار (Plains & Forest)",
        spawnCoordinates = "X: -64, Y: 72, Z: 96",
        summary =
          "تضاريس كلاسيكية متوازنة تجمع بين سهول مفتوحة لبناء القواعد، غابة بلوط وفيرة، وأخدود سحيق يكشف خامات الحديد والذهب والألماس منذ الدقائق الأولى.",
        highlights = listOf("بداية سريعة", "أخدود غني بالخامات", "تضاريس كلاسيكية", "غابة وقرية"),
        traits = setOf(SeedTrait.FOREST, SeedTrait.RESOURCES, SeedTrait.CAVE, SeedTrait.VILLAGE),
      ),
    )

  fun suggestSeeds(
    wishes: String,
    quickTags: List<String>,
    edition: MinecraftEdition,
    version: String,
    excludedSeeds: List<String>,
  ): List<SuggestedSeed> {
    val excludedSet = excludedSeeds.toSet()
    val detectedTraits = detectTraits(wishes, quickTags)
    val minorVersion = MinecraftVersionCatalog.parseMinorVersion(version)

    val availableCurated =
      curatedCatalog.filter { entry ->
        val editionMatches =
          edition == MinecraftEdition.BOTH ||
            entry.edition == MinecraftEdition.BOTH ||
            entry.edition == edition
        val versionMatches = minorVersion == null || entry.minMinor <= minorVersion
        entry.seedValue !in excludedSet && editionMatches && versionMatches
      }

    val random = Random(System.nanoTime())
    val rankedCurated =
      availableCurated
        .map { entry ->
          val overlap =
            if (detectedTraits.isEmpty()) 1 else entry.traits.count { it in detectedTraits }
          entry to (overlap * 100 + random.nextInt(0, 60))
        }
        .filter { (entry, _) ->
          detectedTraits.isEmpty() || entry.traits.any { it in detectedTraits }
        }
        .sortedByDescending { it.second }
        .map { it.first }

    val results = mutableListOf<SuggestedSeed>()
    val chosenSeeds = HashSet(excludedSet)

    val curatedTakeCount = minOf(2, rankedCurated.size)
    for (i in 0 until curatedTakeCount) {
      val item = rankedCurated[i]
      chosenSeeds.add(item.seedValue)
      results.add(
        SuggestedSeed(
          seedValue = item.seedValue,
          title = item.title,
          edition = edition,
          minecraftVersion = version,
          spawnBiome = item.spawnBiome,
          spawnCoordinates = item.spawnCoordinates,
          summary = item.summary,
          highlights = item.highlights,
          imagePrompt = item.spawnBiome,
        )
      )
    }

    while (results.size < 3) {
      val candidate =
        generateProceduralSeed(
          traits = detectedTraits,
          wishes = wishes,
          edition = edition,
          version = version,
          minorVersion = minorVersion,
          excluded = chosenSeeds,
          random = random,
        )
      chosenSeeds.add(candidate.seedValue)
      results.add(candidate)
    }

    return results
  }

  private fun detectTraits(wishes: String, quickTags: List<String>): Set<SeedTrait> {
    val combined = "${wishes.lowercase()} ${quickTags.joinToString(" ").lowercase()}"
    val matched = mutableSetOf<SeedTrait>()
    for (trait in SeedTrait.entries) {
      if (trait.keywords.any { kw -> combined.contains(kw.lowercase()) }) {
        matched.add(trait)
      }
    }
    return matched
  }

  private fun generateProceduralSeed(
    traits: Set<SeedTrait>,
    wishes: String,
    edition: MinecraftEdition,
    version: String,
    minorVersion: Int?,
    excluded: Set<String>,
    random: Random,
  ): SuggestedSeed {
    var seedNum: Long
    var seedStr: String
    do {
      val high = random.nextLong(-8_999_999_999_999_999L, 8_999_999_999_999_999L)
      seedNum = if (abs(high) < 100_000L) high + 748_291_045L else high
      seedStr = seedNum.toString()
    } while (seedStr in excluded)

    val effectiveTraits =
      traits
        .filter { trait ->
          when (trait) {
            SeedTrait.CHERRY -> minorVersion == null || minorVersion >= 20
            else -> true
          }
        }
        .ifEmpty {
          if (traits.isNotEmpty()) listOf(SeedTrait.MOUNTAIN, SeedTrait.FOREST, SeedTrait.RESOURCES)
          else listOf(SeedTrait.MOUNTAIN, SeedTrait.RESOURCES, SeedTrait.VILLAGE, SeedTrait.CAVE)
        }

    val primaryTrait = effectiveTraits[abs((seedNum % effectiveTraits.size).toInt())]
    val secondaryTrait =
      effectiveTraits[abs(((seedNum / 7) % effectiveTraits.size).toInt())]

    val spawnBiome = pickBiomeForTrait(primaryTrait, seedNum, minorVersion)
    val x = ((seedNum % 320) - 160).toInt()
    val z = (((seedNum / 13) % 320) - 160).toInt()
    val isModernTerrain = minorVersion == null || minorVersion >= 18
    val y =
      when (primaryTrait) {
        SeedTrait.MOUNTAIN, SeedTrait.CHERRY ->
          if (isModernTerrain) 112 + abs((seedNum % 38).toInt())
          else 84 + abs((seedNum % 26).toInt())
        SeedTrait.ISLAND -> 64 + abs((seedNum % 8).toInt())
        else -> 68 + abs((seedNum % 24).toInt())
      }

    val title = buildTitle(primaryTrait, secondaryTrait, seedNum)
    val summary =
      buildSummary(
        primary = primaryTrait,
        secondary = secondaryTrait,
        wishes = wishes,
        biome = spawnBiome,
        x = x,
        z = z,
        seed = seedNum,
        version = version,
        minorVersion = minorVersion,
      )
    val highlights =
      (listOf(primaryTrait.arabicLabel, secondaryTrait.arabicLabel) +
          extraTagForSeed(seedNum, version, minorVersion))
        .distinct()
        .take(4)

    return SuggestedSeed(
      seedValue = seedStr,
      title = title,
      edition = edition,
      minecraftVersion = version,
      spawnBiome = spawnBiome,
      spawnCoordinates = "X: $x, Y: $y, Z: $z",
      summary = summary,
      highlights = highlights,
      imagePrompt = spawnBiome,
    )
  }

  private fun pickBiomeForTrait(trait: SeedTrait, seed: Long, minorVersion: Int?): String {
    val v = minorVersion ?: 21
    val options =
      when (trait) {
        SeedTrait.MOUNTAIN ->
          if (v >= 18) {
            listOf(
              "قمم متجمدة (Jagged Peaks)",
              "منحدرات ثلجية (Snowy Slopes)",
              "قمم صخرية شاهقة (Stony Peaks)",
              "مرج جبلي مرتفع (Meadow)",
            )
          } else {
            listOf(
              "تلال شاهقة صخرية (Extreme Hills)",
              "جبال ثلجية كلاسيكية (Snowy Mountains)",
              "هضاب صخرية مرتفعة (Gravelly Mountains)",
            )
          }
        SeedTrait.RESOURCES ->
          if (v >= 18) {
            listOf(
              "تلال الرياح الغنية بالزمرد (Windswept Hills)",
              "وديان صخرية مكشوفة (Stony Shore & Caves)",
              "جبال الحديد والفحم (Jagged Peaks)",
            )
          } else {
            listOf(
              "تلال الزمرد والفحم (Extreme Hills)",
              "أخدود الموارد السطحي (Ravine Valley)",
              "سهول المناجم المكشوفة (Mining Plains)",
            )
          }
        SeedTrait.VILLAGE ->
          listOf(
            "سهول القرى الخصبة (Plains)",
            if (v >= 7) "سهول السافانا (Savanna Plateau)" else "تلال القرى الخضراء (Forest Plains)",
            "تلال التايغا الثلجية (Snowy Taiga)",
          )
        SeedTrait.CAVE ->
          when {
            v >= 18 ->
              listOf(
                "كهوف اللوش العميقة (Lush Caves)",
                "كهوف الهوابط الصخرية (Dripstone Caves)",
                "فجوات الأميثيست السفلية (Deep Cavern)",
              )
            v == 17 ->
              listOf("فجوات الأميثيست السفلية (Amethyst Geode Cave)", "كهوف الهوابط (Dripstone)")
            else ->
              listOf("نظام الكهوف المترابطة (Classic Cavern)", "الأخدود السحيق (Deep Ravine)")
          }
        SeedTrait.CHERRY ->
          if (v >= 20) {
            listOf("بستان أزهار الكرز (Cherry Grove)", "جبال الكرز الوردية (Cherry Peaks)")
          } else {
            listOf("غابة الزهور الربيعية (Flower Forest)", "تلال الغابة الملونة (Forest Hills)")
          }
        SeedTrait.ISLAND ->
          listOf(
            "جزيرة محيطية نادرة (Ocean Island)",
            if (v >= 13) "شاطئ الشعاب المرجانية (Warm Ocean)" else "جزيرة البحر المفتوح (Deep Ocean Island)",
            "جزيرة الفطر المعزولة (Mushroom Fields)",
          )
        SeedTrait.ANCIENT_CITY ->
          if (v >= 19) {
            listOf("الأعماق الداكنة (Deep Dark)", "آثار الغابة القديمة (Old Growth Taiga)")
          } else {
            listOf("معقل البوابة والآثار (Stronghold Grounds)", "معبد الغابة القديمة (Jungle Temple)")
          }
        SeedTrait.NETHER_PORTAL ->
          if (v >= 16) {
            listOf("سهول البوابة المحطمة (Ruined Portal Plains)", "فوهة الحمم السطحية (Lava Basin)")
          } else {
            listOf("بركة اللافا السطحية لبوابة النذر (Surface Lava Pool)", "الأخدود البركاني (Lava Ravine)")
          }
        SeedTrait.FOREST ->
          listOf(
            if (v >= 7) "غابة البلوط الداكن (Dark Forest)" else "غابة البلوط الكثيفة (Oak Forest)",
            "أدغال الأنهار (Jungle Edge)",
            "غابة البتولا (Birch Valley)",
          )
        SeedTrait.DESERT ->
          listOf(
            "صحراء الكثبان الذهبية (Desert)",
            if (v >= 7) "أراضي الميسا الملونة (Badlands)" else "الواحة الصحراوية (Desert Oasis)",
          )
      }
    return options[abs((seed % options.size).toInt())]
  }

  private fun buildTitle(primary: SeedTrait, secondary: SeedTrait, seed: Long): String {
    val prefixes =
      when (primary) {
        SeedTrait.MOUNTAIN -> listOf("شواهق", "قمم", "مرتفعات", "جبال")
        SeedTrait.RESOURCES -> listOf("مناجم", "عروق", "كنوز", "خزائن")
        SeedTrait.VILLAGE -> listOf("ديار", "قرى", "مروج", "مستوطنة")
        SeedTrait.CAVE -> listOf("أعماق", "مغارات", "كهوف", "جوف")
        SeedTrait.CHERRY -> listOf("بساتين", "وادي", "تلال", "حدائق")
        SeedTrait.ISLAND -> listOf("جزر", "أرخبيل", "سواحل", "مرجان")
        SeedTrait.ANCIENT_CITY -> listOf("أطلال", "قلاع", "أسرار", "مدينة")
        SeedTrait.NETHER_PORTAL -> listOf("معبر", "بوابة", "لهيب", "حمم")
        SeedTrait.FOREST -> listOf("أدغال", "غابات", "ضفاف", "أنهار")
        SeedTrait.DESERT -> listOf("واحات", "أهرامات", "كثبان", "بادية")
      }
    val suffixes =
      when (secondary) {
        SeedTrait.RESOURCES -> listOf("الألماس والحديد", "الخامات النادرة", "المعادن الوفيرة")
        SeedTrait.MOUNTAIN -> listOf("السحاب والثلج", "الصخور الشاهقة", "القمم المتجمدة")
        SeedTrait.VILLAGE -> listOf("الحدادين والقرويين", "التجارة والاستقرار", "المزارع الخصبة")
        SeedTrait.CAVE -> listOf("الأميثيست المضيء", "الكهوف السحيقة", "الأنفاق المتصلة")
        SeedTrait.CHERRY -> listOf("أزهار الكرز", "الربيع الوردي", "الغابات المزهرة")
        SeedTrait.ISLAND -> listOf("المحيط الهادئ", "البحار المفتوحة", "الجزر النادرة")
        SeedTrait.ANCIENT_CITY -> listOf("الحضارة المفقودة", "الحصون القديمة", "الأعماق الصامتة")
        SeedTrait.NETHER_PORTAL -> listOf("النذر المحطمة", "الأوبسيديان واللافا", "الأبعاد المشتعلة")
        SeedTrait.FOREST -> listOf("الأنهار المتشعبة", "الأخشاب الوفيرة", "الطبيعة البكر")
        SeedTrait.DESERT -> listOf("المعابد الذهبية", "الرمال القديمة", "الكهوف الرملية")
      }
    val p = prefixes[abs((seed % prefixes.size).toInt())]
    val s = suffixes[abs(((seed / 11) % suffixes.size).toInt())]
    return "$p $s"
  }

  private fun buildSummary(
    primary: SeedTrait,
    secondary: SeedTrait,
    wishes: String,
    biome: String,
    x: Int,
    z: Int,
    seed: Long,
    version: String,
    minorVersion: Int?,
  ): String {
    val dir = listOf("الشمال الشرقي", "الجنوب الغربي", "الشمال الغربي", "الجنوب الشرقي")[abs((seed % 4).toInt())]
    val dist = 110 + abs((seed % 180).toInt())
    val wishNote =
      if (wishes.isNotBlank()) "مصمم ليناسب طلبك (\"${wishes.trim().take(50)}\"). "
      else ""
    val diamondHint =
      if (minorVersion == null || minorVersion >= 18) "طبقات الألماس العميقة (Y: -54)"
      else "طبقة الألماس الكلاسيكية (Y: 11)"
    val versionTag = MinecraftVersionCatalog.formatDisplay(version)
    return "${wishNote}ملائم لإصدار ($versionTag): تنطلق من بيوم $biome قرب الإحداثيات ($x, $z) مع تركيز واضح على ${primary.arabicLabel}. " +
      "عند الاتجاه نحو $dir لمسافة تقارب $dist بلوك ستجد مؤشرات قوية لـ ${secondary.arabicLabel} وسهولة الوصول إلى $diamondHint."
  }

  private fun extraTagForSeed(seed: Long, version: String, minorVersion: Int?): List<String> {
    val versionChip =
      when {
        minorVersion == null -> "كل الإصدارات"
        minorVersion >= 18 -> "تضاريس 1.18+"
        minorVersion >= 14 -> "إصدار $version"
        else -> "كلاسيك $version"
      }
    val pool =
      listOf(
        versionChip,
        "سيد فريد 64-bit",
        "بدون قيود",
        "تنوع تضاريس",
      )
    return listOf(versionChip, pool[abs((seed % pool.size).toInt())])
  }
}
