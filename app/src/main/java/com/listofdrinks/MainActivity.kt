package com.listofdrinks

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Bundle
import android.view.animation.LinearInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.listofdrinks.api.CocktailViewModel
import com.listofdrinks.api.FilterType
import com.listofdrinks.ui.theme.ListOfDrinksTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


val LocalCocktailViewModel = compositionLocalOf<CocktailViewModel> {
    error("No CocktailViewModel provided")
}


val GlobalDarkTheme = mutableStateOf(false)
val LocalDarkTheme = compositionLocalOf { GlobalDarkTheme }


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply{

            splashScreen.setOnExitAnimationListener { provider ->

                val wobble = ObjectAnimator.ofFloat(provider.iconView, "rotation", 0f, -15f,0f,15f, 0f).apply {
                    duration = 400
                    repeatMode = ObjectAnimator.RESTART
                    repeatCount = 1
                    interpolator = LinearInterpolator()
                }


                val fadeOut = ObjectAnimator.ofFloat(provider.iconView, "alpha", 1f, 0f).apply {
                    duration = 800
                    doOnEnd { provider.remove() }
                }

                val animatorSet = AnimatorSet().apply {
                    playSequentially(wobble, fadeOut)
                }

                animatorSet.start()
            }
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var wasInit by rememberSaveable { mutableStateOf(false) }
            val isDarkTheme =
                LocalDarkTheme.current
            val isSystemDarkTheme = isSystemInDarkTheme()

            if(!wasInit) {
                LaunchedEffect(Unit) {
                    isDarkTheme.value = isSystemDarkTheme
                    wasInit = true
                }
            }

            ListOfDrinksTheme(darkTheme = isDarkTheme.value) {


                val cocktailViewModel: CocktailViewModel = viewModel()
                CompositionLocalProvider(LocalCocktailViewModel provides cocktailViewModel,LocalDarkTheme provides GlobalDarkTheme) {

                        ResponsiveLayout()



                }
            }
        }
    }




}
fun checkInternet(connectivityManager: ConnectivityManager): Boolean {
    return connectivityManager.activeNetwork != null
}


@Composable
fun NoInternetScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(enabled = false, onClick = {}),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "No internet",
                tint = Color.Red,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))


            Text(
                text = "No internet connection!",
                color = Color.Red,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

        }
    }
} 


@Preview(showBackground = true)
@Composable
fun ResponsiveLayout() {

    val cocktailViewModel= LocalCocktailViewModel.current

    val context = LocalContext.current

    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val isConnected = rememberSaveable {
        mutableStateOf(checkInternet(connectivityManager))
    }


    LaunchedEffect(isConnected.value) {

        if(isConnected.value
        && (cocktailViewModel.alcoDrinksList == null
                || cocktailViewModel.nonAlcoDrinksList == null)) {
            cocktailViewModel.fetchAlcoholicDrinks(true);
            cocktailViewModel.fetchAlcoholicDrinks(false);
        }
    }


    LaunchedEffect(Unit) {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                isConnected.value = true
            }

            override fun onLost(network: Network) {
                isConnected.value = false
            }
        }

        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    val navController = rememberNavController()
    val drinkState = rememberSaveable { mutableStateOf("No drink chosen") }
    val currentDestination = rememberSaveable { mutableStateOf("list") }


        BoxWithConstraints {
            NavHost(navController = navController, startDestination = currentDestination.value) {

                composable("list") {

                    drinkState.value = "No drink chosen"
                    var grid = false;
                    if (maxWidth > 600.dp) {
                        grid = true
                    }

                    CategoriesScreen(grid, navController = navController)

                }
                composable("details/{drink}") { backStackEntry ->
                    val drinkArgument = backStackEntry.arguments?.getString("drink")
                    if (drinkArgument != null) {
                        drinkState.value = drinkArgument
                    }
                    DetailsScreen(drinkState.value, navController = navController)
                }


            }
        }
        if(!isConnected.value) {
            NoInternetScreen()
        }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(grid: Boolean, navController: NavController){
    val scrollBehavior = TopAppBarDefaults
        .enterAlwaysScrollBehavior(rememberTopAppBarState())


    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {

            DrawerInsides(navController)

        }
    ) {


        Scaffold(topBar = {

            MediumTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text("Choose a drink", color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f))


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
                )
            )




        }, modifier = Modifier.fillMaxSize()) { innerPadding ->
            Categories(
                grid,
                navController = navController,
                innerPadding,
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            )
        }
    }
}

