package dulcinea

import spock.lang.Specification

class WordSpec extends Specification{

  void "Word as map"(){
    when:
      Word word = new Word("tten")

    then:
      word.asMap() == [
          positionOfSingle: [(5+(2*26)), (14+(3*26))],
          negative: (1..26).findAll{ it != 5 && it != 14 && it != 20}.collect{-it}
      ]
  }

  void "PositionToNum A0"(){
    when:
      Integer result = Word.positionToNum("A0")

    then:
      result == 1
  }

  void "PositionToNum A1"(){
    when:
      Integer result = Word.positionToNum("A1")

    then:
      result == 1 + 26
  }

  void "PositionToNum E5"(){
    when:
      Integer result = Word.positionToNum("E5")

    then:
      result == 5 + 5*26
  }

  void "letterToNum A"(){
    when:
      Integer result = Word.letterToNum("A")

    then:
      result == 1
  }

  void "numToLetter A"(){
    when:
      String result = Word.numToLetter(5)

    then:
      result == "E"
  }

  void "numToPosition E0"(){
    when:
      String result = Word.numToPosition(5)

    then:
      result == "E0"
  }

  void "numToPosition Z0"(){
    when:
      String result = Word.numToPosition(26)

    then:
      result == "Z0"
  }

  void "numToPosition A1"(){
    when:
      String result = Word.numToPosition(27)

    then:
      result == "A1"
  }

  void "numToPosition A0"(){
    when:
      String result = Word.numToPosition(1)

    then:
      result == "A0"
  }

  void "thereAndBack Q6"(){
    when:
      String result = Word.numToPosition(Word.positionToNum("Q6"))

    then:
      result == "Q6"
  }

  void "thereAndBack 200"(){
    when:
      Integer result = Word.positionToNum(Word.numToPosition(200))

    then:
      result == 200
  }
}
