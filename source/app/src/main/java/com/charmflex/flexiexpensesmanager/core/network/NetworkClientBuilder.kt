package com.charmflex.flexiexpensesmanager.core.network

import android.content.Context
import android.net.Uri
import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface NetworkClientBuilder {

    fun addInterceptor(interceptor: Interceptor): NetworkClientBuilder

    fun <T> buildApi(c: Class<T>): T

    fun buildRetrofit(): Retrofit

}

// todo this shouldn't be exposed
class DefaultNetworkClientBuilder(
    private val appContext: Context,
    private val interceptors: MutableList<Interceptor> = mutableListOf(),
) : NetworkClientBuilder {

    private val baseUrl: String
        get() = ""

    override fun addInterceptor(interceptor: Interceptor): NetworkClientBuilder {
        interceptors.add(interceptor)
        return this
    }

    override fun <T> buildApi(c: Class<T>): T {
        return buildRetrofit().create(c)
    }

    override fun buildRetrofit(): Retrofit {
        return synchronized(this) {
            Retrofit
                .Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }

    private fun okHttpClient(): OkHttpClient {
        return OkHttpClient()
            .newBuilder()
            .apply {
                interceptors.forEach(this::addInterceptor)
                // more to come?
            }
//            .certificatePinner(certificatePinner())
            .followRedirects(true)
            .followSslRedirects(true)
            .addInterceptor(loggingInterceptor())
            .build()
    }

//    private fun certificatePinner(): CertificatePinner {
//        return CertificatePinner.Builder()
//            .apply {
//                add("charmflex.com", appConfig.certPin)
//            }
//            .build()
//    }
//

    private fun getUrlHost(): String {
        return ""
//        return Uri.parse(appCon"fig.baseUrl).host ?: appConfig.baseUrl
    }

    private fun loggingInterceptor(): Interceptor {
        return HttpLoggingInterceptor(this::log).apply { level = HttpLoggingInterceptor.Level.BODY }
    }

    private fun log(msg: String) {
        Log.d("HTTP::", msg)
    }
}