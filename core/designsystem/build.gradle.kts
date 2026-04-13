plugins {
    id("ashraf.pokedex.mad.android.library")
    id("ashraf.pokedex.mad.android.library.compose")
    id("ashraf.pokedex.mad.spotless")
}

android {
    namespace = "ashraf.pokedex.mad.core.designsystem"
}

dependencies {


    api(libs.landscapist.image)
    api(libs.landscapist.animation)
    api(libs.landscapist.placeholder)
    api(libs.landscapist.palette)
//

    api(libs.androidx.core.splashscreen)

    api(platform(libs.androidx.compose.bom))


    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.tooling)
    api(libs.androidx.compose.ui.tooling.preview)
    api(libs.androidx.compose.animation)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.foundation.layout)
}