package dulcinea


class Word {
  final String word
  final List<Integer> positionOfSingle
  final List<Integer> negative

  Word(String word) {
    this.word = word.toUpperCase()
    List<Integer> letters = word.toUpperCase().split('').collect{letterToNum(it)}
    List<Integer> unique = letters.clone().unique().sort()
    List<String> count = unique.collect{ letter -> "${letters.count{ it == letter}}|$letter" }

    positionOfSingle = (0..(letters.size()-1))
        .findAll{ count.contains("1|${letters[it]}") }
        .collect{ letters[it] + it*26}
    negative = (1..26).findAll{ !unique.contains(it) }.collect{ -it }
  }

  Map asMap(){
    return [
        positionOfSingle: positionOfSingle,
        negative:negative]
  }

  static Integer letterToNum(String x) {
    return ((int)x.charAt(0))-64
  }

  static Integer positionToNum(String x) {
    Integer letterAsNum = letterToNum(x)
    return x.size() == 2 ? (letterAsNum + Integer.parseInt(x[1]) * 26) : -letterAsNum
  }

  static String numToLetter(Integer x) {
    return (char)(x+64)
  }

  static String numToPosition(Integer x) {
    return numToLetter(postionNumToLetterNum(x)) + "" + (x > -1 ? (int)Math.floor((x-1) / 26) : "")
  }

  static Integer postionNumToLetterNum(Integer x) {
    return (Math.abs(x)-1)%26+1
  }
}
