import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class LexicalAnalyzer {
  
  private SurlyDatabase database;
  
  /* Constructor to initialize the database. */
  public LexicalAnalyzer() {
    this.database = new SurlyDatabase();
  }
  
  /*
  * Parses the given file into individual commands
  * and passes each to the appropriate parser
  */
  public void run(String fileName) {
    try {
      FileReader fr = new FileReader(fileName);
      BufferedReader br = new BufferedReader(fr);
      int c;
      StringBuilder sb = new StringBuilder();
      while ((c = br.read()) != -1) {
        char nextChar = (char) c;
        if (nextChar == ';') {
          /* The character is a semicolon - end of command */
          sb.append(nextChar);
          String finalString = sb.toString();
          sb.setLength(0);
          processCommand(finalString);
        } else {
          /* The character is still part of the command */
          sb.append(nextChar);
        }
      }
      br.close();
      fr.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  /*
  * Given a command string, first process the string and remove commented lines.
  * Then verify the command is syntactically correct and execute it.
  */
  private void processCommand(String command) {
    String tempRelationName = "";
    command = dropComment(command);
    command = command.trim();
    if (command.equals("")) {
      return;
    }
    if (command.split("\\s+")[1].equals("=")) {
      tempRelationName = command.split("\\s+")[0];
      command = command.split("\\s+",3)[2];
    }
    String commandType = command.split("\\s+",2)[0];
    switch (commandType.toUpperCase()) {
      case "RELATION": {
        handleRelation(command);
        break;
      }
      case "PROJECT": {
        handleProject(command,tempRelationName);
        break;
      }
      case "INSERT": {
        handleInsert(command);
        break;
      }
      case "PRINT":{
        handlePrint(command);
        break;
      }
      case "DESTROY": {
        handleDestroy(command);
        break;
      }
      case "DELETE": {
        handleDelete(command);
        break;
      }
      /* Need to change LexicalAnalyzer to be able to handle setting temp relations */
      case "SELECT": {
        handleSelect(command, tempRelationName);
        break;
      }
      case "JOIN": {
        handleJoin(command, tempRelationName);
        break;
      }
      default:
      if (!command.equals("")) {
        System.out.println("INVALID COMMAND: " + command);
      }
    }
  }

  private void handleJoin(String command, String name) {
    JoinParser joinparser = new JoinParser(command);
    String[] relationNames = joinparser.parseRelationNames();
    // Validate and get relations
    ArrayList<Relation> relations = new ArrayList<>();
    for (int i = 0; i < relationNames.length; i++) {
      Relation tempRelation = this.database.getRelation(relationNames[i]);
      if(tempRelation == null) {
        System.out.println("RELATION NOT FOUND: " + relationNames[i]);
      }else {
        relations.add(tempRelation);
      }
    }
    // Create a new Relation from the relations to join
    String[] conditions = joinparser.parseJoinConditions();
    Join join = new Join(relations, conditions);
    if(join.validateJoinConditionsExist()){
      Relation joined = join.getRelation(name);
      this.database.createTempRelation(joined);
    } else {
      System.out.println("Error validating join conditions, attribute may not exist.");
    }
  }

  private void handleSelect(String command, String name) {
    
    SelectParser select = new SelectParser(command);
    if (select.getIsValidSyntax()) {
      String relationName = select.parseRelationName();
      Relation selectedRelation = database.getRelation(relationName);
      if (selectedRelation != null) {
        Relation tempRelation = select.selectWhere(selectedRelation, name);
        if (tempRelation != null) {
          database.createTempRelation(tempRelation);
        } 
      } else {
        System.out.print("ERROR SELECTING FROM RELATION \"" + relationName + "\": ");
        System.out.println("RELATION NOT FOUND.");
      }
    } else {
      System.out.println("INVALID SYNTAX: " + command);
    }
  }
  


  private void handleProject(String command, String name) {
    ProjectParser pp = new ProjectParser(command);
    String relationName = pp.parseRelationName(); 
    Relation relationToProject = database.getRelation(relationName);
    if (relationToProject != null) {
      Relation tempRelation = pp.project(this.database.getRelation(relationName), name);
      if (tempRelation != null) {
        this.database.createTempRelation(tempRelation);
      }
    } else {
      System.out.print("ERROR PROJECTING FROM RELATION \"" + relationName + "\": ");
      System.out.println("RELATION NOT FOUND.");
    }
  }
  

  /* Processes a DELETE command by passing it to the delete parser. */
  private void handleDelete(String command) {
    DeleteParser delete = new DeleteParser(command);
    String relationName = delete.parseRelationName();
    Relation relationToDelete = database.getRelation(relationName);
    if (delete.getIsValidSyntax()) {
      if (relationToDelete != null) {
        if (!database.isTempRelation(relationName)) {
          if (!command.matches("(?i).*" + " where " + ".*")) { //  delete the whole relation
            relationToDelete.delete();
          } else {
            delete.deleteWhere(relationToDelete, relationName);
          }
        } else {
          System.out.print("ERROR DELETING FROM RELATION \"" + relationName + "\": ");
          System.out.println("CANNOT DELETE FROM TEMPORARY RELATION.");
        }
      } else {
        System.out.print("ERROR DELETING FROM RELATION \"" + relationName + "\": ");
        System.out.println("RELATION NOT FOUND.");
      }
  } else {
    System.out.println("INVALID SYNTAX: " + command);
  }
  }


  /* Processes a RELATION command by passing it to the relation parser. */
  private void handleRelation(String command) {
    RelationParser relationParser = new RelationParser(command);
    if (relationParser.getIsValidSyntax()) {
      Relation newRelation = relationParser.parseRelation();
      if (newRelation != null) {
        this.database.createRelation(newRelation);
      }
    } else {
      System.out.println("INVALID SYNTAX: " + command);
    }
  }
  
  /* Processes an INSERT command by passing it to the insert parser. */
  private void handleInsert(String command) {
    InsertParser insert = new InsertParser(command);
    if (insert.getIsValidSyntax()) {
      String relationName = insert.parseRelationName();
      if (relationName.equalsIgnoreCase("CATALOG")) {
        System.out.println("ERROR INSERTING TO RELATION: CANNOT INSERT TO "
        + "CATALOG.");
        return;
      }
      Tuple tuple = insert.parseTuple();
      if (tuple == null) {
        return;
      }
      Relation currentRelation = this.database.getRelation(relationName);
      if (currentRelation != null) {
        if (!database.isTempRelation(relationName)) {
          currentRelation.insert(tuple);
        } else {
          System.out.print("ERROR INSERTING TO RELATION \"" + relationName + "\": ");
          System.out.println("CANNOT INSERT TO TEMPORARY RELATION.");
        }
      } else {
        System.out.println("ERROR INSERTING TO RELATION \"" + relationName + "\": "
        + "RELATION NOT FOUND.");
      }
    } else {
      System.out.println("INVALID SYNTAX: " + command);
    }
  }
  
  /* Processes a PRINT command by passing it to the print parser. */
  private void handlePrint(String command) {
    PrintParser print = new PrintParser(command);
    if (print.getIsValidSyntax()) {
      String[] names = print.parseRelationNames();
      for (String relationName: names) {
        Relation currentRelation = this.database.getRelation(relationName);
        if (currentRelation != null) {
          currentRelation.print();
          System.out.println();
        } else {
          System.out.print("ERROR PRINTING RELATION \"" + relationName + "\": ");
          System.out.println("RELATION NOT FOUND.");
        }
      }
    } else {
      System.out.println("INVALID SYNTAX: " + command);
    }
  }
  
  /* Processes a DESTROY command by passing it to the destroy parser. */
  private void handleDestroy(String command) {
    DestroyParser destroy = new DestroyParser(command);
    String relationName = destroy.parseRelationName();
    if (destroy.getIsValidSyntax()) {
      if (this.database.getRelation(relationName) != null) {
        if (!database.isTempRelation(relationName)) {
          this.database.destroyRelation(relationName);
        } else {
          System.out.print("ERROR DESTROYING RELATION \"" + relationName + "\": ");
          System.out.println("CANNOT DESTROY TEMPORARY RELATION.");
        }
      } else {
        System.out.print("ERROR DESTROYING RELATION \"" + relationName + "\": ");
        System.out.println("RELATION NOT FOUND.");
      }
    } else {
      System.out.println("INVALID SYNTAX: " + command);
    }
  }
  
  
  
  /* Drops commented lines from a string. */
  private String dropComment(String command) {
    return command.replaceAll("(?m)^#.*$", "");
  }
}