@Composable
fun Categories(grid: Boolean, navController: NavController, innerPadding: PaddingValues, modifier: Modifier = Modifier) {
    val tabs = listOf("Home", "Alcoholic", "Non-alcoholic")
    val pagerState = rememberPagerState(pageCount = {tabs.size})
    val coroutineScope = rememberCoroutineScope()

    Column (modifier = modifier.padding(innerPadding)){

        TabRow(
            selectedTabIndex = pagerState.currentPage
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(title) }
                )
            }
        }


        HorizontalPager(

            state = pagerState
        ) { page ->
            when (page) {
                0 -> HomeScreen(grid)
                1 -> ListScreen(grid,true,navController = navController, onDrinkSelected = {

                        id ->
                    navController.navigate("details/$id") {
                    }
                }, modifier = modifier
                )
                2 -> ListScreen(grid,false,navController = navController, onDrinkSelected = {

                        id ->
                    navController.navigate("details/$id") {
                    }
                }, modifier = modifier
                )
            }
        }
    }
}

@Composable
fun HomeScreen(grid:Boolean) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clickable(enabled = false, onClick = {})
            .verticalScroll(scrollState),
        contentAlignment = Alignment.Center
    ) {
        if (grid) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Main logo",
                    modifier = Modifier
                        .size(250.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.wrapContentWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Welcome to DrinkApp!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Your perfect cocktail adventure starts here!",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }



        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Main logo",
                    modifier = Modifier
                        .size(250.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Welcome to DrinkApp!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Your perfect cocktail adventure starts here!",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
    }





}

@Composable
fun DrawerInsides(navController: NavController){
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry.value?.destination?.route
    val focusManager = LocalFocusManager.current



    ModalDrawerSheet(
    ) {
        Column(modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())) {
            NavigationDrawerItem(
                label = { Text("Home") },
                selected = currentDestination == "details",
                onClick = { navController.navigate("list") },
                icon = {
                    Icon(Icons.Default.Home, contentDescription = "Home")
                }
            )

            val isDarkTheme =
                LocalDarkTheme.current

            NavigationDrawerItem(
                label = {
                    if (isDarkTheme.value) Text("Light theme")
                    else Text("Dark theme")
                },
                selected = currentDestination == "details",
                onClick = {
                    isDarkTheme.value =
                        !isDarkTheme.value
                },
                icon = {
                    if (isDarkTheme.value)
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.light_mode_24px),
                            contentDescription = "Light theme"
                        )
                    else
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.dark_mode_24px),
                            contentDescription = "Dark theme"
                        )
                }
            )


            Spacer(
                modifier = Modifier.padding(8.dp)
            )

            val cocktailViewModel= LocalCocktailViewModel.current


            TextField(
                value = cocktailViewModel.getSearchedQuery(),
                onValueChange = { cocktailViewModel.updateSearchQuery(it) },
                label = { Text("Search") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                )

            )

            NavigationDrawerItem(
                label = { Text("Clear filter") },
                selected = false,
                onClick = { cocktailViewModel.updateSearchQuery("") },
                icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.close_24px),
                        contentDescription = "Clear"
                    )
                }

            )

            val filterOptions = listOf(FilterType.NAME, FilterType.CATEGORY, FilterType.INGREDIENTS, FilterType.INSTRUCTIONS)
            Column {
                filterOptions.forEach { filter ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { cocktailViewModel.switchMode(filter) }
                    ) {
                        RadioButton(
                            selected = cocktailViewModel.searchMode == filter,
                            onClick = { cocktailViewModel.switchMode(filter) }
                        )
                        Text(text = filter.name.lowercase().replaceFirstChar { it.uppercase() }, modifier = Modifier.padding(start = 8.dp))
                        when(filter) {
                            FilterType.NAME -> if(cocktailViewModel.searchQuery != "") Text(text = "(" + cocktailViewModel.searchQuery + ")" , modifier = Modifier.padding(start = 8.dp))
                            FilterType.CATEGORY -> if(cocktailViewModel.categoryQuery != "") Text(text = "(" + cocktailViewModel.categoryQuery + ")", modifier = Modifier.padding(start = 8.dp))
                            FilterType.INGREDIENTS -> if(cocktailViewModel.ingredientQuery != "") Text(text = "(" + cocktailViewModel.ingredientQuery + ")", modifier = Modifier.padding(start = 8.dp))
                            FilterType.INSTRUCTIONS -> if(cocktailViewModel.instructionQuery != "") Text(text = "(" + cocktailViewModel.instructionQuery + ")", modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }



        }
    }
}