package schach.common;

import java.util.HashSet;
import java.util.Set;

/**
 * Holds and parses environment variables like command line arguments.
 */
public class Environment {
  private Set<Flag> activeFlags;

  /**
   * Represens a single flag setting that is parsed from command line arguments.
   */
  public enum Flag {
    USE_GUI("--no-gui", true), USE_SYMBOLS("--use-symbols", false), PRINT_BACKGROUND("--print-background", false),
    MODE_PRESELECTED("--simple", false);

    private static Set<String> validNames = new HashSet<String>();
    private String argName;
    private boolean invertFlag;

    Flag(String argName, boolean invertFlag) {
      this.argName = argName;
      this.invertFlag = invertFlag;
    }

    static {
      // init the valid flags set
      for (Flag flag : Flag.values()) {
        validNames.add(flag.argName);
      }
    }

    /**
     * Checks if this flag is present in the given flags array. If it's not present,
     * the default value is used. If the flag is inverted, true is returned if the
     * flag is missing.
     * 
     * @param flags Array of string flags to check for this flag.
     * @return If this flag is active
     */
    public boolean isActiveWith(Set<String> flags) {
      return invertFlag != flags.contains(argName);
    }

    /**
     * Checks if the given string is a valid flag name.
     * 
     * @param test String to test for being a flag name
     * @return If the string is a valid flag name
     */
    public static boolean isValid(String test) {
      return validNames.contains(test);
    }
  }

  /**
   * Constructs a new environment with the given program arguments.
   * 
   * @param arguments Program arguments to parse for flags
   */
  public Environment(String[] arguments) {
    // parse the flags into a set of string
    Set<String> flagNames = new HashSet<String>(arguments.length);
    for (String flagName : arguments) {
      // throw on duplicate flags
      if (!flagNames.add(flagName)) {
        throw new IllegalArgumentException("The flag " + flagName + " is present multiple times.");
      }

      // throw on invalid flags
      if (!Flag.isValid(flagName)) {
        throw new IllegalArgumentException("The flag " + flagName + " is invalid.");
      }
    }

    // add flags found to be active to the list of active flags
    activeFlags = new HashSet<Flag>();
    for (Flag flag : Flag.values()) {
      if (flag.isActiveWith(flagNames)) {
        activeFlags.add(flag);
      }
    }
  }

  /**
   * Constructs an environment with active flags directly.
   * 
   * @param activeFlags Flags to activate
   */
  public Environment(Set<Flag> activeFlags) {
    this.activeFlags = activeFlags;
  }

  /**
   * Constructs an environment with no active flags.
   */
  public Environment() {
    this.activeFlags = Set.of();
  }

  /**
   * Returns if a given flag is active currently.
   * 
   * @param flag Flag to check
   * @return If the flag is active
   */
  public boolean flagActive(Flag flag) {
    return activeFlags.contains(flag);
  }
}
