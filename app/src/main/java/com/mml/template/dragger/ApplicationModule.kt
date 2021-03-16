package com.mml.template.dragger

import android.app.Application
import com.mml.template.App
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@Module
@InstallIn(ApplicationComponent::class)
class ApplicationModule {

    @Provides
    fun provideMyApplication(application: Application): App {
        return application as App
    }

}
