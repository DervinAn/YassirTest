package com.example.yassirtest.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.yassirtest.R
import com.example.yassirtest.domain.model.Character
import com.example.yassirtest.presentation.viewModel.CharacterUiState
import com.example.yassirtest.presentation.viewModel.CharacterViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterListScreen(
    viewModel: CharacterViewModel,
    onCharacterClick: (Character) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("All") }
    var selectedSpecies by remember { mutableStateOf("All") }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isFilterOpen by remember { mutableStateOf(false) }
    val animatedItemIds = remember { mutableStateListOf<Int>() }
    val highlightedCharacterIds = remember { mutableStateListOf<Int>() }

    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            val matched = viewModel.uiState.value.let {
                if (it is CharacterUiState.Success) it.characters.map { c -> c.id }
                else emptyList()
            }
            highlightedCharacterIds.clear()
            highlightedCharacterIds.addAll(matched)
        } else {
            highlightedCharacterIds.clear()
        }
    }


    LaunchedEffect(listState) {
        snapshotFlow {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisibleItem to totalItems
        }.distinctUntilChanged().collectLatest { (lastVisibleItem, totalItems) ->
            if (lastVisibleItem != null && lastVisibleItem >= totalItems - 1) {
                viewModel.fetchCharacters()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchCharacters(reset = true)
    }

    if (isFilterOpen) {
        ModalBottomSheet(
            onDismissRequest = {
                coroutineScope.launch { sheetState.hide() }
                isFilterOpen = false
            },
            sheetState = sheetState
        ) {
            FilterSheetContent(
                selectedStatus = selectedStatus,
                onStatusSelected = {
                    selectedStatus = it
                    viewModel.applyFilters(
                        status = if (it == "All") null else it,
                        species = if (selectedSpecies == "All") null else selectedSpecies
                    )
                    coroutineScope.launch { sheetState.hide() }
                    isFilterOpen = false
                },
                selectedSpecies = selectedSpecies,
                onSpeciesSelected = {
                    selectedSpecies = it
                    viewModel.applyFilters(
                        status = if (selectedStatus == "All") null else selectedStatus,
                        species = if (it == "All") null else it
                    )
                    coroutineScope.launch { sheetState.hide() }
                    isFilterOpen = false
                }
            )
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(padding)
        ) {
            SearchBarWithFilterIcon(
                searchQuery = searchQuery,
                onSearchChange = {
                    searchQuery = it
                    viewModel.searchCharacters(it)
                },
                onFilterClick = {
                    coroutineScope.launch {
                        isFilterOpen = true
                        sheetState.show()
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            when (val currentState = state) {
                is CharacterUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is CharacterUiState.Success -> {
                    val characters = currentState.characters
                    if (characters.isNotEmpty()) {
                        LazyColumn(state = listState) {
                            items(characters, key = { it.id }) { character ->
                                AnimatedCharacterItem(
                                    character = character,
                                    animatedItemIds = animatedItemIds,
                                    onClick = { onCharacterClick(character) },
                                    highlight = highlightedCharacterIds.contains(character.id)
                                )
                            }

                            if (viewModel.isPaginating) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            } else if (                                 //this was 10 not 20 i change it
                                !viewModel.isLastPage && characters.size > 20 && listState.firstVisibleItemIndex > 0
                            ) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Scroll to load more…")
                                    }
                                }
                            }
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No characters found.")
                        }
                    }
                }

                is CharacterUiState.Error -> {
                    val message = currentState.message
                    val iconResId = when {
                        message.contains("internet", ignoreCase = true) -> R.drawable.outline_wifi_off_24
                        message.contains("server", ignoreCase = true) -> R.drawable.outline_cloud_off_24
                        else -> R.drawable.outline_error_24
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Icon with slight entrance animation
                            AnimatedVisibility(visible = true) {
                                Image(
                                    painter = painterResource(id = iconResId),
                                    contentDescription = "Error Icon",
                                    modifier = Modifier
                                        .size(80.dp)
                                        .padding(bottom = 8.dp),
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.error)
                                )
                            }

                            // Error text
                            Text(
                                text = message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )

                            // Retry button
                            Button(
                                onClick = { viewModel.fetchCharacters(reset = true) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Retry", style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun SearchBarWithFilterIcon(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onFilterClick: () -> Unit
) {
    TextField(
        value = searchQuery,
        onValueChange = onSearchChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        placeholder = { Text("Search characters…") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        },
        trailingIcon = {
            IconButton(onClick = onFilterClick) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_filter_list_24),
                    contentDescription = "Filter",
                    tint = Color.Black
                )
            }
        },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color(0xFFF0F0F0),
            focusedContainerColor = Color(0xFF2196F3),
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(32.dp),
        singleLine = true
    )
}

@Composable
fun FilterSheetContent(
    selectedStatus: String,
    onStatusSelected: (String) -> Unit,
    selectedSpecies: String,
    onSpeciesSelected: (String) -> Unit
) {
    val statusOptions = listOf("All", "Alive", "Dead", "Unknown")
    val speciesOptions = listOf("All", "Human", "Alien", "Robot", "Mythological", "Other")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Filter by Status",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(statusOptions) { status ->
                FilterChip(
                    text = status,
                    selected = selectedStatus == status
                ) {
                    onStatusSelected(status)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Filter by Species",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(speciesOptions) { species ->
                FilterChip(
                    text = species,
                    selected = selectedSpecies == species
                ) {
                    onSpeciesSelected(species)
                }
            }
        }
    }
}


@Composable
fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (selected) MaterialTheme.colorScheme.primary else Color(0xFFE0E0E0),
        onClick = onClick,
        tonalElevation = if (selected) 4.dp else 1.dp
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}
