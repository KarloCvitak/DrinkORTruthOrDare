package hr.tvz.android.drinkingtruthordare.presenter

import android.content.Context
import hr.tvz.android.drinkingtruthordare.MVP.MVP
import hr.tvz.android.drinkingtruthordare.network.RetrofitInstance

import hr.tvz.android.drinkingtruthordare.network.QuestionsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GamePresenter(private val view: MVP.View, private val context: Context) : MVP.Presenter {

    private val apiService = RetrofitInstance.apiService

    override fun onTruthSelected() {
        fetchQuestions("truth")
    }

    override fun onDareSelected() {
        fetchQuestions("dare")
    }

    private fun fetchQuestions(type: String) {
        val language = context.resources.configuration.locales.get(0).language
        apiService.getQuestions(language, type).enqueue(object : Callback<QuestionsResponse> {
            override fun onResponse(call: Call<QuestionsResponse>, response: Response<QuestionsResponse>) {
                if (response.isSuccessful) {
                    response.body()?.questions?.let { questions ->
                        val question = getRandomQuestion(questions)
                        view.showQuestion(question)
                    }
                } else {
                    view.showError("Failed to load questions")
                }
            }

            override fun onFailure(call: Call<QuestionsResponse>, t: Throwable) {
                view.showError("Error: ${t.message}")
            }
        })
    }

    private fun getRandomQuestion(questions: List<String>): String {
        return questions.random()
    }
}
