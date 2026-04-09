plugins {
    id("ashraf.pokedex.mad.android.feature")
    id("ashraf.pokedex.mad.android.hilt") // Home will have @HiltViewModel etc.
}

android {
    namespace = "ashraf.pokedex.mad.feature.home"
}