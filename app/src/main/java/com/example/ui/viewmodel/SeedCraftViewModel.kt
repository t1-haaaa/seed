package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.data.model.MinecraftEdition
import com.example.data.model.MinecraftVersionCatalog
import com.example.data.model.SavedSeed
import com.example.data.model.SuggestedSeed
import com.example.data.repository.LocalSeedEngine
import com.example.data.repository.SeedVaultRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppTab {
  EXPLORER,
  SEED_MAP,
  VAULT,
}

sealed interface SeedSuggestUiState {
  data object Idle : SeedSuggestUiState

  data class Success(val seeds: List<SuggestedSeed>) : SeedSuggestUiState
}

sealed interface VaultUiState {
  data object RequiresAuth : VaultUiState

  data object Loading : VaultUiState

  data class Success(val seeds: List<SavedSeed>) : VaultUiState

  data class Error(val message: String) : VaultUiState
}

data class ExplorerFormState(
  val wishesText: String = "",
  val selectedTags: Set<String> = emptySet(),
  val selectedEdition: MinecraftEdition = MinecraftEdition.JAVA,
  val selectedVersion: String = MinecraftVersionCatalog.ALL_VERSIONS,
  val excludedSeeds: List<String> = emptyList(),
  val savingSeedValue: String? = null,
)

