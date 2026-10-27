package lt.gencevicius.cv.ui.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import lt.gencevicius.cv.data.PortfolioDao
import lt.gencevicius.cv.data.PortfolioItem

class PortfolioViewModel(private val dao: PortfolioDao) : ViewModel() {
    /** Items the user added; the built-in ones come from string resources (DefaultContent). */
    val userItems: StateFlow<List<PortfolioItem>> =
        dao.observeAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun add(item: PortfolioItem) {
        viewModelScope.launch { dao.insert(item) }
    }

    fun delete(item: PortfolioItem) {
        viewModelScope.launch { dao.delete(item) }
    }
}
