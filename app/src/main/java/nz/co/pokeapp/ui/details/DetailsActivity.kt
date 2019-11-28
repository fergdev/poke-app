package nz.co.pokeapp.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*
import nz.co.pokeapp.Application
import nz.co.pokeapp.databinding.ActivityDetailsBinding
import nz.co.pokeapp.ui.base.BaseActivity
import javax.inject.Inject


class DetailsActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel
        get() = ViewModelProviders.of(
            this,
            viewModelFactory
        )[DetailsViewModel::class.java]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Application.uiComponent.inject(this)

        val binding = ActivityDetailsBinding.inflate(LayoutInflater.from(this))
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setContentView(binding.root)

        viewModel.pokemonState.observe(this, Observer {
            toolbar.title = it.name
        })

        viewModel.errorMessage.observe(this, Observer {
            showError(it)
        })

        viewModel.uiStarted(intent.getIntExtra(KEY_ID, -1))
    }

    companion object {
        const val KEY_ID = "id"
    }
}