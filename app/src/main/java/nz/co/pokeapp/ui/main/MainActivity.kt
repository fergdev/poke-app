package nz.co.pokeapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import nz.co.pokeapp.Application
import nz.co.pokeapp.databinding.ActivityMainBinding
import nz.co.pokeapp.service.PokemonResult
import nz.co.pokeapp.ui.base.BaseActivity
import nz.co.pokeapp.ui.details.DetailsActivity
import nz.co.pokeapp.ui.widget.Listener
import nz.co.pokeapp.ui.widget.PokemonResultAdapter
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel
        get() = ViewModelProviders.of(
            this,
            viewModelFactory
        )[MainViewModel::class.java]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Application.uiComponent.inject(this)

        val binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setContentView(binding.root)
        setSupportActionBar(toolbar)

        val pokemonResultAdapter = PokemonResultAdapter()
        pokemonResultAdapter.listener = object : Listener {
            override fun onPokemonResultSelectedListener(pokemonResult: PokemonResult) {
                viewModel.onPokemonSelected(pokemonResult)
            }
        }
        binding.recyclerView.adapter = pokemonResultAdapter
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!pokemonResultAdapter.loading && dy > 0) {
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val pastVisiblesItems = layoutManager.findFirstVisibleItemPosition()

                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount * VALUE_PAGEINATION_OFFSET) {
                        viewModel.loadMore()
                    }
                }
            }
        })

        viewModel.pokemons.observe(this, Observer {
            pokemonResultAdapter.results = it
        })

        viewModel.loading.observe(this, Observer {
            pokemonResultAdapter.loading = it
        })

        viewModel.errorMessage.observe(this, Observer {
            showError(it)
        })

        viewModel.selectedPokemon.observe(this, Observer {
            startActivity(Intent(this, DetailsActivity::class.java).apply {
                putExtra(DetailsActivity.KEY_ID, it)
            })
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(nz.co.pokeapp.R.menu.menu_main, menu)

        val searchItem = menu.findItem(nz.co.pokeapp.R.id.searchBar)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = getString(nz.co.pokeapp.R.string.search_pokemon)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) viewModel.onViewSearchString(newText)
                return true
            }
        })
        return true
    }

    companion object {
        private const val VALUE_PAGEINATION_OFFSET = 0.8
    }
}