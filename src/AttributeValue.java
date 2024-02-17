/* Class to hold the individual values of an attribute on a tuple. */

public class AttributeValue {
  private String name; 	/* name of the attribute */
  private String value;   /* value of the attribute */
  
  /* Constructor to initialize fields. */
  public AttributeValue(String name, String value) {
    this.name = name;
    this.value = value;
  }
  
  /* Accessor method for name. */
  public String getName() {
    return this.name;
  }
  
  /* Accessor method for value. */
  public String getValue() {
    return this.value;
  }
  
  /* Sets name to the given String. */
  public void setName(String name) {
    this.name = name;
  }
  
  /* Sets value to the given String. */
  public void setValue(String value) {
    this.value = value;
  }
}
