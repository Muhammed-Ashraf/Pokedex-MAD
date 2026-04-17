package ashraf.pokedex.mad.core.data.repository.userdata

import ashraf.pokedex.mad.core.model.UiTheme
import ashraf.pokedex.mad.core.model.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {

    val userData: Flow<UserData>

    suspend fun setUiTheme(uiTheme: UiTheme)
}
