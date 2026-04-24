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

import kotlin.RequiresOptIn

/**
 * Marks a method/property as "restricted API".
 *
 * Purpose:
 * - This is used to prevent outside code (other than the ViewModel implementation)
 *   from calling certain `MutableStateFlow` methods directly.
 *
 * In this project, the goal is to:
 * - allow only ViewModels to emit/update state correctly
 * - keep the rest of the code from accidentally mutating state flows.
 */
@RequiresOptIn(
  message = "This API has been restricted. Do not depend on this API for working properly",
  level = RequiresOptIn.Level.ERROR,
)
@Target(
  AnnotationTarget.CLASS,
  AnnotationTarget.PROPERTY,
  AnnotationTarget.CONSTRUCTOR,
  AnnotationTarget.FUNCTION,
  AnnotationTarget.TYPEALIAS,
)
@Retention(AnnotationRetention.BINARY)
public annotation class RestrictedApi
