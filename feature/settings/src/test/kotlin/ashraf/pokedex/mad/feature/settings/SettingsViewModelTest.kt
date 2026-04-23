package ashraf.pokedex.mad.feature.settings

import ashraf.pokedex.mad.core.data.repository.userdata.UserDataRepository
import ashraf.pokedex.mad.core.model.UiTheme
import ashraf.pokedex.mad.core.model.UserData
import ashraf.pokedex.mad.core.test.MainCoroutinesRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SettingsViewModelTest {

  @get:Rule
  val coroutinesRule = MainCoroutinesRule()

  @Test
  fun uiState_becomes_success_with_user_data_from_repository() =
    coroutinesRule.testScope.runTest {
      val userData = UserData(uiTheme = UiTheme.FOLLOW_SYSTEM)
      val stub = StubUserDataRepository(initialUserData = userData)
      val viewModel = SettingsViewModel(userDataRepository = stub)

      backgroundScope.launch { viewModel.uiState.collect {} }

      val state = viewModel.uiState.value
      assertTrue(state is SettingsUiState.Success)
      assertEquals(userData, (state as SettingsUiState.Success).userData)
    }

  @Test
  fun setUiTheme_updates_repository_and_ui_state() =
    coroutinesRule.testScope.runTest {
      val stub = StubUserDataRepository(initialUserData = UserData(UiTheme.FOLLOW_SYSTEM))
      val viewModel = SettingsViewModel(userDataRepository = stub)

      backgroundScope.launch { viewModel.uiState.collect {} }

      viewModel.setUiTheme(UiTheme.DARK)

      assertEquals(listOf(UiTheme.DARK), stub.appliedThemes)
      val state = viewModel.uiState.value
      assertTrue(state is SettingsUiState.Success)
      assertEquals(UiTheme.DARK, (state as SettingsUiState.Success).userData.uiTheme)
    }

  /**
   * Original [SettingsViewModel] uses `.catch { SettingsUiState.Error(it.message) }` without
   * `emit(...)`, so failures are not forwarded as [SettingsUiState.Error] and the shared
   * [kotlinx.coroutines.flow.StateFlow] keeps the [SettingsUiState.Loading] initial value.
   */
  @Test
  fun uiState_stays_loading_when_user_data_fails_before_emit() =
    coroutinesRule.testScope.runTest {
      val repository =
        object : UserDataRepository {
          override val userData: Flow<UserData> =
            flow<UserData> {
              throw IllegalStateException("preferences unavailable")
            }

          override suspend fun setUiTheme(uiTheme: UiTheme) = Unit
        }
      val viewModel = SettingsViewModel(userDataRepository = repository)

      backgroundScope.launch { viewModel.uiState.collect {} }

      assertTrue(viewModel.uiState.value is SettingsUiState.Loading)
    }

  private class StubUserDataRepository(
    initialUserData: UserData,
  ) : UserDataRepository {
    private val mutableUserData = MutableStateFlow(initialUserData)
    override val userData: Flow<UserData> = mutableUserData.asStateFlow()

    val appliedThemes = mutableListOf<UiTheme>()

    override suspend fun setUiTheme(uiTheme: UiTheme) {
      appliedThemes += uiTheme
      mutableUserData.value = UserData(uiTheme = uiTheme)
    }
  }
}
