package dulcinea.hangman

import org.springframework.stereotype.Service


@Service
class ResultAnalyser {
    fun score(result: Result): Long {
        return recurse(result.aggs)
    }

    private fun recurse(aggs: List<Result.AggResult>): Long {
        return if (aggs[0].aggs.isEmpty()) {
            aggs.map { it.count.toLong() }.min()!!
        } else {
            aggs.map { recurse(it.aggs) }.max()!!
        }
    }
}
