package dulcinea.hangman

import dulcinea.hangman.esrepo.EsResult

interface WordRepository {
    fun setupIndex()
    fun create(word: Word)
    fun get(): List<Word>
    fun refresh()
    fun findAggregations(with: List<String>, without: List<String>): EsResult
}