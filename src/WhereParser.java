import java.util.ArrayList;

public class WhereParser {
    private final char SINGLE_QUOTE = 39;
    private String input;
    private boolean checkedAttributesExist = false;
    private ArrayList<String> attributeNames;
    private ArrayList<String> operators;
    private ArrayList<String> comparisons;
    private ArrayList<String> logicalOperators;
    

    public WhereParser(String input) {
        this.input = input;
        attributeNames = new ArrayList<>();
        operators = new ArrayList<>();
        comparisons = new ArrayList<>();
        logicalOperators = new ArrayList<>();
        parseInput();
    }

    /* Separates the expressions in the WHERE clause into the arribute names,
     * the boolean operators, and the value that the attribute is being compared to*/
    private void parseInput() {
        ArrayList<String> splitInput = splitInput();
        for (int i = 0; i<splitInput.size(); i++) {
            if (isComparisonOperator(splitInput.get(i))) {
                attributeNames.add(splitInput.get(i-1));
                operators.add(splitInput.get(i));
                comparisons.add(splitInput.get(i+1));
            } else if (splitInput.get(i).equalsIgnoreCase("and")) {
                logicalOperators.add("and");
            } else if (splitInput.get(i).equalsIgnoreCase("or")) {
                logicalOperators.add("or");
            }
        }
    }

    /* Returns true if the AttributeValues in the tuple meet the conditions in the
     * saved Where clause, returns false if they don't.*/
    public Boolean meetsConditions(Tuple tuple) {
        if (!checkedAttributesExist && !attributesExist(tuple)) {
            return null;
        }
        return meetsConditions(tuple,logicalOperators.size());
    }


    private boolean meetsConditions(Tuple tuple, int ops) {
        if (input.equals("")) {
            return true;
        }
        boolean first = evaluate(tuple,0);
        boolean second = first;
        /* Evaluates the expression from left to right, grouping all the "and"s together */
        for (int i = 0; i<ops; i++) {
            if (logicalOperators.get(i).equals("or")) {
                second = evaluate(tuple,i+1); 
                while (i+1 < ops && logicalOperators.get(i+1).equals("and")) {
                        i++;
                        second = second && evaluate(tuple,i+1);
                }
                first = first || second;
            } else {
                first = first && evaluate(tuple,i+1);
                while (i+1 < ops && logicalOperators.get(i+1).equals("and")) {
                    i++;
                    first = first && evaluate(tuple,i+1);
                }
            }
        }
        return first;
    }

    public boolean attributesExist(Tuple tuple) {
        for (String name : attributeNames) {
            if (tuple.getValue(name) == null) {
                return false;
            }
        }
        checkedAttributesExist = true;
        return true;
    }

    /* Evaluates an boolean expression of the form <value> <operator> <value>. */
    private boolean evaluate(Tuple tuple, int conditionNum) {
        String attributeName = attributeNames.get(conditionNum);
        String tupleValue = tuple.getValue(attributeName);
        String compareValue = comparisons.get(conditionNum);
        switch(operators.get(conditionNum)) {
            case "=":
              return tupleValue.equals(compareValue);
            case "!=":
              return !tupleValue.equals(compareValue);
            case "<":
              return compareAttributes(tupleValue, compareValue) < 0;
            case ">":
            return compareAttributes(tupleValue, compareValue) > 0;
            case "<=":
              return compareAttributes(tupleValue, compareValue) <= 0;
            case ">=":
            return compareAttributes(tupleValue, compareValue) >= 0;
        }
        return true;
    }

    private ArrayList<String> splitInput() {
        ArrayList<String> split = new ArrayList<>();
        for (int i = 0; i < input.length(); i++) {
      
            /* If the current char is a single quote, add everything
            inside it to the tuple. */
            if (input.charAt(i) == SINGLE_QUOTE) {
              i++;
              String betweenQuotes = "";
              while (input.charAt(i) != SINGLE_QUOTE) {
                betweenQuotes += input.charAt(i);
                i++;
                if (i == input.length()) {
                  System.out.println("SYNTAX ERROR FOR \"" + this.input
                                     + "\": MISSING CLOSING \"'\".");
                  return null;
                }
              }
              split.add(betweenQuotes);
              
              /* If the current char isn't a space, read until a space
              and add that string to the tuple. */
            } else if (!Character.isWhitespace(input.charAt(i))) {
              String element = "";
              while (i < input.length() && !Character.isWhitespace(input.charAt(i))) {
                element += input.charAt(i);
                i++;
              }
              split.add(element);
            }
          }
        return split;
    }

    /* Returns negative if attribute1 is less than attribute2, returns 0 if they are the same,
     * returns positive value otherwise */
    private int compareAttributes(String attribute1, String attribute2) {
        int result = 0;
        try {
            result = Integer.valueOf(attribute1).compareTo(Integer.valueOf(attribute2));
        } catch (NumberFormatException e) {
            result = attribute1.compareTo(attribute2);
        }
        return result;
    }

    private boolean isComparisonOperator(String input) {
        return (input.contains("=") || input.contains("<") || input.contains(">"));
    }
}


