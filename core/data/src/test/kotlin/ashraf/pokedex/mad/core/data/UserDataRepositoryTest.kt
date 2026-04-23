package ashraf.pokedex.mad.core.data

import androidx.datastore.core.DataStore
import ashraf.pokedex.mad.core.data.repository.userdata.UserDataRepositoryImpl
import ashraf.pokedex.mad.core.datastore.PreferencesDataSource
import ashraf.pokedex.mad.core.datastore.UserPreferences
import ashraf.pokedex.mad.core.model.UiTheme
import ashraf.pokedex.mad.core.model.UserData
import ashraf.pokedex.mad.core.test.MainCoroutinesRule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UserDataRepositoryTest {

  private lateinit var repository: UserDataRepositoryImpl
  private lateinit var preferencesDataSource: PreferencesDataSource

  @get:Rule
  val coroutinesRule = MainCoroutinesRule()

  @Before
  fun setup() {
    preferencesDataSource = PreferencesDataSource(
      userPreferences = InMemoryDataStore(UserPreferences.getDefaultInstance()),
    )
    repository = UserDataRepositoryImpl(preferencesDataSource, coroutinesRule.testDispatcher)
  }

  @Test
  fun default_user_data_is_correct() = runTest {
    assertEquals(
        UserData(uiTheme = UiTheme.FOLLOW_SYSTEM),
      repository.userData.first(),
    )
  }

  @Test
  fun set_ui_theme_to_preferences() = runTest {
    repository.setUiTheme(uiTheme = UiTheme.DARK)

    assertEquals(
      UiTheme.DARK,
      repository.userData
        .map { it.uiTheme }
        .first(),
    )
    assertEquals(
      UiTheme.DARK,
      preferencesDataSource.userData
        .map { it.uiTheme }
        .first(),
    )
  }
}

class InMemoryDataStore<T>(initialValue: T) : DataStore<T> {
  override val data = MutableStateFlow(initialValue)
  override suspend fun updateData(transform: suspend (T) -> T): T =
    data.updateAndGet { transform(it) }
}