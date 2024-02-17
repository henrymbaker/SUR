import java.util.Arrays;

public class RelationParser {
  /* Reference to the input string being parsed */
  private String input;
  private boolean isValidSyntax;
  
  /* Constructor to initialize the input field */
  public RelationParser(String input) {
    this.input = input;
    this.isValidSyntax = verifySyntax();
    
  }
  
  /* Returns the name of the relation. */
  public String parseRelationName() {
    return this.input.split("\\s+")[1];
  }
  
  /* Parses and returns a Relation with the given attributes. */
  public Relation parseRelation() {
    String name = this.parseRelationName();
    Relation relation = new Relation(name);
    
    /* Array of individual attribute definitions. */
    String inParentheses = input.substring(input.indexOf("(")+1, (input.indexOf(");")));
    String[] attrDefs = inParentheses.split(",");
    
    /* Creates attributes with given attribute definitions and adds to relation schema. */
    for (int i = 0; i < attrDefs.length; i++) {
      String[] elements = attrDefs[i].trim().split("\\s+");
      
      /* Ensures attribute definition has exactly 3 elements. */
      if (elements.length != 3) {
        System.out.println("INVALID ATTRIBUTE LENGTH FOR RELATION \"" + name
                           + "\" (given: " + elements.length + ", expected: 3");
        return null;
      }
      /* Ensures attribute datatype is either CHAR or NUM */
      if (!elements[1].equals("CHAR") && !elements[1].equals("NUM")) {
        System.out.println("INVALID ATTRIBUTE DATA TYPE FOR RELATION \"" + name
                           + "\": (given: " + elements[1]
                           + ", expected: CHAR or NUM)");
        return null;
      }
      relation.addToSchema(new Attribute(elements[0],elements[1],Integer.valueOf(elements[2])));
    }
    return relation;
  }
  
  /* Accessor for isValidSyntax field. */
  public boolean getIsValidSyntax() {
    return this.isValidSyntax;
  }
  
  /*
  * Regular expression that would match the strings of the form
  * "RELATION ITEM (<list>);" where ITEM can be any word, and <list> is a comma
  * separated list of any length in the form
  * "ITEM1a ITEM1B ITEM1C, ITEM2a ITEM2B ITEM2C" where each list item can only
  * contain 3 words separated by any whitespace
  */
  private boolean verifySyntax() {
    return this.input.matches(
    "(?i)RELATION\\s+\\w+\\s*\\(\\s*((\\w+\\s+\\w+\\s+\\w+\\s*,\\s*)*(\\w+\\s+\\w+\\s+\\w+\\s*)*)\\s*\\)\\s*;");
  }
}
