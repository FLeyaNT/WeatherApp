package com.example.weatherapp.ui.search

import com.example.weatherapp.R
import com.example.weatherapp.ui.common.UiMessage
import com.google.gson.JsonParseException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException
import retrofit2.HttpException

fun searchErrorMessage(error: Exception): UiMessage = when (error) {
    is UnknownHostException -> UiMessage(R.string.server_not_found)
    is SocketTimeoutException -> UiMessage(R.string.server_timeout)
    is SSLException -> UiMessage(R.string.ssl_error)
    is HttpException -> UiMessage(R.string.http_error, listOf(error.code()))
    is JsonParseException -> UiMessage(R.string.json_error)
    is IOException -> UiMessage(R.string.network_error)
    else -> UiMessage(R.string.search_error)
}
