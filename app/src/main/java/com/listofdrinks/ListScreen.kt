package com.listofdrinks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@Composable
fun ListScreen(grid:Boolean, navController: NavController, onDrinkSelected: (String) -> Unit) {
    Scaffold(topBar = {CustomHeader("All drinks")}, modifier = Modifier.fillMaxSize()) { innerPadding ->
        if(grid){
            GridBody(navController,onDrinkSelected,innerPadding)
        } else {
            ListBody(navController, onDrinkSelected, innerPadding)
        }
    }
}


@Composable
fun ListBody(navController: NavController, onDrinkSelected: (String) -> Unit, innerPadding: PaddingValues, modifier: Modifier = Modifier) {
    //val drinks = arrayOf("mojito", "margarita", "piña colada", "screwdriver")

    val cocktailViewModel= LocalCocktailViewModel.current


    val drinkList =  cocktailViewModel.drinksList
    drinkList?.let { drinks ->

        LazyColumn(
            modifier = modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(drinks) { item ->
                ListElement(item.strDrink, item.strDrinkThumb, onDrinkSelected)
            }

        }
    }




}

@Composable
fun ListElement(name: String,link: String?, onDrinkSelected: (String) -> Unit){
    Card(
        modifier = Modifier
            .clickable(onClick = {
                onDrinkSelected(name)
            })
            .padding(horizontal = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
        )
    ) {
        Column {
            link?.let {
                AsyncImage(
                    model = link,
                    contentDescription = "Obraz drinka",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(150.dp)
                )
            }
            Text(
                text = name,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}


@Composable
fun GridBody(navController: NavController, onDrinkSelected: (String) -> Unit, innerPadding: PaddingValues, modifier: Modifier = Modifier) {

    val cocktailViewModel = LocalCocktailViewModel.current


    val drinkList = cocktailViewModel.drinksList
    drinkList?.let { drinks ->

        LazyVerticalGrid(
            columns = GridCells.Fixed(3), // Liczba kolumn w siatce (2 w tym przypadku)
            contentPadding = PaddingValues(16.dp),
            modifier = modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(drinks) { item ->
                ListElement(item.strDrink, item.strDrinkThumb, onDrinkSelected)
            }

        }
    }
}

@Composable
fun CustomHeader(text : String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(10.dp)
            .statusBarsPadding()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),

            ) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}


