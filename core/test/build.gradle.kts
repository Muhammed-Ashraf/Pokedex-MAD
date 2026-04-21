plugins {
    id("ashraf.pokedex.mad.android.library")
    id("ashraf.pokedex.mad.spotless")
}
android { namespace = "ashraf.pokedex.mad.core.test" }

dependencies {
    // Domain types (Pokemon, UserData, etc.) used by test fakes and MockUtil helpers.
    // compileOnly would hide symbols from test sources; implementation is correct for a shared test module.
    implementation(projects.core.model)

    // Main dispatcher on Android; keeps behavior aligned with app modules that use Dispatchers.Main.
    // MainCoroutinesRule uses setMain(...) from the test artifact; this dependency matches the reference setup.
    implementation(libs.kotlinx.coroutines.android)

    // TestCoroutineScheduler, runTest, TestDispatcher, Dispatchers.setMain / resetMain for JVM/unit tests.
    implementation(libs.kotlinx.coroutines.test)

    // JUnit 4 rules (e.g. @get:Rule MainCoroutinesRule : TestWatcher) and @Test annotations used by core:* tests.
    implementation(libs.junit)
}