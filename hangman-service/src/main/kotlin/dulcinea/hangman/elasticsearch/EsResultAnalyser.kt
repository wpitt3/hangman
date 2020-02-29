package dulcinea.hangman.elasticsearch

import org.springframework.stereotype.Service


@Service
class EsResultAnalyser {
    fun score(result: EsResult, seenLetters: List<String>): Long {
        return recurse(result.aggs, seenLetters.map{it[0]})
    }

    private fun recurse(aggs: List<EsResult.AggResult>, seenLetters: List<Char>): Long {
        return if (aggs[0].aggs.isEmpty()) {
            x(aggs, seenLetters).map { (letter, aggs) ->
                aggs.map{it.count}.max()!!
            }.min()!!
        } else {
            x(aggs, seenLetters).map{ (letter, aggs) ->
                aggs.map{recurse(it.aggs, seenLetters + letter)}.max()!!
            }.min()!!
        }
    }

    private fun x(aggs: List<EsResult.AggResult>, seenLetters: List<Char>): Map<Char, List<EsResult.AggResult>> {
        return aggs.groupBy { it.key[0] }.filter{ !seenLetters.contains(it.key)}
    }
}
