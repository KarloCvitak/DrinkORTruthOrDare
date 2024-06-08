package hr.tvz.android.drinkingtruthordare.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.facebook.drawee.view.SimpleDraweeView
import hr.tvz.android.drinkingtruthordare.databinding.FragmentMainMenuBinding
import hr.tvz.android.drinkingtruthordare.network.ApiService
import hr.tvz.android.drinkingtruthordare.network.LogoResponse
import hr.tvz.android.drinkingtruthordare.view.activities.MainActivity
import hr.tvz.android.drinkingtruthordare.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainMenuFragment : Fragment() {
    private lateinit var binding: FragmentMainMenuBinding
    private lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainMenuBinding.inflate(inflater, container, false)

        apiService = RetrofitInstance.apiService

        binding.startGameButton.setOnClickListener {
            (activity as? MainActivity)?.startGame()
        }

        binding.languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedLocale = when (position) {
                    0 -> Locale("en")
                    1 -> Locale("hr")
                    2 -> Locale("es")
                    else -> Locale("en")
                }
                val currentLocale = Locale.getDefault()
                if (selectedLocale.language != currentLocale.language) {
                    (activity as? MainActivity)?.setLocale(selectedLocale)
                }
                fetchLogo(selectedLocale.language)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Load the initial logo
        fetchLogo(Locale.getDefault().language)

        return binding.root
    }

    private fun fetchLogo(language: String) {
        apiService.getLogo(language).enqueue(object : Callback<LogoResponse> {
            override fun onResponse(call: Call<LogoResponse>, response: Response<LogoResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val logoUrl = it.logoUrl
                        val draweeView: SimpleDraweeView = binding.logoImageView // Ensure this ID matches your layout
                        draweeView.setImageURI(logoUrl)
                    }
                } else {
                    showToast("Failed to load logo")
                }
            }

            override fun onFailure(call: Call<LogoResponse>, t: Throwable) {
                showToast("Error: ${t.message}")
            }
        })
    }

    private fun showToast(message: String) {
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }
}
