package com.example.searchbar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.temporal.TemporalQuery

class Top250ViewModel : ViewModel(){

    var state by mutableStateOf(Top250ScreenState())

    private var searchJob: Job? = null

    fun onAction(userAction: UserAction){
        when(userAction){
            UserAction.CloseIconClicked -> {
                state = state.copy(isSearchBarVisible = false)
            }
            UserAction.SearchIconClicked -> {
                state = state.copy(isSearchBarVisible =  true)
            }
            is UserAction.TextFieldInput -> {
                state = state.copy(searchText = userAction.text)
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(500L)
                    searchMovieInList(searchQuery = userAction.text)
                }
            }
            UserAction.SortIconClicked -> {
                state = state.copy(isSortMenuVisible = true)
            }
            UserAction.SortMenuDismiss -> {
                state = state.copy(isSortMenuVisible = false)
            }
            is UserAction.SortItemClicked -> {
                when(userAction.type){
                    SortType.A2Z -> sortMovieListA2Z()
                    SortType.Z2A -> sortMovieListZ2A()
                    SortType.NONE -> sortMovieListNone()
                }
            }
        }
    }

    private fun  sortMovieListA2Z(){
        val newList = movieList.sorted()
        state = state.copy(
            list = newList
        )
    }
    private fun  sortMovieListZ2A(){
        val newList = movieList.sorted().reversed()
        state = state.copy(
            list = newList
        )
    }
    private fun  sortMovieListNone(){
        state = state.copy(
            list = movieList
        )
    }
    private fun searchMovieInList(
        searchQuery: String
    ){
        val newList = movieList.filter {
            it.contains(searchQuery, ignoreCase = true)

        }
        state = state.copy(list = newList)
    }
}


sealed class UserAction{
    object SearchIconClicked : UserAction()
    object CloseIconClicked : UserAction()
    object SortIconClicked : UserAction()
    object SortMenuDismiss : UserAction()

    data class TextFieldInput(val text: String) : UserAction()
    data class SortItemClicked(val type: SortType) : UserAction()
}

enum class SortType{
    A2Z,
    Z2A,
    NONE
}
data class Top250ScreenState(
    val searchText : String = "",
    val list: List<String> = movieList,
    val isSearchBarVisible : Boolean = false,
    val isSortMenuVisible : Boolean = false
)