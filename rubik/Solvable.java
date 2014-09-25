package rubik;

import java.util.Arrays;
import java.nio.file.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

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

  static boolean basicChecks(char[] input){
    String colors = "RGBYOW";
    HashMap<String, Integer> occurances = new HashMap<String, Integer>();
    int i = 0;
    for (char c: colors.toCharArray()){
      occurances.put(""+ c, 0);
    }

    if (input.length != 54) return false;

    String s = "";
    for (char c: input){
      s = "" + c;
      if (occurances.get(s) != null) {
        occurances.put(s, occurances.get(s) + 1);
      }
      else {
        return false;
      }
    }

    Iterator it = occurances.entrySet().iterator();
    while (it.hasNext()) {
        Map.Entry pairs = (Map.Entry)it.next();
        if (pairs.getValue() != 9) return false;
        it.remove();
    }

    String faces = new String(new char[]{input[4],input[19],input[22],input[25],input[40],input[49]});
    if (!faces.equals("RGYBOW")) return false;

    System.out.println("VALID");
    return true;
  }


  static char[][] generateCube(char[] input){
    char[][] output = new char[6][9];
    int index = 0;
    int j;
    int x = 0;
    for (int i = 0; i < 6; i++){
      j = 0;
      if (i < 1 || i > 3) {
        for (; j < 9; j++) {
          output[i][j] = input[index];
          index++;
        }
      }
      else{
        for (j = x; j < x+3; j++){
          output[i][j] = input[index];
          index++;
        }
        if(i > 2 && index < 36){
          i = 0;
          x += 3;
        }
      }
    }
    for (char[] a : output){
      System.out.println(new String(a));
    }
    return output;
  }


  static void printCube(char[][] input) {
  // Prints the cube in a more human readable format.
  System.out.printf("\n   ");
  int i = 0;
  while (i < 9){
    if(i % 3 == 0) System.out.printf("\n   ");
    System.out.printf("%c", input[0][i]);
    i++;
  }

  i = 1;
  int j = 0;
  while (j < 9){
    System.out.printf("\n");
    while (i < 4){
      System.out.printf("%c", input[i][j]);
      j++;
      if (j % 3 == 0) {
        j = j - 3;
        i++;
      }
    }
    j=j+3;
    i = 1;
  }

  for (int x: new int[]{4, 5}){
    i = 0;
    for (char c : input[x]){
      if(i % 3 == 0) System.out.printf("\n   ");
      System.out.printf("%c", c);
      i++;
    }
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


  static boolean permutationTest() {
    return true;
  }


  static boolean cornerTest(char[] cube) {
                              // Y        B      G       W
    int[] initial = new int[] {12, 30, 15, 33, 11, 29, 47, 53};
    int[] inverted = new int[]{14, 32, 17, 35, 9, 27, 45, 51};
    int total = 0;
    char val = '0';

    for (int i: initial){
      val = cube[i];
      if (val == 'R'){
        total += 1;
      }
      else if (val == 'O'){
        total += 2;
      }
    }
    for (int i: inverted){
      val = cube[i];
      if (val == 'R'){
        total += 2;
      }
      else if (val == 'O'){
        total += 1;
      }
    }

    if (total % 3 == 0){ return true;}
    else {return false;}
  }


  static boolean edgeTest() {
    return true;
  }


  public static void main(String[] args) throws IOException{
    if (args.length < 1) {
      System.out.println("Please Input Filename.");
      System.exit(1);
    }
    FileInputStream fs = null;
    char[][] cube = null;

    try{
      fs = new FileInputStream(args[0]);
      byte[] buffer = new byte[fs.available()];
      fs.read(buffer);
      char[] temp = new String(buffer).replaceAll("\\s+", "").toCharArray(); //Remove whitespace
      if (basicChecks(temp)) {
        System.out.println(temp);
        cube = generateCube(temp);
        printCube(cube);
      }
      else{
        System.out.println(false);
        System.exit(1);
      }
    }catch(Exception e){
      e.printStackTrace();
    }finally{
      if(fs != null) fs.close();
    }
  }


}