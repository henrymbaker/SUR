public class JoinParser {
  private String input;

  public JoinParser(String input) {
    this.input = input.split(";")[0];
  }

  /* Parses and returns the names the relations to join. */
  public String[] parseRelationNames() {
    // Splits the string on JOIN, then on ON and creates a list of the relations
    // inbetween the two.
    String[] output = this.input.split("JOIN ")[1].split(" ON ")[0].split(", ");
    return output;
  }

  public String[] parseJoinConditions() {
    String[] outputArray = new String[1]; // Will need to change when we have multiple join conditions
    // Split the input on "JOIN " to isolate the join condition
    String[] intermediate = input.split("JOIN ");
    if (intermediate.length > 1) {
      // Split the join condition on " ON " to isolate the comparison expression
      String[] comparison = intermediate[1].split(" ON ");
      if (comparison.length > 1) {
        // Store the comparison expression in the output array
        outputArray[0] = comparison[1];
      }
    }
    return outputArray;
  }
}
