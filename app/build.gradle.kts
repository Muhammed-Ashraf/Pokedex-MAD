plugins {
    id("ashraf.pokedex.mad.android.application")
    id("ashraf.pokedex.mad.android.application.compose")
    id("ashraf.pokedex.mad.android.hilt")
    id("ashraf.pokedex.mad.spotless")
}

android {
    namespace = "ashraf.pokedex.mad"

    defaultConfig {
        applicationId = "ashraf.pokedex.mad"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        buildConfig = true
    }

}

// Compose stability analyzer plugin configuration.
// Purpose: helps detect unstable types/parameters that can trigger extra recompositions.
// This is a build-time analysis tool; it does not change app runtime behavior.
composeStabilityAnalyzer {
    enabled.set(true)
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)


    // cores
    implementation(projects.core.model)
    implementation(projects.core.data)

}