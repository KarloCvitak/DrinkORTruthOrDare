package hr.tvz.android.drinkingtruthordare.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import hr.tvz.android.drinkingtruthordare.MVP.MVP
import hr.tvz.android.drinkingtruthordare.databinding.FragmentGameBinding
import hr.tvz.android.drinkingtruthordare.presenter.GamePresenter
import hr.tvz.android.drinkingtruthordare.view.activities.MainActivity
import hr.tvz.android.drinkingtruthordare.R

class GameFragment : Fragment(), MVP.View {
    private lateinit var binding: FragmentGameBinding
    private lateinit var presenter: GamePresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameBinding.inflate(inflater, container, false)
        presenter = GamePresenter(this, requireContext())

        binding.truthButton.setOnClickListener {
            presenter.onTruthSelected()
        }

        binding.dareButton.setOnClickListener {
            presenter.onDareSelected()
        }

        binding.finishButton.setOnClickListener {
            chooseNextPlayer()
            binding.questionText.text = ""
            binding.questionText.visibility = View.GONE
            binding.buttonsLayout.visibility = View.VISIBLE
            binding.drinkButton.visibility = View.GONE
            binding.finishButton.visibility = View.GONE
        }

        binding.drinkButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DrinkFragment())
                .addToBackStack(null)
                .commit()
        }



        binding.drinkButton.visibility = View.GONE
        binding.finishButton.visibility = View.GONE
        binding.questionText.text = "" // Ensure the text is empty initially

        chooseNextPlayer()

        return binding.root
    }

    override fun showQuestion(question: String) {
        binding.questionText.text = question
        binding.questionText.visibility = View.VISIBLE
        binding.buttonsLayout.visibility = View.GONE
        binding.drinkButton.visibility = View.VISIBLE
        binding.finishButton.visibility = View.VISIBLE
    }

    override fun showError(message: String) {
        // Handle error
    }

    private fun chooseNextPlayer() {
        val players = (activity as MainActivity).playerInputPresenter.getPlayers()
        if (players.isNotEmpty()) {
            val randomPlayer = players.random().username
            binding.currentPlayerText.text = getString(R.string.current_player) + ": " + randomPlayer

        } else {
            binding.currentPlayerText.text = "No players available"
        }
    }
}
