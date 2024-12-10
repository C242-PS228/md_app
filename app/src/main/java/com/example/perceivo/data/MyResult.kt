package com.example.perceivo.data

import kotlin.Result

sealed class MyResult<out T> {
    data class Success<out T>(val data: T) : MyResult<T>()
    data class Error(val exception: Exception) : MyResult<Nothing>()
    object Loading : MyResult<Nothing>()

}