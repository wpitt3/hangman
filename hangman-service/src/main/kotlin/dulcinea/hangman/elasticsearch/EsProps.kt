package dulcinea.hangman.elasticsearch

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("elasticsearch")
data class EsProps(val index: String, val host: String, val port: Int, val searchDepth: Int) {}