package dictionary

class Dictionary (words: List<Word>) {

}



class Word(val writing: String, val reading:String, val definition:String) {
    override fun toString(): String {
        return this.writing
    }
}