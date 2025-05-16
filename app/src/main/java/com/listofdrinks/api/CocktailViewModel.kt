package com.listofdrinks.api

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


enum class FilterType {
    NAME, CATEGORY, INGREDIENTS, INSTRUCTIONS
}


class CocktailViewModel : ViewModel() {


    var alcoDrinksList by mutableStateOf<MutableList<DrinkIndex>?>(null)
    var nonAlcoDrinksList by mutableStateOf<MutableList<DrinkIndex>?>(null)

    var drinksMap by mutableStateOf(mutableMapOf<Int, Drink>())

    fun addDrink(drink : Drink) {
        drinksMap = drinksMap.toMutableMap().apply {
            this[drink.idDrink.toInt()] = drink
        }
    }

    var searchQuery by mutableStateOf("")
        private set

    var ingredientQuery by mutableStateOf("")
        private set

    var instructionQuery by mutableStateOf("")
        private set


    var categoryQuery by mutableStateOf("")
        private set


    var searchMode by mutableStateOf(FilterType.NAME)

    fun switchMode(newFilter: FilterType) {
        searchMode = newFilter
    }


    fun updateSearchQuery(newQuery: String) {
        when(searchMode){
            FilterType.NAME -> searchQuery = newQuery
            FilterType.CATEGORY -> categoryQuery = newQuery
            FilterType.INGREDIENTS -> ingredientQuery = newQuery
            FilterType.INSTRUCTIONS -> instructionQuery = newQuery
        }
    }

    fun getSearchedQuery() : String{
        when(searchMode){
            FilterType.NAME -> return searchQuery
            FilterType.CATEGORY -> return categoryQuery
            FilterType.INGREDIENTS -> return ingredientQuery
            FilterType.INSTRUCTIONS -> return instructionQuery
        }
    }

    suspend fun getDrinkById(drinkId: Int): Drink? {
        try {
        val response = ApiClient.apiService.lookupDrinkById(drinkId)
        return response.drinks?.firstOrNull()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }


    fun fetchAlcoholicDrinks(alco: Boolean) {
        viewModelScope.launch {
            try {
                val type = if (alco) "Alcoholic" else "Non_Alcoholic"
                val response = withContext(Dispatchers.IO) {
                    ApiClient.apiService.getAlcoholicDrinks(type)
                }

                val drinksList = response.drinks?.toMutableList() ?: mutableListOf()

                if (alco) {
                    alcoDrinksList = drinksList.toMutableList()
                } else {
                    nonAlcoDrinksList = drinksList.toMutableList()
                }

                for (drink in drinksList) {
                    val fetchedDrink = withContext(Dispatchers.IO) {
                        getDrinkById(drink.idDrink.toInt())
                    }
                    if (fetchedDrink != null) {
                        addDrink(fetchedDrink)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchOneDrink(id:String){
        viewModelScope.launch {

            val fetchedDrink =
                getDrinkById(id.toInt())
            if (fetchedDrink != null) {
                addDrink(fetchedDrink)
            }
        }
    }

}