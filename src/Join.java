import java.util.ArrayList;
import java.util.LinkedList;

public class Join {
  private ArrayList<Relation> relations;
  private String[] joinConditions;
  private ArrayList<String> attributeNames;
  private ArrayList<String> operators;
  private ArrayList<String> comparisons;
  private ArrayList<String> logicalOperators;

  public Join(ArrayList<Relation> relations, String[] joinConditions) {
    this.relations = relations;
    if (joinConditions.length != 0) {
      this.joinConditions = joinConditions;
      attributeNames = new ArrayList<>();
      operators = new ArrayList<>();
      comparisons = new ArrayList<>();
      logicalOperators = new ArrayList<>();
      if (joinConditions[0] != null) {
        parseConditions(joinConditions[0]); // needs to change with multiple conditions
      }
    }
  }
  /*
  * Create a new (temporary) relation where attribute names are <attribute names
  * from the first relation>
  * then <attribute names from the second relation, minus the repeated attribute
  * name>
  */
  public Relation getRelation(String name) {
    Relation newRelation = new Relation(name);
    Boolean noJoinCondition = this.joinConditions[0] == null;
    // Check the Join attributes for compatible formats
    if (noJoinCondition || validateAttributes()) {
      LinkedList<Attribute> addedAttributes = new LinkedList<>();
      for (Relation rel : relations) {
        int schemaSize = rel.schemaSize();
        for (int i = 0; i < schemaSize; i++) {
          Attribute att = rel.getSchema(i);
          Boolean alreadyAdded = false;
          for (Attribute attTemp : addedAttributes) {
            String attName = att.getName();
            String tempAttName = attTemp.getName();
            if (attName.equalsIgnoreCase(tempAttName)) {
              alreadyAdded = true;
            }
          }
          // Add attributes to schema
          if (!alreadyAdded) {
            addedAttributes.push(att);
            newRelation.addToSchema(att);
          }
        }
      }
      // insert only required tuples
      for (int i = 0; relations.size() / 2 > i; i += 2) {
        Relation attributeRelation = relations.get(i);
        Relation comparisonRelation = relations.get(i + 1);
        Integer attributeRelationSize = attributeRelation.size();
        Integer comparisonRelationSize = comparisonRelation.size();
        for (int j = 0; j < attributeRelationSize; j++) {
          Tuple attributeTuple = attributeRelation.getTuple(j);
          for (int k = 0; k < comparisonRelationSize; k++) {
            Tuple comparisonTuple = comparisonRelation.getTuple(k);
            if (noJoinCondition || meetsConditions(attributeTuple, comparisonTuple)) {
              Tuple tuple = buildTuple(attributeTuple, comparisonTuple, newRelation.getSchema());
              newRelation.insert(tuple);
            }
          }
        }
      }

      // return new relation
      return newRelation;
    } else {
      System.out
          .println("Error validating attributes on: " + joinConditions[0] + ". Attributes must have compatible types.");
      return null;
    }
  }

  private Tuple buildTuple(Tuple attributeTuple, Tuple comparisonTuple, LinkedList<Attribute> schema) {
    LinkedList<Attribute> schemaClone = new LinkedList<Attribute>();
    for (Attribute att : schema) {
      schemaClone.add(att);
    }
    Tuple tuple = new Tuple();
    LinkedList<AttributeValue> allValues = new LinkedList<AttributeValue>();
    for (AttributeValue value : attributeTuple.getValues()) {
      allValues.add(value);
    }
    for (AttributeValue value : comparisonTuple.getValues()) {
      allValues.add(value);
    }
    for (int i = 0; i < allValues.size(); i++) {
      AttributeValue attributeValue = allValues.get(i);
      String attributeValueName = attributeValue.getName();
      for (int j = 0; j < schemaClone.size(); j++) {
        String attributeName = schemaClone.get(j).getName();
        if (attributeValueName.equalsIgnoreCase(attributeName)) {
          schemaClone.remove(j);
          tuple.add(attributeValue);
        }
      }
    }
    return tuple;
  }

