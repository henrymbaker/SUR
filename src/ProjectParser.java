import java.util.ArrayList;
import java.util.HashMap;

public class ProjectParser {
    private String input;
    private ArrayList<String> attributes;

    public ProjectParser(String input) {
        this.input = input;
        attributes = new ArrayList<>();
        parseAttributes();
    }

    public Relation project(Relation relation, String name) {
        Relation newRelation = new Relation(name);
        /* Setting schema */
        for (String attrName : attributes) {
            Attribute currentAttribute = relation.getAttribute(attrName);
            if (currentAttribute == null) { /* Check if attribute exists */
                System.out.println("ERROR PROJECTING FROM RELATION: ATTRIBUTE \"" + attrName + "\" DOES NOT EXIST.");
                return null;
            }
            newRelation.addToSchema(currentAttribute);
        }
        int numTuples = relation.size();
        HashMap<String,Integer> existingTuples = new HashMap<>();
        /* Creating tuples. */
        for (int i = 0; i < numTuples; i++) {
            Tuple tuple = relation.getTuple(i);
            Tuple newTuple = new Tuple();
            boolean notDuplicate = false;
            for (int j = 0; j < attributes.size(); j++) {
                String attr = attributes.get(j);
                String currVal = tuple.getValue(attr);
                if (!existingTuples.containsKey(currVal) || existingTuples.get(currVal) != j) {
                    notDuplicate = true;
                    existingTuples.put(currVal,j);
                } 
                AttributeValue av = new AttributeValue(attr,currVal);
                newTuple.add(av);
            }
            if (notDuplicate) {
                newRelation.insert(newTuple);
            }
            
        }
        return newRelation;
    }

    public String parseRelationName() {
        String[] splitInput = input.split("\\s+|;");
        return splitInput[splitInput.length-1];
    }

    private void parseAttributes() {
        String[] isolated = isolateAttributes().split(",\\s+");
        for (String s : isolated) {
            attributes.add(s);
        }
    }

    private String isolateAttributes() {
        String isolated = "";
        String removedProject = input.split("\\s+",2)[1].trim();
        int fromIndex = removedProject.indexOf("FROM");
        isolated = removedProject.substring(0,fromIndex);
        //maybe need to account for if there is no "FROM", but there should always be a FROM
        return isolated.trim();
    }
}


