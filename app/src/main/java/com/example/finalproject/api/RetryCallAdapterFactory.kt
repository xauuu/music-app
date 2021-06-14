package com.example.finalproject.api

import android.util.Log
import okhttp3.Request
import okio.Timeout
import retrofit2.*
import java.io.IOException
import java.lang.reflect.Type
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicInteger


class RetryCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type, annotations: Array<Annotation>,
        retrofit: Retrofit,
    ): CallAdapter<*, *> {

        var itShouldRetry = 0
        val retry = getRetry(annotations)
        if (retry != null) {
            itShouldRetry = retry.value
        }
        Log.d(TAG,
            "Starting a CallAdapter with {} retries.$itShouldRetry")
        return RetryCallAdapter(
            retrofit.nextCallAdapter(this, returnType, annotations),
            itShouldRetry
        )
    }

    private fun getRetry(annotations: Array<Annotation>): Retry? {
        for (annotation in annotations) {
            if (annotation is Retry) {
                return annotation
            }
        }
        return null
    }

    internal class RetryCallAdapter<R, T>(
        private val delegated: CallAdapter<R, T>,
        private val maxRetries: Int,
    ) :
        CallAdapter<R, T> {
        override fun responseType(): Type {
            return delegated.responseType()
        }

        override fun adapt(call: Call<R>): T {
            return delegated.adapt(if (maxRetries > 0) RetryingCall(call,
                maxRetries) else call)
        }
    }

    internal class RetryingCall<R>(private val delegated: Call<R>, private val maxRetries: Int) :
        Call<R> {
        @Throws(IOException::class)
        override fun execute(): Response<R> {
            return delegated.execute()
        }

        override fun enqueue(callback: Callback<R>) {
            delegated.enqueue(RetryCallback(delegated, callback,
                maxRetries))
        }

        override fun isExecuted(): Boolean {
            return delegated.isExecuted
        }

        override fun cancel() {
            delegated.cancel()
        }

        override fun isCanceled(): Boolean {
            return delegated.isCanceled
        }

        override fun clone(): Call<R> {
            return RetryingCall(delegated.clone(), maxRetries)
        }

        override fun request(): Request {
            return delegated.request()
        }

        override fun timeout(): Timeout {
            return delegated.timeout()
        }
    }

    internal class RetryCallback<T>(
        private val call: Call<T>,
        private val callback: Callback<T>,
        private val maxRetries: Int,
    ) :
        Callback<T> {
        private val retryCount = AtomicInteger(0)
        override fun onResponse(call: Call<T>, response: Response<T>) {
            if (!response.isSuccessful && retryCount.incrementAndGet() <= maxRetries) {
                Log.d(TAG, "Call with no success result code: {} " + response.code())
                retryCall()
            } else {
                callback.onResponse(call, response)
            }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            Log.d(TAG, "Call failed with message:  " + t.message, t)
            if (retryCount.incrementAndGet() <= maxRetries) {
                retryCall()
            } else if (maxRetries > 0) {
                Log.d(TAG, "No retries left sending timeout up.")
                callback.onFailure(call,
                    TimeoutException(String.format("No retries left after %s attempts.",
                        maxRetries)))
            } else {
                callback.onFailure(call, t)
            }
        }

        private fun retryCall() {
            Log.w(TAG, "" + retryCount.get() + "/" + maxRetries + " " + " Retrying...")
            call.clone().enqueue(this)
        }
    }

    companion object {
        private const val TAG = "RetryCallAdapterFactory"
        fun create(): RetryCallAdapterFactory {
            return RetryCallAdapterFactory()
        }
    }
}