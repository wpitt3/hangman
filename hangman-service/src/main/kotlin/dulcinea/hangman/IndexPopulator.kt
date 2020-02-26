package dulcinea.hangman

import java.io.File

class IndexPopulator(val wordRepository: WordRepository) {

    fun populateRepo() {
        val words: List<String> = File("src/main/resources/six_letter_words.txt").readLines()
        words.forEach {
            wordRepository.create(Word(it))
        }
    }
}