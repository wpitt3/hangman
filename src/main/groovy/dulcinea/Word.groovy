package dulcinea


class Word {
  final String word
  final List<String> position
  final List<String> unique
  final List<String> count

  Word(String word) {
    this.word = word
    List letters = word.split('')
    unique = letters.clone().unique().sort()
    count = unique.collect{ letter -> "${letters.count{ it == letter}}$letter" }
    position = (0..(letters.size()-1)).collect{ "${letters[it]}$it"}
  }

  Map asMap(){
    return [word: word, unique: unique, position: position, count: count]
  }

}
