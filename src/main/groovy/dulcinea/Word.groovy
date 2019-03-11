package dulcinea


class Word {
  final String word
  final List<String> position
  final List<String> unique
  final List<String> count
  final List<String> positionOfSingle
  final List<String> negative

  Word(String word) {
    this.word = word.toUpperCase()
    List letters = word.toUpperCase().split('')
    unique = letters.clone().unique().sort()
    count = unique.collect{ letter -> "${letters.count{ it == letter}}$letter" }
    position = (0..(letters.size()-1)).collect{ "${letters[it]}$it"}
    positionOfSingle = (0..(letters.size()-1))
        .findAll{ count.contains("1${letters[it]}") }
        .collect{ "${letters[it]}$it"}
    negative = (0..25).collect{ ((char)(it + 65)).toString()}.findAll{ !unique.contains(it) }
  }

  Map asMap(){
    return [word: word, unique: unique, position: position, count: count, positionOfSingle:positionOfSingle, negative:negative]
  }

}
