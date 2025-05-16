package com.listofdrinks

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.listofdrinks.api.Drink
import kotlinx.coroutines.launch
import kotlin.math.round


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun DetailsScreen(drink : String?, navController: NavController) {
    val item = drink ?: "No drink chosen"


    val scrollBehavior = TopAppBarDefaults
        .enterAlwaysScrollBehavior(rememberTopAppBarState())

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val cocktailViewModel = LocalCocktailViewModel.current
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickContact()) { uri ->
        uri?.let {
            val selectedContact = getPhoneNumberFromContactUri(context, it)

            Log.d("Contacts", "Selected Contact: $selectedContact")

            val drinkData = cocktailViewModel.drinksMap[item.toInt()]
            if (drinkData != null) {
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

                val ingredientsString =
                    listOfIngr.joinToString(separator = "\n") { "${it.first}: ${it.second}" }

                val message =
                    "Ingredients for: ${drinkData.strDrink}: \n\n" + ingredientsString


                // Utwórz intent do wysłania SMS-a
                val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("smsto:" + selectedContact)
                    putExtra("sms_body", message)
                }

                // Uruchom aktywność wysyłania SMS-a
                context.startActivity(smsIntent)


            }
        }
    }



    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerInsides(navController)

        }
    ) {

        Scaffold(
            topBar = {

                MediumTopAppBar(
                    title = {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(onClick = {
                                if (!navController.popBackStack()) {
                                    navController.navigate("list")
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            Text(
                                text = "Back",
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(start = 8.dp).weight(1f)
                            )
                            IconButton(onClick = {
                                coroutineScope.launch { drawerState.open() } // Otwiera szufladę

                            }, modifier = Modifier.padding(8.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )

                            }
                        }

                    },
                    scrollBehavior = scrollBehavior,
                    collapsedHeight = 0.dp,
                    expandedHeight = 48.dp,
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    ),

                )

            },

            floatingActionButton = {
                val contactsPermissionState = rememberPermissionState(permission = Manifest.permission.READ_CONTACTS)

                FloatingActionButton(
                    onClick = {

                        if (contactsPermissionState.status.isGranted) {
                            launcher.launch(null)
                        } else {
                            contactsPermissionState.launchPermissionRequest()
                        }

                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Send ingredients via SMS"
                    )
                }
            }
        ) { innerPadding ->


            Details(
                item,
                innerPadding,
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            )

        }
    }

}



fun getPhoneNumberFromContactUri(context: Context, contactUri: Uri): String {
    var phoneNumber = "No number found"

    val cursor = context.contentResolver.query(
        contactUri,
        arrayOf(ContactsContract.Contacts._ID),
        null, null, null
    )

    cursor?.use {
        if (it.moveToFirst()) {
            val contactId = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts._ID))

            val phoneCursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                arrayOf(contactId),
                null
            )

            phoneCursor?.use { pc ->
                if (pc.moveToFirst()) {
                    phoneNumber = pc.getString(pc.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                }
            }
        }
    }

    return phoneNumber
}

@Composable
fun Details(item: String, innerPadding: PaddingValues, modifier: Modifier = Modifier) {
    val cocktailViewModel = LocalCocktailViewModel.current


    val drinkData = cocktailViewModel.drinksMap[item.toInt()]

    if (drinkData != null) {
        val name = drinkData.strDrink;
        Box(
            modifier = modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
        ) {


            LazyColumn() {

                item{
                    var photo = drinkData.strDrinkThumb


                    BoxWithConstraints {

                        val configuration = LocalConfiguration.current
                        val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp
                        var horizon = false;
                        if (maxWidth > 600.dp && isLandscape) {
                            horizon = true
                        }

                        if(horizon){
                            Row() {
                                Box(
                                    Modifier
                                        .fillMaxWidth(0.4f)

                                ) {
                                    TimerCard( modifier = Modifier
                                        .fillMaxSize()
                                        .zIndex(2f))

                                    Column() {
                                        Spacer(
                                            modifier = Modifier
                                                .height(96.dp)
                                                .fillMaxWidth()
                                                .padding(32.dp)
                                        )

                                        photo?.let { DisplayImage(it) }


                                }

                                }
                                VerticalDivider(color = MaterialTheme.colorScheme.tertiary,
                                    thickness = 2.dp,
                                    modifier = Modifier.padding(vertical = 8.dp))

                                Column(){

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(88.dp)
                                        .align(Alignment.Start),
                                    contentAlignment = Alignment.CenterStart
                                    ) {

                                        Text(
                                            text = name,
                                            style = MaterialTheme.typography.bodyLarge,
                                            modifier = Modifier.padding(8.dp, top = 8.dp)/*.height(78.dp)*/,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 30.sp
                                        )
                                    }



                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.tertiary,
                                        thickness = 2.dp,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )

                                    DisplayText(drinkData)

                                }


                            }
                        } else {
                            Column() {
                                photo?.let { DisplayImage(it) }

                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.tertiary,
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )


                                Text(
                                    text = name,
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

                                DisplayText(drinkData)

                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.tertiary,
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )

                                TimerCard()


                            }

                        }

                    }



                }


            }
        }

    } else {

        cocktailViewModel.fetchOneDrink(item)
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            waitingAnimation()
        }

    }
}


