package dulcinea

import groovy.json.JsonBuilder
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.TransportAddress
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.transport.client.PreBuiltTransportClient

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
    println result
    onComplete(result)
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
