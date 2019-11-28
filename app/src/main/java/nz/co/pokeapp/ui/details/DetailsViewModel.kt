package nz.co.pokeapp.ui.details

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nz.co.pokeapp.R
import nz.co.pokeapp.service.PokemonService
import javax.inject.Inject

class DetailsViewModel @Inject constructor(
    private val pokemonService: PokemonService,
    application: Application
) : AndroidViewModel(application) {

    data class PokemonState(
        val name: String,
        val height: String,
        val weight: String,
        val imageUrl: String
    )

    private val context: Context = application

    private val _pokemonState = MutableLiveData<PokemonState>()
    val pokemonState: LiveData<PokemonState> = _pokemonState

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun uiStarted(pokemonId: Int) {
        if (pokemonId == -1) {
            _errorMessage.value = context.getString(R.string.error_message_default)
            return
        }
        viewModelScope.launch {
            _loading.value = true
            try {
                pokemonService.getPokemonAsync(pokemonId).await().let {
                    _pokemonState.value = PokemonState(
                        it.name,
                        context.getString(R.string.height).format(it.height),
                        context.getString(R.string.weight).format(it.weight),
                        it.sprites.frontDefault
                    )
                }
            } catch (ioException: Exception) {
                _errorMessage.value = context.getString(R.string.error_message_default)
            }
            _loading.value = false
        }
    }
}