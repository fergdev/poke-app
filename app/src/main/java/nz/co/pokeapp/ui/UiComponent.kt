package nz.co.pokeapp.ui

import dagger.Component
import nz.co.pokeapp.ApplicationModule
import nz.co.pokeapp.service.ServiceModule
import nz.co.pokeapp.ui.details.DetailsActivity
import nz.co.pokeapp.ui.main.MainActivity
import nz.co.pokeapp.ui.viewmodel.ViewModelModule
import javax.inject.Singleton


@Singleton
@Component(modules = [ServiceModule::class, ViewModelModule::class, ApplicationModule::class])
interface UiComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(detailsActivity: DetailsActivity)
}