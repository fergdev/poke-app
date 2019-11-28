package nz.co.pokeapp.ui.details

import android.app.Application
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import nz.co.pokeapp.BaseUnitTest
import nz.co.pokeapp.R
import nz.co.pokeapp.service.Pokemon
import nz.co.pokeapp.service.PokemonService
import nz.co.pokeapp.service.Sprites
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`

@ExperimentalCoroutinesApi
class DetailsViewModelTest : BaseUnitTest() {

    @Mock
    private lateinit var pokemonService: PokemonService
    @Mock
    private lateinit var application: Application
    private lateinit var vm: DetailsViewModel

    @Before
    override fun setup() {
        super.setup()
        `when`(application.getString(R.string.error_message_default))
            .thenReturn("Unable to load pokemon data")
        `when`(application.getString(R.string.height))
            .thenReturn("Height %1\$d")
        `when`(application.getString(R.string.weight))
            .thenReturn("Weight %1\$d")
    }

    @Test
    fun `ui started - shows error`() {
        // given
        vm = DetailsViewModel(pokemonService, application)

        //when
        vm.uiStarted(-1)

        //then
        assertThat(vm.errorMessage.value, `is`("Unable to load pokemon data"))
    }

    @Test
    fun `ui started - shows pokemon data`() {
        // given
        val service = mock<PokemonService> {
            on { getPokemonAsync(0) } doReturn GlobalScope.async { stubResults() }
        }
        vm = DetailsViewModel(service, application)

        //when
        vm.uiStarted(0)

        //then
        assertThat(
            vm.pokemonState.value, `is`(
                DetailsViewModel.PokemonState(
                    "pokeA",
                    "Height 100",
                    "Weight 200",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/7.png"
                )
            )
        )
    }

    @Test
    fun `ui started - handles error`() {
        // given
        val service = mock<PokemonService> {
            on { getPokemonAsync(0) } doThrow RuntimeException("Can't load")
        }
        vm = DetailsViewModel(service, application)

        //when
        vm.uiStarted(0)

        //then
        assertThat(vm.errorMessage.value, `is`("Unable to load pokemon data"))
    }

    private fun stubResults(): Pokemon {
        return Pokemon(
            "pokeA",
            100,
            200,
            Sprites("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/7.png")
        )
    }

}