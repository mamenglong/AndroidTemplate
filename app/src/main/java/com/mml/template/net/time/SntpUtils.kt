package com.mml.template.net.time

import android.os.SystemClock
import com.mml.template.net.time.TimeService.elapsedRealTimeMillis
import com.mml.template.util.QuietFinalUtils.close
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.experimental.and
import kotlin.math.abs

/*
 * Original work Copyright (C) 2008 The Android Open Source Project
 * Modified work Copyright (C) 2016, Instacart
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ /**
 * Simple SNTP client class for retrieving network time.
 */
object SntpUtils {
    private const val RESPONSE_INDEX_ORIGINATE_TIME = 0
    private const val RESPONSE_INDEX_RECEIVE_TIME = 1
    private const val RESPONSE_INDEX_TRANSMIT_TIME = 2
    private const val RESPONSE_INDEX_RESPONSE_TIME = 3
    private const val RESPONSE_INDEX_ROOT_DELAY = 4
    private const val RESPONSE_INDEX_DISPERSION = 5
    private const val RESPONSE_INDEX_STRATUM = 6
    private const val RESPONSE_INDEX_RESPONSE_TICKS = 7
    private const val RESPONSE_INDEX_SIZE = 8
    private const val NTP_PORT = 123
    private const val NTP_MODE = 3
    private const val NTP_VERSION = 3
    private const val NTP_PACKET_SIZE = 48
    private const val INDEX_VERSION = 0
    private const val INDEX_ROOT_DELAY = 4
    private const val INDEX_ROOT_DISPERSION = 8
    private const val INDEX_ORIGINATE_TIME = 24
    private const val INDEX_RECEIVE_TIME = 32
    private const val INDEX_TRANSMIT_TIME = 40

    // 70 years plus 17 leap days
    private const val OFFSET_1900_TO_1970 = (365L * 70L + 17L) * 24L * 60L * 60L
    private const val DEFAULT_ROOT_DELAY_MAX = 100f
    private const val DEFAULT_ROOT_DISPERSION_MAX = 100f
    private const val DEFAULT_SERVER_RESPONSE_DELAY_MAX = 500
    private const val DEFAULT_UDP_SOCKET_TIMEOUT_IN_MILLIS = 30000

    /**
     * See θ :
     * https://en.wikipedia.org/wiki/Network_Time_Protocol#Clock_synchronization_algorithm
     */
    private fun getClockOffset(response: LongArray): Long {
        return (response[RESPONSE_INDEX_RECEIVE_TIME] - response[RESPONSE_INDEX_ORIGINATE_TIME] +
                (response[RESPONSE_INDEX_TRANSMIT_TIME] - response[RESPONSE_INDEX_RESPONSE_TIME])) / 2
    }

