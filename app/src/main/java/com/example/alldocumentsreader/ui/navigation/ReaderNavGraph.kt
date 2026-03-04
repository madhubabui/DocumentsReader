package com.example.alldocumentsreader.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.alldocumentsreader.ui.screens.home.HomeRootScreen
import com.example.alldocumentsreader.ui.screens.onboarding.IntroScreen
import com.example.alldocumentsreader.ui.screens.onboarding.LanguageScreen
import com.example.alldocumentsreader.ui.screens.onboarding.PermissionScreen
import com.example.alldocumentsreader.ui.screens.viewer.ViewerScreen
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

private object Routes {
    const val LANGUAGE = "language"
    const val INTRO = "intro"
    const val PERMISSION = "permission"
    const val HOME = "home"
    const val VIEWER = "viewer/{uri}/{mime}/{name}/{type}"

    fun viewer(uri: String, mime: String, name: String, type: String): String =
        "viewer/${enc(uri)}/${enc(mime)}/${enc(name)}/${enc(type)}"

    private fun enc(v: String): String = URLEncoder.encode(v, StandardCharsets.UTF_8.toString())
}

@Composable
fun ReaderNavGraph(vm: ReaderViewModel, modifier: Modifier = Modifier) {
    val nav = rememberNavController()
    val state by vm.state.collectAsStateWithLifecycle()

    val start = when {
        !state.onboardingDone -> Routes.LANGUAGE
        state.folderUri.isNullOrBlank() -> Routes.PERMISSION
        else -> Routes.HOME
    }

    NavHost(navController = nav, startDestination = start, modifier = modifier) {
        composable(Routes.LANGUAGE) {
            LanguageScreen(onNext = {
                vm.setLanguage(it)
                nav.navigate(Routes.INTRO)
            })
        }
        composable(Routes.INTRO) {
            IntroScreen(onNext = { nav.navigate(Routes.PERMISSION) })
        }
        composable(Routes.PERMISSION) {
            PermissionScreen(
                onFolderSelected = {
                    vm.onFolderSelected(it)
                    vm.completeOnboarding()
                    nav.navigate(Routes.HOME) { popUpTo(0) }
                },
                onLater = {
                    vm.completeOnboarding()
                    nav.navigate(Routes.HOME)
                },
            )
        }
        composable(Routes.HOME) {
            HomeRootScreen(
                folderUri = state.folderUri,
                category = state.category,
                documents = state.documents,
                onCategoryChange = vm::setCategory,
                onFolderSelected = vm::onFolderSelected,
                onOpenDocument = {
                    vm.openDocument(it)
                    nav.navigate(Routes.viewer(it.uri.toString(), it.mimeType, it.displayName, it.extension))
                },
                onToggleFavorite = vm::toggleFavorite,
            )
        }
        composable(
            route = Routes.VIEWER,
            arguments = listOf(
                navArgument("uri") { type = NavType.StringType },
                navArgument("mime") { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType },
                navArgument("type") { type = NavType.StringType },
            )
        ) { backStack ->
            ViewerScreen(
                fileUri = backStack.arguments?.getString("uri").orEmpty(),
                mimeType = backStack.arguments?.getString("mime").orEmpty(),
                displayName = backStack.arguments?.getString("name").orEmpty(),
                fileType = backStack.arguments?.getString("type").orEmpty(),
                onBack = { nav.popBackStack() },
            )
        }
    }
}
