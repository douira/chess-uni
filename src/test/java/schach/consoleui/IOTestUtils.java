package schach.consoleui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Assists in testing things that accept a PrintStream for output or a Scanner
 * for input
 */
public class IOTestUtils {
  private static final Charset CHARSET = StandardCharsets.UTF_8;

  /**
   * Tests that a function filled the print stream with the desired string data by
   * piping the data into a local output stream and then checking that it has the
   * correct content.
   * 
   * @param expected String expected to be printed
   * @param executor Function that takes a print stream and is expected to fill
   *                 the print stream with the expected string
   */
  public static void assertPrinted(String expected, Consumer<PrintStream> executor) {
    assertPrinted(expected, executor, false);
  }

  /**
   * Does the same as assertPrinted but optionally prints the exact char
   * difference as well for finding the difference in the strings.
   * 
   * 
   * @param expected  String expected to be printed
   * @param executor  Function that takes a print stream and is expected to fill
   *                  the print stream with the expected string
   * @param printDiff if the char-by-char diff should be printed during the test
   *                  run
   */
  public static void assertPrinted(String expected, Consumer<PrintStream> executor, boolean printDiff) {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    try (PrintStream printStream = new PrintStream(buffer, true)) {
      executor.accept(printStream);
    }
    String printed = new String(buffer.toByteArray(), CHARSET);

    if (printDiff) {
      char[] expectedChars = expected.toCharArray();
      char[] printedChars = new String(buffer.toByteArray(), CHARSET).toCharArray();
      for (int i = 0; i < expectedChars.length || i < printedChars.length; i++) {
        char expectedChar = i < expectedChars.length ? expectedChars[i] : '#';
        char printedChar = i < printedChars.length ? printedChars[i] : '#';
        if (expectedChar != printedChar) {
          System.out.println("diff at " + i + " with expected char=" + expectedChar + " and found char=" + printedChar);
        }
      }
    }

    assertEquals(expected, printed);
  }

  /**
   * Runs a function with a scanner that returns the string given with the insert
   * string. The asserter is expected to assert that some object behaved correctly
   * with the given input from the scanner.
   * 
   * @param insert   String to put in the scanner
   * @param asserter Function to run with the constructed scanner
   */
  public static InputStream mockInputStream(String insert) {
    return new ByteArrayInputStream(insert.getBytes(CHARSET));
  }
}
