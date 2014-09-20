package rubik;

import java.util.Arrays; //REMOVE THIS SHIT
import java.nio.file.*;
import java.io.IOException;

class Solvable {

  static String readFile(String file_name) {
    String cwd = System.getProperty("user.dir");
    try {
      byte[] content = Files.readAllBytes(Paths.get(cwd, file_name));
      return new String(content).replaceAll("\\s+", ""); //Return String with whitespace removed.
    }
    catch (IOException e){
      System.out.println(e.toString());
    }
    return null;
  }

  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println("Please Input Filename.");
      System.exit(1);
    }
    String file_name = args[0];
    String input = readFile(file_name);
    System.out.println(input);
  }
}