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

  public byte[] cube;
  public int level;
  public int last_face;
  public Map<Integer, int[]> rotation_indexes = null;

  public Cube(){
    this.cube = new byte[54];
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


  public Cube(byte[] input){
    this.cube = this.generateCube(input);
  }


  public Cube(byte[] input, int level){
    this.cube = this.generateCube(input);
    this.level = level;
  }


  public void setLevel(int level){
    this.level = level;
  }

  public void setFace(int face){
    this.last_face = face;
  }


  private byte[] generateCube(char[] input){
    byte[] output = new byte[54];

    for (int i = 0; i < 9; i++){
      output[i] = (byte)input[i];
    }

    int index = 9;
    for (int i = 9; i < 18; i = i + 3){
      output[index] = (byte)input[i];
      index++;
      output[index] = (byte)input[i+1];
      index++;
      output[index] = (byte)input[i+2];
      index++;
      output[index] = (byte)input[i + 9];
      index++;
      output[index] = (byte)input[i + 10];
      index++;
      output[index] = (byte)input[i + 11];
      index++;
      output[index] = (byte)input[i + 18];
      index++;
      output[index] = (byte)input[i + 19];
      index++;
      output[index] = (byte)input[i + 20];
      index++;
    }

    for (int i = 36; i < 54; i++){
      output[i] = (byte)input[i];
    }

    return output;
  }

  // Deep cloning the input.
  private byte[] generateCube(byte[] input){
    byte[] temp = new byte[54];
    for (int i = 0; i < 54; i++){
      temp[i] = input[i];
    }
    return temp;
  }


  public void printCube() {
  // Prints the cube in a more human readable format.
  int i = 0;
  while (i < 9){
    if(i % 3 == 0) System.out.printf("\n   ");
    System.out.printf("%c", (char)this.cube[i]);
    i++;
  }

  while (i < 18){
    System.out.printf("\n");
    System.out.printf("%c%c%c", (char)this.cube[i], (char)this.cube[i+1], (char)this.cube[i+2]);
    System.out.printf("%c%c%c", (char)this.cube[i+9], (char)this.cube[i+10], (char)this.cube[i+11]);
    System.out.printf("%c%c%c", (char)this.cube[i+18], (char)this.cube[i+19], (char)this.cube[i+20]);
    i = i + 3;
  }

  i = 36;
  while (i < 54){
    if(i % 3 == 0) System.out.printf("\n   ");
    System.out.printf("%c", (char)this.cube[i]);
    i++;
  }
  System.out.printf("\n\n");
}

  // Rotates a face clockwise a given number of turns
  public Cube rotate(int face, int turns){
    if (turns < 1 || turns > 3) {throw new IllegalArgumentException("Turns need to be between 1 and 3.");}
    if (face < 0 || face > 5) {throw new IllegalArgumentException("Invalid face, must be between 0 and 5.");}

    Cube output = new Cube(this.cube);
    byte temp;
    while(turns > 0){
      temp = output.cube[face * 9];
      output.cube[face * 9] = output.cube[face * 9 + 6];
      output.cube[face * 9 + 6] = output.cube[face * 9 + 8];
      output.cube[face * 9 + 8] = output.cube[face * 9 + 2];
      output.cube[face * 9 + 2] = temp;
      temp = output.cube[face * 9 + 1];
      output.cube[face * 9 + 1] = output.cube[face * 9 + 3];
      output.cube[face * 9 + 3] = output.cube[face * 9 + 7];
      output.cube[face * 9 + 7] = output.cube[face * 9 + 5];
      output.cube[face * 9 + 5] = temp;
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
    byte[] temp = new byte[3];
    for(int x: rotation_indexes[0]){
      temp[i] = this.cube[index_keys[0] * 9 + x];
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
        this.cube[current_face * 9 + current_indexes[j]] = this.cube[next_face * 9 + next_indexes[j]];
      }
    }

    for(int j = 0; j < 3; j++){
      this.cube[next_face * 9 + next_indexes[j]] = temp[j];
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
    byte[][] corners = new byte[][]{
      {cube[0], cube[9], cube[51]},
      {cube[2], cube[53], cube[29]},
      {cube[6], cube[18], cube[11]},
      {cube[8], cube[27], cube[20]},

      {cube[36], cube[17], cube[24]},
      {cube[38], cube[26], cube[33]},
      {cube[42], cube[45], cube[15]},
      {cube[44], cube[35], cube[47]}
    };

    String temp = "";
    for (int i = 0; i < 8; i++){
      Arrays.sort(corners[i]);
      temp = new String(corners[i]);
      output[i] = keys.indexOf(temp);
    }

    return output;
  }


  // The cube's orientation is originally a base 3 number.
  public int getCornerOrientation(){
    int[] indexes = new int[]{0, 2, 6, 8};
    byte[] base3 = new byte[7];

    // TODO: move this to its own function
    byte[][] corners = new byte[][]{
      {cube[0], cube[9], cube[51]},
      {cube[2], cube[53], cube[29]},
      {cube[6], cube[18], cube[11]},
      {cube[8], cube[27], cube[20]},

      {cube[36], cube[17], cube[24]},
      {cube[38], cube[26], cube[33]},
      {cube[42], cube[45], cube[15]}
    };

    int current_index = 0;
    byte i = 0;
    for (byte[] c : corners){
      i = 0;
      for (byte x : c){
        if( x == 'R' || x == 'O') {
          base3[current_index] = i;
          current_index++;
          break;
        }
        i++;
      }
    }
    int total = 0;
    for (i = 0; i < base3.length; i++){
      total += base3[6 - i] * Math.pow(3, i);
    }
    return total;
  }


  private int[] getEdges(){
    List<String> keys = Arrays.asList("RW", "GR", "BR", "RY", "GW", "GY", "BY", "BW", "OY", "GO", "BO", "OW");
    int[] output = new int[12];

    byte[][] edges = new byte[][]{
      {cube[1], cube[52]}, {cube[3], cube[10]}, {cube[5], cube[28]}, {cube[7], cube[19]},
      {cube[12], cube[48]}, {cube[14], cube[21]}, {cube[23], cube[30]}, {cube[32], cube[50]},
      {cube[37], cube[25]}, {cube[39], cube[16]}, {cube[41], cube[34]}, {cube[43], cube[46]}
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
    byte face = '0';
    byte side = '0';
    for (int i: new int[]{0, 4}){
      for(int j: new int[]{3, 5}){
        face = cube[i * 9 + j];
        if(!(face == 'Y' || face == 'W')){
          side = cube[ 9 * (j - 2) + pos];
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
        face = cube[i * 9 + j];
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
            side = cube[sideFace(i, j) * 9 + pos];
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
    int[] sequence = new int[7];;
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

    for(int i = 0; i < 6; i++){
      //System.out.printf("Sequence: %d, Weight: %d, Orientation: %d. Total: %d\n", sequence[i], weights[i], edgeGroupOrientation[i], (sequence[i] * 2 + edgeGroupOrientation[i]) * weights[i]);
      value += (sequence[i] * 2 + edgeGroupOrientation[i]) * weights[i];
    }

    return value;
  }

}