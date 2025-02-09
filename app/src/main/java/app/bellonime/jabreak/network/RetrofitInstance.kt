package app.bellonime.jabreak.network

// Misalnya, pada RetrofitInstance.kt kamu sudah menginisialisasi Retrofit:
object RetrofitInstance {
    private const val BASE_URL = "https://bellonime.vercel.app/"

    private val retrofit = retrofit2.Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
        .build()

    val api: ApiService = retrofit.create(ApiService::class.java)
}

