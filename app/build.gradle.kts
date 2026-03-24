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

    //todo signingconfig

    buildTypes { //todo
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

    // Hilt build optimization (reference-aligned).
// Purpose:
// - Enables Hilt's aggregating task mode, which can improve incremental build behavior
//   and reduce unnecessary annotation processing work in multi-module projects.
// - Build-time only setting; no runtime behavior change.
    hilt {
        enableAggregatingTask = true
    }

    //todo testoption

}

kotlin {
    compilerOptions {
        // Reference-aligned compiler flags:
        // disable selected Kotlin runtime assertions to reduce generated checks.
        // Tradeoff: fewer runtime guardrails during debugging.
        freeCompilerArgs.addAll(
            "-Xno-param-assertions",
            "-Xno-call-assertions",
            "-Xno-receiver-assertions",
        )
    }
}

// Compose stability analyzer plugin configuration.
// Purpose: helps detect unstable types/parameters that can trigger extra recompositions.
// This is a build-time analysis tool; it does not change app runtime behavior.
composeStabilityAnalyzer {
    enabled.set(true)
}

dependencies {


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)


    // cores
    implementation(projects.core.model) //todo check last whether model is needed
    implementation(projects.core.data)
    implementation(projects.core.designsystem)
    implementation(projects.core.navigation)

}