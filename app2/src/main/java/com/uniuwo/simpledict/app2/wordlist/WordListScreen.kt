package com.uniuwo.simpledict.app2.wordlist

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
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
import androidx.navigation.compose.rememberNavController
import com.uniuwo.simpledcit.core.models.WordDetailViewModel
import com.uniuwo.simpledcit.core.models.WordListViewModel
import com.uniuwo.simpledict.app2.NavDestinations
import com.uniuwo.simpledict.app2.components.AlphabetCard
import com.uniuwo.simpledict.app2.components.WordItemCard
import com.uniuwo.simpledict.app2.components.WordListInfoCard
import com.uniuwo.simpledict.app2.settings.DictSettingDrawerContent
import kotlinx.coroutines.launch
import android.annotation.SuppressLint as SuppressLint1

@Preview(showSystemUi = true, showBackground = true)
//@Preview
@Composable
fun WordListScreen(
    navController: NavHostController = rememberNavController(),
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
//    val words = (1..30).toList().map { "Word_$it" }
//    WordListViewModel.wordlist = SimpleDataBus.wordListRepo.firstOrNull()
    val wordList = WordListViewModel.wordlist
    if (wordList == null) {
        WordDetailViewModel.words.clear()
    } else {
        WordDetailViewModel.words.clear()
        WordDetailViewModel.words.addAll(WordListViewModel.wordlist!!.items)
    }

    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    val sectionState = remember { mutableStateOf("") }
    val visibleItemIndex by remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }
    LaunchedEffect(visibleItemIndex) {
        if (wordList != null && wordList.items.isNotEmpty()) {
            val item = wordList.items[visibleItemIndex]
            val nextSection = item.first().toString().lowercase()
            scope.launch { sectionState.value = nextSection }
        }
    }

    val onItemClick: (String) -> Unit = {
        WordDetailViewModel.currentWord = it
        navController.navigate(NavDestinations.WORD_DETAIL)
    }

    /*    Row(horizontalArrangement = Arrangement.SpaceAround) {
            Button(onClick = { *//*TODO*//* }) {
            Text(text = "Button")
        }
        AlphabetCard(modifier = Modifier.width(48.dp))
    }*/

    Row(modifier = Modifier.padding(top = contentPadding.calculateTopPadding()),
     horizontalArrangement = Arrangement.SpaceBetween) {
        Column(modifier = Modifier
            .fillMaxWidth(0.95f)
            .fillMaxHeight()) {
            WordListInfoCard("Word List", (if (wordList == null) "" else wordList.path.name))

            LazyColumn(
                state = lazyListState,
                modifier = Modifier.padding(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (wordList != null) {
                    items(wordList.items, key = { it }) {
                        WordItemCard(word = it, onItemClick = onItemClick)
                    }
                }

            }
        }


        Column(
            modifier = Modifier
                .width(36.dp)
                .padding(top = 80.dp, bottom = 64.dp)
        ) {
            AlphabetCard(
                modifier = Modifier
                    .background(Color.LightGray.copy(alpha = 0.4f)),
                currentSectionState = sectionState
            ) { section ->
                if (section.isNotBlank() && wordList != null) {
                    val itemIndex = wordList.items.indexOfFirst {
                        it.startsWith(
                            section,
                            ignoreCase = true
                        )
                    }
                    if (itemIndex >= 0) {
                        scope.launch {
                            lazyListState.scrollToItem(itemIndex + 1) // one offset for header card
                        }
                    }
                }
            }
        }

    }
}

@SuppressLint1("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
//@Preview
@Composable
fun WordListScreenExt(navController: NavHostController = rememberNavController()) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch {
            drawerState.close()
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Simple Dict",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { if (drawerState.isClosed) drawerState.open() else drawerState.close() } }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(NavDestinations.WORD_SEARCH) } ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Localized description"
                        )
                    }

                }
            )
        },
        content = { innerPadding ->
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet() {
                        DictSettingDrawerContent(
                            navController = navController,
                            contentPadding = innerPadding,
                            onItemClick = { scope.launch { drawerState.close() } }
                        )
                    }
                }
            ) {
                WordListScreen(navController, contentPadding = innerPadding)
            }
        }
    )
}

@Composable
fun DictDrawerContent(
    navController: NavHostController = rememberNavController(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onItemClick: () -> Unit = {}
) {
    // icons to mimic drawer destinations
    val items = listOf(Icons.Default.Favorite, Icons.Default.Face, Icons.Default.Email)
    val selectedItem = remember { mutableStateOf(items[0]) }

    DismissibleDrawerSheet(
        modifier = Modifier.padding(top = contentPadding.calculateTopPadding())
    ) {
        Spacer(Modifier.height(12.dp))
        items.forEach { item ->
            NavigationDrawerItem(
                icon = { Icon(item, contentDescription = null) },
                label = { Text(item.name) },
                selected = item == selectedItem.value,
                onClick = {
                    onItemClick()
                    selectedItem.value = item
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}