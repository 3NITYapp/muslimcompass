package muslimcompass.direction.finddirection.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
//@InstallIn(SingletonComponent::class)
@InstallIn(ViewModelComponent::class)
class ApplicationModule {

    @Provides
    fun provideBaseUrl() = "myurlis"


    @Provides
    @Singleton()
    fun provideContext(@ApplicationContext appContext: Context): Context {
        return appContext
    }

}