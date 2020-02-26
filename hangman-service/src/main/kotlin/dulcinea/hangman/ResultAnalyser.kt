package dulcinea.hangman

open class ResultAnalyser {

    fun score(result: Result): Int {
        return result.total
    }
}
