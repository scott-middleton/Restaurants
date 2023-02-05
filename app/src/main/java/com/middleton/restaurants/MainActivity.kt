package com.middleton.restaurants

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.middleton.restaurants.features.restaurant_search.presentation.RestaurantSearchScreen
import com.middleton.restaurants.navigation.Route
import com.middleton.restaurants.ui.theme.RestaurantsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RestaurantsTheme {
                val navController = rememberNavController()
                val scaffoldState = rememberScaffoldState()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    scaffoldState = scaffoldState
                ) { padding ->
                    NavHost(
                        modifier = Modifier.padding(padding),
                        navController = navController,
                        startDestination = Route.RESTAURANT_SEARCH
                    ) {
                        composable(Route.RESTAURANT_SEARCH) {
                            RestaurantSearchScreen(scaffoldState = scaffoldState)
                        }
                    }
                }
            }
        }
    }
}