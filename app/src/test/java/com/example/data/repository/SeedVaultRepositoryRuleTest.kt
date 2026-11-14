package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.MinecraftEdition
import com.example.data.model.SuggestedSeed
import com.google.firebase.firestore.FirebaseFirestoreException
import java.util.UUID
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SeedVaultRepositoryRuleTest : FirestoreEmulatorTestBase() {

  private fun sampleSeed() =
    SuggestedSeed(
      seedValue = "-749102847561920",
      title = "جبال الزمرد المتجمدة",
      edition = MinecraftEdition.JAVA,
      minecraftVersion = "1.21.4",
      spawnBiome = "قمم متجمدة",
      spawnCoordinates = "X: 120, Y: 135, Z: -45",
      summary = "عالم غني بالحديد والألماس مع قرية قريبة.",
      highlights = listOf("رسبون جبلي", "وفرة الألماس", "قرية قريبة"),
      imagePrompt = "Snowy emerald mountain peaks at sunrise",
    )

  @Test
  fun saveSeed_authenticatedOwner_createsAndObservesDocument() = runBlocking {
    val uid = signInTestUser("alice_${UUID.randomUUID()}@test.com")
    val repository = SeedVaultRepository(firestore, auth)

    val saveResult =
      withTimeout(DEFAULT_TIMEOUT_MS) {
        repository.saveSeed(sampleSeed(), "رسبون في جبل مع موارد كثيرة")
      }
    assertTrue("Expected saveSeed to succeed: ${saveResult.exceptionOrNull()}", saveResult.isSuccess)
    val docId = saveResult.getOrThrow()
    assertTrue(docId.isNotEmpty())

    val seeds =
      withTimeout(FLOW_TIMEOUT_MS) {
        repository.observeSavedSeeds(uid).first { list -> list.any { it.id == docId } }
      }
    val saved = seeds.first { it.id == docId }
    assertEquals("-749102847561920", saved.seedValue)
    assertTrue(saved.isFavorite)

    val toggleResult = withTimeout(DEFAULT_TIMEOUT_MS) { repository.toggleFavorite(saved) }
    assertTrue(toggleResult.isSuccess)

    val updated = withTimeout(DEFAULT_TIMEOUT_MS) { repository.getSavedSeedById(docId).getOrThrow() }
    assertNotNull(updated)
    assertFalse(updated!!.isFavorite)

    val deleteResult = withTimeout(DEFAULT_TIMEOUT_MS) { repository.deleteSeed(docId) }
    assertTrue(deleteResult.isSuccess)
  }

  @Test
  fun crossUserAccess_isRejectedWithPermissionDenied() = runBlocking {
    val aliceUid = signInTestUser("alice_cross_${UUID.randomUUID()}@test.com")
    val aliceRepo = SeedVaultRepository(firestore, auth)
    val docId =
      withTimeout(DEFAULT_TIMEOUT_MS) {
        aliceRepo.saveSeed(sampleSeed(), "موارد كثيرة").getOrThrow()
      }

    signInTestUser("bob_cross_${UUID.randomUUID()}@test.com")
    val bobRepo = SeedVaultRepository(firestore, auth)

    var deniedError: FirebaseFirestoreException? = null
    try {
      withTimeout(FLOW_TIMEOUT_MS) { bobRepo.observeSavedSeeds(aliceUid).first() }
    } catch (e: Exception) {
      deniedError = e.unwrapFirestoreException()
    }
    assertNotNull("Expected PERMISSION_DENIED on cross-user observe", deniedError)
    assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, deniedError!!.code)

    // Also verify Bob cannot fetch Alice's document path directly
    var directGetError: FirebaseFirestoreException? = null
    try {
      withTimeout(DEFAULT_TIMEOUT_MS) {
        firestore
          .collection("users")
          .document(aliceUid)
          .collection("savedSeeds")
          .document(docId)
          .get()
          .await()
      }
    } catch (e: Exception) {
      directGetError = e.unwrapFirestoreException()
    }
    assertNotNull("Expected PERMISSION_DENIED on cross-user get", directGetError)
    assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, directGetError!!.code)
  }

  @Test
  fun unauthenticatedObserver_failsWithPermissionDenied() = runBlocking {
    auth.signOut()
    val repository = SeedVaultRepository(firestore, auth)

    var deniedError: FirebaseFirestoreException? = null
    try {
      withTimeout(FLOW_TIMEOUT_MS) { repository.observeSavedSeeds("some_user_123").first() }
    } catch (e: Exception) {
      deniedError = e.unwrapFirestoreException()
    }
    assertNotNull("Expected PERMISSION_DENIED when unauthenticated", deniedError)
    assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, deniedError!!.code)
  }

  private companion object {
    const val DEFAULT_TIMEOUT_MS = 5000L
    const val FLOW_TIMEOUT_MS = 3000L
  }
}
