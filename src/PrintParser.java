import java.util.Arrays;

public class PrintParser {
  private String input; /* Reference to the input string being parsed */
  private boolean isValidSyntax;
  
  /* Constructor to initialize the input field. */
  public PrintParser(String input) {
    this.input = input;
    this.isValidSyntax = verifySyntax();
  }
  
  /* Parses and returns the names the relations to print. */
  public String[] parseRelationNames() {
    String elements = this.input.substring(5); /* Removes "PRINT" from command */
    String[] relationNames = elements.trim().split(",\\s+|;"); /* Isolates names */
    return relationNames;
  }
  
  /* Accessor for isValidSyntax field. */
  public boolean getIsValidSyntax() {
    return this.isValidSyntax;
  }
  
  /*
  * Check if a string is in the form "PRINT <list>;", where <list> is a list of
  * items separated by commas and any amount of whitespace
  */
  private boolean verifySyntax() {
    return this.input.matches("(?i)PRINT\\s+(\\w+\\s*,\\s*)*\\w+\\s*;");
  }
}
