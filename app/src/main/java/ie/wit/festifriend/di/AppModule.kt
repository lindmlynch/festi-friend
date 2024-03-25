package ie.wit.festifriend.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ie.wit.festifriend.api.ApiService
import ie.wit.festifriend.repositories.CommunityRepository
import ie.wit.festifriend.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideBaseUrl() = Constants.BASE_URL

    @Provides
    @Singleton
    fun provideRetrofitInstance(BASE_URL: String): ApiService =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideCommunityRepository(): CommunityRepository = CommunityRepository()

}