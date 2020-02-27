package dulcinea.hangman

import org.springframework.stereotype.Service


@Service
class ResultAnalyser {
    fun score(result: Result): Int {
        return result.total
    }
}
