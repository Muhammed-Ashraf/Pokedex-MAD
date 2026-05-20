plugins {
    alias(libs.plugins.android.test)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.baselineprofile)
    id("ashraf.pokedex.mad.spotless")
}


android {
    namespace = "ashraf.pokedex.mad.baselineprofile"
    compileSdk = 36

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    defaultConfig {
        minSdk = 29
        targetSdk = 36
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    targetProjectPath = ":app"

    // Optional: keep managed device config disabled for now.
    // testOptions.managedDevices.devices {
    //     maybeCreate<com.android.build.api.dsl.ManagedVirtualDevice>("pixel6api31").apply {
    //         device = "Pixel 6"
    //         apiLevel = 31
    //         systemImageSource = "aosp"
    //     }
    // }
}

// Optional: use only when switching to managed devices.
// baselineProfile {
//     managedDevices += "pixel6api31"
//     useConnectedDevices = false
// }

dependencies {
    implementation(libs.androidx.junit)
    implementation(libs.androidx.espresso.core)
    implementation(libs.macrobenchmark)
    implementation(libs.uiautomator)
}

// Pass the tested app's applicationId (targetAppId) to instrumentation tests.
// BaselineProfileGenerator / StartupBenchmarks read this via InstrumentationRegistry
// so packageName stays correct across variants, suffixes, or future flavors.
androidComponents {
    onVariants { v ->
        val artifactsLoader = v.artifacts.getBuiltArtifactsLoader()
        v.instrumentationRunnerArguments.put(
            "targetAppId",
            v.testedApks.map { artifactsLoader.load(it)?.applicationId },
        )
    }
}