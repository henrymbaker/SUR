/* Class for attribute, used to define the schema of a relation. */

public class Attribute {
  private String name;	/* name of the attribute */
  private String dataType;	/* data type of the attribute */
  private int length;		/* length of the attribute */
  
  /* Constructor to initialize fields. */
  public Attribute(String name, String dataType, int length) {
    this.name = name;
    this.dataType = dataType;
    this.length = length;
  }
  
  /* Accessor method for name. */
  public String getName() {
    return this.name;
  }
  
  /* Accessor method for dataType. */
  public String getDataType() {
    return this.dataType;
  }
  
  /* Accessor method for length. */
  public int getLength() {
    return this.length;
  }
  
  /* Sets name to the given String. */
  public void setName(String name) {
    this.name = name;
  }
  
  /* Sets dataType to the given String. */
  public void setDataType(String dataType) {
    this.dataType = dataType;
  }
  
  /* Sets length to the given value. */
  public void setLength(int length) {
    this.length = length;
  }
}
