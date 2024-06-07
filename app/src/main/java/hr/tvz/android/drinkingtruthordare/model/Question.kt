package hr.tvz.android.drinkingtruthordare.model

data class Question(val text: String, val type: QuestionType)

enum class QuestionType {
    TRUTH, DARE
}