package com.uniuwo.simpledict.app2.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.uniuwo.simpledcit.core.databus.SimpleDataBus
import com.uniuwo.simpledcit.core.models.WordListViewModel
import com.uniuwo.simpledict.app2.NavDestinations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.util.Objects

@Preview
@Composable
fun DictSettingDrawerContent(
    navController: NavHostController = rememberNavController(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onItemClick: () -> Unit = {},
    settingViewModel: SettingViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()

    val uiState by settingViewModel.uiState.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(key1 = null) {
        scope.launch {
            try {
                val initFolderUri = Uri.fromFile(context.getExternalFilesDir(null))
                settingViewModel.checkInitFolder(initFolderUri)
            } catch (_: Exception) {
            }
        }
    }

    val selectedItem = remember { mutableStateOf<String>("") }
    val scrollState = rememberScrollState()

    Column(
        Modifier
            .padding(top = contentPadding.calculateTopPadding())
            .verticalScroll(scrollState, enabled = true)
    ) {
        Spacer(Modifier.height(12.dp))

        if (uiState.isDictsFolderSet) {

            NavHeaderTitle(text = "Favorites")
            NavigationDrawerItem(
                icon = {
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = Color.Blue
                    )
                },
                label = { Text("Favorite Words") },
                selected = NavDestinations.WORD_FAVORITE == selectedItem.value,
                onClick = {
                    onItemClick()
                    selectedItem.value = NavDestinations.WORD_FAVORITE
                    scope.launch {
                        showFavorites(navController)
                    }
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Divider(Modifier.padding(top = 16.dp, bottom = 16.dp))

            NavHeaderTitle(text = "Word List")
            //note: difference dsl
            settingViewModel.wordListFiles.forEach {
                NavigationDrawerItem(
                    icon = { Icon(Icons.Filled.List, contentDescription = null) },
                    label = { Text(it.name) },
                    selected = it.name == selectedItem.value,
                    onClick = {
                        onItemClick()
                        selectedItem.value = it.name
                        scope.launch {
                            showWordListFile(it, navController)
                        }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            Divider(Modifier.padding(top = 16.dp, bottom = 16.dp))

            Text(text = "Path: ${settingViewModel.initFolderUri?.toFile()?.absolutePath}")
            Divider(Modifier.padding(top = 16.dp, bottom = 16.dp))

            Text(text = "Dicts", style = MaterialTheme.typography.titleMedium)
            NavHeaderTitle(text = "Simple")
            //note: difference dsl
            settingViewModel.simpleDicts.forEach {
                NavigationDrawerItem(
                    icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                    label = { Text(it.name) },
                    selected = it.name == selectedItem.value,
                    onClick = {
                        onItemClick()
                        selectedItem.value = it.name
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            Divider(Modifier.padding(top = 16.dp, bottom = 16.dp))

            NavHeaderTitle(text = "Detail")
            //note: difference dsl
            settingViewModel.detailDicts.forEach {
                NavigationDrawerItem(
                    icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                    label = { Text(it.name) },
                    selected = it.name == selectedItem.value,
                    onClick = {
                        onItemClick()
                        selectedItem.value = it.name
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            Divider(Modifier.padding(top = 16.dp, bottom = 16.dp))

        }

        //demo
        repeat(4) {
            SettingItemCard()
        }
    }

}

@Composable
fun NavHeaderTitle(text: String) {
    Spacer(Modifier.height(16.dp))
    Text(text = text, modifier = Modifier.padding(8.dp))
}

fun showFavorites(navController: NavHostController) {
    navController.navigate(NavDestinations.WORD_FAVORITE, navOptions { launchSingleTop = true })
}

fun showWordListFile(file: File, navController: NavHostController) {
    if (WordListViewModel.wordlist == null) {
        WordListViewModel.wordlist = SimpleDataBus.wordListRepo.find {
            Objects.equals(
                it.path.absolutePath,
                file.absolutePath
            )
        }
        navController.navigate(NavDestinations.WORD_LIST, navOptions { launchSingleTop = true })
        return
    }

    if (Objects.equals(WordListViewModel.wordlist?.path?.absolutePath, file.absolutePath)) {
        navController.navigate(NavDestinations.WORD_LIST, navOptions { launchSingleTop = true })
        return
    } else {
        WordListViewModel.wordlist = SimpleDataBus.wordListRepo.find {
            Objects.equals(
                it.path.absolutePath,
                file.absolutePath
            )
        }
        navController.navigate(NavDestinations.WORD_LIST, navOptions { launchSingleTop = true })
    }
}

@Preview
@Composable
fun SettingScreen(settingViewModel: SettingViewModel = viewModel()) {
    val uiState by settingViewModel.uiState.collectAsState()
    val context = LocalContext.current
    try {
        val initFolderUri = Uri.fromFile(context.getExternalFilesDir(null))
        settingViewModel.checkInitFolder(initFolderUri)
    } catch (_: Exception) {
    }

    /*Surface (modifier = Modifier.padding(8.dp)) {
        Text(text = "Setting")
    }*/

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            Text(text = "Setting")
        }

        item {
            SettingFolderItemCard(
                isDictsFolderSet = uiState.isDictsFolderSet,
                viewModel = settingViewModel
            )
        }

        if (uiState.isDictsFolderSet) {
            item {
                Spacer(Modifier.height(16.dp))
            }
            item {
                Text(text = "Simple")
            }
            //note: difference dsl
            items(settingViewModel.simpleDicts) {
                SettingDictItemCard(documentFile = it)
            }

            item {
                Spacer(Modifier.height(16.dp))
            }
            item {
                Text(text = "Detail")
            }
            //note: difference dsl
            items(settingViewModel.detailDicts) {
                SettingDictItemCard(documentFile = it)
            }

            item {
                Spacer(Modifier.height(16.dp))
            }
            item {
                Text(text = "Word List")
            }
            //note: difference dsl
            items(settingViewModel.wordListFiles) {
                SettingDictItemCard(documentFile = it)
            }

        }

        item {
//            Divider()
            Spacer(Modifier.height(16.dp))
        }
        // demo
        item {
            Text(text = "Demo")
        }
        items(4) {
            SettingItemCard()
        }
    }
}

@Composable
fun SettingItemCard() {
    var checked by remember { mutableStateOf(false) }

    Card {
        ListItem(
            headlineContent = { Text("Headline Text") },
            supportingContent = { Text("Secondary text") },
            leadingContent = {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = "Localized description",
                )
            },
            trailingContent = {
                IconToggleButton(checked = checked, onCheckedChange = { checked = it }) {
                    if (checked) {
                        Icon(Icons.Filled.Lock, contentDescription = "Localized description")
                    } else {
                        Icon(Icons.Outlined.Lock, contentDescription = "Localized description")
                    }
                }
            }
        )

    }
}

@Composable
fun SettingFolderItemCard(
    isDictsFolderSet: Boolean,
    viewModel: SettingViewModel = viewModel()
) {
    OutlinedCard {
        ListItem(
            headlineContent = { Text("Dicts Folder") },
            supportingContent = {
                if (!isDictsFolderSet)
                    Text("Select Folder path \"Dicts\"")
                else
                    Text("Selected Folder ${viewModel.initFolderUri}")
            },
            leadingContent = {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = "Localized description",
                )
            }
        )

    }
}

@Composable
fun SettingDictItemCard(documentFile: File) {
    var checked by remember { mutableStateOf(false) }

    ElevatedCard {
        ListItem(
            headlineContent = { Text(documentFile.name ?: "[missing name]") },
            supportingContent = { Text("Path: ${documentFile.path}") },
            leadingContent = {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = "Localized description",
                )
            }
        )

    }
}

data class Post(val title: String, val url: String)

/**
 * Show a share sheet for a post
 *
 * @param post to share
 * @param context Android context to show the share sheet in
 */
fun sharePost(post: Post, context: Context) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TITLE, post.title)
        putExtra(Intent.EXTRA_TEXT, post.url)
    }
    context.startActivity(
        Intent.createChooser(
            intent,
            "Share Post ${post.title}"
        )
    )
}

@Composable
fun GetContentExample(context: Context = LocalContext.current) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
        }
    Column {
        Button(onClick = { launcher.launch("image/*") }) {
            Text(text = "Load Image")
        }

        imageUri?.let {
            Text(text = imageUri.toString())
            Image(
                bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
                    .asImageBitmap(),
                contentDescription = "Loaded Bitmap Image"
            )
        }
    }
}

class SettingViewModel : ViewModel() {
    private var _uiState = MutableStateFlow(SettingUiState())

    // Backing property to avoid state updates from other classes
    val uiState: StateFlow<SettingUiState> = _uiState.asStateFlow()

    var simpleDicts: MutableList<File> = mutableListOf()
    var detailDicts: MutableList<File> = mutableListOf()
    var wordListFiles: MutableList<File> = mutableListOf()

    var initFolderUri: Uri? = null

    init {
        simpleDicts.clear()
        detailDicts.clear()
        wordListFiles.clear()
    }


    private fun scanFiles(folderUri: Uri?) {
        if (folderUri == null) return

        simpleDicts.clear()
        detailDicts.clear()
        wordListFiles.clear()

        val folder = folderUri.toFile()
        if (folder != null && folder.isDirectory && folder.canRead()) {
            val simpleFolder = folder.resolve("simple")
            simpleFolder?.let { f ->
                val files = f.listFiles().filter { it.name!!.endsWith(".db", ignoreCase = true) }
                simpleDicts.addAll(files)
            }

            val detailFolder = folder.resolve("detail")
            detailFolder?.let { f ->
                val files = f.listFiles().filter { it.name!!.endsWith(".db", ignoreCase = true) }
                detailDicts.addAll(files)
            }

            val wordlistFolder = folder.resolve("wordlist")
            wordlistFolder?.let { f ->
                val files = f.listFiles().filter { it.name!!.endsWith(".txt", ignoreCase = true) }
                wordListFiles.addAll(files)
            }
        }
    }

    fun checkInitFolder(folderUri: Uri?): Boolean {
        initFolderUri = folderUri

        if (initFolderUri == null) {
            _uiState.update { it.copy(isDictsFolderSet = false) }
            return false
        }

        if (initFolderUri!!.scheme!!.startsWith("file", ignoreCase = true)) {
            val folder = initFolderUri!!.toFile()

            if (folder != null && folder.isDirectory && folder.canRead()) {
                scanFiles(initFolderUri)
                _uiState.update { it.copy(isDictsFolderSet = true) }
                return true
            }
        }

        return false
    }

    companion object {
        const val KEY_FOLDER_URI = "folderUri"
    }
}


data class SettingUiState(
    val isDictsFolderSet: Boolean = false
)