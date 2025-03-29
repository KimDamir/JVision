package dataclasses

class Dictionary (words: List<Word>) {

}



class Word(val writings: List<String>, val readings:List<String>, val translations:List<String>) {
    override fun toString(): String {
        return this.writings[0]
    }
}

class Query(val writing: String, val translation:String, val query_text: String, val time: String)

class User(val email: String, val username: String, val password: String)

class AuthorizationResponse(val token:String = "", val error: String = "")

class RegistrationResponse(val message: String = "", val error: String = "")