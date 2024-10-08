public class Streams {

  /*
   * Returns true if its argument is a single-character string and a vowel.
   */
  public boolean isVowel(String s) {
    return s.length() == 1 && "aeiou".indexOf(s.toLowerCase()) != -1;
  }

  /*
   * Returns true if its argument is a prime number.
   */
  public boolean isPrime(int n) {
    for (int d = 2; d <= Math.sqrt(n); d++) {
      if (n % d == 0) return false;
    }
    return n > 1;
  }

}
