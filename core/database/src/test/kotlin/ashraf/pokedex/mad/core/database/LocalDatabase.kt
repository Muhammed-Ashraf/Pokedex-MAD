/*
 * Designed and developed for Pokedex-MAD (learning project)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ashraf.pokedex.mad.core.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Before

/**
 * In-memory Room database setup for JVM unit tests.
 *
 * This class is meant to be inherited by test classes that need a database.
 *
 * IMPORTANT:
 * - Uses Robolectric because we need Android Context on JVM tests.
 * - Database is created fresh before each test and closed after.
 */
abstract class LocalDatabase {

    // The in-memory Room database instance
    // lateinit means it will be initialized later in @Before
    lateinit var db: PokedexDatabase

    /**
     * Runs BEFORE each test case.
     * Responsible for creating a fresh in-memory database.
     */
    @Before
    fun initDB() {

        // Create a Kotlin serialization JSON instance
        // ignoreUnknownKeys = true allows API/model changes without breaking parsing
        val json = Json { ignoreUnknownKeys = true }

        // Build an in-memory Room database
        db = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),          // Android Context (provided by Robolectric)
            PokedexDatabase::class.java       // Your Room database class
        )
            // Allows DB queries on main thread (only safe in tests)
            .allowMainThreadQueries()

            // Register type converters for complex objects
            .addTypeConverter(TypeResponseConverter(json))
            .addTypeConverter(StatsResponseConverter(json))

            // Create the database instance
            .build()
    }

    /**
     * Runs AFTER each test case.
     * Responsible for cleaning up resources.
     */
    @After
    fun closeDB() {
        // Closes the database to avoid memory leaks
        db.close()
    }
}
