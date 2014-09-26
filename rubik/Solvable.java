package rubik;

import java.util.Arrays;
import java.nio.file.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

class Solvable {
/*
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
        if (pairs.getValue() != 9) {
          return false;
        }
        it.remove();
    }

    String faces = new String(new char[]{input[4],input[19],input[22],input[25],input[40],input[49]});
    if (!faces.equals("RGYBOW")) return false;

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
    return output;
  }


  static void printCube(char[][] input) {
  // Prints the cube in a more human readable format.
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
  System.out.printf("\n\n");
}


  static boolean checkStickers(char[][] corners){
    List<String> invalid = Arrays.asList("RWG", "RBW", "RGY", "RYB", "OYG", "OBY", "OGW", "OWB");
    for (char[] c: corners){
      if (invalid.indexOf(new String(c)) != -1){
        return false;
      }
    }
    return true;
  }

  static int[] getCorners(char[][] cube) {
    List<String> keys = Arrays.asList("GRW", "BRW", "GRY", "BRY", "GOY", "BOY", "GOW", "BOW");
    int[] output = new int[8];

    char[][] corners = new char[][]{
      {cube[0][0], cube[1][0], cube[5][6]},
      {cube[0][2], cube[5][8], cube[3][2]},
      {cube[0][6], cube[2][0], cube[1][2]},
      {cube[0][8], cube[3][0], cube[2][2]},

      {cube[4][0], cube[1][8], cube[2][6]},
      {cube[4][2], cube[2][8], cube[3][6]},
      {cube[4][6], cube[5][0], cube[1][6]},
      {cube[4][8], cube[3][8], cube[5][2]}
    };

    if (!checkStickers(corners)) return null;

    String temp = "";
    for (int i = 0; i < 8; i++){
      Arrays.sort(corners[i]);
      temp = new String(corners[i]);
      output[i] = keys.indexOf(temp);
    }

    return output;
  }


  static int[] getEdges(char[][] cube){
    List<String> keys = Arrays.asList("RW", "GR", "BR", "RY", "GW", "GY", "BY", "BW", "OY", "GO", "BO", "OW");
    int[] output = new int[12];

    char[][] edges = new char[][]{
      {cube[0][1], cube[5][7]}, {cube[0][3], cube[1][1]}, {cube[0][5], cube[3][1]}, {cube[0][7], cube[2][1]},
      {cube[1][3], cube[5][3]}, {cube[1][5], cube[2][3]}, {cube[2][5], cube[3][3]}, {cube[3][5], cube[5][5]},
      {cube[4][1], cube[2][7]}, {cube[4][3], cube[1][7]}, {cube[4][5], cube[3][7]}, {cube[4][7], cube[5][1]}
    };

    String temp = "";
    for (int i = 0; i < 12; i++){
      Arrays.sort(edges[i]);
      temp = new String(edges[i]);
      output[i] = keys.indexOf(temp);
    }

    return output;
  }

  static int getInversions(int[] input){
    int inversions = 0;
    int val = -1;
    int size = input.length;
    int j = 0;

    for (int i = 0; i < size; i++){
      val = input[i];
      for (j = i; j < size; j++){
        if (val > input[j]) {
          inversions++;
        }
      }
    }
    return inversions;
  }

  static boolean permutationTest(char[][] cube) {
    int[] corners = getCorners(cube);
    if (corners == null) return false;
    int[] edges = getEdges(cube);
    int total = getInversions(corners) + getInversions(edges);
    return total % 2 == 0;
  }


  static boolean cornerTest(char[][] cube) {
    int total = 0;
    char val = '0';
    int[] indexes = new int[]{0, 2, 6, 8};
    for (int face: new int[]{1, 2, 3, 5}){
      for (int index: indexes){
        if (cube[face][index] == 'R' || cube[face][index] == 'O'){
          if (index == 2 || index == 6){
            total += 1;
          }
          else total += 2;
        }
      }
    }

    return total % 3 == 0;
  }


  static boolean edgeTest(char [][] cube) {
    int total = 0;
    int pos = 1;
    char face = '0';
    char side = '0';
    for (int i: new int[]{0, 4}){
      for(int j: new int[]{3, 5}){
        face = cube[i][j];
        if(!(face == 'Y' || face == 'W')){
          side = cube[j - 2][pos];
          if(side == 'Y' || side == 'W'){
            total += 1;
          }
        }
      }
      pos = 7; // Bottom index on Green/Blue.
    }

    for (int i: new int[]{2, 5}){
      for(int j: new int[]{1, 3, 5, 7}){
        face = cube[i][j];
        if(!(face == 'Y' || face == 'W')){
          if (face == 'B' || face == 'G') {
            total += 1;
          }
          else { // Red/Yellow face, confirm if it is a Red/Yellow + Blue/Green.
            if (i == 5 && (j == 3 || j == 5)){
              pos = j;
            }
            else{
              pos = 8 - j;
            }
            side = cube[sideFace(i, j)][pos];
            if(side == 'Y' || side == 'W'){
              total += 1;
            }
          }
        }
      }
    }

    return total % 2 == 0;
  }

  static int sideFace(int face, int index){
    if (index == 3) return 1;
    if (index == 5) return 3;
    if (index == 1) return (face == 2) ? 0 : 4;
    else return (face == 2) ? 4 : 0;
  }

  public static boolean runTests(char[][] cube){
    return permutationTest(cube) && cornerTest(cube) && edgeTest(cube);
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
        cube = generateCube(temp);
        System.out.println(runTests(cube));
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