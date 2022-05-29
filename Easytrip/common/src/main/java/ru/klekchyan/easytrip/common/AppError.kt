package ru.klekchyan.easytrip.common

sealed class AppError(val code: Int, private val message: String = "") {

    class Db(message: String = "") : AppError(db, message)
    class Network(message: String = "") : AppError(network, message)
    class Unknown(message: String = "") : AppError(unknown, message)

    companion object {
        private const val db = 10001
        private const val network = 10002
        private const val unknown = 100009

        fun error(code: Int, message: String = ""): AppError = when (code) {
            db -> Db(message)
            network -> Network(message)
            else -> Unknown(message)
        }
    }

    fun message() = message.ifEmpty {
        when (code) {
            db -> "Возникла ошибка на сервере"
            network -> "Ошибка сети internet, проверьте подключние"
            else -> "Произошла неизвестная ошибка"
        }
    }
}
