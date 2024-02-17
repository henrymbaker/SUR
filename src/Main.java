public class Main {
  // Read a Command File and print out the elements of each command
  public static void main(String[] args) throws Exception {
    LexicalAnalyzer surly = new LexicalAnalyzer();
    surly.run(args[0]);
  }
}
