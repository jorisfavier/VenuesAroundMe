package fr.jorisfavier.venuesaroundme.api

import fr.jorisfavier.venuesaroundme.BuildConfig
import fr.jorisfavier.venuesaroundme.api.model.ResponseWrapper
import fr.jorisfavier.venuesaroundme.api.model.VenuesSearchResult
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.*

interface VenueService {
    @GET("venues/search")
    suspend fun searchVenues(
        @Query("ll") ll: String,
        @Query("radius") radius: Int,
        @Query("categoryId") categoryId: String
    ): ResponseWrapper<VenuesSearchResult>

    companion object {
        private const val baseUrl = BuildConfig.FOURSQUARE_BASE_URL
        private const val apiSecret = BuildConfig.CLIENT_SECRET
        private const val apiClientId = BuildConfig.CLIENT_ID
        private val apiVersion = SimpleDateFormat("yyyyMMdd", Locale.ROOT).format(Date())
        const val foodCategory = "4d4b7105d754a06374d81259"

        fun create(): VenueService {
            //We create an interceptor to add the client_id, client_secret et version to each
            //call for this API
            val interceptor = Interceptor { chain ->
                val newUrl = chain
                    .request()
                    .url()
                    .newBuilder()
                    .addQueryParameter("client_id", apiClientId)
                    .addQueryParameter("client_secret", apiSecret)
                    .addQueryParameter("v", apiVersion).build()
                val requestBuilder = chain.request().newBuilder()
                    .url(newUrl)

                val request = requestBuilder.build()
                chain.proceed(request)
            }
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
            val retrofit = Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create())
                .baseUrl(baseUrl)
                .client(client)
                .build()

            return retrofit.create(VenueService::class.java)
        }
    }
}
