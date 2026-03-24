plugins {
    id("ashraf.pokedex.mad.android.library")
    id("ashraf.pokedex.mad.android.library.compose")
    id("ashraf.pokedex.mad.spotless")
}

android {
    namespace = "ashraf.pokedex.mad.core.designsystem"
}

dependencies {

//todo    // image loading
//    api(libs.landscapist.image)
//    api(libs.landscapist.animation)
//    api(libs.landscapist.placeholder)
//    api(libs.landscapist.palette)
//
//  todo  // splash screen
//    api(libs.androidx.core.splashscreen)

    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.ui.tooling.preview)
}