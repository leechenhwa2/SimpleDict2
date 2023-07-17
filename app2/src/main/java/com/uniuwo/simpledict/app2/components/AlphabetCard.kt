package com.uniuwo.simpledict.app2.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val alphabet_en: List<String> = ('a'..'z').map { it.toString() }.toList()


@Composable
fun AlphabetCard(
    modifier: Modifier = Modifier,
    currentSectionState: MutableState<String> = mutableStateOf(""),
    onSectionClick: (String) -> Unit = {}
) {
    val alphabet = alphabet_en
    var section by remember { mutableStateOf(currentSectionState.value) }

    LazyColumn(modifier) {
        items(alphabet, key = { it }) {

            Text(
                text = it.uppercase(),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                    onClick = {
                        section = it
                        onSectionClick(section)
                    },
                    enabled = (it != currentSectionState.value)
                ),
                color = if (it == currentSectionState.value) Color.LightGray else Color.Gray
            )

            Divider()
        }
    }

}

@Preview(showSystemUi = true, showBackground = true)
//@Preview
@Composable
fun AlphabetCardPreview() {
    Row(
        modifier = Modifier.width(1080.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.fillMaxWidth(0.95f)) {
            Text(text = "Left")
            LazyColumn() {
                item {
                    Text(text = "Left 2")
                }
            }
        }
        AlphabetCard(modifier = Modifier.width(40.dp))
    }
}