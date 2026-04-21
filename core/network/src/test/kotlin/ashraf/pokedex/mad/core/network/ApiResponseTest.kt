package ashraf.pokedex.mad.core.network

import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.SandwichInitializer
import com.skydoves.sandwich.retrofit.responseOf
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Response

@RunWith(JUnit4::class) // Tells JUnit to run this test class using JUnit4
class ApiResponseTest {

  @Test
  fun exception() {
    // Create a normal Exception with message "foo"
    val exception = Exception("foo")

    // Wrap the exception into ApiResponse (this becomes ApiResponse.Exception internally)
    val apiResponse = ApiResponse.exception(exception)

    // Verify that the message inside ApiResponse is preserved correctly
    assertThat(apiResponse.message, `is`("foo"))
  }

  @Test
  fun success() {

    // Create an ApiResponse from a Retrofit Response
    val apiResponse =
      ApiResponse.responseOf(SandwichInitializer.successCodeRange) {

        // This creates a successful Retrofit Response
        // IMPORTANT: "foo" is stored inside Response.body()
        Response.success("foo")
      }

    // Check if the result is a Success type
    if (apiResponse is ApiResponse.Success) {

      // Inside Sandwich library, this happens internally:
      // response.body() → "foo"
      // ApiResponse.Success(response.body())
      //
      // So apiResponse.data = "foo"

      // Verify that the data inside ApiResponse is "foo"
      assertThat(apiResponse.data, `is`("foo"))
    }
  }
}