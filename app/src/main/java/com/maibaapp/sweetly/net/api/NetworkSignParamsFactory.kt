package com.maibaapp.sweetly.net.api
import com.maibaapp.sweetly.ConstString.URL.REQUEST_SECRET_KEY
import com.maibaapp.sweetly.manager.UserManager
import com.maibaapp.sweetly.net.time.TimeService
import com.maibaapp.sweetly.util.Utils
import okhttp3.Call
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.tikteam.commons.codec.digest.DigestUtils
import java.nio.ByteBuffer
import java.util.*

/**
 * Created by showzeng on 2020-07-07
 * email: kingstageshow@gmail.com
 *
 * description: 自家网络请求增加一些签名参数
 */
class NetworkSignParamsFactory(val mClient: OkHttpClient) : Call.Factory {

    @Volatile
    private var mSalt: String = ""

    @Volatile
    private var mLastSaltTime: Long = 0L

    override fun newCall(request: Request): Call {
        val builder = request.newBuilder()
        addUserAgent(builder)
        addAuthorization(builder)
        signRequest(request, builder)
        return mClient.newCall(builder.build())
    }

    private fun addAuthorization(builder: Request.Builder) {
        builder.header("Authorization", UserManager.authentication)
    }

    private fun addUserAgent(builder: Request.Builder) {
        val userAgent = UserManager.userAgent
        if (!userAgent.isBlank()) {
            builder.header("User-Agent", UserManager.userAgent)
        }
    }

    private fun signParams(params: List<Pair<String, String?>>): List<Pair<String, String?>> {
        var data = params.toMutableList()
        data.add(Pair("oaid", UserManager.oaid))
        data.add(Pair("ts", "${TimeService.currentTimeMillis()}"))
        data.add(Pair("salt", getSalt()))
        data = data.sortedBy {
            it.first
        }.toMutableList()
        val str = StringBuilder()
        for (item in data) {
            str.append(item.first).append("=")
            if (item.second != null) {
                str.append(item.second)
            }
            str.append("&")
        }
        str.append("key=").append(REQUEST_SECRET_KEY)
        val sign = Utils.sha1(str.toString()).toUpperCase(Locale.ROOT)
        data.add(Pair("sign", sign))
        return data
    }

    private fun getSalt(): String {
        var salt = mSalt
        val lastSaltTime = mLastSaltTime

        val time = TimeService.elapsedRealTimeMillis()
        if (salt.isBlank() || kotlin.math.abs(time - lastSaltTime) > 120000L) {

            salt = createSalt()

            mSalt = salt
            mLastSaltTime = time
        }
        return salt
    }

    private fun createSalt(): String {
        val saltBytes = ByteArray(128)
        Utils.secureRandom.nextBytes(saltBytes)
        val timeBytes = ByteBuffer.allocate(8).putLong(TimeService.currentTimeStamp).array()

        for (i in 0 until 8) {
            saltBytes[i + i] = timeBytes[7 - i]
        }

        val saltStr = Utils.base64.encodeToString(DigestUtils.sha256(saltBytes))
        return "salt:$saltStr"
    }

    private fun signGetRequest(request: Request, builder: Request.Builder) {
        val url = request.url
        val count = url.querySize

        if (count <= 0) {
            return
        }

        val params: MutableList<Pair<String, String?>> = mutableListOf()
        for (i in 0 until count) {
            val name = url.queryParameterName(i)
            val value = url.queryParameterValue(i)
            params.add(Pair(name, value))
        }

        val urlBuilder = url.newBuilder()
        urlBuilder.query(null)

        signParams(params).forEach {
            urlBuilder.addQueryParameter(it.first, it.second)
        }

        builder.url(urlBuilder.build())
    }

    private fun signPostRequest(request: Request, builder: Request.Builder) {
        val body = request.body
        if (body == null || body !is FormBody) {
            return
        }

        val count = body.size

        if (count <= 0) {
            return
        }

        val params: MutableList<Pair<String, String>> = mutableListOf()
        for (i in 0 until count) {
            params.add(Pair(body.name(i), body.value(i)))
        }

        val formBuilder = FormBody.Builder()

        signParams(params).forEach {
            val key = it.first
            val value = it.second
            if (value != null) {
                formBuilder.add(key, value)
            }
        }

        builder.method(request.method, formBuilder.build())
    }

    private fun signRequest(request: Request, builder: Request.Builder) {
        when (request.method.toUpperCase(Locale.ROOT)) {
            "GET" -> signGetRequest(request, builder)
            "POST" -> signPostRequest(request, builder)
        }
    }
}