package hr.tvz.android.drinkingtruthordare.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import hr.tvz.android.drinkingtruthordare.databinding.FragmentPlayerInputBinding
import hr.tvz.android.drinkingtruthordare.presenter.PlayerInputPresenter
import hr.tvz.android.drinkingtruthordare.view.activities.MainActivity
import hr.tvz.android.drinkingtruthordare.R

class PlayerInputFragment : Fragment() {
    private lateinit var binding: FragmentPlayerInputBinding
    private lateinit var presenter: PlayerInputPresenter
    private lateinit var adapter: ArrayAdapter<String>

    companion object {
        private const val STATE_PLAYERS = "players"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = (activity as MainActivity).playerInputPresenter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerInputBinding.inflate(inflater, container, false)

        val playerNames = savedInstanceState?.getStringArray(STATE_PLAYERS)?.toMutableList()
            ?: presenter.getPlayers().map { it.username }.toMutableList()

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, playerNames)
        binding.playersListView.adapter = adapter

        binding.addPlayerButton.setOnClickListener {
            val playerName = binding.playerNameInput.text.toString()
            if (playerName.isNotEmpty()) {
                presenter.addPlayer(playerName)
                adapter.add(playerName)
                binding.playerNameInput.text.clear()
            }
        }

        binding.playersListView.setOnItemClickListener { _, _, position, _ ->
            val playerName = adapter.getItem(position)
            adapter.remove(playerName)
            presenter.removePlayer(playerName!!)
        }

        binding.startGameButton.setOnClickListener {
            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.fragment_container, GameFragment())
                .addToBackStack(null)
                .commit()
        }

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (::presenter.isInitialized) {
            val playerNames = presenter.getPlayers().map { it.username }.toTypedArray()
            outState.putStringArray(STATE_PLAYERS, playerNames)
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null && ::presenter.isInitialized) {
            val playerNames = savedInstanceState.getStringArray(STATE_PLAYERS)?.toMutableList() ?: mutableListOf()
            adapter.clear()
            adapter.addAll(playerNames)
            presenter.clearPlayers()
            playerNames.forEach { presenter.addPlayer(it) }
        }
    }
}
