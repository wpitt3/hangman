package dulcinea

import groovy.json.JsonBuilder
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest
import org.elasticsearch.action.delete.DeleteResponse
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.TransportAddress
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.index.query.BoolQueryBuilder
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.terms.Terms
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder
import org.elasticsearch.transport.client.PreBuiltTransportClient

class EsClient {

  private TransportClient client
  private String index

  EsClient(String index = "hangman") {
    this.index = index
  }

  void index(Word word, Closure onComplete) {
    def result = client.prepareIndex(index, "_doc", word.word)
        .setSource(new JsonBuilder(word.asMap()).toString(), XContentType.JSON)
        .get()
    onComplete(result)
  }

  void delete(String word, Closure onComplete) {
    DeleteResponse r = client.prepareDelete(index, "_doc", word).get()
    onComplete(r)
  }

  void refresh(String word, Closure onComplete) {
    DeleteResponse r = client.indices().refresh(new RefreshRequest(index), RequestOptions.DEFAULT)
    onComplete(r)
  }

  void get(Closure onComplete) {
    SearchResponse x = client.prepareSearch(index)
        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
        .setQuery(QueryBuilders.matchAllQuery())
//        .setQuery(QueryBuilders.termQuery("position", "d"))
        .setFrom(0).setSize(60)
        .get()
    x.hits.collect {
      println it
    }
    onComplete(x)
  }

  private TermsAggregationBuilder buildPOSAgg(Integer size, String field) {
    TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms(field).field(field)
    termsAggregationBuilder.size(size)
    return termsAggregationBuilder
  }

  //"positionOfSingle"
  //negative

  private List getAggs(def response) {
    return (response.aggregations.asMap.positionOfSingle?.buckets ?: []) + (response.aggregations.asMap.negative?.buckets  ?: [])
  }

  private Map parseAgg(Terms.Bucket agg) {
    List aggs = getAggs(agg)
    return [key: agg.key, count: agg.docCount, aggs: aggs ? aggs.collect { parseAgg(it) } : null]
  }

  private TermsAggregationBuilder buildAggToDepth(Integer depth, Integer size, String fieldA, String fieldB) {
    if( depth == 1) {
      return buildPOSAgg(size, fieldA)
    } else {
      TermsAggregationBuilder termsAggregationBuilder = buildPOSAgg(size, fieldA)
      termsAggregationBuilder.subAggregation(buildAggToDepth(depth-1, size, fieldA, fieldB))
      termsAggregationBuilder.subAggregation(buildAggToDepth(depth-1, size, fieldB, fieldA))
    }
  }

  void findAggregations(List<String> with, List<String> without, Closure onComplete) {
    BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
    with.each { queryBuilder.must(QueryBuilders.termQuery("positionOfSingle", it)) }
    without.each { queryBuilder.must(QueryBuilders.termQuery("negative", it)) }
    findAggregations(onComplete, (with.size() + without.size() == 0 ? QueryBuilders.matchAllQuery() : queryBuilder))
  }

  void findAggregations(Closure onComplete, QueryBuilder queryBuilder) {
    SearchResponse searchResponse = client.prepareSearch(index)
        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
        .setQuery(queryBuilder)
        .addAggregation(buildAggToDepth(2, 160, "positionOfSingle", "negative") )
        .addAggregation(buildAggToDepth(2, 160, "negative", "positionOfSingle"))
        .setFrom(0).setSize(10)
        .get()

    int total = searchResponse.hits.totalHits
    List words = searchResponse.hits.hits.collect{it.id}
    List aggs = getAggs(searchResponse).collect{ parseAgg(it) }
    onComplete([total: total, words: words, aggs: aggs])
  }

  void setup(Closure onComplete) {
    Settings settings = Settings.builder()
      .put("cluster.name", "docker-cluster").build()
    client = new PreBuiltTransportClient(settings)
      .addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300))
    onComplete()
  }

  void close() {
    if (client) {
      client.close()
    }
  }
}

