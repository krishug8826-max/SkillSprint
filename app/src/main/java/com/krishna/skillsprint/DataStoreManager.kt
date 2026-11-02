
package com.krishna.skillsprint

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DataStoreManager(
    private val context: Context,
    private val userId: String
) {

    private val dataStore: DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            scope = CoroutineScope(
                Dispatchers.IO + SupervisorJob()
            ),
            produceFile = {
                context.preferencesDataStoreFile(
                    "skillsprint_preferences_${userId}"
                )
            }
        )

    companion object {
        private val MISSION_COUNT =
            intPreferencesKey("completed_missions")

        private val PROJECT_COUNT =
            intPreferencesKey("completed_projects")

        private val TOTAL_POINTS =
            intPreferencesKey("total_points")
    }

    val completedMissions: Flow<Int> =
        dataStore.data.map { preferences ->
            preferences[MISSION_COUNT] ?: 0
        }

    val completedProjects: Flow<Int> =
        dataStore.data.map { preferences ->
            preferences[PROJECT_COUNT] ?: 0
        }

    val totalPoints: Flow<Int> =
        dataStore.data.map { preferences ->
            preferences[TOTAL_POINTS] ?: 0
        }

    suspend fun loadMissionStates(count: Int): List<Boolean> {
        val preferences = dataStore.data.first()

        return List(count) { index ->
            preferences[
                booleanPreferencesKey("mission_$index")
            ] ?: false
        }
    }

    suspend fun loadProjectStates(count: Int): List<Boolean> {
        val preferences = dataStore.data.first()

        return List(count) { index ->
            preferences[
                booleanPreferencesKey("project_$index")
            ] ?: false
        }
    }

    suspend fun saveMissionStates(states: List<Boolean>) {
        dataStore.edit { preferences ->
            states.forEachIndexed { index, completed ->
                preferences[
                    booleanPreferencesKey("mission_$index")
                ] = completed
            }

            preferences[MISSION_COUNT] =
                states.count { it }
        }
    }

    suspend fun saveProjectStates(states: List<Boolean>) {
        dataStore.edit { preferences ->
            states.forEachIndexed { index, completed ->
                preferences[
                    booleanPreferencesKey("project_$index")
                ] = completed
            }

            preferences[PROJECT_COUNT] =
                states.count { it }
        }
    }

    suspend fun savePoints(points: Int) {
        dataStore.edit { preferences ->
            preferences[TOTAL_POINTS] = points
        }
    }
}

