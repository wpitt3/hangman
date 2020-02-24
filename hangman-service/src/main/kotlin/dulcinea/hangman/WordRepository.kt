package dulcinea.hangman

import com.google.gson.Gson
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest
import org.elasticsearch.action.delete.DeleteRequest
import org.elasticsearch.action.delete.DeleteResponse
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.transport.client.PreBuiltTransportClient
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.TransportAddress
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.index.query.QueryBuilders

import java.net.InetAddress

class WordRepository(val index: String) {

    private val client: TransportClient

    init {
        val settings: Settings = Settings.builder().put("cluster.name", "docker-cluster").build()
        client = PreBuiltTransportClient(settings)
                .addTransportAddress(TransportAddress(InetAddress.getByName("localhost"), 9300))
    }

    fun create(word: Word) {
        client.prepareIndex(index, "_doc", word.word)
            .setSource(Gson().toJson(word), XContentType.JSON).get()
    }

    fun delete(word: String) {
        val response: DeleteResponse = client.delete(DeleteRequest(index, "_doc", word)).get()

    }

    fun get(): List<Word> {
        val response: SearchResponse = client.prepareSearch(index)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.matchAllQuery())
                .setFrom(0).setSize(60)
                .get()
        val gson = Gson()
        return response.hits.map { gson.fromJson(it.sourceAsString, Word::class.java) }
    }

    fun refresh() {
        client.admin().indices().refresh(RefreshRequest(index)).get()
    }
}
