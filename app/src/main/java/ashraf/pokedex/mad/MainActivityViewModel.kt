/*
 * Designed and developed for Pokedex-MAD (learning project)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ashraf.pokedex.mad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ashraf.pokedex.mad.core.data.repository.userdata.UserDataRepository
import ashraf.pokedex.mad.core.model.UiTheme
import ashraf.pokedex.mad.core.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
  userDataRepository: UserDataRepository,
) : ViewModel() {

  val userData = userDataRepository.userData
    .map(MainActivityUiState::Success)
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = MainActivityUiState.Loading,
    )
}

sealed interface MainActivityUiState {
  data object Loading : MainActivityUiState
  data class Success(val userData: UserData) : MainActivityUiState {
    fun shouldUseDarkTheme(isSystemDarkTheme: Boolean): Boolean = when (userData.uiTheme) {
      UiTheme.FOLLOW_SYSTEM -> isSystemDarkTheme
      UiTheme.DARK -> true
      UiTheme.LIGHT -> false
    }
  }
}

fun MainActivityUiState.shouldKeepSplashScreen() = this is MainActivityUiState.Loading

// Extension function on MainActivityUiState
// This means it can be called like: uiState.shouldUseDarkTheme(...)
fun MainActivityUiState.shouldUseDarkTheme(isSystemDarkTheme: Boolean): Boolean =

// 'this' refers to the object on which this function is called
// Example:
// if uiState = Loading      → this = Loading
  // if uiState = Success(...) → this = Success(...)
  when (this) {
    // Case 1: UI state is still loading
    // No user preference available yet
    // So fallback to system theme
    MainActivityUiState.Loading -> isSystemDarkTheme

    // Case 2: UI state has loaded successfully
    // Now we have userData available inside Success
    is MainActivityUiState.Success ->

      // Delegate decision to the Success object itself
      // It uses userData.uiTheme to decide dark/light/system
      shouldUseDarkTheme(isSystemDarkTheme = isSystemDarkTheme)
  }
