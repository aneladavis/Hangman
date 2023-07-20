/** The class that keeps track of the state of the Hangman game.
  * This class has several helper methods that break down the steps of the game.
  * 
  * 
  * @author Anela Davis
  * @author Paige Johnson
*/

import java.util.List;
import java.util.*;


public class HangmanManager {
    // Definitions of Global Variables
    Set<String> setOfWords;
    SortedSet<Character> lettersGuessed;
    int guessesRemaining;
    String currentPattern;
    List<Character> currentPatternArray; // List that contains the pattern to be displayed to the user
    int length;
    Map<List<Character>, List<String>> wordFamilies; // map containing the different word patterns for each guessed char
    List<Character> testPattern; // array list that finds all combinations of patterns
    List<String> dictionary;
    List<String> newWords; // List of words that will update the dictionary each guess
    List<Character> maxPattern; // List containing the pattern that will have the most corresponding words

    /* Constructs each object.
     * 
     * @param the list of all possible words named dictionary, length
     * @throws FileNotFoundException if dictionary file is not found*/
    public HangmanManager(List<String> dictionary, int length, int max) {
        if (length < 1 || max < 0) {
            throw new IllegalArgumentException();
        }
        this.dictionary = new ArrayList<>();
        lettersGuessed = new TreeSet<>();
        currentPatternArray = new ArrayList<>();
        maxPattern = new ArrayList<>();
        // removed initiating hashmap here
        // for loop that fills current pattern array with dashes
        for (int i = 0; i < length; i++) {
            currentPatternArray.add('-');
            maxPattern.add('-');
        }
        // for loop that creates the dictionary of applicable words
        for (String word : dictionary) {
            if (word.length() == length) {
                this.dictionary.add(word);
            
            }
        }
        this.length = length;

        guessesRemaining = max;
    }

    /* Creates a Hash Set of words with applicable words for the game
     * 
     * @return the set of words left to be considered */
    public Set<String> words() {
        setOfWords = new HashSet<>();
        for (String word : this.dictionary) {
            setOfWords.add(word);
        }
    
        return setOfWords;
    }
    /* Keeps track of the number of guesses remaining for the player to guess
     *
     * @return number of guesses remaining */
    public int guessesLeft() {
        return guessesRemaining;
    }
    /* Keeps track of letters guessed by the player
     * 
     * @return Sorted Set of letters guessed so far */
    public SortedSet<Character> guesses() {
        return lettersGuessed;
    }
    /* Converts the current pattern array to a string with spaces between characters
     * 
     * @throws IllegalStateException if the set of words is empty
     * @return the trimmed current pattern string to be displayed*/
    public String pattern() {
        if (dictionary.isEmpty()) {
            throw new IllegalStateException();
        }
        currentPattern = "";
        for(int i = 0; i < length; i ++){
            currentPattern += currentPatternArray.get(i) + " ";
    
        }
  

        return currentPattern.trim();
    }

    /* Updates the current pattern array to include the most recent letter guessed
     * Uses maxPattern which is the pattern with the largest number of words
     * 
     */
    public void updateCurrentPattern() {
        for(int i = 0; i < length; i ++){
            if(maxPattern.get(i) != '-'){
            currentPatternArray.set(i, maxPattern.get(i));
            }
        
        }
    }
    /* Updates the new max list representing the pattern with the corresponding List of words
     * 
     */
    public void updateMaxs(List<Character> pattern, List<Character> maxPattern){
        for(int i = 0; i < length; i++){
            maxPattern.set(i, pattern.get(i));
        }
    }
    /* Gets the pattern with th)e max amount of words
     * 
     * @param Map that stores the pattern as a key and the list of words that apply as the value
     * @return ArrayList with the characters of the best pattern*/
    public void getBestPattern(char guess) {
        int maxNum = 0;
        for(List<Character> pattern : wordFamilies.keySet()) {
            if (maxNum == wordFamilies.get(pattern).size()){
                if(maxPattern.contains(guess)){ // chooses pattern that does not have to reveal a letter if possible
                    maxNum = wordFamilies.get(pattern).size();
                    updateMaxs(pattern, maxPattern);
                }
            }
            else if (maxNum < wordFamilies.get(pattern).size()) {
                maxNum = wordFamilies.get(pattern).size();
                updateMaxs(pattern, maxPattern);
            }
        }
        
        updateCurrentPattern();
    }
    /* Counts the number of times the guessed letter occurs in the best pattern
     * 
     * @param Character guess that is the most recent guessed letter
     * @return the count of times the letter occurs */
    public int getInstances(char guess) {
        int count = 0;
        for (int i = 0; i < length; i++) {
            if (maxPattern.get(i) == guess) {
                count++;
            }
        }
        return count;
    }
    /* Creates a Map WordFamilies that has all possible patterns stored with the words that fit them
     * Uses ArrayLists to store the patterns and sets of words as the key and value, respectively
     * 
     * @param Character guess the most recent letter guessed
     * @return ArrayList of words that can be used for the rest of the game */
    public List<String> getNewSetOfWords( char guess) {
        // loop that builds the ArrayLists for the keys and values of the map
        // the key is test pattern and the value is the corresponding words
        wordFamilies = new HashMap<>(); //initiate hashmap
        newWords = new ArrayList<>();
        for (String word : this.dictionary) {
            testPattern = new ArrayList<>(); 
            for (int i = 0; i < length; i++) {
                if (word.charAt(i) == guess) {
                    testPattern.add(guess);
                } else {
                    testPattern.add('-');
                }
            

            }
            
            // adding the keys and values to the map
            if (wordFamilies.containsKey(testPattern)) {
                wordFamilies.get(testPattern).add(word);
                

            } else {
                wordFamilies.put(testPattern, new ArrayList<String>());
                wordFamilies.get(testPattern).add(word);
            }

        }
        // using helper to find the best parretn in the map
    
        getBestPattern(guess);

        for(String word: wordFamilies.get(maxPattern)){
            newWords.add(word);
        }
        if(!(newWords.isEmpty())){
            this.dictionary.clear();
            this.dictionary = newWords;
        }

       
        
        
       
        return this.dictionary;
    }

    /* Uses helper methods to run the game
     * Calls letters guessed to update set of letters guessed
     * Calls getNewSetOfWords to find set of words that apply to the updated pattern
     * Updates guesses remaining
     * 
     * @param character most recently guessed
     * @throws IllegalStateException if the number of guesses left is fewer than 1
     * @throws IllegalArguementException if the list of words is non-empty and character was guessed previously
     * @return number of instances the most recent guess occured in the new pattern*/
    public int record(char guess) {
        if (guessesRemaining < 0 || this.dictionary.isEmpty()) {
            throw new IllegalStateException();
        } else if (dictionary.isEmpty() && lettersGuessed.contains(guess)) {
            throw new IllegalArgumentException();
        } else {
            // calling helper functions
            lettersGuessed.add(guess);
            getNewSetOfWords(guess);
            // updates guesses remaining if the guess was not in the word
            if(!(currentPatternArray.contains(guess))){
                guessesRemaining--;
            }
           
            return getInstances(guess);
        }
    }

}

    

