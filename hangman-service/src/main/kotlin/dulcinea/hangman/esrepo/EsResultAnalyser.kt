package dulcinea.hangman.esrepo

import org.springframework.stereotype.Service


@Service
class EsResultAnalyser {
    fun score(result: EsResult, searchTerm: List<String>): Long {
        return recurse(result.aggs, searchTerm)
    }

    private fun recurse(aggs: List<EsResult.AggResult>, termsSeen: List<String>): Long {
        return if (aggs[0].aggs.isEmpty()) {
            aggs.filter{ !termsSeen.contains(it.key) }.map { it.count }.min()!!
        } else {
            aggs.filter{ !termsSeen.contains(it.key) }.map { recurse(it.aggs, termsSeen + it.key) }.max()!!
        }
    }
}
