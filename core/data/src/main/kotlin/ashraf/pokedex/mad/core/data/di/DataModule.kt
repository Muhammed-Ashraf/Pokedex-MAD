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
package ashraf.pokedex.mad.core.data.di

import ashraf.pokedex.mad.core.data.repository.details.DetailsRepository
import ashraf.pokedex.mad.core.data.repository.details.DetailsRepositoryImpl
import ashraf.pokedex.mad.core.data.repository.home.HomeRepository
import ashraf.pokedex.mad.core.data.repository.home.HomeRepositoryImpl
import ashraf.pokedex.mad.core.data.repository.userdata.UserDataRepository
import ashraf.pokedex.mad.core.data.repository.userdata.UserDataRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface DataModule {
  @Binds
  @Singleton
  fun bindHomeRepository(impl: HomeRepositoryImpl): HomeRepository

  @Binds
  @Singleton
  fun bindDetailsRepository(impl: DetailsRepositoryImpl): DetailsRepository

  @Binds
  fun bindsUserDataRepository(userDataRepositoryImpl: UserDataRepositoryImpl): UserDataRepository
}
