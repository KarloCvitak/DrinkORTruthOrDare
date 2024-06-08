package hr.tvz.android.drinkingtruthordare.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("getLogo")
    fun getLogo(@Query("language") language: String): Call<LogoResponse>
}
