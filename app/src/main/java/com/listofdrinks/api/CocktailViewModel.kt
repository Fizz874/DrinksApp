package com.listofdrinks.api

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class CocktailViewModel : ViewModel() {
    /*var drinksList: List<Drink>? = null
        private set*/
    var drinksList by mutableStateOf<MutableList<Drink>?>(null)

    fun fetchDrinks(/*firstLetter: Char*/) {
        viewModelScope.launch {
            try {
                val alphabet = listOf(
                    'A'/*, 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
                    'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
                    'U', 'V', 'W', 'X', 'Y', 'Z'*/
                )

                alphabet.forEach { letter ->
                    val list = mutableListOf<Drink>()
                    drinksList?.let { list.addAll(it) }
                    val response = ApiClient.apiService.searchDrinks(letter)
                    val responseBody = response.drinks
                    Log.d("API_JSON", responseBody?.get(1)?.strDrink.toString())
                    response.drinks?.let { list.addAll(it) }
                    drinksList = list

                    //Log.d("API_JSON2", drinksList?.get(1)?.strDrink.toString())
                }
                //Log.d("Ilość drinków", drinksList?.size.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}