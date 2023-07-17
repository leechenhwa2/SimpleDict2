package com.uniuwo.simpledict.app2.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.uniuwo.simpledcit.core.databus.SimpleDataBus
import com.uniuwo.simpledcit.core.models.WordHolder
import com.uniuwo.simpledcit.core.models.WordListViewModel
import com.uniuwo.simpledict.app2.ui.theme.SimpleDictTheme
import kotlinx.coroutines.launch

@Composable
fun WordItemCard(
    word: String,
    onItemClick: (String) -> Unit = {}
) {
    var content by remember { mutableStateOf("") }
    var isFavorite by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    LaunchedEffect(key1 = word){
        scope.launch {
            val dictItems = WordListViewModel.findByWord(word)
            val dictItem = if (dictItems.isNotEmpty()) dictItems[0] else null
            if (dictItem != null) {
                content = dictItems.map { it.entry.content }.joinToString("; ")
            }

            isFavorite = SimpleDataBus.isFavorite(word)
        }
    }

    val onFavoriteClick: () -> Unit = {
        scope.launch {
            isFavorite = !isFavorite
            SimpleDataBus.saveFavorite(word, isFavorite)
        }
    }

    ElevatedCard() {
        Row() {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(0.9f)
                    .clickable {
                        onItemClick(word)
                    }) {
                if(isFavorite){
                    Text(
                        text = word,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.Blue,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = word,
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                if (content.isNotEmpty()) {
                    Text(
                        text = " $content",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                } else {
                    Text(
                        text = " "
                    )
                }
            }

            IconButton(onClick = { onFavoriteClick() }) {
                Icon(
                    imageVector = Icons.Outlined.Favorite,
                    contentDescription = "Favorite",
                    tint = if (!isFavorite) Color.LightGray else Color.Blue
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CardPreview() {
    SimpleDictTheme {
        WordItemCard("Word")
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun WordCardListPreview() {
    val words = (1..30).toList().map { "Word_$it" }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            WordItemCard("Word")
        }

        items(words) {
            WordItemCard(word = it)
        }

    }
}

@Composable
fun WordListInfoCard(
    title: String,
    content: String = ""
) {
    ElevatedCard() {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            )

            Text(
                text = " ${content}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .align(alignment = Alignment.CenterHorizontally)
            )
        }
    }
}

@Preview
@Composable
fun WordListInfoCardPreview() {
   WordListInfoCard(title = "Title", content = "Info")
}