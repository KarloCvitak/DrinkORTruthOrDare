package hr.tvz.android.drinkingtruthordare.presenter

import hr.tvz.android.drinkingtruthordare.model.Player

class PlayerInputPresenter {
    private val players: MutableList<Player> = mutableListOf()

    fun getPlayers(): List<Player> {
        return players
    }

    fun addPlayer(username: String) {
        players.add(Player(username))
    }

    fun removePlayer(username: String) {
        players.removeAll { it.username == username }
    }

    fun clearPlayers() {
        players.clear()
    }
}
