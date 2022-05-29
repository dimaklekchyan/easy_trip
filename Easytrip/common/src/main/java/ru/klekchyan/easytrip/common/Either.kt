package ru.klekchyan.easytrip.common

class Either<out T>(
    val status: Status,
    val data: T? = null,
    val errorCode: Int = 0,
    val errorInfo: String = "",
) {
    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    class None {}

    fun isError() = status == Status.ERROR
    fun isSuccess() = status == Status.SUCCESS
    fun isLoading() = status == Status.LOADING

    suspend fun onError(callBack: suspend (code: Int, info: String) -> Unit) {
        if (isError()) {
            callBack(errorCode, errorInfo)
        }
    }

    suspend fun onLoading(callBack: suspend () -> Unit) {
        if (isLoading()) {
            callBack()
        }
    }

    suspend fun onSuccess(callBack: suspend (data: T?) -> Unit) {
        if (isSuccess()) {
            callBack(data)
        }
    }

    companion object {
        fun <T> success(data: T?): Either<T> =
            Either(
                Status.SUCCESS,
                data
            )

        fun <T> loading(): Either<T> =
            Either(
                Status.LOADING
            )

        fun <T> error(code: Int, info: String = ""): Either<T> =
            Either(
                status = Status.ERROR,
                errorCode = code,
                errorInfo = info
            )
    }
}

typealias EitherNone = Either<Either.None>