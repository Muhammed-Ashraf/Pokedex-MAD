plugins {
    id("ashraf.pokedex.mad.android.feature")
    id("ashraf.pokedex.mad.android.hilt")
}

android {
    namespace = "ashraf.pokedex.mad.feature.settings"
}

dependencies {
    testImplementation(projects.core.test)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.junit)
}