package ashraf.pokedx.mad.core.viewmodel

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