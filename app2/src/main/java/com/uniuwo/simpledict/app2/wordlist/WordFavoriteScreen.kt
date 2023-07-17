package com.uniuwo.simpledict.app2.wordlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.Navigator
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.uniuwo.simpledcit.core.models.WordDetailViewModel
import com.uniuwo.simpledcit.core.models.WordFavoriteViewModel
import com.uniuwo.simpledcit.core.models.WordListViewModel
import com.uniuwo.simpledict.app2.NavDestinations
import com.uniuwo.simpledict.app2.components.AlphabetCard
import com.uniuwo.simpledict.app2.components.WordItemCard
import com.uniuwo.simpledict.app2.components.WordListInfoCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun WordFavoriteScreen(
    navController: NavHostController = rememberNavController()
) {
//    val words = (1..30).toList().map { "Word_$it" }.toMutableList()
    val words = WordFavoriteViewModel.getFavorites().map { it.word }
    WordDetailViewModel.words.clear()
    WordDetailViewModel.words.addAll(words)

    val onItemClick: (String) -> Unit = {
        WordDetailViewModel.currentWord = it
        navController.navigate(NavDestinations.WORD_DETAIL, navOptions { launchSingleTop = true })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Favorite",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {navController.popBackStack()}) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {

                }
            )
        },
        content = { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(4.dp)
                    .padding(top = innerPadding.calculateTopPadding()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                item {
                    WordListInfoCard("Favorite Words")
                }

                items(words, key = { it }) {
                    WordItemCard(word = it, onItemClick = onItemClick)
                }
            }
        }
    )

}