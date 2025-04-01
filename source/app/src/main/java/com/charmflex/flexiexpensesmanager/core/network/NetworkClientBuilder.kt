package com.charmflex.flexiexpensesmanager.core.network

import android.content.Context
import android.util.Log
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

interface NetworkClientBuilder {

    fun addInterceptor(interceptor: Interceptor): NetworkClientBuilder

    fun <T> buildApi(c: Class<T>): T

    fun buildRetrofit(): Retrofit

}

// todo this shouldn't be exposed
internal class DefaultNetworkClientBuilder(
    private val appContext: Context,
    private val interceptors: MutableList<Interceptor> = mutableListOf(),
) : NetworkClientBuilder {

    private val baseUrl: String
        get() = "https://fem.charmflex.com"

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
                .addConverterFactory(MoshiConverterFactory.create(buildMoshi()))
                .build()
        }
    }

    private fun buildMoshi(): Moshi {
        return Moshi.Builder()
            .add(Instant::class.java, object : JsonAdapter<Instant>() {
                override fun toJson(writer: JsonWriter, value: Instant?) {
                    value?.let {
                        // Convert to ISO 8601 in UTC (ending with 'Z')
                        writer.value(it.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                    } ?: writer.nullValue()
                }

                // Parse ISO 8601 string to Instant
                override fun fromJson(reader: JsonReader): Instant? {
                    return try {
                        OffsetDateTime.parse(reader.nextString()).toInstant()
                    } catch (e: Exception) {
                        null
                    }
                }
            })
            .build()
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