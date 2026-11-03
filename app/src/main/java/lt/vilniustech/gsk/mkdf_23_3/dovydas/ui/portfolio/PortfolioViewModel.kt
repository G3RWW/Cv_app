package lt.vilniustech.gsk.mkdf_23_3.dovydas.ui.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import lt.vilniustech.gsk.mkdf_23_3.dovydas.data.PortfolioDao
import lt.vilniustech.gsk.mkdf_23_3.dovydas.data.PortfolioItem

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
