package hr.tvz.android.drinkingtruthordare.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import hr.tvz.android.drinkingtruthordare.databinding.FragmentDrinkBinding
import kotlin.random.Random

class DrinkFragment : Fragment() {
    private lateinit var binding: FragmentDrinkBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDrinkBinding.inflate(inflater, container, false)

        // Generate a random number between 1 and 2
        val shots = Random.nextInt(1, 3)
        binding.drinkText.text = "$shots shots!"

        binding.finishButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }
}
