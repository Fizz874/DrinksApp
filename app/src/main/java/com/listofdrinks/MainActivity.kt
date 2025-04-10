package com.listofdrinks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.listofdrinks.api.CocktailViewModel
import com.listofdrinks.ui.theme.ListOfDrinksTheme


val LocalCocktailViewModel = compositionLocalOf<CocktailViewModel> {
    error("No CocktailViewModel provided")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ListOfDrinksTheme {


                val cocktailViewModel: CocktailViewModel = viewModel()
                CompositionLocalProvider(LocalCocktailViewModel provides cocktailViewModel) {
                    ResponsiveLayout()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResponsiveLayout() {

    val cocktailViewModel= LocalCocktailViewModel.current

    LaunchedEffect(Unit) {
        cocktailViewModel.fetchDrinks()

    }


    val navController = rememberNavController()
    val drinkState = rememberSaveable { mutableStateOf("No drink chosen") }
    val currentDestination = rememberSaveable { mutableStateOf("list") }

    BoxWithConstraints {
            NavHost(navController = navController, startDestination = currentDestination.value) {
                composable("list") {

                    drinkState.value =  "No drink chosen"
                    var grid = false;
                    if (maxWidth > 600.dp) {
                        grid = true
                    }

                        ListScreen(grid,navController = navController, onDrinkSelected = {

                                name ->
                            navController.navigate("details/$name") {
                            }
                        }
                        )


                }
                composable("details/{drink}") {
                    backStackEntry ->
                    val drinkArgument = backStackEntry.arguments?.getString("drink")
                    if (drinkArgument != null) {
                        drinkState.value = drinkArgument
                    }
                        DetailsScreen(drinkState.value, navController = navController)
                }
            }
    }
}
