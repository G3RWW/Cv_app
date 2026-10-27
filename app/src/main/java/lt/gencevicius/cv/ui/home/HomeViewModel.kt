package lt.gencevicius.cv.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import lt.gencevicius.cv.data.ProfileRepository
import java.io.File

class HomeViewModel(private val profile: ProfileRepository) : ViewModel() {
    val photoPath: StateFlow<String?> =
        profile.photoPath.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun newPhotoFile(): File = profile.newPhotoFile()

    fun onPhotoResult(file: File, saved: Boolean) {
        viewModelScope.launch {
            if (saved && file.length() > 0) profile.setPhoto(file) else profile.discard(file)
        }
    }
}
