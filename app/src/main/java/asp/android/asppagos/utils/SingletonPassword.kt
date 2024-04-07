package asp.android.asppagos.utils

object SingletonPassword {
    private var password: String = ""

    fun saveSessionPassword(password: String) {
        this.password = password
    }

    fun getSessionPassword() = this.password
}