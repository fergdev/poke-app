package nz.co.pokeapp

import android.app.Application
import dagger.Module
import dagger.Provides
import nz.co.pokeapp.service.ServiceModule
import nz.co.pokeapp.ui.DaggerUiComponent
import nz.co.pokeapp.ui.UiComponent
import javax.inject.Singleton

class Application : Application() {

    init {
        uiComponent = DaggerUiComponent.builder()
            .serviceModule(ServiceModule())
            .applicationModule(ApplicationModule(this))
            .build()
    }

    companion object {
        internal lateinit var uiComponent: UiComponent
    }

}

@Module
class ApplicationModule(private val application: Application) {

    @Provides
    @Singleton
    internal fun provideApplication(): Application {
        return application
    }
}
