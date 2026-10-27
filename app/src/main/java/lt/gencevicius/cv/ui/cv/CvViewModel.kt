package lt.gencevicius.cv.ui.cv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import lt.gencevicius.cv.data.CvDao
import lt.gencevicius.cv.data.CvEntry

class CvViewModel(private val dao: CvDao) : ViewModel() {
    /** Entries the user added; the built-in ones come from string resources (DefaultContent). */
    val userEntries: StateFlow<List<CvEntry>> =
        dao.observeAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun add(entry: CvEntry) {
        viewModelScope.launch { dao.insert(entry) }
    }

    fun delete(entry: CvEntry) {
        viewModelScope.launch { dao.delete(entry) }
    }
}
