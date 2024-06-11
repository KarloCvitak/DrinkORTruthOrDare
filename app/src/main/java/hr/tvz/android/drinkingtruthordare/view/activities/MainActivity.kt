package hr.tvz.android.drinkingtruthordare.view.activities

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.facebook.drawee.backends.pipeline.Fresco
import hr.tvz.android.drinkingtruthordare.R
import hr.tvz.android.drinkingtruthordare.databinding.ActivityMainBinding
import hr.tvz.android.drinkingtruthordare.presenter.PlayerInputPresenter
import hr.tvz.android.drinkingtruthordare.view.fragments.MainMenuFragment
import hr.tvz.android.drinkingtruthordare.view.fragments.PlayerInputFragment
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val playerInputPresenter = PlayerInputPresenter()
    private val PREFS_NAME = "prefs"
    private val PREF_LANGUAGE = "language"
    var retrofit: Retrofit? = null
    private val URL_CONST = "http://192.168.111.211:8081/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this)

        retrofit = Retrofit.Builder()
            .baseUrl(URL_CONST)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        loadLocale()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MainMenuFragment())
                .commitNow()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the current fragment's instance
        supportFragmentManager.findFragmentById(R.id.fragment_container)?.let {
            supportFragmentManager.putFragment(outState, "currentFragment", it)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Restore the state
        val currentFragment = supportFragmentManager.getFragment(savedInstanceState, "currentFragment")
        currentFragment?.let {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, it)
                .commitNow()
        }
    }

    fun startGame() {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_left, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_right)
            .replace(R.id.fragment_container, PlayerInputFragment())
            .addToBackStack(null)
            .commit()
    }

    fun setLocale(locale: Locale) {
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        val editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
        editor.putString(PREF_LANGUAGE, locale.language)
        editor.apply()
        recreate()
    }

    private fun loadLocale() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val language = prefs.getString(PREF_LANGUAGE, "en")
        val locale = Locale(language!!)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}