class SeedCraftViewModel(
  private val vaultRepository: SeedVaultRepository?,
  private val seedEngine: LocalSeedEngine,
  private val currentUserId: String?,
) : ViewModel() {

  private val _activeTab = MutableStateFlow(AppTab.EXPLORER)
  val activeTab: StateFlow<AppTab> = _activeTab.asStateFlow()

  private val _explorerForm = MutableStateFlow(ExplorerFormState())
  val explorerForm: StateFlow<ExplorerFormState> = _explorerForm.asStateFlow()

  private val _seedSuggestState = MutableStateFlow<SeedSuggestUiState>(SeedSuggestUiState.Idle)
  val seedSuggestState: StateFlow<SeedSuggestUiState> = _seedSuggestState.asStateFlow()

  private val _mapSeedText = MutableStateFlow("-6842190574312088")
  val mapSeedText: StateFlow<String> = _mapSeedText.asStateFlow()

  private val _snackbarMessage = MutableStateFlow<String?>(null)
  val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

  private val _authPromptReason = MutableStateFlow<String?>(null)
  val authPromptReason: StateFlow<String?> = _authPromptReason.asStateFlow()

  // Strictly gated behind currentUserId so zero Firestore queries run in Guest mode
  val vaultState: StateFlow<VaultUiState> =
    if (currentUserId == null || vaultRepository == null) {
      MutableStateFlow<VaultUiState>(VaultUiState.RequiresAuth).asStateFlow()
    } else {
      vaultRepository
        .observeSavedSeeds(currentUserId)
        .map<List<SavedSeed>, VaultUiState> { seeds -> VaultUiState.Success(seeds) }
        .catch { error ->
          val firestoreError =
            generateSequence(error) { it.cause }
              .filterIsInstance<FirebaseFirestoreException>()
              .firstOrNull()
          if (firestoreError == null && error is CancellationException) throw error
          val rootError = firestoreError ?: error
          Log.w(TAG, "Error observing saved seeds", rootError)
          emit(VaultUiState.Error(rootError.localizedMessage ?: "تعذر تحميل البذور المحفوظة."))
        }
        .stateIn(
          scope = viewModelScope,
          started = SharingStarted.WhileSubscribed(5000L),
          initialValue = VaultUiState.Loading,
        )
    }

  fun selectTab(tab: AppTab) {
    _activeTab.value = tab
  }

  fun updateWishesText(text: String) {
    _explorerForm.update { it.copy(wishesText = text) }
  }

  fun toggleQuickTag(tag: String) {
    _explorerForm.update { state ->
      val updated =
        if (tag in state.selectedTags) state.selectedTags - tag else state.selectedTags + tag
      state.copy(selectedTags = updated)
    }
  }

  fun selectEdition(edition: MinecraftEdition) {
    _explorerForm.update { it.copy(selectedEdition = edition) }
  }

  fun selectVersion(version: String) {
    val clean = version.trim().ifEmpty { MinecraftVersionCatalog.ALL_VERSIONS }.take(32)
    _explorerForm.update { it.copy(selectedVersion = clean) }
  }

  fun generateSeeds() {
    val form = _explorerForm.value
    if (form.wishesText.isBlank() && form.selectedTags.isEmpty()) {
      _snackbarMessage.value = "اكتب رغباتك أو اختر وسماً واحداً على الأقل"
      return
    }

    val vaultSeedValues =
      (vaultState.value as? VaultUiState.Success)?.seeds?.map { it.seedValue }.orEmpty()
    val allExcluded = (form.excludedSeeds + vaultSeedValues).distinct()

    val suggestions =
      seedEngine.suggestSeeds(
        wishes = form.wishesText,
        quickTags = form.selectedTags.toList(),
        edition = form.selectedEdition,
        version = form.selectedVersion,
        excludedSeeds = allExcluded,
      )

    _explorerForm.update { current ->
      current.copy(
        excludedSeeds = (current.excludedSeeds + suggestions.map { it.seedValue }).takeLast(200)
      )
    }
    _seedSuggestState.value = SeedSuggestUiState.Success(suggestions)
  }

  fun saveSuggestedSeed(seed: SuggestedSeed) {
    if (currentUserId == null || vaultRepository == null) {
      _authPromptReason.value =
        "ميزة حفظ البذور وتعيينها كمفضلة متاحة فقط بعد تسجيل الدخول بحساب Google."
      return
    }
    val wishSummary =
      buildString {
        val form = _explorerForm.value
        if (form.wishesText.isNotBlank()) append(form.wishesText.trim())
        if (form.selectedTags.isNotEmpty()) {
          if (isNotEmpty()) append(" • ")
          append(form.selectedTags.joinToString("، "))
        }
      }
        .ifBlank { seed.title }

    viewModelScope.launch {
      _explorerForm.update { it.copy(savingSeedValue = seed.seedValue) }
      val result = vaultRepository.saveSeed(seed, wishSummary)
      _explorerForm.update { it.copy(savingSeedValue = null) }
      result
        .onSuccess { _snackbarMessage.value = "تم حفظ السيد في خزنتك السحابية" }
        .onFailure { err ->
          _snackbarMessage.value = err.localizedMessage ?: "تعذر حفظ السيد."
        }
    }
  }

  fun toggleFavorite(seed: SavedSeed) {
    if (currentUserId == null || vaultRepository == null) {
      _authPromptReason.value =
        "تعديل المفضلة وحفظ البذور يتطلب تسجيل الدخول بحساب Google."
      return
    }
    viewModelScope.launch {
      vaultRepository.toggleFavorite(seed).onFailure { err ->
        _snackbarMessage.value = err.localizedMessage ?: "تعذر تحديث المفضلة."
      }
    }
  }

  fun deleteSavedSeed(seedId: String) {
    if (currentUserId == null || vaultRepository == null) {
      _authPromptReason.value = "إدارة البذور المحفوظة تتطلب تسجيل الدخول بحساب Google."
      return
    }
    viewModelScope.launch {
      vaultRepository
        .deleteSeed(seedId)
        .onSuccess { _snackbarMessage.value = "تم حذف السيد من الخزنة" }
        .onFailure { err ->
          _snackbarMessage.value = err.localizedMessage ?: "تعذر حذف السيد."
        }
    }
  }

  fun dismissAuthPrompt() {
    _authPromptReason.value = null
  }

  fun openSeedInMap(seedValue: String) {
    _mapSeedText.value = seedValue
    _activeTab.value = AppTab.SEED_MAP
  }

  fun updateMapSeedText(seedValue: String) {
    _mapSeedText.value = seedValue
  }

  fun notifyMessage(msg: String) {
    _snackbarMessage.value = msg
  }

  fun consumeSnackbar() {
    _snackbarMessage.value = null
  }

  companion object {
    private const val TAG = "SeedCraftVM"

    fun provideFactory(
      databaseId: String,
      currentUserId: String?,
    ): ViewModelProvider.Factory = viewModelFactory {
      initializer {
        val vaultRepo =
          if (currentUserId != null) {
            SeedVaultRepository(FirebaseFirestore.getInstance(databaseId))
          } else {
            null
          }
        SeedCraftViewModel(
          vaultRepository = vaultRepo,
          seedEngine = LocalSeedEngine(),
          currentUserId = currentUserId,
        )
      }
    }
  }
}