    /**
     * Sends an NTP request to the given host and processes the response.
     *
     * @param ntpHost host name of the server.
     */
    @Throws(IOException::class)
    internal fun requestTime(
        ntpHost: String,
        rootDelayMax: Float = DEFAULT_ROOT_DELAY_MAX,
        rootDispersionMax: Float = DEFAULT_ROOT_DISPERSION_MAX,
        serverResponseDelayMax: Int =
            DEFAULT_SERVER_RESPONSE_DELAY_MAX,
        timeoutInMillis: Int = DEFAULT_UDP_SOCKET_TIMEOUT_IN_MILLIS
    ): OffsetTime {
        var socket: DatagramSocket? = null
        return try {
            val buffer = ByteArray(NTP_PACKET_SIZE)
            val address = InetAddress.getByName(ntpHost)
            this.console.log { arrayOf("requestTime ntpHost: ", address) }
            val request =
                DatagramPacket(buffer, buffer.size, address, NTP_PORT)
            writeVersion(buffer)
            // -----------------------------------------------------------------------------------
// get current time and write it to the request packet
            val requestTime = elapsedRealTimeMillis() // TimeUtils.currentTimeMillis();
            val requestTicks = elapsedRealTimeMillis()
            writeTimeStamp(buffer, INDEX_TRANSMIT_TIME, requestTime)
            socket = DatagramSocket()
            socket.soTimeout = timeoutInMillis
            socket.send(request)
            // -----------------------------------------------------------------------------------
// read the response
            val t = LongArray(RESPONSE_INDEX_SIZE)
            val response = DatagramPacket(buffer, buffer.size)
            socket.receive(response)
            val responseTicks = SystemClock.elapsedRealtime()
            t[RESPONSE_INDEX_RESPONSE_TICKS] = responseTicks
            // -----------------------------------------------------------------------------------
// extract the results
// See here for the algorithm used:
// https://en.wikipedia.org/wiki/Network_Time_Protocol#Clock_synchronization_algorithm
            val originateTime =
                readTimeStamp(buffer, INDEX_ORIGINATE_TIME) // T0
            val receiveTime =
                readTimeStamp(buffer, INDEX_RECEIVE_TIME) // T1
            val transmitTime =
                readTimeStamp(buffer, INDEX_TRANSMIT_TIME) // T2
            val responseTime = requestTime + (responseTicks - requestTicks) // T3
            t[RESPONSE_INDEX_ORIGINATE_TIME] = originateTime
            t[RESPONSE_INDEX_RECEIVE_TIME] = receiveTime
            t[RESPONSE_INDEX_TRANSMIT_TIME] = transmitTime
            t[RESPONSE_INDEX_RESPONSE_TIME] = responseTime
            // -----------------------------------------------------------------------------------
// check validity of response
            t[RESPONSE_INDEX_ROOT_DELAY] = read(buffer, INDEX_ROOT_DELAY)
            val rootDelay = doubleMillis(t[RESPONSE_INDEX_ROOT_DELAY])
            if (rootDelay > rootDelayMax) {
                throw InvalidNtpServerResponseException(
                    "Invalid response from NTP server. %s violation. %f [actual] > %f [expected]",
                    "root_delay",
                    rootDelay.toFloat(),
                    rootDelayMax
                )
            }
            t[RESPONSE_INDEX_DISPERSION] = read(buffer, INDEX_ROOT_DISPERSION)
            val rootDispersion = doubleMillis(t[RESPONSE_INDEX_DISPERSION])
            if (rootDispersion > rootDispersionMax) {
                throw InvalidNtpServerResponseException(
                    "Invalid response from NTP server. %s violation. %f [actual] > %f [expected]",
                    "root_dispersion",
                    rootDispersion.toFloat(),
                    rootDispersionMax
                )
            }
            val mode = (buffer[0] and 0x7)
            if (mode.toInt() != 4 && mode.toInt() != 5) {
                throw InvalidNtpServerResponseException("untrusted mode value for TrueTime: $mode")
            }
            val stratum: Int = (buffer[1] and 0xff.toByte()).toInt()
            t[RESPONSE_INDEX_STRATUM] = stratum.toLong()
            if (stratum < 1 || stratum > 15) {
                throw InvalidNtpServerResponseException("untrusted stratum value for TrueTime: $stratum")
            }
            val leap = ((buffer[0].toInt() shr 6) and 0x3).toByte()
            if (leap.toInt() == 3) {
                throw InvalidNtpServerResponseException("unsynchronized server responded for TrueTime")
            }
            val delay =
                abs(responseTime - originateTime - (transmitTime - receiveTime))
                    .toDouble()
            if (delay >= serverResponseDelayMax) {
                throw InvalidNtpServerResponseException(
                    "%s too large for comfort %f [actual] >= %f [expected]",
                    "server_response_delay",
                    delay.toFloat(),
                    serverResponseDelayMax.toFloat()
                )
            }
            val timeElapsedSinceRequest =
                abs(originateTime - elapsedRealTimeMillis())
            if (timeElapsedSinceRequest >= 10000) {
                throw InvalidNtpServerResponseException(
                    "Request was sent more than 10 seconds back " +
                            timeElapsedSinceRequest
                )
            }
            val offsetTime = parseServerTime(t)

            this.console.log { "[REQUEST] SUCCESS Host: $ntpHost OffsetTime: {$offsetTime} Cost: ${responseTicks - requestTicks} ms" }
            offsetTime
        } catch (e: Exception) {
            this.console.log { arrayOf("[REQUEST] FAIL for $ntpHost", " -> ", e) }
            throw e
        } finally {
            close(socket)
        }
    }


