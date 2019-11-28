package nz.co.pokeapp.ui.main

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nz.co.pokeapp.R
import nz.co.pokeapp.service.PokemonResult
import nz.co.pokeapp.service.PokemonService
import java.util.regex.Pattern
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val pokemonService: PokemonService,
    application: Application
) : AndroidViewModel(application) {

    private val context: Context = application
    private var isSearching = false
    private var canLoadMore = true

    private val pokemonsList = mutableListOf<PokemonResult>()
    private val _pokemons =
        MutableLiveData<List<PokemonResult>>().apply { this.value = pokemonsList }
    val pokemons: LiveData<List<PokemonResult>> = _pokemons

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _selectedPokemon = MutableLiveData<Int>()
    val selectedPokemon: LiveData<Int> = _selectedPokemon

    init {
        _pokemons.value = mutableListOf()
        loadMore()
    }

    fun onViewSearchString(newText: String) {
        isSearching = newText.isNotBlank()
        _pokemons.value = pokemonsList.filter { it.name.startsWith(newText) }
    }

    fun onPokemonSelected(pokemonResult: PokemonResult) {
        val m = URL_INDEX_PATTERN.matcher(pokemonResult.url)
        if (m.find()) {
            _selectedPokemon.value = m.group(1).toInt()
        } else {
            _errorMessage.value = context.getString(R.string.error_message_default)
        }
    }

    fun loadMore() {
        if (isSearching || !canLoadMore) return
        viewModelScope.launch {
            _loading.value = true
            try {
                val pokemonResults =
                    pokemonService.getPokemonListAsync(_pokemons.value?.size ?: 0, PAGINATION_SIZE)
                        .await()
                pokemonsList.addAll(pokemonResults.results)
                _pokemons.value = pokemonsList
                canLoadMore = pokemonResults.next != null
            } catch (ioException: Exception) {
                _errorMessage.value = context.getString(R.string.error_message_default)
            }
            _loading.value = false
        }
    }

    companion object {
        private const val PAGINATION_SIZE = 20
        private val URL_INDEX_PATTERN = Pattern.compile("https://pokeapi.co/api/v2/pokemon/(\\d+)/")
    }
}