@Composable
fun DisplayText(drinkData: Drink){

    var recipe = drinkData.strInstructions;

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


    Column(){
        Text(
            text = recipe,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp),
            fontSize = 18.sp
        )


        val cat = drinkData.strCategory
        cat?.let{
            Row(modifier = Modifier
                .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Category: ",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .padding(horizontal = 16.dp).alignByBaseline(),
                    fontSize = 22.sp

                )

                Text(
                    text = cat,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp).alignByBaseline(),
                    fontSize = 18.sp
                )
            }
        }


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
                    text = pair.first + ":",
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
fun CustomNumberPicker(
    selectedValue: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    modifier: Modifier = Modifier
) {
    val rangeList = range.toList()
    val listState = rememberLazyListState()

    Box(
        modifier = Modifier
            .height(120.dp)
            .width(100.dp)

    ) {

        LazyColumn(
            state = listState,
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(rangeList.size * 3) { index ->
                val actualIndex = index % rangeList.size  // Zapętlenie listy
                val value = rangeList[actualIndex]
                val isSelected = actualIndex == (listState.firstVisibleItemIndex +1 ) % rangeList.size



                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent // Zmiana tła
                        )) {

                    Text(
                        text = value.toString(),
                        fontSize = if (isSelected) 24.sp else 18.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(8.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
                    )

                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(MaterialTheme.colorScheme.onSecondaryContainer)
                .align(Alignment.TopCenter)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(MaterialTheme.colorScheme.onSecondaryContainer)
                .align(Alignment.BottomCenter)
        )
    }



    LaunchedEffect(selectedValue) {

        val secondIndex = (rangeList.indexOf(selectedValue-1) + rangeList.size) % (rangeList.size * 3)

        listState.scrollToItem(secondIndex)
    }



    LaunchedEffect(listState.firstVisibleItemIndex) {
        val selectedIndex = (listState.firstVisibleItemIndex+1 ) % rangeList.size
        onValueChange(rangeList[selectedIndex])
    }

}

@Composable
fun TimerCard(modifier: Modifier = Modifier) {

    val TimerVM: TimerViewModel = viewModel()

    var inputMinutes by rememberSaveable { mutableStateOf("1") }
    var inputSeconds by rememberSaveable { mutableStateOf("0") }


    var isCardVisible by rememberSaveable { mutableStateOf(false) }
    Card(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )

    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            IconButton(
                onClick = { isCardVisible = !isCardVisible },
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

                AnimatedVisibility(visible = isCardVisible
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {

                        AnimatedVisibility(
                            visible = !TimerVM.isRunning && TimerVM.timeleft == TimerVM.initialTime,
                            enter = fadeIn() + slideInVertically(initialOffsetY = { -300 })
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                AnimatedVisibility(
                                    visible = true,
                                    enter = fadeIn() + slideInVertically(initialOffsetY = { -50 })
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = "Minutes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                        CustomNumberPicker(
                                            selectedValue = inputMinutes.toInt(),
                                            onValueChange = {
                                                inputMinutes = it.toString()
                                                TimerVM.setTimer(inputMinutes, inputSeconds)
                                            },
                                            range = 0..59,
                                            modifier
                                        )
                                    }
                                }

                                AnimatedVisibility(
                                    visible = true,
                                    enter = fadeIn() + slideInVertically(initialOffsetY = { 50 })
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = "Seconds", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                        CustomNumberPicker(
                                            selectedValue = inputSeconds.toInt(),
                                            onValueChange = {
                                                inputSeconds = it.toString()
                                                TimerVM.setTimer(inputMinutes, inputSeconds)
                                            },
                                            range = 0..59,
                                            modifier
                                        )
                                    }
                                }
                            }
                        }

                        // Wyświetlanie pozostałego czasu
                        if(!TimerVM.isFinished ) {
                            Text(
                                text = "Remaining: ${(round(TimerVM.timeleft / 1000.0) * 1000).toInt() / 60000} min ${
                                    (round(
                                        TimerVM.timeleft / 1000.0
                                    ).toInt() % 60)
                                } sec",
                                fontSize = 24.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        } else{
                            Text(
                                text = "Time's up!",
                                fontSize = 24.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }

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


