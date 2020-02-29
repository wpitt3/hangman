package dulcinea.hangman

import org.springframework.stereotype.Service


@Service
class ResultAnalyser {
    fun score(result: Result, searchTerm: List<String>): Long {
        return recurse(result.aggs, searchTerm)
    }

    private fun recurse(aggs: List<Result.AggResult>, termsSeen: List<String>): Long {
        return if (aggs[0].aggs.isEmpty()) {
            aggs.filter{ !termsSeen.contains(it.key) }.map { it.count }.min()!!
        } else {
            aggs.filter{ !termsSeen.contains(it.key) }.map { recurse(it.aggs, termsSeen + it.key) }.max()!!
        }
    }
}
