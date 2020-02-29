package dulcinea.hangman.elasticsearch;

import org.junit.ClassRule;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

public class ElasticsearchWrapper {
    @ClassRule
    public static ElasticsearchContainer elasticsearchContainer = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch-oss:6.5.4");
}
