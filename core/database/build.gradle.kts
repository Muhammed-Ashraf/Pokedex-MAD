plugins {
    // Android library conventions (compileSdk, minSdk, Java 17, etc.)
    id("ashraf.pokedex.mad.android.library")

    // Hilt + KSP + Hilt deps in one line (for DatabaseModule @Module/@InstallIn).
    id("ashraf.pokedex.mad.android.hilt")

    // Shared formatting + license headers.
    id("ashraf.pokedex.mad.spotless")

    // Room uses KSP for code generation.
    alias(libs.plugins.ksp)
}

android {
    namespace = "ashraf.pokedex.mad.core.database"

    defaultConfig {
        // Room writes schema JSON files here (one per version). Needed for migrations.
        // TODO(AutoMigrations): When we start using Room auto-migrations, keep the generated
        // JSON schemas under $projectDir/schemas and use them to define proper migrations.
        // For now we'll use fallbackToDestructiveMigration() in DatabaseModule (like reference).
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    // Let Room test code read schemas (for auto-migrations and tests).
    sourceSets.getByName("test") {
        assets.srcDir(files("$projectDir/schemas"))
    }
}

dependencies {
    // Domain models (Pokemon) live in core:model; we map to/from Room entities.
    implementation(projects.core.model)

    // Room runtime + Kotlin extensions.
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Coroutines for suspend DAO methods.
    implementation(libs.kotlinx.coroutines.android)

    // TODO(Phase6-Tests): Add core:test and Room/coroutines test deps when we start writing DB tests,
    // mirroring the reference core:database module:
    //  - testImplementation(projects.core.test)
    //  - testImplementation(libs.kotlinx.coroutines.test)
    //  - testImplementation(libs.androidx.arch.core.testing)
    //  - testImplementation(libs.junit)
    //  - testImplementation(libs.androidx.test.core)
    //  - testImplementation(libs.robolectric)

    // TODO(DetailScreen): Reference also uses kotlinx.serialization.json in core:database and adds
    // extra entities (PokemonInfoEntity) plus TypeConverters (e.g. TypeResponseConverter).
    // When we implement the detail screen and need to cache richer Pokemon detail data:
    //  - Add implementation(libs.kotlinx.serialization.json)
    //  - Add PokemonInfoEntity + TypeConverters
    //  - Register them on PokedexDatabase and in DatabaseModule.
}