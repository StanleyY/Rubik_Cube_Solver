package rubik;

import java.util.Arrays;
import java.nio.file.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class Cube {
/*
R = 0
G = 1
Y = 2
B = 3
O = 4
W = 5

*/

  public static final String GOAL_STATE = "RRRRRRRRRGGGYYYBBBGGGYYYBBBGGGYYYBBBOOOOOOOOOWWWWWWWWW";

  public char[][] cube;
  public int[] corners;
  public int[] edges;

  public Cube(){
    this.cube = new char[6][9];
    this.corners = new int[8];
    this.edges = new int[12];
  }

  public Cube(char[] input){
    this.cube = this.generateCube(input);
    this.corners = this.getCorners(this.cube);
    this.edges = this.getEdges(this.cube);
  }

  public Cube(String s){
    char[] input = s.toCharArray();
    this.cube = this.generateCube(input);
    this.corners = this.getCorners(this.cube);
    this.edges = this.getEdges(this.cube);
  }

  public char[][] generateCube(char[] input){
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


  public void printCube() {
  // Prints the cube in a more human readable format.
  int i = 0;
  while (i < 9){
    if(i % 3 == 0) System.out.printf("\n   ");
    System.out.printf("%c", this.cube[0][i]);
    i++;
  }

  i = 1;
  int j = 0;
  while (j < 9){
    System.out.printf("\n");
    while (i < 4){
      System.out.printf("%c", this.cube[i][j]);
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
    for (char c : this.cube[x]){
      if(i % 3 == 0) System.out.printf("\n   ");
      System.out.printf("%c", c);
      i++;
    }
  }
  System.out.printf("\n\n");
}


  private int[] getCorners(char[][] cube) {
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

  // Relic of prerefactoring. Can't be easily removed unfortunately. Maybe I'll revisit it later.
  private boolean checkStickers(char[][] corners){
    List<String> invalid = Arrays.asList("RWG", "RBW", "RGY", "RYB", "OYG", "OBY", "OGW", "OWB");
    for (char[] c: corners){
      if (invalid.indexOf(new String(c)) != -1){
        return false;
      }
    }
    return true;
  }

  /* The cube's orientation is originally a base 3 number.
  */
  public int getCornerOrientation(){
    int[] indexes = new int[]{0, 2, 6, 8};
    String total = "";

    // TODO: move this to its own function
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

    int i = 0;
    for (char[] c : corners){
      i = 0;
      for (char x : c){
        if( x == 'R' || x == 'O') total = total + i;
        i++;
      }
    }
    return Integer.valueOf(total, 3);
  }


  private int[] getEdges(char[][] cube){
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


  public int sideFace(int face, int index){
    if (index == 3) return 1;
    if (index == 5) return 3;
    if (index == 1) return (face == 2) ? 0 : 4;
    else return (face == 2) ? 4 : 0;
  }


  public int getEncodedCorners(){
    return this.factoradic() * 2187 + getCornerOrientation();
  }


  /* Generates the fatoradic value of this Cube's permutation of corners.
    Currently hardcoded for 8 digit factorials so it won't need to recompute the factorials.
  */
  private int factoradic(){
    //                            7! ,  6!,  5!, 4!...
    int[] factorials = new int[]{5040, 720, 120, 24, 6, 2, 1, 1};
    int value = 0;
    for(int i = 0; i < 8; i++){
      value += this.corners[i] * factorials[i];
    }
    return value;
  }

}