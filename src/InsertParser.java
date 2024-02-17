public class InsertParser {
  /* Reference to the input string being parsed */
  private String input;
  private boolean isValidSyntax;
  private final String DELIMITER = "\\s+";
  private final char SINGLE_QUOTE = 39;
  
  /* Constructor to initialize the input field */
  public InsertParser(String input) {
    this.input = input;
    this.isValidSyntax = verifySyntax();
  }
  
  /* Parses and returns the name of the relation to insert into */
  public String parseRelationName() {
    String[] words = this.input.split(DELIMITER);
    return words[1];
  }
  
  /* Parses and returns a tuple based on a command.*/
  public Tuple parseTuple() {
    String attributeValues = removeSemicolon(this.input);
    attributeValues = attributeValues.split(DELIMITER,3)[2]; /* Isolate attribute values. */
    Tuple tuple = new Tuple();
    for (int i = 0; i < attributeValues.length(); i++) {
      
      /* If the current char is a single quote, add everything
      inside it to the tuple. */
      if (attributeValues.charAt(i) == SINGLE_QUOTE) {
        i++;
        String betweenQuotes = "";
        while (attributeValues.charAt(i) != SINGLE_QUOTE) {
          betweenQuotes += attributeValues.charAt(i);
          i++;
          if (i == attributeValues.length()) {
            System.out.println("SYNTAX ERROR FOR \"" + this.input
                               + "\": MISSING CLOSING \"'\".");
            return null;
          }
        }
        tuple.add(new AttributeValue("",betweenQuotes));
        
        /* If the current char isn't a space, read until a space
        and add that string to the tuple. */
      } else if (!Character.isWhitespace(attributeValues.charAt(i))) {
        String element = "";
        while (i < attributeValues.length() && !Character.isWhitespace(attributeValues.charAt(i))) {
          element += attributeValues.charAt(i);
          i++;
        }
        tuple.add(new AttributeValue("",element));
      }
    }
    return tuple;
  }
  
  /* Accessor for isValidSyntax field. */
  public boolean getIsValidSyntax() {
    return this.isValidSyntax;
  }
  
  /*
  * Regular expression that would match the strings of the form
  * "INSERT ITEM <list>;" where ITEM can be any word and <list> is a whitespace
  * separated list in the form "Item1 Item2" of any length and single quoted
  * strings are accepted in <list> :
  */
  private boolean verifySyntax() {
    return this.input.matches("(?i)INSERT\\s+\\w+\\s+(\\S+)\\s*((\\s+(\\S+))*)\\s*;");
  }
  
  /* Removes semicolon from end of given String and returns it. */
  private String removeSemicolon(String input) {
    if (input.endsWith(";")) {
      input = input.substring(0, input.length() - 1);
    }
    return input;
  }
}
