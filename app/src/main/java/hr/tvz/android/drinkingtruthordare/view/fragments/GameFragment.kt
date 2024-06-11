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
    private var countDownTimer: CountDownTimer? = null
    private var currentQuestion: String? = null
    private var currentPlayer: String? = null
    private var mediaPlayer: MediaPlayer? = null
    private var remainingTime: Long = 0L

    companion object {
        private const val STATE_CURRENT_QUESTION = "current_question"
        private const val STATE_CURRENT_PLAYER = "current_player"
        private const val STATE_TIMER_RUNNING = "is_timer_running"
        private const val STATE_REMAINING_TIME = "remaining_time"
        private const val STATE_DRINK_BUTTON_VISIBLE = "is_drink_button_visible"
        private const val STATE_FINISH_BUTTON_VISIBLE = "is_finish_button_visible"
        private const val STATE_START_TIMER_BUTTON_VISIBLE = "is_start_timer_button_visible"
        private const val STATE_STOP_TIMER_BUTTON_VISIBLE = "is_stop_timer_button_visible"
    }

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

        if (savedInstanceState != null) {
            currentQuestion = savedInstanceState.getString(STATE_CURRENT_QUESTION)
            currentPlayer = savedInstanceState.getString(STATE_CURRENT_PLAYER)
            val isTimerRunning = savedInstanceState.getBoolean(STATE_TIMER_RUNNING)
            val isDrinkButtonVisible = savedInstanceState.getBoolean(STATE_DRINK_BUTTON_VISIBLE)
            val isFinishButtonVisible = savedInstanceState.getBoolean(STATE_FINISH_BUTTON_VISIBLE)
            val isStartTimerButtonVisible = savedInstanceState.getBoolean(STATE_START_TIMER_BUTTON_VISIBLE)
            val isStopTimerButtonVisible = savedInstanceState.getBoolean(STATE_STOP_TIMER_BUTTON_VISIBLE)
            remainingTime = savedInstanceState.getLong(STATE_REMAINING_TIME)

            currentQuestion?.let { showQuestion(it) }
            currentPlayer?.let { showCurrentPlayer(it) }

            if (isTimerRunning) {
                startTimer(currentQuestion ?: "", remainingTime)
            }

            binding.drinkButton.visibility = if (isDrinkButtonVisible) View.VISIBLE else View.GONE
            binding.finishButton.visibility = if (isFinishButtonVisible) View.VISIBLE else View.GONE
            binding.startTimerButton.visibility = if (isStartTimerButtonVisible) View.VISIBLE else View.GONE
            binding.stopTimerButton.visibility = if (isStopTimerButtonVisible) View.VISIBLE else View.GONE
        } else {
            binding.drinkButton.visibility = View.GONE
            binding.finishButton.visibility = View.GONE
            binding.startTimerButton.visibility = View.GONE
            binding.stopTimerButton.visibility = View.GONE
            binding.questionText.text = ""
            chooseNextPlayer()
        }

        return binding.root
    }

    override fun showQuestion(question: String) {
        currentQuestion = question
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
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun chooseNextPlayer() {
        val players = (activity as MainActivity).playerInputPresenter.getPlayers()
        if (players.isNotEmpty()) {
            val randomPlayer = players.random().username
            showCurrentPlayer(randomPlayer)
        } else {
            binding.currentPlayerText.text = getString(R.string.no_players_available)
        }
    }

    private fun showCurrentPlayer(player: String) {
        currentPlayer = player
        binding.currentPlayerText.text = getString(R.string.current_player) + ": " + player
    }


    private fun startTimer(question: String, remaining: Long = 0L) {
        val duration = when {
            question.contains("20") -> 20000L
            question.contains("30") -> 30000L
            question.contains("1") -> 60000L
            question.contains("3") -> 180000L
            else -> 0L
        }

        val timeLeft = if (remaining > 0) remaining else duration

        if (timeLeft > 0) {
            binding.startTimerButton.visibility = View.GONE
            binding.stopTimerButton.visibility = View.VISIBLE
            countDownTimer = object : CountDownTimer(timeLeft, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    remainingTime = millisUntilFinished
                    val secondsRemaining = millisUntilFinished / 1000
                    binding.questionText.text = "$question\n${getString(R.string.remaining_time)}: $secondsRemaining ${getString(R.string.seconds)}"
                }

                override fun onFinish() {
                    playBeepSound()
                    Toast.makeText(context, getString(R.string.time_is_up), Toast.LENGTH_SHORT).show()
                    binding.questionText.text = question.replace(Regex("\\d+ ${getString(R.string.seconds)}|\\d+ ${getString(R.string.minutes)}"), "")
                    binding.stopTimerButton.visibility = View.GONE
                }
            }.start()
        }
    }

    private fun stopTimer() {
        countDownTimer?.cancel()
        Toast.makeText(context, getString(R.string.timer_stopped), Toast.LENGTH_SHORT).show()
        binding.stopTimerButton.visibility = View.GONE
        binding.startTimerButton.visibility = View.GONE
        binding.questionText.text = binding.questionText.text.toString().replace(Regex("\n${getString(R.string.remaining_time)}: \\d+ ${getString(R.string.seconds)}"), "")
    }

    private fun resetView() {
        binding.questionText.text = ""
        binding.questionText.visibility = View.GONE
        binding.buttonsLayout.visibility = View.VISIBLE
        binding.drinkButton.visibility = View.GONE
        binding.finishButton.visibility = View.GONE
        binding.startTimerButton.visibility = View.GONE
        binding.stopTimerButton.visibility = View.GONE
        countDownTimer?.cancel()
        currentQuestion = null
    }

    private fun playBeepSound() {
        mediaPlayer = MediaPlayer.create(context, R.raw.beep)
        mediaPlayer?.setOnCompletionListener {
            it.release()
        }
        mediaPlayer?.start()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (::presenter.isInitialized) {
            outState.putString(STATE_CURRENT_QUESTION, currentQuestion)
            outState.putString(STATE_CURRENT_PLAYER, currentPlayer)
            outState.putBoolean(STATE_TIMER_RUNNING, countDownTimer != null)
            outState.putLong(STATE_REMAINING_TIME, remainingTime)
            outState.putBoolean(STATE_DRINK_BUTTON_VISIBLE, binding.drinkButton.visibility == View.VISIBLE)
            outState.putBoolean(STATE_FINISH_BUTTON_VISIBLE, binding.finishButton.visibility == View.VISIBLE)
            outState.putBoolean(STATE_START_TIMER_BUTTON_VISIBLE, binding.startTimerButton.visibility == View.VISIBLE)
            outState.putBoolean(STATE_STOP_TIMER_BUTTON_VISIBLE, binding.stopTimerButton.visibility == View.VISIBLE)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        countDownTimer?.cancel()
    }
}
