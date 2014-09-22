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

R = 0
G = 1
Y = 2
B = 3
O = 4
W = 5

*/

  static void prettyPrint(char[] input) {
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


  static char[][] getCorners(char[] input) {
    char[][] output = new char[8][3];
    int i = 0;                                              // Goal State Values
    output[0] = new char[]{input[6], input[12], input[11]}; // RYG
    output[1] = new char[]{input[8], input[15], input[14]}; // RBY
    output[2] = new char[]{input[2], input[53], input[17]}; // RWB
    output[3] = new char[]{input[0], input[9], input[51]};  // RGW
    output[4] = new char[]{input[36], input[30], input[29]};// OYG
    output[5] = new char[]{input[38], input[33], input[32]};// OBY
    output[6] = new char[]{input[44], input[47], input[35]};// OWB
    output[7] = new char[]{input[42], input[27], input[45]};// OGW
    return output;
  }


  static String readFile(String file_name) {
    String cwd = System.getProperty("user.dir");
    try {
      byte[] content = Files.readAllBytes(Paths.get(cwd, file_name));
      return new String(content).replaceAll("\\s+", ""); // Removed whitespace.
    }
    catch (IOException e){
      System.out.println(e.toString());
    }
    return null;
  }

  static boolean permutationTest() {
    return true;
  }


  static boolean cornerTest(char[] cube) {
    int[] initial = new int[]{12, 30, 15, 33, 11, 29, 47, 53};
    int[] inverted = new int[]{14, 32, 17, 35, 9, 27, 45, 51};
    int total = 0;
    char val = '0';

    for (int i: initial){
      val = cube[i];
      if (val == 'R'){
        total += 1;
        System.out.printf("Cube Index: %d, Color: R\n", i);
        System.out.printf("Current Total: %d\n", total);
      }
      else if (val == 'O'){
        total += 2;
        System.out.printf("Cube Index: %d, Color: O\n", i);
        System.out.printf("Current Total: %d\n", total);
      }
    }
    System.out.println("Starting Inverted");
    for (int i: inverted){
      val = cube[i];
      if (val == 'R'){
        total += 2;
        System.out.printf("Cube Index: %d, Color: R\n", i);
        System.out.printf("Current Total: %d\n", total);
      }
      else if (val == 'O'){
        total += 1;
        System.out.printf("Cube Index: %d, Color: O\n", i);
        System.out.printf("Current Total: %d\n", total);
      }
    }

    System.out.printf("Total is %d.\n", total);
    if (total % 3 == 0){ return true;}
    else {return false;}
  }


  static boolean edgeTest() {
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
    prettyPrint(charArray);
    System.out.println(input);
    char[][] corners = getCorners(charArray);
    for (int i = 0; i < corners.length; i++){
      System.out.println(corners[i]);
    }
    System.out.println(cornerTest(charArray));
  }

}