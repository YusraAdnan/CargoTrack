package com.CargoTrack.cargotrack.API

import com.CargoTrack.cargotrack.Model.ApiResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("prediction")
    fun SendImage(@Part image: MultipartBody.Part): Observable<ApiResponse>
}