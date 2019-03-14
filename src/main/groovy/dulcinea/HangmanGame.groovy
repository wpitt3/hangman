package dulcinea

import io.vertx.core.CompositeFuture
import io.vertx.core.Future

class HangmanGame {
  EsClient client

  List<Integer> with = []
  List<Integer> without = []

  HangmanGame() {

  }

  void start() {
    with = []
    without = []
  }

  void addLetter(String letter, Closure onComplete) {
    Integer playerGuess = Word.letterToNum(letter)
    if ((with + without).contains(playerGuess)){
      onComplete(currentToString()); return
    }

    List options =  (0..5).collect{ [with: (with.clone() + Word.positionToNum(letter + it)), without: without] }
    options += [with: with, without: without.clone() + Word.positionToNum(letter)]
    Map<Future, Map> futures = options.collectEntries{ [(buildFuture(it.with, it.without)) : it] }
    CompositeFuture.all(futures.keySet().asList()).setHandler({ done ->
      if (done.succeeded()) {
        List results = futures.collect{ k, v -> k.result() + v }
        Map winner = results.max{it.score}
        with = winner.with
        without = winner.without
        onComplete(currentToString())
      }
    })
  }

  private Future buildFuture(List with, List without) {
    Future<Map> future = Future.future()
    client.findAggregations(with, without, { Map result ->
      Map groupedAggs = EsAggAnalyser.groupAggsByLetter(result.aggs, with.collect{Word.postionNumToLetterNum(it)})
      def score = result.aggs ? EsAggAnalyser.findValueForMinScore(groupedAggs) : null
      future.complete([
          total: result.total,
          words: result.words,
          score: score ? score.value : 0,
          bestLetter: score ? score.key : ""])
    })
    return future
  }

  void setup(Closure onComplete) {
    client = new EsClient()
    client.setup(onComplete)
  }

  void close() {
    client.close()
  }

  private String currentToString() {
    List toPrint = (0..5).collect{ "_"}
    with.collect{Word.numToPosition(it)}.each{  toPrint[Integer.parseInt(it[1])] = it[0] }
    return toPrint.join(" ") + "   " + without.collect{Word.numToPosition(it)}
  }
}
