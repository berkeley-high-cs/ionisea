import java.nio.charset.StandardCharsets;

public class Xor {

  // This is the cipher text, encoded as a hex string. If you translate
  // every two characters of this string into a byte you will have an
  // array of bytes which can be decoded by xor'ing the bytes with bits
  // from the key, taking first the 8 least significant bits of the key
  // for the 0th byte, the the next 8, and so on, looping back around to
  // the least significant bits every four bytes.
  private static final String CIPHERTEXT =
    "452dbb017333a6456328a64f6064a6522722ba4f26";

  private byte[] cipherBytes = new byte[CIPHERTEXT.length()/2];
  private int key;
  public Xor (int key) {
    this.key = key;
  }

  public String decode (String s) {
    String deciph = "";
    System.out.println(s.getBytes(StandardCharsets.UTF_8));
    for (int i = 0; i<cipherBytes.length; i++) {
      cipherBytes[i] = (byte) Integer.parseInt(s.substring(2*i, 2*(i+1)),16);
      System.out.print(cipherBytes[i] + " ");
    }
    System.out.println((byte) Integer.parseInt("ff", 16));
    return "f";
  }

  // You shouldn't change the code in main but you do need to implement
  // the decode method and an appropriate constructor to make it work.
  // And you'll probably want to implement some helper methods along the
  // way.
  //
  // To convert a String containing a hex digit into a numeric value
  // you'll want to look up the Integer.parseInt method.
  //
  // Also I should have talked to you about the byte data type and how
  // to use the cast operator to cast an int to a byte.
  //
  // To make a String from an array of bytes (byte[]) you can use the
  // String constructor String(byte[] bytes, String enc) like:
  //
  //   new String(bytes, StandardCharsets.UTF_8);
  //
  // The value 567231495 passed to the constructor is the secret key.
  // It's just a random int, i.e. 32 random bits. I produced the value
  // of CIPHERTEXT by first converting a String message to bytes via the
  // String method:
  //
  //   s.getBytes(StandardCharsets.UTF_8)
  //
  // and then encoding those bytes via the same process as I described
  // above for decoding. (This is a symmetric cipher meaning encryption
  // and decryption are the same process.)

  public static void main(String[] argv) throws Exception {
    
    //System.out.println(Integer.parseInt("ff", 16));
    System.out.println(new Xor(567231495).decode(CIPHERTEXT));
  }
}
