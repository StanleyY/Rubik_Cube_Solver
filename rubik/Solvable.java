package rubik;

import java.util.Arrays;
import java.nio.file.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class Solvable {
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


  static boolean checkStickers(char[][] corners){
    List<String> invalid = Arrays.asList("RWG", "RBW", "RGY", "RYB", "OYG", "OBY", "OGW", "OWB");
    for (char[] c: corners){
      if (invalid.indexOf(new String(c)) != -1){
        return false;
      }
    }
    return true;
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

  static boolean permutationTest(int[] corners, int[] edges) {
    if (corners == null) return false;
    int total = getInversions(corners) + getInversions(edges);
    return total % 2 == 0;
  }


  static boolean cornerTest(char[][] cube) {
    int total = 0;
    int[] indexes = new int[]{0, 2, 6, 8};
    for (int face: new int[]{1, 2, 3, 5}){
      for (int index: indexes){
        if (cube[face][index] == 'R' || cube[face][index] == 'O'){
          if (index == 2 || index == 6){
            total += 2;
          }
          else total += 1;
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

  public static boolean runTests(Cube cube){
    return permutationTest(cube.getCorners(), cube.getEdges()) && cornerTest(cube.cube) && edgeTest(cube.cube);
  }

  public static void main(String[] args) throws IOException{
    if (args.length < 1) {
      System.out.println("Please Input Filename.");
      System.exit(1);
    }
    FileInputStream fs = null;

    try{
      fs = new FileInputStream(args[0]);
      byte[] buffer = new byte[fs.available()];
      fs.read(buffer);
      char[] temp = new String(buffer).replaceAll("\\s+", "").toCharArray(); //Remove whitespace
      if (basicChecks(temp)) {
        Cube cube = new Cube(temp);
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