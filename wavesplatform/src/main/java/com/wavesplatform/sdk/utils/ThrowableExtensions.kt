package com.wavesplatform.sdk.utils

import com.wavesplatform.sdk.net.NetworkException
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

fun Throwable.asRetrofitException(): NetworkException {

    // Non-200 http error
    if (this is HttpException) {
        val response = this.response()
        if (response != null) {
            return NetworkException.httpError(
                response.raw().request()
                    .url().toString(),
                response, null
            )
        }
    }

    if (this is TimeoutException ||
        this is ConnectException ||
        this is SocketTimeoutException ||
        this is UnknownHostException
    ) {
        return NetworkException.networkError(IOException(message, this))
    }

    return NetworkException.unexpectedError(this)
}
