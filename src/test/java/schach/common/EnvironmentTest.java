package schach.common;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tetsthe behavior of the environment and that setting and reading flags works
 * correctly.
 */
public class EnvironmentTest {
  private static final String PRINT_BACKGROUND_OPTION = "--print-background";
  private static final String NO_GUI_OPTION = "--no-gui";
  private static final String USE_SYMBOLS_OPTION = "--use-symbols";

  /**
   * Tests the default constructor.
   */
  @Test
  public void testConstructor() {
    Environment env = new Environment();
    for (Environment.Flag flag : Environment.Flag.values()) {
      assertFalse(env.flagActive(flag));
    }
  }

  /**
   * Test that flags are active on the correct flags
   */
  @Test
  public void testFlagIsActiveWith() {
    assertTrue(Environment.Flag.PRINT_BACKGROUND.isActiveWith(Set.of(PRINT_BACKGROUND_OPTION, "foo")));
    assertFalse(Environment.Flag.PRINT_BACKGROUND.isActiveWith(Set.of()));
    assertTrue(Environment.Flag.USE_GUI.isActiveWith(Set.of("foo")));
    assertFalse(Environment.Flag.USE_GUI.isActiveWith(Set.of(NO_GUI_OPTION)));
  }

  /**
   * Tests that the correct strings are seen as valid.
   */
  @Test
  public void testFlagIsValid() {
    Map<Boolean, Set<String>> strings = Map.of(Boolean.valueOf(true),
        Set.of(NO_GUI_OPTION, USE_SYMBOLS_OPTION, PRINT_BACKGROUND_OPTION), Boolean.valueOf(false),
        Set.of("", "baz", "--foo", "--use-gui"));

    // assert many true and false
    for (Map.Entry<Boolean, Set<String>> entry : strings.entrySet()) {
      for (String test : entry.getValue()) {
        assertEquals(entry.getKey(), Environment.Flag.isValid(test));
      }
    }
  }

  /**
   * Tests that invalid arguments are thrown
   */
  @Test
  public void testParseErrors() {
    String[] invalidArgs1 = { PRINT_BACKGROUND_OPTION, "buzz" };
    String[] invalidArgs2 = { NO_GUI_OPTION, NO_GUI_OPTION };
    assertThrows(IllegalArgumentException.class, () -> new Environment(invalidArgs1));
    assertThrows(IllegalArgumentException.class, () -> new Environment(invalidArgs2));
  }

  /**
   * Tests that valid arguments are parsed correctly from a list of arguments
   */
  @Test
  public void testParsing() {
    String[] validArgs = { NO_GUI_OPTION, USE_SYMBOLS_OPTION };
    Environment env = new Environment(validArgs);
    assertTrue(env.flagActive(Environment.Flag.USE_SYMBOLS));
    assertFalse(env.flagActive(Environment.Flag.USE_GUI));
    assertFalse(env.flagActive(Environment.Flag.PRINT_BACKGROUND));
    assertFalse(env.flagActive(Environment.Flag.MODE_PRESELECTED));
  }

  /**
   * Tests that empty args are parsed correctly
   */
  @Test
  public void testEmptyArgs() {
    String[] emptyArgs = {};
    Environment envEmpty = new Environment(emptyArgs);
    assertFalse(envEmpty.flagActive(Environment.Flag.PRINT_BACKGROUND));
    assertTrue(envEmpty.flagActive(Environment.Flag.USE_GUI));
  }

  /**
   * Tests the that an empty set of active flags is parsed correctly
   */
  @Test
  public void testEmptySetArgs() {
    Environment env = new Environment(Set.of());
    for (Environment.Flag flag : Environment.Flag.values()) {
      assertFalse(env.flagActive(flag));
    }
  }
}
