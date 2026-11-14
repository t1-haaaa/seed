package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.MinecraftEdition
import com.example.data.model.SavedSeed
import com.example.data.model.SuggestedSeed
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class SeedVaultRepository(
  private val db: FirebaseFirestore,
  private val auth: FirebaseAuth = Firebase.auth,
) {
  constructor(
    context: Context
  ) : this(
    FirebaseFirestore.getInstance(
      context.applicationContext.getString(R.string.firestore_database_id)
    ),
    Firebase.auth,
  )

  private fun requireUserId(): String {
    return auth.currentUser?.uid
      ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
  }

  fun observeSavedSeeds(userId: String): Flow<List<SavedSeed>> = flow {
    val path = "users/$userId/savedSeeds"
    emitAll(
      db.collection("users")
        .document(userId)
        .collection("savedSeeds")
        .snapshots()
        .map { snapshot ->
          snapshot.documents
            .mapNotNull { doc ->
              doc.toObject(
                SavedSeed::class.java,
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE,
              )
            }
            .sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
        }
        .catch { error ->
          val firestoreError =
            generateSequence(error) { it.cause }
              .filterIsInstance<FirebaseFirestoreException>()
              .firstOrNull()
          if (firestoreError == null && error is CancellationException) throw error
          val exceptionToLog = firestoreError ?: (error as? Exception)
          if (exceptionToLog != null && auth.currentUser?.uid == userId) {
            handleFirestoreError(exceptionToLog, OperationType.LIST, path, auth)
          }
          throw error
        }
    )
  }

  suspend fun saveSeed(seed: SuggestedSeed, userWishPrompt: String): Result<String> {
    val uid =
      try {
        requireUserId()
      } catch (e: Exception) {
        return Result.failure(e)
      }
    val docRef = db.collection("users").document(uid).collection("savedSeeds").document()
    val payload =
      mapOf(
        "userId" to uid,
        "seedValue" to seed.seedValue.trim().ifEmpty { "0" }.take(100),
        "title" to seed.title.trim().ifEmpty { "عالم ماينكرافت" }.take(120),
        "edition" to
          when (seed.edition) {
            MinecraftEdition.JAVA -> "java"
            MinecraftEdition.BEDROCK -> "bedrock"
            MinecraftEdition.BOTH -> "both"
          },
        "minecraftVersion" to seed.minecraftVersion.trim().ifEmpty { "1.21.4" }.take(32),
        "spawnBiome" to seed.spawnBiome.trim().take(80),
        "spawnCoordinates" to seed.spawnCoordinates.trim().take(64),
        "summary" to seed.summary.trim().take(1000),
        "userWishPrompt" to userWishPrompt.trim().take(500),
        "highlights" to seed.highlights.map { it.trim().take(80) }.filter { it.isNotEmpty() }.take(10),
        "isFavorite" to true,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp(),
      )
    return try {
      docRef.set(payload).await()
      Result.success(docRef.id)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, docRef.path, auth)
      Result.failure(e)
    }
  }

  suspend fun getSavedSeedById(seedId: String): Result<SavedSeed?> {
    val uid =
      try {
        requireUserId()
      } catch (e: Exception) {
        return Result.failure(e)
      }
    val docRef = db.collection("users").document(uid).collection("savedSeeds").document(seedId)
    return try {
      val snapshot = docRef.get().await()
      val item =
        snapshot.toObject(
          SavedSeed::class.java,
          DocumentSnapshot.ServerTimestampBehavior.ESTIMATE,
        )
      Result.success(item)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.GET, docRef.path, auth)
      Result.failure(e)
    }
  }

  suspend fun toggleFavorite(seed: SavedSeed): Result<Unit> {
    val uid =
      try {
        requireUserId()
      } catch (e: Exception) {
        return Result.failure(e)
      }
    require(seed.id.isNotBlank()) { "SavedSeed ID must not be blank for update" }
    val docRef = db.collection("users").document(uid).collection("savedSeeds").document(seed.id)
    return try {
      docRef
        .update(
          mapOf(
            "isFavorite" to !seed.isFavorite,
            "updatedAt" to FieldValue.serverTimestamp(),
          )
        )
        .await()
      Result.success(Unit)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, docRef.path, auth)
      Result.failure(e)
    }
  }

  suspend fun deleteSeed(seedId: String): Result<Unit> {
    val uid =
      try {
        requireUserId()
      } catch (e: Exception) {
        return Result.failure(e)
      }
    require(seedId.isNotBlank()) { "SavedSeed ID must not be blank for delete" }
    val docRef = db.collection("users").document(uid).collection("savedSeeds").document(seedId)
    return try {
      docRef.delete().await()
      Result.success(Unit)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.DELETE, docRef.path, auth)
      Result.failure(e)
    }
  }
}
