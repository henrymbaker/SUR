/* Tuple class for storing attribute values. */
import java.util.LinkedList;

public class Tuple {
  private LinkedList<AttributeValue> values;  /* Values of each attribute in the tuple */
  
  /* Constructor to initialize the linked list */
  public Tuple() {
    this.values = new LinkedList<>();
  }

  // Returns an attributeValue at index "i" of  values
  public AttributeValue getAttributeValue(int i) {
    return this.values.get(i);
  }
  
  /* Returns the value of the specified attribute, or returns null
  if none of the attributes have the specified attribute name. */
  public String getValue(String attributeName) {
    
    /* If the name of the AttributeValue at i equals the given name,
    returns the value of the AttributeValue. */
    for (int i = 0; i < this.values.size(); i++) {
      AttributeValue currentAttributeValue = this.values.get(i);
      if (currentAttributeValue.getName().equalsIgnoreCase(attributeName)) {
        return currentAttributeValue.getValue();
      }
    }
    return null;
  }
  
  /* Returns the value of the attribute at index i in the tuple. */
  public String getValue(int i) {
    return this.values.get(i).getValue();
  }
  
  /* Returns the name of the attribute at index i */
  public String getName(int i) {
    return this.values.get(i).getName();
  }

  /* Sets the name of the AttributeValue at index i to the given String. */
  public void setName(int i, String name) {
    this.values.get(i).setName(name);
  }
  
  /* Trims the value of AttributeValue at i down to the given max length. */
  public void trimValue(int i, int maxLength) {
    AttributeValue currentAttributeValue = this.values.get(i);
    currentAttributeValue.setValue(
    currentAttributeValue.getValue().substring(0,maxLength));
  }
  
  /* Returns the number of AttributeValues in the tuple. */
  public int length() {
    return values.size();
  }
  
  /* Adds given AttributeValue to the list of values. */
  public void add(AttributeValue attribute) {
    this.values.add(attribute);
  }

  public LinkedList<AttributeValue> getValues() {
    return values;
  }
}
