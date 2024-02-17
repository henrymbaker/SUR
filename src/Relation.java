import java.util.LinkedList;

public class Relation {
  private String name; /* name of the relation */
  private LinkedList<Attribute> schema; /* Schema of the relation */
  private LinkedList<Tuple> tuples; /* Tuples stored on the relation */
  private final String CATALOG_NAME = "CATALOG";
  
  public Relation(String name) {
    this.name = name;
    this.schema = new LinkedList<>();
    this.tuples = new LinkedList<>();
  }
  
  
  public Tuple getTuple(int index) {
    return tuples.get(index);
  }

  public Attribute getSchema(int index) {
    return schema.get(index);
  }

  public LinkedList<Attribute> getSchema() {
    return this.schema;
  }

  public void setName(String name) {
    this.name = name;
  }

  /* Formats and prints the relation's name, schema, and tuples */
  public void print() {
    
    /* Calculates how much space each element needs in the table. */
    int schemaSize = this.schema.size();
    int[] attributeLengths = new int[schemaSize];
    int totalLength = 0;
    for (int i = 0; i < schemaSize; i++) {
      Attribute currAttr = this.schema.get(i);
      /* Gets the size of the attribute's max length or the
      length of the attribute name, whichever is largest*/
      int attributeLength = Math.max(currAttr.getLength(), currAttr.getName().length());
      attributeLengths[i] = attributeLength;
      totalLength += attributeLength + 3; /* Gives buffer space for " " and "|". */
    }
    totalLength++; /* One more space for the rightmost "|". */
    System.out.println("*".repeat(totalLength));
    printRelationName(totalLength);
    System.out.println("-".repeat(totalLength));
    printSchema(totalLength,attributeLengths);
    System.out.println("-".repeat(totalLength));
    printTuples(totalLength,attributeLengths);
    System.out.println("*".repeat(totalLength));
  }
  
  /* Formats and prints the name of the relation. */
  private void printRelationName(int length) {
    System.out.print("| ");
    System.out.printf("%-" + (length - 3) +"S", this.name);
    System.out.println('|');
  }
  
  /* Formats and prints the schema of the relation. */
  private void printSchema(int length, int[] lengths) {
    for (int i = 0; i < schema.size(); i++) {
      System.out.print("| ");
      System.out.printf("%-" + (lengths[i] + 1) + "S", schema.get(i).getName());
    }
    System.out.println('|');
  }
  
  /* Formats and prints the tuples of the relation. */
  private void printTuples(int length, int[] lengths) {
    for (Tuple t : this.tuples) {
      for (int i = 0; i < this.schema.size(); i++) {
        System.out.print("| ");
        System.out.printf("%-" + (lengths[i] + 1) + "S", t.getValue(i));
      }
      System.out.println('|');
    }
  }
  
  /* Accessor method for name. */
  public String getName() {
    return this.name;
  }
  
  /* Adds the specified tuple to the relation. */
  public void insert(Tuple tuple) {
    if (tuple.length() != schema.size()) {
      System.out.println("ERROR INSERTING TO RELATION \"" + this.name + "\":"
                         + " (tuple length does not match schema length).");
      return;
    }
    
    for (int i = 0; i < this.schema.size(); i++) {
      Attribute currentAttribute = this.schema.get(i);
      
      /* Sets name of attribute value to corresponding attribute name. */
      tuple.setName(i,currentAttribute.getName());
      
      /* Check if the datatypes of the current attribute
      and the corresponding attribute value match. */
      String datatype = currentAttribute.getDataType();
      if (datatype.equalsIgnoreCase("NUM") && !isNumeric(tuple.getValue(i))) {
        System.out.println("ERROR INSERTING TO RELATION \"" + this.name + "\": "
                           + "INVALID DATA TYPE FOR ATTRIBUTE \"" + currentAttribute.getName()
                           + "\" (given: \"" + tuple.getValue(i) + "\" expected: NUM).");
        return;
      }
      
      /* Trims down attribute value to the max length of the attribute. */
      int maxLength = currentAttribute.getLength();
      if (tuple.getValue(i).length() > maxLength) {
        tuple.trimValue(i, maxLength);
      }
    }
    this.tuples.add(tuple);
  }
  
  /* Returns true if str is numeric, returns false otherwise. */
  public boolean isNumeric(String str) {
    if (str == null) {
      return false;
    }
    try {
      Integer d = Integer.parseInt(str);
    } catch (NumberFormatException nfe) {
      return false;
    }
    return true;
  }
  
  /* Deletes a tuple from the linked list by the name of its first attribute. */
  public boolean deleteTuple(String name) {
    Boolean isDeleted = false;
    for (int i = 0; i < this.tuples.size(); i++) {
      Tuple currentTuple = this.tuples.get(i);
      if (currentTuple.getValue(0).equalsIgnoreCase(name)) {
        this.tuples.remove(i);
        isDeleted = true;
      }
    }
    return isDeleted;
  }
  
public void delete_ith_Tuple(int i) {
  this.tuples.remove(i);
}

  /* Adds specified attribute to the schema. */
  public void addToSchema(Attribute attribute) {
    this.schema.add(attribute);
  }

  /* Return attribute with the given name */
  public Attribute getAttribute(String name) {
    for (Attribute attr : schema) {
      if (attr.getName().equals(name)) {
        return attr;
      }
    }
    return null;
  }
  
  /* Remove all tuples from the relation */
  public void delete() {
    if (this.name.equalsIgnoreCase(CATALOG_NAME)) {
      System.out.println("ERROR DELETING FROM RELATION: CANNOT DELETE FROM "
                         + "CATALOG.");
    } else {
      this.tuples.clear();
    }
  }
  
  /* Returns number of tuples in relation. */
  public int size() {
    return tuples.size();
  }

  /* Returns the number of attributes in the schema. */
  public Integer schemaSize() {
    return this.schema.size();
  }
}
