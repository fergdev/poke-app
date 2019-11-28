package nz.co.pokeapp.ui.main

import android.app.Application
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runBlockingTest
import nz.co.pokeapp.BaseUnitTest
import nz.co.pokeapp.R
import nz.co.pokeapp.service.PokemonResult
import nz.co.pokeapp.service.PokemonResults
import nz.co.pokeapp.service.PokemonService
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`

@ExperimentalCoroutinesApi
class MainViewModelTest : BaseUnitTest() {

    @Mock
    private lateinit var pokemonService: PokemonService
    @Mock
    private lateinit var application: Application
    private lateinit var vm: MainViewModel

    @Before
    override fun setup() {
        super.setup()
        `when`(application.getString(R.string.error_message_default)).thenReturn("Unable to load pokemon data")
    }

    @Test
    fun `init - invokes loading`() {
        // given
        assignVm()
        // when + then
        verify(pokemonService).getPokemonListAsync(0, 20)
        assertThat(vm.pokemons.value!!.isEmpty(), `is`(true))
    }

    @Test
    fun `init - sets pokemons`() = runBlockingTest {
        // given
        val service = mock<PokemonService> {
            on { getPokemonListAsync(0, 20) } doReturn GlobalScope.async { stubResults() }
        }

        // when
        vm = MainViewModel(service, application)

        // then
        assertThat(vm.pokemons.value?.size, `is`(3))
    }

    @Test
    fun `init - displays error`() = runBlockingTest {
        // given
        val service = mock<PokemonService> {
            on { getPokemonListAsync(0, 20) } doThrow RuntimeException("Not working")
        }

        // when
        vm = MainViewModel(service, application)

        // then
        assertThat(vm.pokemons.value?.size, `is`(0))
        assertThat(vm.errorMessage.value, `is`("Unable to load pokemon data"))
    }

    @Test
    fun `on view search string - filters list`() {
        // given
        val service = mock<PokemonService> {
            on { getPokemonListAsync(0, 20) } doReturn GlobalScope.async { stubResults() }
        }
        vm = MainViewModel(service, application)

        // when
        vm.onViewSearchString("pokeA")

        //then
        assertThat(vm.pokemons.value!!.size, `is`(1))
        assertThat(
            vm.pokemons.value!!.first(),
            `is`(PokemonResult("pokeA", "https://pokeapi.co/api/v2/pokemon/1/"))
        )
    }

    @Test
    fun `on pokemon selected - sets selected`() {
        //given
        val service = mock<PokemonService> {
            on { getPokemonListAsync(0, 20) } doReturn GlobalScope.async { stubResults() }
        }
        vm = MainViewModel(service, application)

        //when
        vm.onPokemonSelected(stubResults().results.first())

        //then
        assertThat(vm.selectedPokemon.value, `is`(1))
    }

    @Test
    fun `on pokemon selected - shows error`() {
        //given
        val service = mock<PokemonService> {
            on { getPokemonListAsync(0, 20) } doReturn GlobalScope.async { stubResults() }
        }
        vm = MainViewModel(service, application)

        //when
        vm.onPokemonSelected(PokemonResult("pokeA", "not here"))

        //then
        assertThat(vm.selectedPokemon.value, nullValue())
        assertThat(vm.errorMessage.value, `is`("Unable to load pokemon data"))
    }

    private fun assignVm() {
        vm = MainViewModel(pokemonService, application)
    }

    private fun stubResults(): PokemonResults {
        return PokemonResults(
            1, "nextUrl", listOf(
                PokemonResult("pokeA", "https://pokeapi.co/api/v2/pokemon/1/"),
                PokemonResult("pokeB", "https://pokeapi.co/api/v2/pokemon/2/"),
                PokemonResult("pokeC", "https://pokeapi.co/api/v2/pokemon/2/")
            )
        )
    }
}