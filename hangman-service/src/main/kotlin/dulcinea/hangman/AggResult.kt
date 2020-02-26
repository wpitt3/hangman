package dulcinea.hangman

data class AggResult(val key: String, val count: Long, val aggs: List<AggResult>) {}