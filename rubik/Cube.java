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
  public int level;

  public Cube(){
    this.cube = new char[6][9];
  }


  public Cube(char[] input){
    this.cube = this.generateCube(input);
  }


  public Cube(char[] input, int level){
    this.cube = this.generateCube(input);
    this.level = level;
  }


  public Cube(String s){
    char[] input = s.toCharArray();
    this.cube = this.generateCube(input);
  }


  public Cube(char[][] input){
    this.cube = this.generateCube(input);
  }


  public Cube(char[][] input, int level){
    this.cube = this.generateCube(input);
    this.level = level;
  }


  public void setLevel(int level){
    this.level = level;
  }


  private char[][] generateCube(char[] input){
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

  // Deep cloning the input.
  private char[][] generateCube(char[][] input){
    char[][] temp = new char[6][9];
    for (int i = 0; i < 6; i++){
      for(int j = 0; j < 9; j++){
        temp[i][j] = input[i][j];
      }
    }
    return temp;
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

  // Rotates a face clockwise a given number of turns
  public Cube rotate(int face, int turns){
    if (turns < 1 || turns > 3) {throw new IllegalArgumentException("Turns need to be between 1 and 3.");}
    if (face < 0 || face > 5) {throw new IllegalArgumentException("Invalid face, must be between 0 and 5.");}

    Cube output = new Cube(this.cube);
    char temp;
    while(turns > 0){
      temp = output.cube[face][0];
      output.cube[face][0] = output.cube[face][6];
      output.cube[face][6] = output.cube[face][8];
      output.cube[face][8] = output.cube[face][2];
      output.cube[face][2] = temp;
      temp = output.cube[face][1];
      output.cube[face][1] = output.cube[face][3];
      output.cube[face][3] = output.cube[face][7];
      output.cube[face][7] = output.cube[face][5];
      output.cube[face][5] = temp;
      output.rotateCubies(face);
      turns = turns - 1;
    }
    return output;
  }


  private void rotateCubies(int face){
    int[][] rotation_keys = new int[][]{new int[]{50,10,20,30}, new int[]{1,51,41,21},
                                        new int[]{2,12,42,32}, new int[]{3, 23,43,53},
                                        new int[]{24,14,54,34}, new int[]{45,15,5,35}};
    int[] index_keys = rotation_keys[face];
    Map<Integer, int[]> rotation_indexes = initRotationIndex();
    int i = 0;

    // Storing the first values for later displacing.
    char[] temp = new char[3];
    for(int x: rotation_indexes.get(index_keys[0])){
      temp[i] = this.cube[index_keys[0] / 10][x];
      i++;
    }

    int next_face = 0;
    int[] next_indexes = new int[3];
    for(i = 0; i < 3; i++){
      int current_face = index_keys[i];
      int[] current_indexes = rotation_indexes.get(current_face);
      next_face = index_keys[i + 1];
      next_indexes = rotation_indexes.get(next_face);

      for(int j = 0; j < 3; j++){
        this.cube[current_face / 10][current_indexes[j]] = this.cube[next_face / 10][next_indexes[j]];
      }
    }

    for(int j = 0; j < 3; j++){
      this.cube[next_face / 10][next_indexes[j]] = temp[j];
    }
  }


  private Map<Integer, int[]> initRotationIndex(){
    Map<Integer, int[]> indexes = new HashMap<Integer, int[]>();
    indexes.put(50, new int[]{6,7,8});
    indexes.put(10, new int[]{2,1,0});
    indexes.put(20, new int[]{2,1,0});
    indexes.put(30, new int[]{2,1,0});

    indexes.put(1, new int[]{0,3,6});
    indexes.put(21, new int[]{0,3,6});
    indexes.put(41, new int[]{0,3,6});
    indexes.put(51, new int[]{0,3,6});

    indexes.put(2, new int[]{6,7,8});
    indexes.put(12, new int[]{8,5,2});
    indexes.put(32, new int[]{0,3,6});
    indexes.put(42, new int[]{2,1,0});

    indexes.put(3, new int[]{8,5,2});
    indexes.put(23, new int[]{8,5,2});
    indexes.put(43, new int[]{8,5,2});
    indexes.put(53, new int[]{8,5,2});

    indexes.put(14, new int[]{6,7,8});
    indexes.put(24, new int[]{6,7,8});
    indexes.put(34, new int[]{6,7,8});
    indexes.put(54, new int[]{2,1,0});

    indexes.put(5, new int[]{2,1,0});
    indexes.put(15, new int[]{0,3,6});
    indexes.put(35, new int[]{8,5,2});
    indexes.put(45, new int[]{6,7,8});

    return indexes;
  }


  private int[] getCorners() {
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


  // The cube's orientation is originally a base 3 number.
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
      {cube[4][6], cube[5][0], cube[1][6]}
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


  private int[] getEdges(){
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
    return this.stateMap(this.getCorners()) * 2187 + getCornerOrientation();
  }


  /* Generates the fatoradic value of this Cube's permutation of corners or edges.
    Currently hardcoded for 8 or 12 digit factorials so it won't need to recompute the factorials.
  */
  private int stateMap(int[] input){
    int value = 0;
    ArrayList<Integer> input_list = new ArrayList<Integer>(input.length);
    for(int x : input) {
      input_list.add(x);
    }
    int[] factorials;
    if (input.length == 8) {
      //                            7! ,  6!,  5!, 4!...
      factorials = new int[]{5040, 720, 120, 24, 6, 2, 1};
    }
    else {
      //                            11!,      10!,    9!,      8!,  7! ,  6!,  5!, 4!...
      factorials = new int[]{39916800, 3628800, 362880, 40320, 5040, 720, 120, 24, 6, 2, 1};
    }

    int[] sequence = new int[input.length - 1];
    for (int j = 0; j < (input.length - 1); j++){
      sequence[j] = input_list.indexOf(j);
      input_list.remove(input_list.indexOf(j));
    }

    for(int i = 0; i < input.length - 1; i++){
      value += sequence[i] * factorials[i];
    }
    return value;
  }

}