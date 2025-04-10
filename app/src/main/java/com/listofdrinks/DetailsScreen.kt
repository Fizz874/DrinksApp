package com.listofdrinks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.listofdrinks.api.CocktailViewModel
import com.listofdrinks.ui.theme.ListOfDrinksTheme
import kotlin.math.round



@Composable
fun DetailsScreen(drink : String?, navController: NavController) {
    val item = drink ?: "No drink chosen"

    Scaffold( topBar = {
        CustomHeaderFallback(item, onBackClick = {
            //activity?.finish()
            if(!navController.popBackStack()){
                navController.navigate("list")
            }

        })},
        modifier = Modifier.fillMaxSize()) { innerPadding ->
        Details(item,innerPadding)


    }


}


@Composable
fun Details(item: String, innerPadding: PaddingValues, modifier: Modifier = Modifier) {
    val cocktailViewModel = LocalCocktailViewModel.current
    val drinkData = cocktailViewModel.drinksList?.find { it.strDrink == item }



    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding)
        ,
    ){
        LazyColumn() {
            items(1) { index ->
                Text(
                    text = item,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(8.dp, top = 8.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.tertiary,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                if (drinkData != null) {
                    var recipe = drinkData.strInstructions;
                    var photo = drinkData.strDrinkThumb
                    val listOfIngr =
                        listOfNotNull(
                            drinkData.strIngredient1?.let { it to drinkData.strMeasure1 },
                            drinkData.strIngredient2?.let { it to drinkData.strMeasure2 },
                            drinkData.strIngredient3?.let { it to drinkData.strMeasure3 },
                            drinkData.strIngredient4?.let { it to drinkData.strMeasure4 },
                            drinkData.strIngredient5?.let { it to drinkData.strMeasure5 },
                            drinkData.strIngredient6?.let { it to drinkData.strMeasure6 },
                            drinkData.strIngredient7?.let { it to drinkData.strMeasure7 },
                            drinkData.strIngredient8?.let { it to drinkData.strMeasure8 },
                            drinkData.strIngredient9?.let { it to drinkData.strMeasure9 },
                            drinkData.strIngredient10?.let { it to drinkData.strMeasure10 },
                            drinkData.strIngredient11?.let { it to drinkData.strMeasure11 },
                            drinkData.strIngredient12?.let { it to drinkData.strMeasure12 },
                            drinkData.strIngredient13?.let { it to drinkData.strMeasure13 },
                            drinkData.strIngredient14?.let { it to drinkData.strMeasure14 },
                            drinkData.strIngredient15?.let { it to drinkData.strMeasure15 }
                        ).filter { pair ->
                            pair.first.isNotEmpty() && !pair.second.isNullOrEmpty()
                        }


                    Text(
                        text = recipe,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 18.sp
                    )

                    Text(
                        text = "Ingredients: ",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .padding(horizontal = 16.dp),
                        fontSize = 22.sp

                    )

                    listOfIngr.forEach { pair ->
                        Row() {
                            Text(
                                text = pair.first+":",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .padding(start = 24.dp),
                                fontSize = 18.sp

                            )
                            Text(
                                text = pair.second ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .padding(start = 8.dp),
                                fontSize = 18.sp

                            )


                        }


                    }
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.tertiary,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    TimerCard()

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.tertiary,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    photo?.let { DisplayImage(it) }

                }
            }


        }
    }
}

@Composable
fun DisplayImage(link :String, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        AsyncImage(
            model = link,
            contentDescription = "Obraz drinka",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun CustomHeaderFallback(item: String, onBackClick: () -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            //.padding(8.dp)
            .statusBarsPadding()
    ) {
        Column() {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                //horizontalArrangement = Arrangement.SpaceBetween

            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Text(
                    text = "Back",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

    }
}



@Composable
fun TimerCard(modifier: Modifier = Modifier) {

    val TimerVM: TimerViewModel = viewModel()

    var inputMinutes by rememberSaveable { mutableStateOf("1") } // Wprowadzone minuty
    var inputSeconds by rememberSaveable { mutableStateOf("0") } // Wprowadzone sekundy


    var isCardVisible by rememberSaveable { mutableStateOf(false) }
    Card(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()

    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            IconButton(
                onClick = { isCardVisible = !isCardVisible }, // Przełącz widoczność
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
            ) {
                if (isCardVisible) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Hide Card"
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowUp,
                        contentDescription = "Show Card"
                    )
                }
            }

            Text(
                text = "Timer",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.TopStart).padding(20.dp),
                fontSize = 20.sp
            )


            Column {
                TextButton(
                    onClick = { isCardVisible = !isCardVisible },
                    modifier = Modifier.height(60.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
                {}

                AnimatedVisibility(visible = isCardVisible) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {

                        // Wprowadzanie minut
                        OutlinedTextField(
                            value = inputMinutes,
                            onValueChange = { newValue ->
                                inputMinutes = newValue.filter { it.isDigit() }
                            },
                            label = { Text(text = "Minutes") },
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Wprowadzanie sekund
                        OutlinedTextField(
                            value = inputSeconds,
                            onValueChange = { newValue ->
                                inputSeconds = newValue.filter { it.isDigit() }
                            },
                            label = { Text(text = "Seconds") },
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Button(
                            onClick = {
                                TimerVM.setTimer(inputMinutes, inputSeconds)
                            },
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Text(text = "Set Timer")
                        }

                        // Wyświetlanie pozostałego czasu
                        Text(
                            text = "Remaining: ${(round(TimerVM.timeleft / 1000.0) * 1000).toInt() / 60000} min ${
                                (round(
                                    TimerVM.timeleft / 1000.0
                                ).toInt() % 60)
                            } sec",
                            fontSize = 24.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Dynamika przycisków w zależności od stanu minutnika
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (!TimerVM.isRunning && !TimerVM.isFinished) {

                                IconButton(
                                    onClick = {
                                        TimerVM.runTheTimer()
                                    },
                                    modifier = Modifier.background(
                                        MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.PlayArrow,
                                        contentDescription = "Start",
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }


                                if (TimerVM.timeleft != TimerVM.initialTime)
                                    Spacer(modifier = Modifier.size(60.dp, 1.dp))
                            }

                            if (TimerVM.isRunning) {

                                IconButton(
                                    onClick = {
                                        TimerVM.pause()
                                    },
                                    modifier = Modifier.background(
                                        MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                ) {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.pause_24px),
                                        contentDescription = "Stop",
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.size(60.dp, 1.dp))
                            }
                            if (TimerVM.isRunning || TimerVM.isFinished || (TimerVM.timeleft != TimerVM.initialTime)) {

                                IconButton(
                                    onClick = {
                                        TimerVM.refresh()
                                    },
                                    modifier = Modifier.background(
                                        MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Refresh,
                                        contentDescription = "Reset",
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}