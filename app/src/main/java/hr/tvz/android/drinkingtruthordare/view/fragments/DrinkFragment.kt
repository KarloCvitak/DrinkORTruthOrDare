package hr.tvz.android.drinkingtruthordare.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import hr.tvz.android.drinkingtruthordare.databinding.FragmentDrinkBinding
import kotlin.random.Random
import hr.tvz.android.drinkingtruthordare.R

class DrinkFragment : Fragment() {
    private lateinit var binding: FragmentDrinkBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDrinkBinding.inflate(inflater, container, false)

        // Generate a random number between 1 and 2
        val shots = Random.nextInt(1, 3)
        val shotsText = getString(R.string.shots)
        binding.drinkText.text = "$shots $shotsText!"

        binding.finishButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }
}
