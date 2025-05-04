package preferences

import java.util.prefs.Preferences

class UserPreferences {
    private var userPrefs: Preferences = Preferences.userRoot().node("jvision")
    fun getToken(): String? {
        return userPrefs.get("token", "")
    }

    fun putToken(token: String) {
        userPrefs.put("token", token)
    }
}