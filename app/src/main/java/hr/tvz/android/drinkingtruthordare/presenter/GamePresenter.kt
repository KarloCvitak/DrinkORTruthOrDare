package hr.tvz.android.drinkingtruthordare.presenter

import android.content.Context
import hr.tvz.android.drinkingtruthordare.MVP.MVP
import hr.tvz.android.drinkingtruthordare.R

class GamePresenter : MVP.Presenter {
    private lateinit var view: MVP.View
    private lateinit var context: Context

    constructor()

    constructor(view: MVP.View, context: Context) {
        this.view = view
        this.context = context
    }

    override fun onTruthSelected() {
        val truthQuestions = context.resources.getStringArray(R.array.truth_questions).toList()
        val truthQuestion = getRandomQuestion(truthQuestions)
        view.showQuestion(truthQuestion)
    }

    override fun onDareSelected() {
        val dareQuestions = context.resources.getStringArray(R.array.dare_questions).toList()
        val dareQuestion = getRandomQuestion(dareQuestions)
        view.showQuestion(dareQuestion)
    }

    private fun getRandomQuestion(questions: List<String>): String {
        return questions.random()
    }
}
