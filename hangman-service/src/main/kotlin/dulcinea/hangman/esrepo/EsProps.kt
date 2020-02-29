package dulcinea.hangman.esrepo

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("elasticsearch")
data class EsProps(var index: String, val host: String, val port: Int) {}