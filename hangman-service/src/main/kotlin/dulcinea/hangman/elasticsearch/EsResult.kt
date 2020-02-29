package dulcinea.hangman.elasticsearch

data class EsResult(val total: Int, val words: List<String>, val aggs: List<AggResult>) {
    data class AggResult(val key: String, val count: Long, val aggs: List<AggResult>) {}
}