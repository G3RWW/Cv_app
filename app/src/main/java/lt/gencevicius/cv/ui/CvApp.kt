package lt.gencevicius.cv.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import lt.gencevicius.cv.R
import lt.gencevicius.cv.ui.common.LanguageMenu
import lt.gencevicius.cv.ui.contact.ContactScreen
import lt.gencevicius.cv.ui.cv.CvScreen
import lt.gencevicius.cv.ui.home.HomeScreen
import lt.gencevicius.cv.ui.portfolio.PortfolioScreen

enum class Destination(val route: String, @StringRes val label: Int, val icon: ImageVector) {
    HOME("home", R.string.nav_home, Icons.Default.Home),
    CV("cv", R.string.nav_cv, Icons.Default.Person),
    PORTFOLIO("portfolio", R.string.nav_portfolio, Icons.Default.Star),
    CONTACT("contact", R.string.nav_contact, Icons.Default.Email),
}

/**
 * App shell. Phones in portrait get a bottom navigation bar; anything 600dp or wider
 * (landscape phones, foldables, tablets) gets a navigation rail on the side instead.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CvApp(viewModelFactory: ViewModelProvider.Factory) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val current = Destination.entries.firstOrNull { it.route == backStackEntry?.destination?.route } ?: Destination.HOME
    val compact = LocalConfiguration.current.screenWidthDp < 600

    val navigate: (Destination) -> Unit = { destination ->
        navController.navigate(destination.route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(if (current == Destination.HOME) R.string.app_name else current.label)) },
                navigationIcon = {
                    if (current != Destination.HOME) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.action_back))
                        }
                    }
                },
                actions = { LanguageMenu() },
            )
        },
        bottomBar = {
            if (compact) {
                NavigationBar {
                    Destination.entries.forEach {
                        NavigationBarItem(
                            selected = it == current,
                            onClick = { navigate(it) },
                            icon = { Icon(it.icon, contentDescription = null) },
                            label = { Text(stringResource(it.label)) },
                        )
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { padding ->
        Row(Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding)) {
            if (!compact) {
                NavigationRail(windowInsets = WindowInsets(0)) {
                    Spacer(Modifier.height(8.dp))
                    Destination.entries.forEach {
                        NavigationRailItem(
                            selected = it == current,
                            onClick = { navigate(it) },
                            icon = { Icon(it.icon, contentDescription = null) },
                            label = { Text(stringResource(it.label)) },
                        )
                    }
                }
            }
            NavHost(
                navController = navController,
                startDestination = Destination.HOME.route,
                modifier = Modifier.weight(1f).fillMaxHeight(),
            ) {
                composable(Destination.HOME.route) {
                    HomeScreen(
                        viewModel = viewModel(factory = viewModelFactory),
                        onOpenCv = { navigate(Destination.CV) },
                        onOpenPortfolio = { navigate(Destination.PORTFOLIO) },
                        onOpenContact = { navigate(Destination.CONTACT) },
                    )
                }
                composable(Destination.CV.route) { CvScreen(viewModel(factory = viewModelFactory)) }
                composable(Destination.PORTFOLIO.route) { PortfolioScreen(viewModel(factory = viewModelFactory)) }
                composable(Destination.CONTACT.route) { ContactScreen() }
            }
        }
    }
}
