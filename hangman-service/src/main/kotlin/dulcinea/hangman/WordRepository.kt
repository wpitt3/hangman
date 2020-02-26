package dulcinea.hangman

import org.elasticsearch.action.admin.indices.refresh.RefreshRequest
import org.elasticsearch.action.delete.DeleteRequest
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.index.query.QueryBuilders

interface WordRepository {
    fun setupIndex()
    fun create(word: Word)
    fun delete(word: String)
    fun get(): List<Word>
    fun refresh()
    fun findAggregations(with: List<String>, without: List<String>): Result
}