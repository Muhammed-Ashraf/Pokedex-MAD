package ashraf.pokedex.mad.core.data.repository.userdata

import ashraf.pokedex.mad.core.model.UiTheme
import ashraf.pokedex.mad.core.model.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeUserDataRepository : UserDataRepository {
  override val userData: Flow<UserData> = flowOf(
    UserData(uiTheme = UiTheme.FOLLOW_SYSTEM),
  )

  override suspend fun setUiTheme(uiTheme: UiTheme) {
  }
}