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

  public char[][] cube;
  public int level;
  public int last_face;

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

  public void setFace(int face){
    this.last_face = face;
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
    int[] index_keys = null;
    switch (face) {
      case 0: index_keys = new int[]{5,1,2,3};
              break;
      case 1: index_keys = new int[]{0,5,4,2};
              break;
      case 2: index_keys = new int[]{0,1,4,3};
              break;
      case 3: index_keys = new int[]{0,2,4,5};
              break;
      case 4: index_keys = new int[]{2,1,5,3};
              break;
      case 5: index_keys = new int[]{4,1,0,3};
              break;
      default: break;
    }
    int[][] rotation_indexes = initRotationIndex(face);
    int i = 0;

    // Storing the first values for later displacing.
    char[] temp = new char[3];
    for(int x: rotation_indexes[0]){
      temp[i] = this.cube[index_keys[0]][x];
      i++;
    }

    int next_face = 0;
    int[] next_indexes = new int[3];
    for(i = 0; i < 3; i++){
      int current_face = index_keys[i];
      int[] current_indexes = rotation_indexes[i];
      next_face = index_keys[i + 1];
      next_indexes = rotation_indexes[i + 1];

      for(int j = 0; j < 3; j++){
        this.cube[current_face][current_indexes[j]] = this.cube[next_face][next_indexes[j]];
      }
    }

    for(int j = 0; j < 3; j++){
      this.cube[next_face][next_indexes[j]] = temp[j];
    }
  }


  private int[][] initRotationIndex(int face){
    switch(face) {
      case 0: return new int[][]{new int[]{6,7,8},new int[]{2,1,0}, new int[]{2,1,0}, new int[]{2,1,0}};
      case 1: return new int[][]{new int[]{0,3,6}, new int[]{0,3,6}, new int[]{0,3,6}, new int[]{0,3,6}};
      case 2: return new int[][]{new int[]{6,7,8}, new int[]{8,5,2}, new int[]{2,1,0}, new int[]{0,3,6}};
      case 3: return new int[][]{new int[]{8,5,2}, new int[]{8,5,2}, new int[]{8,5,2}, new int[]{8,5,2}};
      case 4: return new int[][]{new int[]{6,7,8}, new int[]{6,7,8}, new int[]{2,1,0}, new int[]{6,7,8}};
      case 5: return new int[][]{new int[]{6,7,8}, new int[]{0,3,6}, new int[]{2,1,0}, new int[]{8,5,2}};
      default: return null;
    }
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


  private int[] getEdgeOrientation() {
    int[] edges_values = new int[]{0,0,0,0,0,0,0,0,0,0,0,0};
    int index = 0;
    int pos = 1;
    char face = '0';
    char side = '0';
    for (int i: new int[]{0, 4}){
      for(int j: new int[]{3, 5}){
        face = cube[i][j];
        if(!(face == 'Y' || face == 'W')){
          side = cube[j - 2][pos];
          if(side == 'Y' || side == 'W'){
            edges_values[index] = 1;
          }
        }
        index++;
      }
      pos = 7; // Bottom index on Green/Blue.
    }

    for (int i: new int[]{2, 5}){
      for(int j: new int[]{1, 3, 5, 7}){
        face = cube[i][j];
        if(!(face == 'Y' || face == 'W')){
          if (face == 'B' || face == 'G') {
            edges_values[index] = 1;
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
              edges_values[index] = 1;
            }
          }
        }
        index++;
      }
    }

    return edges_values;
  }


  public int sideFace(int face, int index){
    if (index == 3) return 1;
    if (index == 5) return 3;
    if (index == 1) return (face == 2) ? 0 : 4;
    else return (face == 2) ? 4 : 0;
  }


  public int getEncodedCorners(){
    int value = 0;
    ArrayList<Integer> input_list = new ArrayList<Integer>(8);
    for(int x : this.getCorners()) {
      input_list.add(x);
    }
    int[] weights = new int[]{5040, 720, 120, 24, 6, 2, 1};
    int[] sequence = new int[7];
    for (int j = 0; j < 7; j++){
      sequence[j] = input_list.indexOf(j);
      input_list.remove(input_list.indexOf(j));
    }

    for(int i = 0; i < 7; i++){
      value += sequence[i] * weights[i];
    }

    return value * 2187 + getCornerOrientation();
  }


  public int getEncodedEdges(int half){
    int value = 0;
    int[] edgeGroupOrientation = Arrays.copyOfRange(getEdgeOrientation(), half * 6, half * 6 + 6); // half is either 0 or 1.
    ArrayList<Integer> input_list = new ArrayList<Integer>(12);
    for(int x : this.getEdges()) {
      input_list.add(x);
    }


    int[] weights = new int[]{1774080, 80640, 4032, 224, 14, 1};
    int[] sequence = new int[6];
    int index = 0;
    for (int j = 11; j > 5; j--){
      sequence[index] = input_list.indexOf(j);
      input_list.remove(input_list.indexOf(j));
      index++;
    }

    //System.out.println("VALUE BEFORE: " + value);
    for(int i = 0; i < 6; i++){
      //System.out.printf("Sequence: %d, Weight: %d, Orientation: %d. Total: %d\n", sequence[i], weights[i], edgeGroupOrientation[i], (sequence[i] * 2 + edgeGroupOrientation[i]) * weights[i]);
      value += (sequence[i] * 2 + edgeGroupOrientation[i]) * weights[i];
    }

    //System.out.println("Value: " + value);
    return value;
  }


  /* Generates the fatoradic value of this Cube's permutation of corners or edges.
    Currently hardcoded for weight values so they don't need to be recomputed everytime.
  */

}