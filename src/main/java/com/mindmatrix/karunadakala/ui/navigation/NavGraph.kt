package com.mindmatrix.karunadakala.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.mindmatrix.karunadakala.ui.screens.detail.ArtFormDetailScreen
import com.mindmatrix.karunadakala.ui.screens.events.EventsScreen
import com.mindmatrix.karunadakala.ui.screens.explorer.ArtFormExplorerScreen
import com.mindmatrix.karunadakala.ui.screens.home.HomeScreen
import com.mindmatrix.karunadakala.ui.screens.map.ArtisanMapScreen
import com.mindmatrix.karunadakala.ui.screens.profile.ArtisanProfileScreen
import com.mindmatrix.karunadakala.ui.screens.signup.WorkshopSignupScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.mindmatrix.karunadakala.viewmodel.SeedViewModel

sealed class Screen(val route: String) {
    object Home        : Screen("home")
    object Explorer    : Screen("explorer")
    object Events      : Screen("events")
    object Map         : Screen("map")
    object ArtFormDetail : Screen("art_form/{artFormId}") {
        fun createRoute(id: String) = "art_form/$id"
    }
    object ArtisanProfile : Screen("artisan/{artisanId}") {
        fun createRoute(id: String) = "artisan/$id"
    }
    object WorkshopSignup : Screen("signup/{artFormName}") {
        fun createRoute(name: String) = "signup/$name"
    }
}

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val screen: Screen
)

val bottomNavItems = listOf(
    BottomNavItem("Home",    Icons.Filled.Home,       Screen.Home),
    BottomNavItem("Explore", Icons.Filled.Explore,    Screen.Explorer),
    BottomNavItem("Map",     Icons.Filled.LocationOn, Screen.Map),
    BottomNavItem("Events",  Icons.Filled.Event,      Screen.Events),
)

@Composable
fun KarunadaKalaNavGraph() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDest = navBackStackEntry?.destination
            // Hide bottom bar on detail screens
            val showBar = bottomNavItems.any { it.screen.route == currentDest?.route } ||
                currentDest?.route == Screen.Home.route
            if (currentDest?.route in listOf(
                    Screen.Home.route, Screen.Explorer.route,
                    Screen.Map.route,  Screen.Events.route)) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentDest?.hierarchy?.any { it.route == item.screen.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = Screen.Home.route,
            modifier         = Modifier.padding(innerPadding)
        ) {
            composable("admin/seed") {
                val vm: SeedViewModel = hiltViewModel()
                val state by vm.uiState.collectAsState()
                com.mindmatrix.karunadakala.ui.screens.admin.SeedDataScreen(
                    onSeedClick    = vm::seedData,
                    isSeedingDone  = state.isDone,
                    isSeedingError = state.error,
                    isSeeding      = state.isSeeding
                )
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    onArtFormClick   = { id -> navController.navigate(Screen.ArtFormDetail.createRoute(id)) },
                    onExploreClick   = { navController.navigate(Screen.Explorer.route) },
                    onMapClick       = { navController.navigate(Screen.Map.route) },
                    onEventsClick    = { navController.navigate(Screen.Events.route) }
                )
            }
            composable(Screen.Explorer.route) {
                ArtFormExplorerScreen(
                    onArtFormClick = { id -> navController.navigate(Screen.ArtFormDetail.createRoute(id)) }
                )
            }
            composable(Screen.Events.route) {
                EventsScreen()
            }
            composable(Screen.Map.route) {
                ArtisanMapScreen(
                    onArtisanClick = { id -> navController.navigate(Screen.ArtisanProfile.createRoute(id)) }
                )
            }
            composable(
                route = Screen.ArtFormDetail.route,
                arguments = listOf(navArgument("artFormId") { type = NavType.StringType })
            ) { backStack ->
                val artFormId = backStack.arguments?.getString("artFormId") ?: return@composable
                ArtFormDetailScreen(
                    artFormId       = artFormId,
                    onBack          = { navController.popBackStack() },
                    onSignupClick   = { name -> navController.navigate(Screen.WorkshopSignup.createRoute(name)) }
                )
            }
            composable(
                route = Screen.ArtisanProfile.route,
                arguments = listOf(navArgument("artisanId") { type = NavType.StringType })
            ) { backStack ->
                val artisanId = backStack.arguments?.getString("artisanId") ?: return@composable
                ArtisanProfileScreen(
                    artisanId     = artisanId,
                    onBack        = { navController.popBackStack() },
                    onSignupClick = { name -> navController.navigate(Screen.WorkshopSignup.createRoute(name)) }
                )
            }
            composable(
                route = Screen.WorkshopSignup.route,
                arguments = listOf(navArgument("artFormName") { type = NavType.StringType })
            ) { backStack ->
                val artFormName = backStack.arguments?.getString("artFormName") ?: ""
                WorkshopSignupScreen(
                    preselectedArtForm = artFormName,
                    onBack             = { navController.popBackStack() }
                )
            }
        }
    }
}
