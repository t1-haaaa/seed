package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.auth.attemptAutoSignIn
import com.example.auth.authStateFlow
import com.example.auth.onGoogleSignInClicked
import com.example.auth.signOutUser
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.SavedSeedsScreen
import com.example.ui.screens.SeedExplorerScreen
import com.example.ui.screens.SeedMapScreen
import com.example.ui.theme.SeedCraftTheme
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.SeedCraftViewModel
import com.example.ui.viewmodel.VaultUiState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    enableEdgeToEdge()

    setContent {
      SeedCraftTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
          AppNavigation()
        }
      }
    }
  }
}

@Composable
fun AppNavigation() {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val credentialManager = remember { CredentialManager.create(context) }
  val auth = remember { Firebase.auth }
  val currentUser by
    auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
  var hasSkippedAuth by rememberSaveable { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    if (auth.currentUser == null) {
      attemptAutoSignIn(
        context = context,
        credentialManager = credentialManager,
        onAuthSuccess = { hasSkippedAuth = false },
        onUnauthenticated = {},
        scope = scope,
      )
    }
  }

  val user = currentUser
  if (user == null && !hasSkippedAuth) {
    AuthScreen(
      onAuthSuccess = { hasSkippedAuth = false },
      onSkipAuth = { hasSkippedAuth = true },
    )
  } else {
    val uid = user?.uid
    key(uid ?: "guest") {
      MainFeatureShell(
        currentUserId = uid,
        userDisplayName = user?.displayName ?: user?.email ?: "زائر",
        onRequestGoogleSignIn = { onError ->
          onGoogleSignInClicked(
            context = context,
            credentialManager = credentialManager,
            onAuthSuccess = { hasSkippedAuth = false },
            onAuthError = onError,
            scope = scope,
          )
        },
        onSignOut = {
          signOutUser(credentialManager = credentialManager, scope = scope) {
            hasSkippedAuth = false
          }
        },
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainFeatureShell(
  currentUserId: String?,
  userDisplayName: String,
  onRequestGoogleSignIn: (onError: (String) -> Unit) -> Unit,
  onSignOut: () -> Unit,
) {
  val context = LocalContext.current
  val databaseId = remember { context.getString(R.string.firestore_database_id) }

  val viewModel: SeedCraftViewModel =
    viewModel(
      key = currentUserId ?: "guest",
      factory = SeedCraftViewModel.provideFactory(databaseId, currentUserId),
    )

  val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
  val explorerForm by viewModel.explorerForm.collectAsStateWithLifecycle()
  val suggestState by viewModel.seedSuggestState.collectAsStateWithLifecycle()
  val mapSeedText by viewModel.mapSeedText.collectAsStateWithLifecycle()
  val vaultState by viewModel.vaultState.collectAsStateWithLifecycle()
  val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()
  val authPromptReason by viewModel.authPromptReason.collectAsStateWithLifecycle()

  val savedSeedValues =
    remember(vaultState) {
      (vaultState as? VaultUiState.Success)?.seeds?.map { it.seedValue }?.toSet().orEmpty()
    }

  val snackbarHostState = remember { SnackbarHostState() }
  var showSignOutDialog by remember { mutableStateOf(false) }

  LaunchedEffect(snackbarMessage) {
    val msg = snackbarMessage
    if (msg != null) {
      snackbarHostState.showSnackbar(msg)
      viewModel.consumeSnackbar()
    }
  }

  BackHandler(enabled = activeTab != AppTab.EXPLORER) {
    viewModel.selectTab(AppTab.EXPLORER)
  }

  if (authPromptReason != null) {
    AlertDialog(
      onDismissRequest = viewModel::dismissAuthPrompt,
      title = { Text("تسجيل الدخول مطلوب") },
      text = { Text(authPromptReason!!) },
      confirmButton = {
        TextButton(
          onClick = {
            viewModel.dismissAuthPrompt()
            onRequestGoogleSignIn { err -> viewModel.notifyMessage(err) }
          }
        ) {
          Text("تسجيل الدخول بـ Google")
        }
      },
      dismissButton = {
        TextButton(onClick = viewModel::dismissAuthPrompt) { Text("لاحقاً") }
      },
    )
  }

  if (showSignOutDialog) {
    AlertDialog(
      onDismissRequest = { showSignOutDialog = false },
      title = { Text("تسجيل الخروج") },
      text = { Text("هل تريد تسجيل الخروج من حساب ($userDisplayName)؟") },
      confirmButton = {
        TextButton(
          onClick = {
            showSignOutDialog = false
            onSignOut()
          }
        ) {
          Text("خروج")
        }
      },
      dismissButton = {
        TextButton(onClick = { showSignOutDialog = false }) { Text("إلغاء") }
      },
    )
  }

  BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    val useNavigationRail = maxWidth >= 600.dp

    Scaffold(
      contentWindowInsets = WindowInsets.safeDrawing,
      snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
      topBar = {
        TopAppBar(
          title = {
            Text(
              text =
                when (activeTab) {
                  AppTab.EXPLORER -> "سيد كرافت • المكتشف"
                  AppTab.SEED_MAP -> "سيد كرافت • الخريطة"
                  AppTab.VAULT -> "سيد كرافت • خزنة البذور"
                },
              style = MaterialTheme.typography.titleLarge,
            )
          },
          actions = {
            if (currentUserId != null) {
              IconButton(
                onClick = { showSignOutDialog = true },
                modifier = Modifier.testTag("sign_out_button"),
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.Logout,
                  contentDescription = "تسجيل الخروج",
                )
              }
            } else {
              TextButton(
                onClick = { onRequestGoogleSignIn { err -> viewModel.notifyMessage(err) } },
                modifier = Modifier.testTag("header_sign_in_button"),
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.Login,
                  contentDescription = null,
                )
                Text(" تسجيل الدخول")
              }
            }
          },
          colors =
            TopAppBarDefaults.topAppBarColors(
              containerColor = MaterialTheme.colorScheme.background,
              titleContentColor = MaterialTheme.colorScheme.onBackground,
            ),
        )
      },
      bottomBar = {
        if (!useNavigationRail) {
          NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
            AppTab.entries.forEach { tab ->
              val selected = activeTab == tab
              NavigationBarItem(
                selected = selected,
                onClick = { viewModel.selectTab(tab) },
                icon = { TabIcon(tab = tab, selected = selected) },
                label = { Text(tabLabel(tab)) },
                modifier = Modifier.testTag("nav_${tab.name.lowercase()}"),
              )
            }
          }
        }
      },
    ) { innerPadding ->
      Row(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
        if (useNavigationRail) {
          NavigationRail(containerColor = MaterialTheme.colorScheme.surface) {
            AppTab.entries.forEach { tab ->
              val selected = activeTab == tab
              NavigationRailItem(
                selected = selected,
                onClick = { viewModel.selectTab(tab) },
                icon = { TabIcon(tab = tab, selected = selected) },
                label = { Text(tabLabel(tab)) },
                modifier = Modifier.testTag("rail_${tab.name.lowercase()}"),
              )
            }
          }
        }

        Box(modifier = Modifier.weight(1f).fillMaxSize()) {
          when (activeTab) {
            AppTab.EXPLORER ->
              SeedExplorerScreen(
                formState = explorerForm,
                suggestState = suggestState,
                savedSeedValues = savedSeedValues,
                onWishesChange = viewModel::updateWishesText,
                onToggleTag = viewModel::toggleQuickTag,
                onSelectEdition = viewModel::selectEdition,
                onSelectVersion = viewModel::selectVersion,
                onGenerateSeeds = viewModel::generateSeeds,
                onSaveSeed = viewModel::saveSuggestedSeed,
                onOpenMap = { seed -> viewModel.openSeedInMap(seed.seedValue) },
                onCopiedSeed = { seed -> viewModel.notifyMessage("تم نسخ السيد: $seed") },
              )

            AppTab.SEED_MAP ->
              SeedMapScreen(
                seedText = mapSeedText,
                onSeedTextChange = viewModel::updateMapSeedText,
                onCopiedSeed = { seed -> viewModel.notifyMessage("تم نسخ السيد: $seed") },
              )

            AppTab.VAULT ->
              SavedSeedsScreen(
                vaultState = vaultState,
                onToggleFavorite = viewModel::toggleFavorite,
                onDeleteSeed = viewModel::deleteSavedSeed,
                onOpenMap = { seed -> viewModel.openSeedInMap(seed.seedValue) },
                onCopiedSeed = { seed -> viewModel.notifyMessage("تم نسخ السيد: $seed") },
                onGoToExplorer = { viewModel.selectTab(AppTab.EXPLORER) },
                onRequestSignIn = {
                  onRequestGoogleSignIn { err -> viewModel.notifyMessage(err) }
                },
              )
          }
        }
      }
    }
  }
}

@Composable
private fun TabIcon(tab: AppTab, selected: Boolean) {
  val icon =
    when (tab) {
      AppTab.EXPLORER -> if (selected) Icons.Filled.Explore else Icons.Outlined.Explore
      AppTab.SEED_MAP -> if (selected) Icons.Filled.Map else Icons.Outlined.Map
      AppTab.VAULT -> if (selected) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder
    }
  Icon(imageVector = icon, contentDescription = tabLabel(tab))
}

private fun tabLabel(tab: AppTab): String =
  when (tab) {
    AppTab.EXPLORER -> "المكتشف"
    AppTab.SEED_MAP -> "الخريطة"
    AppTab.VAULT -> "الخزنة"
  }
