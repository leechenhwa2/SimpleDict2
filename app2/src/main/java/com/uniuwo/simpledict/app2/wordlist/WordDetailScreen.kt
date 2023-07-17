package com.uniuwo.simpledict.app2.wordlist

import android.webkit.WebView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.uniuwo.simpledcit.core.models.WordDetailViewModel
import com.uniuwo.simpledcit.core.models.WordListViewModel
import kotlinx.coroutines.launch

@Preview
@Composable
fun WordDetailScreen() {
    var word by remember { mutableStateOf(WordDetailViewModel.currentWord) }
    var content by remember { mutableStateOf("") }
    var prevWord by remember { mutableStateOf("") }
    var nextWord by remember { mutableStateOf("") }

    var scrolledDeep by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    LaunchedEffect(key1 = word){
        scope.launch {
            if(!word.isNullOrBlank()) {
                content = WordListViewModel.makeDetailContent(word)

                prevWord = WordDetailViewModel.prevWord(word)
                nextWord = WordDetailViewModel.nextWord(word)
            }
        }
    }

    val gotoWord: (String) -> Unit = { newWord ->
        WordDetailViewModel.currentWord = newWord
        scope.launch {
            word = newWord
            if(!word.isNullOrBlank()) {
                content = WordListViewModel.makeDetailContent(newWord)

                prevWord = WordDetailViewModel.prevWord(newWord)
                nextWord = WordDetailViewModel.nextWord(newWord)
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    // Sets up listeners for View -> Compose communication
                    setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                        scrolledDeep = scrollY > 50
                    }
                }
            },
            update = { webView ->
                //AndroidView will recompose whenever the state changes
                // Example of Compose -> View communication
                webView.loadData(content, "text/html", "UTF-8")
            }
        )

        //toolbar
        if(!scrolledDeep) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {

                Button(
                    onClick = { gotoWord(prevWord) },
                    enabled = !(prevWord.isBlank() || prevWord == word)
                ) {
                    Text(text = "< $prevWord")
                }

                Button(
                    onClick = { gotoWord(nextWord) },
                    enabled = !(nextWord.isBlank() || nextWord == word)
                ) {
                    Text(text = "$nextWord >")
                }
            }
        }
    }
}