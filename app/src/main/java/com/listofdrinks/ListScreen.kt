package com.listofdrinks

import android.animation.ObjectAnimator
import android.view.animation.LinearInterpolator
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.listofdrinks.api.CocktailViewModel
import com.listofdrinks.api.DrinkIndex
import kotlinx.coroutines.delay

@Composable
fun ListScreen(grid:Boolean, alco:Boolean, navController: NavController,modifier: Modifier = Modifier, onDrinkSelected: (String) -> Unit) {

    val scrollViewModel: ScrollStateViewModel = viewModel(key = if (alco) "alco" else "nonAlco")

    val listState = remember(alco)  { scrollViewModel.listState }
    val gridState = remember(alco)  { scrollViewModel.gridState }

    val configuration = LocalConfiguration.current
    val lastOrientation = rememberSaveable { mutableStateOf(configuration.orientation) }


    LaunchedEffect(configuration.orientation) {
        if (configuration.orientation != lastOrientation.value) {
            lastOrientation.value = configuration.orientation

            val listTemp = LazyListState(
                firstVisibleItemIndex = listState.firstVisibleItemIndex,
                firstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset
            )

            val gridTemp = LazyGridState(
                firstVisibleItemIndex = gridState.firstVisibleItemIndex,
                firstVisibleItemScrollOffset = gridState.firstVisibleItemScrollOffset
            )
            delay(50)

            listState.scrollToItem(gridTemp.firstVisibleItemIndex, 0)
            gridState.scrollToItem(listTemp.firstVisibleItemIndex, 0)

        }

    }


    Scaffold(
        topBar = {
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        if (grid) {
            GridBody(
                alco,
                gridState,
                navController,
                onDrinkSelected,
                innerPadding,
                modifier = modifier
            )
        } else {
            ListBody(
                alco,
                listState,
                navController,
                onDrinkSelected,
                innerPadding,
                modifier = modifier
            )


        }
    }

}


@Composable
fun ListBody(alco: Boolean, listState: LazyListState,navController: NavController, onDrinkSelected: (String) -> Unit, innerPadding: PaddingValues, modifier: Modifier = Modifier) {

    val cocktailViewModel= LocalCocktailViewModel.current

    val drinkList = filterDrinks(alco,cocktailViewModel)


    drinkList?.let { drinks ->

        LazyColumn(
            modifier = modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
            ,
            state = listState,
            verticalArrangement = Arrangement.spacedBy(12.dp),

            ) {
            items(drinkList) { item ->
                ListElement(item.idDrink,item.strDrink, item.strDrinkThumb, onDrinkSelected)
            }

        }
    }

    if(drinkList == null || drinkList.size == 0){

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "No results",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Oops! No drinks found.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

    }

}

@Composable
fun ListElement(id: String,name: String,link: String?, onDrinkSelected: (String) -> Unit){
    var isLoading by remember { mutableStateOf(true) }
    Card(
        modifier = Modifier
            .clickable(onClick = {
                onDrinkSelected(id)
            })
            .padding(horizontal = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
        )
    ) {
        Column {
            Box(modifier = Modifier
                .fillMaxSize(),
                contentAlignment = Alignment.Center) {
                if (isLoading) {

                    waitingAnimation()

                }

                link?.let {
                    AsyncImage(
                        model = link,
                        contentDescription = "Obraz drinka",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().height(150.dp),
                        onSuccess = { isLoading = false },
                        onError = { isLoading = false }
                    )
                }
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
fun GridBody(alco: Boolean, gridState: LazyGridState, navController: NavController, onDrinkSelected: (String) -> Unit, innerPadding: PaddingValues, modifier: Modifier = Modifier) {

    val cocktailViewModel = LocalCocktailViewModel.current

    val drinkList = filterDrinks(alco,cocktailViewModel)

    drinkList?.let { drinks ->

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(16.dp),
            state = gridState,
            modifier = modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(drinkList) { item ->
                ListElement(item.idDrink,item.strDrink, item.strDrinkThumb, onDrinkSelected)
            }

        }
    }
}

fun filterDrinks(alco: Boolean, cocktailViewModel: CocktailViewModel): MutableList<DrinkIndex>? {


    var drinkList = if (alco) {
        cocktailViewModel.alcoDrinksList
    } else {
        cocktailViewModel.nonAlcoDrinksList
    }

    //Filtorwanie po nazwie
    val filterPhrase = cocktailViewModel.searchQuery;
    drinkList = drinkList?.filter{it.strDrink.contains(filterPhrase, ignoreCase = true)} as MutableList<DrinkIndex>?

    //Filtrowanie po składnikach
    if(cocktailViewModel.ingredientQuery != "") {
        drinkList = drinkList?.filter {drinkId ->
            val drink = cocktailViewModel.drinksMap[drinkId.idDrink.toInt()]
            listOfNotNull(
                drink?.strIngredient1, drink?.strIngredient2, drink?.strIngredient3, drink?.strIngredient4, drink?.strIngredient5,
                drink?.strIngredient6, drink?.strIngredient7, drink?.strIngredient8, drink?.strIngredient9, drink?.strIngredient10,
                drink?.strIngredient11, drink?.strIngredient12, drink?.strIngredient13, drink?.strIngredient14, drink?.strIngredient15
            ).any { it.contains(cocktailViewModel.ingredientQuery, ignoreCase = true) == true }
        } as MutableList<DrinkIndex>?
    }


    //Filtrowanie po opisie
    if(cocktailViewModel.instructionQuery != "") {
        drinkList = drinkList?.filter{ drinkId ->
            val drink = cocktailViewModel.drinksMap[drinkId.idDrink.toInt()]
            drink?.strInstructions?.contains(cocktailViewModel.instructionQuery, ignoreCase = true) ?: false
        } as MutableList<DrinkIndex>?

    }


    //Filtrowanie po kategorii
    if(cocktailViewModel.categoryQuery != "") {
        drinkList = drinkList?.filter{ drinkId ->
            val drink = cocktailViewModel.drinksMap[drinkId.idDrink.toInt()]
            drink?.strCategory?.contains(cocktailViewModel.categoryQuery, ignoreCase = true) ?: false
        } as MutableList<DrinkIndex>?
    }
    return drinkList
}

@Composable
fun waitingAnimation(){
    val rotationAnimator = ObjectAnimator.ofFloat(0f, 360f).apply {
        duration = 1000
        repeatCount = ObjectAnimator.INFINITE
        interpolator = LinearInterpolator()
    }

    val animatedRotation = remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        rotationAnimator.addUpdateListener { animation ->
            animatedRotation.value = animation.animatedValue as Float
        }
        rotationAnimator.start()
    }

    Icon(
        imageVector = ImageVector.vectorResource(R.drawable.local_bar_24px),
        contentDescription = "Rotating Icon",
        modifier = Modifier
            .size(48.dp)
            .rotate(animatedRotation.value)
    )
}