plugins {
    id("ashraf.pokedex.mad.android.library")
    id("ashraf.pokedex.mad.spotless")
}

android {
    namespace = "ashraf.pokedex.mad.core.viewmodel"
}

dependencies {
    // Shared ViewModel dependency for Compose feature modules.
    api(libs.androidx.lifecycle.viewModelCompose)
}