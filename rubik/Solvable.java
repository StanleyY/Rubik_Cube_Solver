package rubik;

import java.util.Arrays; //REMOVE THIS SHIT
import java.nio.file.*;
import java.io.IOException;

class Solvable {

// Corners char values
/*
RGY: 242
RYB: 237
RBW: 235
RWG: 240
OGY: 239
OYB: 234
OBW: 232
OGW: 237
*/

  static void PrettyPrint(char[] input) {
  // Prints the cube in a more human readable format.
    int i = 0;
    while (i < 9){
      System.out.printf("\t ");
      System.out.printf(Arrays.toString(Arrays.copyOfRange(input,i, i+3)));
      System.out.printf("\n");
      i = i+3;
    }

    while (i < 36){
      System.out.printf(Arrays.toString(Arrays.copyOfRange(input,i, i+9)));
      System.out.printf("\n");
      i = i+9;
    }

    while (i < 54){
      System.out.printf("\t ");
      System.out.printf(Arrays.toString(Arrays.copyOfRange(input,i, i+3)));
      System.out.printf("\n");
      i = i+3;
    }
  }

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

  static boolean PermutationTest() {
    return true;
  }

  static boolean CornerTest() {
    return true;
  }

  static boolean EdgeTest() {
    return true;
  }
  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println("Please Input Filename.");
      System.exit(1);
    }
    String file_name = args[0];
    String input = readFile(file_name);
    char[] charArray = input.toCharArray();
    PrettyPrint(charArray);
    System.out.println(input);
  }
}