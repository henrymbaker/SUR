public class DeleteParser {
  /* Reference to the input string being parsed */
  private String input;
  private boolean isValidSyntax;
  
  /* Constructor to initialize the input field */
  public DeleteParser(String input) {
    this.input=input.split(";")[0];
    this.isValidSyntax = verifySyntax();
  }
  
  public void deleteWhere(Relation relation, String name) {
    WhereParser whereParser = new WhereParser(getWhereClause());
    int relationSize = relation.size();
    for (int i = 0; i<relationSize; i++) {
        Tuple temp = relation.getTuple(i);
        Boolean meetsConditions = whereParser.meetsConditions(temp);
        if (meetsConditions != null) {
            if (meetsConditions) {
              relation.delete_ith_Tuple(i);
              relationSize = relation.size();
              i--;
            }
        } else {
            System.out.println("ERROR DELETING FROM RELATION: ATTRIBUTE DOES NOT EXIST.");
            return;
        }  
    }
  }

private String getWhereClause() {
  int whereIndex = input.toUpperCase().indexOf("WHERE");
  if (whereIndex != -1) {
      return input.substring(whereIndex, input.length());
  }
  return ""; 
}

  /* Parses and returns the name of the relation for delete */
  public String parseRelationName() {
    return input.split("\\s+")[1];
}
  
  /* Accessor for isValidSyntax field. */
  public boolean getIsValidSyntax() {
    return this.isValidSyntax;
  }
  
  /* Checks if String is in the form "DELETE <Relation Name>;"
  * where <Relation Name> is any string of non-whitespace characters. */
  public boolean verifySyntax() {
    return this.input.matches("(?i)DELETE\\s+\\w+(\\s+WHERE\\s+\\S+\\s+(=|!=|<|>|<=|>=)\\s+(\\S+|'.*')(\\s+(and|or)\\s+\\S+\\s+(=|!=|<|>|<=|>=)\\s+(\\S+|'.*'))*){0,1}\\s*");
  }
}
