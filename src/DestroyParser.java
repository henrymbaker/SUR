public class DestroyParser {
  private String input; /* Reference to the input string being parsed */
  private boolean isValidSyntax;
  
  /* Constructor to initialize the input field */
  public DestroyParser(String input) {
    this.input = input;
    this.isValidSyntax = verifySyntax();
  }
  
  /* Parses and returns the name of the relation to destroy */
  public String parseRelationName() {
    String relationName = this.input.split("\\s+")[1];
    return relationName.substring(0, relationName.length() - 1);
  }
  
  /* Accessor for isValidSyntax field. */
  public boolean getIsValidSyntax() {
    return this.isValidSyntax;
  }
  
  /* Checks if String is in the form "DESTROY <Relation Name>;"
  * where <Relation Name> is any string of non-whitespace characters. */
  public boolean verifySyntax() {
    return this.input.matches("(?i)DESTROY\\s+(\\w+\\s*,\\s*)*\\w+\\s*;");
  }
}
