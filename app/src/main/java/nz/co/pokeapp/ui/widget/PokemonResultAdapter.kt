package nz.co.pokeapp.ui.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import nz.co.pokeapp.R
import nz.co.pokeapp.service.PokemonResult

class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)

class PokemonResultViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private var titleTextView: TextView = itemView.findViewById(R.id.pokemonResultListItem_name)

    fun bind(
        result: PokemonResult,
        locationSelectedListener: Listener?
    ) {
        titleTextView.text = result.name
        itemView.setOnClickListener {
            locationSelectedListener?.onPokemonResultSelectedListener(result)
        }
    }

    fun showEmpty() {
        titleTextView.text = itemView.context.getString(R.string.empty_message)
    }
}

class PokemonResultAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var loading: Boolean = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var _results = mutableListOf<PokemonResult>()
    var results: List<PokemonResult> = _results
        set(value) {
            _results.clear()
            _results.addAll(value)
            notifyDataSetChanged()
        }

    var listener: Listener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_STANDARD, TYPE_EMPTY -> PokemonResultViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.pokemon_result_list_item, parent, false
                )
            )
            else -> LoadingViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.loading_list_item, parent, false
                )
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            loading && position >= results.size -> TYPE_LOADING
            results.isEmpty() -> TYPE_EMPTY
            else -> TYPE_STANDARD
        }
    }

    override fun getItemCount(): Int {
        return if (results.isEmpty()) {
            1
        } else {
            return results.size + if (loading) 1 else 0
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PokemonResultViewHolder -> {
                if (position == 0 && results.isEmpty()) holder.showEmpty()
                else holder.bind(results[position], listener)
            }
        }
    }

    companion object {
        private const val TYPE_EMPTY = 0
        private const val TYPE_LOADING = 1
        private const val TYPE_STANDARD = 2
    }
}

interface Listener {
    fun onPokemonResultSelectedListener(pokemonResult: PokemonResult)
}