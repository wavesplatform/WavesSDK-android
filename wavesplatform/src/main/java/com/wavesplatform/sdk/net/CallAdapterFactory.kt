package com.wavesplatform.sdk.net

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.IOException
import java.lang.reflect.Type
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

internal class CallAdapterFactory(private val errorListener: OnErrorListener? = null) : CallAdapter.Factory() {

    private val original: RxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create()

    /**
     * Returns an [RxCallAdapterWrapper] instance
     */
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        val originalCallAdapter = original.get(returnType, annotations, retrofit) ?: return null
        return RxCallAdapterWrapper(
            retrofit,
            originalCallAdapter
                as CallAdapter<out Any, *>,
            returnType
        )
    }

    inner class RxCallAdapterWrapper<R>(
        private val retrofit: Retrofit,
        private val wrapped: CallAdapter<R, *>,
        private val returnType: Type
    ) : CallAdapter<R, Any> {

        override fun responseType(): Type {
            return wrapped.responseType()
        }

        override fun adapt(call: Call<R>): Any {
            return convert(wrapped.adapt(call))
        }

        private fun handleErrorToShow(throwable: Throwable): NetworkException {
            val retrofitException = asRetrofitException(throwable)
            errorListener?.onError(retrofitException)
            return retrofitException
        }

        private fun convert(o: Any): Any {
            return when (o) {
                is Observable<*> -> o.onErrorResumeNext { throwable: Throwable -> Observable.error(handleErrorToShow(throwable)) }
                is Single<*> -> o.onErrorResumeNext { Single.error(handleErrorToShow(it)) }
                is Completable -> o.onErrorResumeNext { Completable.error(handleErrorToShow(it)) }
                is Maybe<*> -> o.onErrorResumeNext { throwable: Throwable -> Maybe.error(handleErrorToShow(throwable)) }
                else -> o
            }
        }

        private fun asRetrofitException(throwable: Throwable): NetworkException {

            // Non-200 http error
            if (throwable is HttpException) {
                val response = throwable.response()
                if (response != null) {
                    return NetworkException.httpError(
                        response.raw().request()
                            .url().toString(),
                        response, retrofit
                    )
                }
            }

            if (throwable is TimeoutException ||
                throwable is ConnectException ||
                throwable is SocketTimeoutException ||
                throwable is UnknownHostException
            ) {
                return NetworkException.networkError(IOException(throwable.message, throwable))
            }

            return NetworkException.unexpectedError(throwable)
        }
    }
}
