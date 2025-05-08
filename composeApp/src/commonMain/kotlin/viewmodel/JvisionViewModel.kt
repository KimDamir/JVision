package const.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dataclasses.Query
import dataclasses.Word
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class JVisionViewModel: ViewModel() {
    private val _queries = MutableSharedFlow<List<Query>>()
    private val _wordList = MutableSharedFlow<List<Word>>()
    private var _chosenWord = MutableSharedFlow<Word>()
    val queries = _queries.asSharedFlow()
    val wordList = _wordList.asSharedFlow()
    val chosenWord = _chosenWord.asSharedFlow()

    fun changeWordList(newWordList: List<Word>) {

        viewModelScope.launch {
            _wordList.emit(newWordList)
            if (newWordList.isNotEmpty()) _chosenWord.emit(newWordList[0])
            else _chosenWord.emit(Word(listOf(""), listOf(""), listOf("")))
        }
    }

    fun setHistory(setQueries: List<Query>) {
        viewModelScope.launch {
            _queries.emit(setQueries)
            println("Emitted$setQueries")
        }
    }
    fun changeChosenWord(newWord: Word) {
        viewModelScope.launch {
            _chosenWord.emit(newWord)
        }
    }
}