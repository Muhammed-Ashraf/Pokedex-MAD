plugins {
    id("ashraf.pokedex.mad.android.library")
    id("ashraf.pokedex.mad.android.library.compose")
    id("ashraf.pokedex.mad.spotless")
}

android {
    namespace = "ashraf.pokedex.mad.core.designsystem"
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
}