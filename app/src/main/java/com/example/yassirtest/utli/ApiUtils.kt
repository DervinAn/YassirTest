package com.example.yassirtest.utli


import retrofit2.HttpException
import java.io.IOException

suspend fun <T> safeApiCall(apiCall: suspend () -> T): Resource<T> {
    return try {
        Resource.Success(apiCall())
    } catch (e: IOException) {
        Resource.Error("No internet connection. Please check your network.")
    } catch (e: HttpException) {
        Resource.Error("Server error: ${e.code()} ${e.message()}")
    } catch (e: Exception) {
        Resource.Error("Something went wrong: ${e.localizedMessage ?: "Unknown error"}")
    }
}
