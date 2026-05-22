plugins {
    // Kotlin JVM plugin.
    // This makes core:common a pure Kotlin/JVM module (no Android plugin, no android{} block).
    // JVM library plugin: we use kotlin("jvm") here because the catalog + build-logic
    // the plugin is already on the classpath, so we must use kotlin("jvm") to avoid
    // a version conflict error.
    alias(libs.plugins.kotlin.jvm)

    // KSP so Hilt can generate code in this JVM-only module.
    // core:common uses Hilt *core* (not hilt-android) — no Android dependency in this module.
    alias(libs.plugins.ksp)
}

dependencies {
    // Hilt core for NON-Android modules.
    // javax.inject only — DI helpers here stay JVM-pure without Android APIs.
    implementation(libs.hilt.core)

    // Coroutines core used by shared utilities (e.g. dispatcher abstractions).
    // Lets core:network and core:data reuse coroutine helpers from core:common.
    implementation(libs.kotlinx.coroutines.core)

    // Hilt compiler used with KSP to generate DI code for Hilt core.
    ksp(libs.hilt.compiler)
}