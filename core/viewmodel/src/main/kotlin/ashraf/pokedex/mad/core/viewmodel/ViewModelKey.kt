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
package ashraf.pokedex.mad.core.viewmodel

/**
 * Key used to distinguish state flows for different ViewModel types.
 *
 * Why this exists:
 * - When you keep multiple ViewModels alive (e.g. in Navigation back stack),
 *   you want a stable identifier per ViewModel type/class.
 * - The custom [ViewModelStateFlow] stores a map internally keyed by this value,
 *   so only the correct state slice is exposed for a given ViewModel instance/type.
 */
data class ViewModelKey(val key: String)
