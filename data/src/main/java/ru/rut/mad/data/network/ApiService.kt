package ru.rut.mad.data.network

import retrofit2.http.GET
import retrofit2.http.Query
import ru.rut.mad.data.network.response.CatImageDto

interface ApiService {
    @GET("v1/images/search")
    suspend fun getCatImages(
        @Query("limit") limit: Int = 20
    ): List<CatImageDto>
}