  private Boolean validateAttributes() {
    Boolean verfied = false;
    for (int i = 0; i < attributeNames.size(); i++) {
      // Check that the attribute is qualified
      Attribute attribute = getQualifiedAttribute(attributeNames.get(i));
      Attribute comparison = getQualifiedAttribute(comparisons.get(i));
      if (attribute.getDataType().equalsIgnoreCase(comparison.getDataType())) {
        verfied = true;
      }
    }
    return verfied;
  }

  // Returns an attribute from a qualified or non qualified string, ie
  // "COURSE.CNUM" or just "CNUM".
  private Attribute getQualifiedAttribute(String name) {
    String[] qualifiedAttribute = name.split("\\.");
    String attributeName;
    Relation relation;
    if (qualifiedAttribute.length == 2) {
      // The attribute is qualified
      attributeName = qualifiedAttribute[1];
      relation = getRelationFromName(qualifiedAttribute[0]);
    } else {
      // The attribute is not qualified
      attributeName = qualifiedAttribute[0];
      relation = findRelationfromAttribute(attributeName);
    }

    Attribute attribute = relation.getAttribute(attributeName);
    return attribute;
  }

  // Returns the relation with a given name from this.relations
  private Relation getRelationFromName(String name) {
    for (Relation rel : relations) {
      if (rel.getName().equalsIgnoreCase(name)) {
        return rel;
      }
    }
    return null;
  }

  // Finds a relation from an attribute name from this.relations
  private Relation findRelationfromAttribute(String attributeName) {
    for (Relation rel : relations) {
      Attribute att = rel.getAttribute(attributeName);
      if (att != null) {
        return rel;
      }
    }
    return null;
  }

  private void parseConditions(String input) {
    String[] splitInput = input.split("\\s+");
    for (int i = 0; i < splitInput.length; i++) {
      if (isComparisonOperator(splitInput[i])) {
        attributeNames.add(splitInput[i - 1]);
        operators.add(splitInput[i]);
        comparisons.add(splitInput[i + 1]);
      } else if (splitInput[i].equalsIgnoreCase("and")) {
        logicalOperators.add("and");
      } else if (splitInput[i].equalsIgnoreCase("or")) {
        logicalOperators.add("or");
      }
    }
  }

  /*
   * Returns true if the AttributeValues in the tuple meet the conditions in the
   * saved Where clause, returns false if they don't.
   */
  public boolean meetsConditions(Tuple attTuple, Tuple compTuple) {
    return meetsConditions(attTuple, compTuple, logicalOperators.size());
  }

  private boolean meetsConditions(Tuple attTuple, Tuple compTuple, int ops) {
    if (joinConditions[0].equals("")) {
      return true;
    }
    boolean first = evaluate(attTuple, compTuple, 0);
    boolean second = first;
    /*
     * Evaluates the expression from left to right, grouping all the "and"s together
     */
    for (int i = 0; i < ops; i++) {
      if (logicalOperators.get(i).equals("or")) {
        second = evaluate(attTuple, compTuple, i + 1);
        while (i + 1 < ops && logicalOperators.get(i + 1).equals("and")) {
          i++;
          second = second && evaluate(attTuple, compTuple, i + 1);
        }
        first = first || second;
      } else {
        while (i + 1 < ops && logicalOperators.get(i + 1).equals("and")) {
          i++;
          first = first && evaluate(attTuple, compTuple, i + 1);
        }
      }
    }
    return first;
  }

  /* Evaluates an boolean expression of the form <value> <operator> <value>. */
  private boolean evaluate(Tuple Attributetuple, Tuple ConditionTuple, int conditionNum) {
    String attributeName = getQualifiedAttribute(attributeNames.get(conditionNum)).getName();
    String conditionName = getQualifiedAttribute(comparisons.get(conditionNum)).getName();
    String tupleValue = Attributetuple.getValue(attributeName);
    String compareValue = ConditionTuple.getValue(conditionName);
    switch (operators.get(conditionNum)) {
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

  /*
   * Returns negative if attribute1 is less than attribute2, returns 0 if they are
   * the same,
   * returns positive value otherwise
   */
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

  public Boolean validateJoinConditionsExist() {
    Boolean valid = false;
    if(joinConditions[0] == null){
      valid = true;
    } else {
      try {
        validateAttributes();
        valid = true;
      } catch (Exception e) {
        System.out.println("Attribute does not exist.");
      }
    }

    return valid;
  }
}