    private fun parseServerTime(response: LongArray): OffsetTime {
        val seed = sntpTime(response)
        val offset = response[RESPONSE_INDEX_RESPONSE_TICKS]
        return OffsetTime(
            OffsetTime.TYPE_NTP,
            seed,
            offset
        )
    }

    private fun sntpTime(response: LongArray): Long {
        val clockOffset = getClockOffset(response)
        val responseTime = response[RESPONSE_INDEX_RESPONSE_TIME]
        return responseTime + clockOffset
    }
    // -----------------------------------------------------------------------------------
// private helpers
    /**
     * Writes NTP version as defined in RFC-1305
     */
    private fun writeVersion(buffer: ByteArray) { // mode is in low 3 bits of first byte
// version is in bits 3-5 of first byte
        buffer[INDEX_VERSION] = (NTP_MODE or (NTP_VERSION shl 3)).toByte()
    }

    /**
     * Writes system time (milliseconds since January 1, 1970)
     * as an NTP time stamp as defined in RFC-1305
     * at the given offset in the buffer
     */
    private fun writeTimeStamp(
        buffer: ByteArray,
        @Suppress("SameParameterValue") offset: Int,
        time: Long
    ) {
        var offsetLocal = offset
        var seconds = time / 1000L
        val milliseconds = time - seconds * 1000L
        // consider offset for number of seconds
// between Jan 1, 1900 (NTP epoch) and Jan 1, 1970 (Java epoch)
        seconds += OFFSET_1900_TO_1970
        // write seconds in big endian format
        buffer[offsetLocal++] = (seconds shr 24).toByte()
        buffer[offsetLocal++] = (seconds shr 16).toByte()
        buffer[offsetLocal++] = (seconds shr 8).toByte()
        buffer[offsetLocal++] = (seconds shr 0).toByte()
        val fraction = milliseconds * 0x100000000L / 1000L
        // write fraction in big endian format
        buffer[offsetLocal++] = (fraction shr 24).toByte()
        buffer[offsetLocal++] = (fraction shr 16).toByte()
        buffer[offsetLocal++] = (fraction shr 8).toByte()
        // low order bits should be random data
        @Suppress("UNUSED_CHANGED_VALUE")
        buffer[offsetLocal++] = (Math.random() * 255.0).toInt().toByte()
    }

    /**
     * @param offset offset index in buffer to start reading from
     * @return NTP timestamp in Java epoch
     */
    private fun readTimeStamp(buffer: ByteArray, offset: Int): Long {
        val seconds = read(buffer, offset)
        val fraction = read(buffer, offset + 4)
        return (seconds - OFFSET_1900_TO_1970) * 1000 + fraction * 1000L / 0x100000000L
    }

    /**
     * Reads an unsigned 32 bit big endian number
     * from the given offset in the buffer
     *
     * @return 4 bytes as a 32-bit long (unsigned big endian)
     */
    private fun read(buffer: ByteArray, offset: Int): Long {
        val b0 = buffer[offset]
        val b1 = buffer[offset + 1]
        val b2 = buffer[offset + 2]
        val b3 = buffer[offset + 3]
        return (ui(b0).toLong() shl 24) +
                (ui(b1).toLong() shl 16) +
                (ui(b2).toLong() shl 8) +
                ui(b3).toLong()
    }

    /***
     * Convert (signed) byte to an unsigned int
     *
     * Java only has signed types so we have to do
     * more work to get unsigned ops
     *
     * @param b input byte
     * @return unsigned int value of byte
     */
    private fun ui(b: Byte): Int {
        return b.toInt() and 0xFF
    }

    /**
     * Used for root delay and dispersion
     *
     *
     * According to the NTP spec, they are in the NTP Short format
     * viz. signed 16.16 fixed point
     *
     * @param fix signed fixed point number
     * @return as a double in milliseconds
     */
    private fun doubleMillis(fix: Long): Double {
        return fix / 65.536
    }
}