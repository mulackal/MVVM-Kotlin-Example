package com.eduvy.demo.di

import com.eduvy.demo.data.api.EduvyApi
import com.eduvy.demo.di.utils.ServiceInterSeptor
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

const val BASE_URL = "http://13.235.128.120:8005/api/"


val mNetworkModule = module {
    single {
        createWebService<EduvyApi>(
            okHttpClient = createHttpClient(),
            baseUrl = BASE_URL,
            factory = RxJava2CallAdapterFactory.create()
        )
    }
}


/* Returns a custom OkHttpClient instance with interceptor. Used for building Retrofit service */
fun createHttpClient(): OkHttpClient {
    val client = OkHttpClient.Builder()
    client.readTimeout(8 * 60, TimeUnit.SECONDS)
    client.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
    return client.addInterceptor(
//        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
        ServiceInterSeptor()
    ).build()
}



inline fun <reified T> createWebService(
    okHttpClient: OkHttpClient,
    factory: CallAdapter.Factory,
    baseUrl: String
): T {
    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addCallAdapterFactory(factory)
        .client(okHttpClient)
        .build()
    return retrofit.create(T::class.java)

}