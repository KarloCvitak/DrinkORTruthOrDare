package hr.tvz.android.drinkingtruthordare.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import hr.tvz.android.drinkingtruthordare.databinding.FragmentMainMenuBinding
import hr.tvz.android.drinkingtruthordare.view.activities.MainActivity
import java.util.*

class MainMenuFragment : Fragment() {
    private lateinit var binding: FragmentMainMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainMenuBinding.inflate(inflater, container, false)

        binding.startGameButton.setOnClickListener {
            (activity as MainActivity).startGame()
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
                    (activity as MainActivity).setLocale(selectedLocale)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        return binding.root
    }
}
