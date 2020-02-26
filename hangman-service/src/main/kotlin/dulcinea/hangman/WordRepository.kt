package dulcinea.hangman

import com.google.gson.Gson
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest
import org.elasticsearch.action.delete.DeleteRequest
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.transport.client.PreBuiltTransportClient
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.TransportAddress
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.aggregations.Aggregation
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms
import org.elasticsearch.search.aggregations.bucket.terms.Terms
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder

import java.net.InetAddress

class WordRepository(val index: String, port: Int, host: String) {

    private val client: TransportClient
    private val gson = Gson()

    init {
        val settings: Settings = Settings.builder().put("cluster.name", "docker-cluster").build()
        client = PreBuiltTransportClient(settings)
                .addTransportAddress(TransportAddress(InetAddress.getByName(host), port))
    }

    fun setupIndex() {
        client.admin().indices().prepareCreate(index).setSettings(Settings.builder().put("index.refresh_interval", "5s")).get()
        val source = "{\"_doc\":{\"include_in_all\":false,\"dynamic\":\"strict\",\"properties\":{\"count\":{\"type\":\"keyword\"},\"negative\":{\"type\":\"keyword\"},\"position\":{\"type\":\"keyword\"},\"positionOfSingle\":{\"type\":\"keyword\"},\"unique\":{\"type\":\"keyword\"},\"word\":{\"type\":\"keyword\"}}}}"

        client.admin().indices().preparePutMapping(index)
                .setType("_doc")
                .setSource(source, XContentType.JSON)
                .get();


    }

    fun create(word: Word) {
        println(gson.toJson(word))

        client.prepareIndex(index, "_doc", word.word)
                .setSource(gson.toJson(word), XContentType.JSON).get()
    }

    fun delete(word: String) {
        client.delete(DeleteRequest(index, "_doc", word)).get()
    }

    fun get(): List<Word> {
        val response: SearchResponse = client.prepareSearch(index)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.matchAllQuery())
                .setFrom(0).setSize(60)
                .get()

        return response.hits.map { gson.fromJson(it.sourceAsString, Word::class.java) }
    }

    fun refresh() {
        client.admin().indices().refresh(RefreshRequest(index)).get()
    }

    fun findAggregations(with: List<String>, without: List<String>): Result {
        return findAggregations(createQueryBuilder(with, without))
    }

    private fun createQueryBuilder(with: List<String>, without: List<String>): QueryBuilder {
        var queryBuilder = QueryBuilders.boolQuery()
        with.forEach { queryBuilder.must(QueryBuilders.termQuery("positionOfSingle", it)) }
        without.forEach { queryBuilder.must(QueryBuilders.termQuery("negative", it)) }
        return if (with.size + without.size == 0) QueryBuilders.matchAllQuery() else queryBuilder
    }

    private fun findAggregations(queryBuilder: QueryBuilder): Result {
        val searchResponse: SearchResponse = client.prepareSearch(index)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(queryBuilder)
                .addAggregation(buildAggToDepth(2, 160, "positionOfSingle", "negative"))
                .addAggregation(buildAggToDepth(2, 160, "negative", "positionOfSingle"))
                .setFrom(0).setSize(10)
                .get()

        val total: Int = searchResponse.hits.totalHits.toInt()
        val words = searchResponse.hits.hits.map { it.id }
        val aggs = getAggs(searchResponse.aggregations.asMap).map{ parseAgg(it) }
        return Result(total, words, aggs)
    }
//

    private fun getAggs(aggregations: Map<String, Aggregation>): List<StringTerms.Bucket> {
        return getAgg(aggregations, "positionOfSingle") + getAgg(aggregations, "negative")
    }

    private fun getAgg(aggregations: Map<String, Aggregation>, name: String): List<StringTerms.Bucket> {
        val positionOfSingle: Aggregation? = aggregations[name]
        if (positionOfSingle != null && positionOfSingle is StringTerms) {
            return positionOfSingle.buckets
        }
        return listOf()
    }

    private fun parseAgg(agg: Terms.Bucket): AggResult {
        val aggs = getAggs(agg.aggregations.asMap)
        return AggResult(agg.key.toString(), agg.docCount, if (aggs.isNotEmpty()) aggs.map { parseAgg(it) } else listOf())
    }

    private fun buildAggToDepth(depth: Int, size: Int, fieldA: String, fieldB: String): TermsAggregationBuilder {
        if( depth == 1) {
            return buildPOSAgg(size, fieldA)
        }
        val termsAggregationBuilder: TermsAggregationBuilder = buildPOSAgg(size, fieldA)
        termsAggregationBuilder.subAggregation(buildAggToDepth(depth-1, size, fieldA, fieldB))
        termsAggregationBuilder.subAggregation(buildAggToDepth(depth-1, size, fieldB, fieldA))
        return termsAggregationBuilder
    }

    private fun buildPOSAgg(size: Int, field: String): TermsAggregationBuilder{
        val termsAggregationBuilder: TermsAggregationBuilder = AggregationBuilders.terms(field).field(field)
        termsAggregationBuilder.size(size)
        return termsAggregationBuilder
    }
}
