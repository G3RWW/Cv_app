package lt.gencevicius.cv.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File

private val Context.profileStore by preferencesDataStore(name = "profile")

/** Profile photo taken with the camera: the file lives in filesDir/photos, its path in DataStore. */
class ProfileRepository(context: Context) {
    private val appContext = context.applicationContext
    private val photoPathKey = stringPreferencesKey("photo_path")

    val photoPath: Flow<String?> = appContext.profileStore.data.map { it[photoPathKey] }

    /** A fresh file for the camera to write into; a new name each time so image caches never show a stale photo. */
    fun newPhotoFile(): File {
        val dir = File(appContext.filesDir, "photos").apply { mkdirs() }
        return File(dir, "profile_${System.currentTimeMillis()}.jpg")
    }

    /** Keeps [photo] as the profile photo and deletes every older one. */
    suspend fun setPhoto(photo: File) {
        appContext.profileStore.edit { it[photoPathKey] = photo.path }
        withContext(Dispatchers.IO) {
            photo.parentFile?.listFiles()?.filter { it != photo }?.forEach { it.delete() }
        }
    }

    /** Removes a file the camera was given but did not fill (photo cancelled). */
    suspend fun discard(photo: File) {
        withContext(Dispatchers.IO) { photo.delete() }
    }
}
