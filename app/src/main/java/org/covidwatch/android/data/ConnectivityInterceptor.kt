package org.covidwatch.android.data

import android.content.Context
import android.net.ConnectivityManager
import okhttp3.Interceptor
import okhttp3.Response
import org.covidwatch.android.exposurenotification.NoConnectionException
import java.io.IOException

class ConnectivityInterceptor(private val mContext: Context) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return if (isOnline(mContext)) {
            val builder = chain.request().newBuilder()
            chain.proceed(builder.build())
        } else {
            throw NoConnectionException()
        }
    }

    // TODO: 07.06.2020 Replace deprecated code
    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connectivityManager.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }
}