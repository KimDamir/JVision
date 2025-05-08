package dataclasses

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParcelableWord(val writings: List<String>, val readings:List<String>, val translations:List<String>): Parcelable {
    override fun toString(): String {
        return this.writings[0]
    }
}

fun wordToParcelable(wordList: List<Word>): ArrayList<ParcelableWord> {
    val parcelableList = ArrayList<ParcelableWord>()
    for (word in wordList) {
        parcelableList.add(ParcelableWord(word.writings, word.readings, word.translations))
    }
    return parcelableList
}

fun fromParcelable(parcelableList: ArrayList<ParcelableWord>): List<Word> {
    val wordList = ArrayList<Word>()
    for (word in parcelableList) {
        wordList.add(Word(word.writings, word.readings, word.translations))
    }
    return wordList
}
