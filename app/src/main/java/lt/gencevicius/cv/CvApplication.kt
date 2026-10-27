package lt.gencevicius.cv

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import lt.gencevicius.cv.data.AppDatabase
import lt.gencevicius.cv.data.ProfileRepository
import lt.gencevicius.cv.ui.cv.CvViewModel
import lt.gencevicius.cv.ui.home.HomeViewModel
import lt.gencevicius.cv.ui.portfolio.PortfolioViewModel

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
