package hr.tvz.android.drinkingtruthordare.MVP

interface MVP {
    interface View {
        fun showQuestion(question: String)
        fun showError(message: String)
    }

    interface Presenter {

        fun onTruthSelected()
        fun onDareSelected()
    }

    interface Model {
        fun getRandomTruth(): String
        fun getRandomDare(): String
    }
}
