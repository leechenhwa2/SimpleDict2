package com.uniuwo.simpledict.app2.wordlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.isContainer
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.uniuwo.simpledcit.core.models.WordDetailViewModel
import com.uniuwo.simpledcit.core.models.WordListViewModel
import com.uniuwo.simpledict.app2.NavDestinations
import com.uniuwo.simpledict.app2.components.WordItemCard
import kotlinx.coroutines.launch


@ExperimentalMaterial3Api
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WordSearchScreen(navController: NavHostController = rememberNavController()) {
    var text by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }

    var listFiltered by rememberSaveable { mutableStateOf(false) }
    var preFiltered by rememberSaveable { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val words = remember { mutableStateListOf<String>() }

    LaunchedEffect(listFiltered) {
        words.clear()
        words.addAll(WordListViewModel.searchWords)

        WordDetailViewModel.words.clear()
        WordDetailViewModel.words.addAll(words)
    }

    val onItemClick: (String) -> Unit = {
        WordDetailViewModel.currentWord = it
        navController.navigate(NavDestinations.WORD_DETAIL, navOptions { launchSingleTop = true })
    }

    val searches = WordListViewModel.searchWords
    val addSearch: (String) -> Unit = { searchWord ->
        scope.launch {
            val result = WordListViewModel.findByWord(searchWord).map { it.word }.distinct()

            words.clear()
            words.addAll((result + WordListViewModel.searchWords).distinct())
            WordListViewModel.pushSearchWord(searchWord)

            WordDetailViewModel.words.clear()
            WordDetailViewModel.words.addAll(words)

            listFiltered = true;
        }
    }

    val suggests = remember { mutableStateListOf<String>() }
    val preSearch: (String) -> Unit = { searchWord ->
        scope.launch {
            suggests.clear()
            suggests.addAll(WordListViewModel.searchWord(searchWord).distinct())

            preFiltered = true;
        }
    }

    Box(modifier = Modifier.fillMaxSize()){

        Box(modifier = Modifier
            .semantics { isContainer = true }
            .zIndex(1f)
            .fillMaxWidth()) {
            SearchBar(
                modifier = Modifier.align(Alignment.TopCenter),
                query = text,
                onQueryChange = { text = it ; preSearch(text) },
                onSearch = { active = false ; addSearch(text)  },
                active = active,
                onActiveChange = {
                    active = it
                },
                placeholder = { Text("Hinted search text") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (text.isNotBlank()) Icon(
                        Icons.Default.Clear,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            text = ""
                        })
                },
            ) {
                //searches
                LazyRow(modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(searches.size) {
                        Badge(
                            modifier = Modifier
                                .requiredWidth(42.dp)
                                .clickable {
                                    text = searches.elementAt(it)
                                    active = false
                                }
                        ) {
                            Text(searches.elementAt(it))
                        }
                    }
                }

                // suggestions
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    items(suggests, key = {"sg-$it"}) { item ->
                        ListItem(
                            headlineContent = { Text(item) },
//                            leadingContent = { Icon(Icons.Filled.Star, contentDescription = null) },
                            modifier = Modifier.clickable {
                                text = item
                                active = false
                                addSearch(text)
                            }
                        )
                    }

                }
            }

        }

        //results
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, top = 72.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            //Note: list update
            items(words, key = { "wd-$it" }) {
                WordItemCard(word = it, onItemClick = onItemClick)
            }

        }

    }
}