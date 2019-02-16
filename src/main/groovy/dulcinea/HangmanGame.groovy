package dulcinea

class HangmanGame {
  EsClient client

  HangmanGame() {

  }

  void setup(Closure onComplete) {
    client.setup(onComplete)
  }

  void close() {
    client.close()
  }

}
