package com.middleton.restaurants.features.restaurant_search.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.middleton.restaurants.R
import com.middleton.restaurants.features.restaurant_search.domain.model.Restaurant
import com.middleton.restaurants.ui.theme.LocalSpacing

@Composable
fun RestaurantSearchScreen(
    viewModel: RestaurantSearchViewModel = hiltViewModel(),
    scaffoldState: ScaffoldState
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val context = LocalContext.current

    LaunchedEffect(true) {
        viewModel.snackBarEvent.collect { event ->
            scaffoldState.snackbarHostState.showSnackbar(
                message = event.message.asString(context)
            )
        }
    }

    RestaurantsSearchContent(state = state, onSearch = { postcode ->
        viewModel.onSearch(postcode)
    })
}

@Composable
fun RestaurantsSearchContent(state: RestaurantsState, onSearch: (String) -> Unit) {
    val spacing = LocalSpacing.current
    Column {
        SearchView(onSearch = onSearch)

        if (state.isLoading) {
            LoadingScreen()
        } else {
            if (state.restaurants.isEmpty()) {
                Column(
                    modifier = Modifier.padding(
                        start = spacing.spaceMedium,
                        end = spacing.spaceMedium
                    )
                ) {
                    Text(
                        text = stringResource(R.string.search_screen_description),
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(
                            top = spacing.spaceMedium,
                            bottom = spacing.spaceMedium
                        )
                    )
                    Divider()
                }
            } else {
                RestaurantsList(state.restaurants)
            }
        }
    }
}

@Composable
fun SearchView(onSearch: (String) -> Unit) {
    val focusManager = LocalFocusManager.current
    var inputValue by rememberSaveable { mutableStateOf("") }

    TextField(
        modifier = Modifier
            .fillMaxWidth(),
        value = inputValue,
        onValueChange = { input ->
            inputValue = input
        },
        placeholder = {
            Text(
                text = stringResource(R.string.search_by_postcode),
                style = MaterialTheme.typography.h4,
            )
        },
        textStyle = MaterialTheme.typography.h4,
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = stringResource(id = R.string.search_by_postcode),
                modifier = Modifier
                    .padding(15.dp)
                    .size(24.dp)
            )
        },
        trailingIcon = {
            if (inputValue.isNotBlank()) {
                IconButton(
                    onClick = {
                        inputValue = ""
                        onSearch("")
                    }
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(R.string.clear_postcode_content_description),
                        modifier = Modifier
                            .padding(15.dp)
                            .size(24.dp)
                    )
                }
            }
        },
        singleLine = true,
        shape = RectangleShape,
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search,
            capitalization = KeyboardCapitalization.Characters
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch(inputValue)
                focusManager.clearFocus()
            }
        )
    )
}

@Composable
fun RestaurantsList(restaurants: List<Restaurant>) {
    val spacing = LocalSpacing.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = spacing.spaceSmall, end = spacing.spaceSmall)
    ) {
        items(restaurants) { restaurant ->
            RestaurantListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = spacing.spaceSmall),
                restaurant = restaurant
            )
        }
    }
}

@Composable
fun RestaurantListItem(modifier: Modifier, restaurant: Restaurant) {
    val spacing = LocalSpacing.current
    Card(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(spacing.spaceSmall)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = restaurant.name,
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Bold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Star,
                        tint = MaterialTheme.colors.primary,
                        contentDescription = null
                    )
                    Text(text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(
                                restaurant.rating
                            )
                        }
                        append("/6 ")
                        append("(${restaurant.ratingCount})")
                    })
                }

                Text(text = restaurant.cuisineTypes)
            }
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterVertically)
            ) {
                AsyncImage(
                    model = restaurant.logoUrl,
                    contentDescription = null
                )
            }

        }
    }
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            Modifier
                .size(48.dp)
                .align(Alignment.Center),
            color = MaterialTheme.colors.onBackground
        )
    }
}
