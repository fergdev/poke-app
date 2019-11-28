package nz.co.pokeapp.service

import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonService {

    @GET("pokemon")
    fun getPokemonListAsync(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Deferred<PokemonResults>

    @GET("pokemon/{id}")
    fun getPokemonAsync(@Path("id") id: Int): Deferred<Pokemon>

}

data class PokemonResults(
    val count: Int,
    val next: String?,
    val results: List<PokemonResult>
)

data class PokemonResult(
    val name: String,
    val url: String
)

data class Pokemon(
    val name: String,
    val height: Int,
    val weight: Int,
    val sprites: Sprites
)

data class Sprites(
    @SerializedName("front_default")
    val frontDefault: String
)