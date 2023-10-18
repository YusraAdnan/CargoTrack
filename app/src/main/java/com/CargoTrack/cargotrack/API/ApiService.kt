package com.CargoTrack.cargotrack.API

import android.net.Uri
import com.CargoTrack.cargotrack.Model.ApiResponse
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import android.content.Context
import retrofit2.http.Body

interface ApiService {
    //@Multipart
    @POST("prediction")
    //https://stackoverflow.com/questions/69371464/upload-image-to-server-using-multipart-and-request-body-in-android-in-kotlin
    //fun SendImage(@Part image: MultipartBody.Part): Observable<ApiResponse>
    fun SendImage(@Body body: RequestBody): Observable<ApiResponse>

}
