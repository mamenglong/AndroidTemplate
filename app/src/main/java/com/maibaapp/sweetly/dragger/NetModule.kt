package com.maibaapp.sweetly.dragger

import com.maibaapp.base.App
import com.maibaapp.sweetly.BuildConfig
import com.maibaapp.base.DataReport
import com.maibaapp.base.log.LogUtil
import com.maibaapp.sweetly.manager.UserManager
import com.maibaapp.sweetly.net.api.HEADER
import com.maibaapp.sweetly.net.api.NetworkSignParamsFactory
import com.maibaapp.sweetly.net.cookie.CookieJarImpl
import com.maibaapp.sweetly.net.cookie.PersistentCookieStore
import com.maibaapp.sweetly.net.ssl.AlwaysTrustHostnameVerifier
import com.readystatesoftware.chuck.ChuckInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.CacheControl
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(ApplicationComponent::class)
object NetModule {
    const val LOG_OK_HTTP_TAG = "okHttp"
    @Singleton
    @Provides
  fun provideOkHttpClient(
  ): OkHttpClient {
      return getOkClient()
  }
    private fun getOkClient(): OkHttpClient {
        try {
            SSLContext.getInstance("TLSv1.2")
        } catch (ignored: Throwable) {
        }
        // Create a trust manager that does not validate certificate chains
        val trustManager = object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate?>?, authType: String?) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate?>?, authType: String?) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return emptyArray()
            }


        }
        // Install the all-trusting trust manager
        val sslContext = SSLContext.getInstance("TLSv1.2")
        sslContext.init(null, arrayOf(trustManager), SecureRandom())
        // Create an ssl socket factory with our all-trusting manager
        val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
        val builder = OkHttpClient.Builder()
        builder.sslSocketFactory(sslSocketFactory,trustManager )
        builder.connectTimeout(300, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .hostnameVerifier(AlwaysTrustHostnameVerifier)
            // .protocols(listOf(Protocol.HTTP_1_1, Protocol.HTTP_2))
            .followRedirects(true)
            .followSslRedirects(true)
        builder.addInterceptor { chain ->
            val newRequestBuilder = chain.request().newBuilder()
                .removeHeader("User-Agent")
                .addHeader(
                    "User-Agent",
                    UserManager.userAgent
                )
            newRequestBuilder.header("channel", com.maibaapp.base.DataReport.getChannel(App.application))
            newRequestBuilder.header(HEADER.AUTHORIZATION, UserManager.authentication)
            newRequestBuilder.cacheControl(CacheControl.FORCE_NETWORK)
            newRequestBuilder.build()
            chain.proceed(newRequestBuilder.build())
        }
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(ChuckInterceptor(App.application).showNotification(BuildConfig.DEBUG))
            val loggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    LogUtil.i(message, LOG_OK_HTTP_TAG)
                }
            })
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            builder.addNetworkInterceptor(loggingInterceptor)
        }

        return builder.build()
    }

    @Singleton
    @Provides
    fun provideCallFactory(client:OkHttpClient): Call.Factory {
       return NetworkSignParamsFactory(
           client.newBuilder()
                .cookieJar(CookieJarImpl(PersistentCookieStore("cook"))) // 私有 cookie jar
                .build()
        )
    }
}