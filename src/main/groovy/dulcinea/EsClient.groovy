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
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.terms.Terms
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder
import org.elasticsearch.transport.client.PreBuiltTransportClient

import javax.management.Query

class EsClient {

  private TransportClient client
  private String index

  EsClient(String index = "word") {
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

  private TermsAggregationBuilder buildPOSAgg(Integer size) {
    TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("pos").field("positionOfSingle")
    termsAggregationBuilder.size(size)
    return termsAggregationBuilder
  }

  private getAggs(def response) {
    return response.aggregations.asMap.pos?.buckets
  }

  private parseAgg(Terms.Bucket agg) {
    List aggs = getAggs(agg)
    return [key: agg.key, count: agg.docCount, aggs: aggs ? aggs.collect { parseAgg(it) } : null]
  }

  private TermsAggregationBuilder buildPOSAggToDepth(Integer depth, Integer size) {
    if( depth == 1) {
      return buildPOSAgg(size)
    } else {
      return buildPOSAgg(size).subAggregation(buildPOSAggToDepth(depth-1, size))
    }
  }

  void someBetterName(Closure onComplete, String a) {
    QueryBuilder queryBuilder = QueryBuilders.termQuery("unique", a)
    TermsAggregationBuilder termsAggregationBuilder = buildPOSAggToDepth(3, 10)

    SearchResponse searchResponse = client.prepareSearch(index)
        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
        .setQuery(queryBuilder)
        .addAggregation(termsAggregationBuilder)
        .setFrom(0).setSize(0)
        .get()

    List aggs = getAggs(searchResponse).collect{ parseAgg(it) }
    onComplete(aggs)
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
