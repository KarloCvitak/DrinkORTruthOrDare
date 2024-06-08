package hr.tvz.android.drinkingtruthordare.view.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import hr.tvz.android.drinkingtruthordare.MVP.MVP
import hr.tvz.android.drinkingtruthordare.R
import hr.tvz.android.drinkingtruthordare.databinding.FragmentGameBinding
import hr.tvz.android.drinkingtruthordare.presenter.GamePresenter
import hr.tvz.android.drinkingtruthordare.view.activities.MainActivity

class GameFragment : Fragment(), MVP.View {
    private lateinit var binding: FragmentGameBinding
    private lateinit var presenter: GamePresenter
    private lateinit var mediaPlayer: MediaPlayer
    private var countDownTimer: CountDownTimer? = null // Dodajemo varijablu za timer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameBinding.inflate(inflater, container, false)
        presenter = GamePresenter(this, requireContext())
        // mediaPlayer = MediaPlayer.create(context, R.raw.timer_end_sound) // Dodajte zvuk završetka timera

        binding.truthButton.setOnClickListener {
            presenter.onTruthSelected()
        }

        binding.dareButton.setOnClickListener {
            presenter.onDareSelected()
        }

        binding.finishButton.setOnClickListener {
            chooseNextPlayer()
            resetView()
        }

        binding.drinkButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DrinkFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.stopTimerButton.setOnClickListener {
            stopTimer()
        }

        binding.drinkButton.visibility = View.GONE
        binding.finishButton.visibility = View.GONE
        binding.startTimerButton.visibility = View.GONE
        binding.stopTimerButton.visibility = View.GONE
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

        if (question.contains("20") || question.contains("30") || question.contains("1") || question.contains("3")) {
            binding.startTimerButton.visibility = View.VISIBLE
            binding.startTimerButton.setOnClickListener {
                startTimer(question)
            }
        } else {
            binding.startTimerButton.visibility = View.GONE
        }
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

    private fun startTimer(question: String) {
        val duration = when {
            question.contains("20") -> 20000L
            question.contains("30") -> 30000L
            question.contains("1") -> 60000L
            question.contains("3") -> 180000L
            else -> 0L
        }

        if (duration > 0) {
            binding.startTimerButton.visibility = View.GONE
            binding.stopTimerButton.visibility = View.VISIBLE
            countDownTimer = object : CountDownTimer(duration, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val secondsRemaining = millisUntilFinished / 1000
                    binding.questionText.text = "$question\nPreostalo vrijeme: $secondsRemaining sekundi"
                }

                override fun onFinish() {
                    // mediaPlayer.start() // Reprodukcija zvuka
                    Toast.makeText(context, "Vrijeme je isteklo!", Toast.LENGTH_SHORT).show()
                    binding.questionText.text = question.replace(Regex("\\d+ sekundi|\\d+ minuta|\\d+ segundos|\\d+ minutos"), "")
                    binding.stopTimerButton.visibility = View.GONE
                }
            }.start()
        }
    }

    private fun stopTimer() {
        countDownTimer?.cancel()
        Toast.makeText(context, "Timer je zaustavljen!", Toast.LENGTH_SHORT).show()
        binding.stopTimerButton.visibility = View.GONE
        binding.startTimerButton.visibility = View.GONE
        binding.questionText.text = binding.questionText.text.toString().replace(Regex("\nPreostalo vrijeme: \\d+ sekundi"), "")
    }

    private fun resetView() {
        binding.questionText.text = ""
        binding.questionText.visibility = View.GONE
        binding.buttonsLayout.visibility = View.VISIBLE
        binding.drinkButton.visibility = View.GONE
        binding.finishButton.visibility = View.GONE
        binding.startTimerButton.visibility = View.GONE
        binding.stopTimerButton.visibility = View.GONE
        countDownTimer?.cancel() // Otkazujemo timer kad resetiramo view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // mediaPlayer.release() // Oslobodite MediaPlayer resurse
        countDownTimer?.cancel() // Otkazujemo timer kad fragment bude uništen
    }
}
