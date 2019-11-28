package nz.co.pokeapp.ui.base

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    fun showError(message: String) {
        AlertDialog.Builder(this)
            .setTitle(getString(nz.co.pokeapp.R.string.error))
            .setMessage(message)
            .setPositiveButton(getString(nz.co.pokeapp.R.string.ok)) { _, _ -> }.create()
            .show()
    }
}

