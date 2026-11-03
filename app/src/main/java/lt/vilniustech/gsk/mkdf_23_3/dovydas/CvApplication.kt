package lt.vilniustech.gsk.mkdf_23_3.dovydas

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import lt.vilniustech.gsk.mkdf_23_3.dovydas.data.AppDatabase
import lt.vilniustech.gsk.mkdf_23_3.dovydas.data.ProfileRepository
import lt.vilniustech.gsk.mkdf_23_3.dovydas.ui.cv.CvViewModel
import lt.vilniustech.gsk.mkdf_23_3.dovydas.ui.home.HomeViewModel
import lt.vilniustech.gsk.mkdf_23_3.dovydas.ui.portfolio.PortfolioViewModel

class CvApplication : Application() {
    val container by lazy { AppContainer(this) }
}

/** Hand-written dependency container: one database and one preferences store per process. */
class AppContainer(context: Context) {
    private val database = AppDatabase.create(context)
    private val profileRepository = ProfileRepository(context)

    val viewModelFactory: ViewModelProvider.Factory = viewModelFactory {
        initializer { HomeViewModel(profileRepository) }
        initializer { CvViewModel(database.cvDao()) }
        initializer { PortfolioViewModel(database.portfolioDao()) }
    }
}
