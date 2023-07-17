package com.uniuwo.simpledict.app2

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.uniuwo.simpledict.app2.settings.SettingScreen
import com.uniuwo.simpledict.app2.wordlist.WordDetailScreen
import com.uniuwo.simpledict.app2.wordlist.WordFavoriteScreen
import com.uniuwo.simpledict.app2.wordlist.WordListScreenExt
import com.uniuwo.simpledict.app2.wordlist.WordSearchScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictAppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavDestinations.WORD_LIST
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavDestinations.WORD_FAVORITE) { WordFavoriteScreen(navController) }
        composable(NavDestinations.WORD_LIST) { WordListScreenExt(navController) }
        composable(NavDestinations.WORD_DETAIL) { WordDetailScreen() }
        composable(NavDestinations.WORD_SEARCH) { WordSearchScreen(navController) }
        composable(NavDestinations.SETTING) { SettingScreen() }
    }
}

object NavDestinations {
    const val WORD_FAVORITE = "word favorite"
    const val WORD_LIST = "word list"
    const val WORD_DETAIL = "word detail"
    const val WORD_SEARCH = "word search"

    const val SETTING = "setting"
}
