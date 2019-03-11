package dulcinea

import spock.util.concurrent.BlockingVariable

BlockingVariable deployed = new BlockingVariable(10)

EsClient esClient = new EsClient("hangman")
esClient.setup {
  deployed.set(true)
}
assert deployed.get()

List sixLetterWords = new File(getClass().getResource('/6letterwords.txt').toURI()).readLines()

int count = 0
for (String x : sixLetterWords) {
  if(count % 100 == 0) {
    println count
  }
  esClient.index(new Word(x), Closure.IDENTITY)
  count += 1
}