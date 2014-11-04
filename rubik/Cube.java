package rubik;

import java.util.Arrays;

/**
 * Representation of a Rubik's Cube.
 *
 * @author     Stanley Yang
 * @version    1.0
 * @since      2014-11-04
 */
public class Cube {
  /**
   * Cube's public variables.
   * cube is a byte[54] that represents all 54 faces on the cube.
   * The faces in order are R, G, Y, B, O, W or 0, 1, 2, 3, 4, 5.
   * The face multiplied by 9 will be the first index of that face.
   * Ex: Front face = 2 * 9 and the next 8 indexes are the front face, 18 - 26.
   * <p>
   * level is an optional variable that assigns a number to this cube. This is
   * typically used to say how many moves were used to generate this cube.
   * <p>
   * last_face is an optional variable that indicates what face was turned to
   * get to this cube. This is used to help decreases the number of rotations
   * done on this cube by 3 because there is no need to rotate the same face.
   */
  public byte[] cube;
  public int level;
  public int last_face;

  /**
   * Basic constructor for Cube.
   */
  public Cube(){
    this.cube = new byte[54];
  }

  /**
   * Generates a Cube from a String. Essentially the same as Cube(char[]).
   * The String should not be formatted properly yet.
   *
   * @param s   String of length 54 containing the cube.
   */
  public Cube(String s){
    char[] input = s.toCharArray();
    this.cube = this.generateCube(input);
  }

  /**
   * Generates a Cube from a char array.
   * The char array should not be formatted properly yet.
   *
   * @param input  A char[54] containing the cube.
   */
  public Cube(char[] input){
    this.cube = this.generateCube(input);
  }

  /**
   * Generates a Cube from a char array and initializes it with a level.
   * The char array should not be formatted properly yet.
   *
   * @param input  A char[54] containing the cube.
   * @param level  An integer, generally the number of moves used to get to this cube.
   */
  public Cube(char[] input, int level){
    this.cube = this.generateCube(input);
    this.level = level;
  }

  /**
   * Generates a Cube from a byte array. This is generally used to clone a Cube.
   *
   * @param input  A byte[54] containing the cube.
   */
  public Cube(byte[] input){
    this.cube = this.generateCube(input);
  }

  /**
   * Generates a Cube from a byte array and initializes it with a level.
   *
   * @param input  A byte[54] containing the cube.
   * @param level  An integer, generally the number of moves used to get to this cube.
   */
  public Cube(byte[] input, int level){
    this.cube = this.generateCube(input);
    this.level = level;
  }

  /**
   * Sets the level variable in the Cube.
   *
   * @param level  An integer, generally the number of moves used to get to this cube.
   */
  public void setLevel(int level){
    this.level = level;
  }

  /**
   * Sets the last_face variable in the Cube.
   *
   * @param last_face  An integer ranging [0,5], should be the face rotated to generate this cube.
   */
  public void setFace(int face){
    this.last_face = face;
  }

  /**
   * Properly formats the cube from a String or char[] input.
   *
   * @param input  Expected to be formatted as from a text file.
   * @return       A properly formatted cube.
   */
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

  /**
   * Deep clones the input byte array and returns it.
   *
   * @param input  A byte[54], should be a valid cube state.
   * @return       A deep cloned copy of input.
   */
  private byte[] generateCube(byte[] input){
    byte[] temp = new byte[54];
    for (int i = 0; i < 54; i++){
      temp[i] = input[i];
    }
    return temp;
  }

  /**
   * Prints the cube in a more human readable format.
   */
  public void printCube() {
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

  /**
   * Rotates a face clockwise a given number of turns
   *
   * @param face  The face to be turned.
   * @param turns The amount of clockwise turns. Should be between 1 - 3.
   * @return      A new Cube object with the rotation done.
   */
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

  /**
   * A helper function for rotate(). Rotates the cubies related to the face that was turned.
   * <p>
   * rotate() only rotates the stickers on the face requested. This rotates the related stickers.
   * Example: A rotation of the top face (0) requires the stickers on the back, left, front, right (5,1,2,3)
   *          to be rotated as well.
   *
   * @param face  The face that was turned.
   */
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

  /**
   * A helper function for rotateCubies(). These are the indexes to be rotated corresponding to the switch
   * statement in rotateCubies().
   *
   * @param face  The face that was turned.
   */
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

  /**
   * Returns the permutation of corners on this cube.
   *
   * @return An int[] containing the permutation of corners.
   */
  private int[] getCorners() {
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

    int[] output = new int[8];
    for (int i = 0; i < 8; i++){
      Arrays.sort(corners[i]);
      output[i] = getCornersHelper(corners[i]);
    }
    return output;
  }

  /**
   * A helper function to speed up getCorners(). Maps a given corner to a number.
   * <p>
   *    0      1      2      3      4      5      6      7
   * ("GRW", "BRW", "GRY", "BRY", "GOY", "BOY", "GOW", "BOW")
   *
   * @return The integer value of a corner.
   */
  private int getCornersHelper(byte[] b) {
    if (b[0] == 'B'){
      if (b[1] == 'R'){
        if(b[2] == 'W') return 1; //BRW
        else return 3; // BRY
      }
      else {
        if(b[2] == 'W') return 7; //BOW
        else return 5; // BOY
      }
    }
    else {        //I know it is Green now
      if (b[1] == 'R'){
        if(b[2] == 'W') return 0; //GRW
        else return 2; // GRY
      }
      else {
        if(b[2] == 'W') return 6; //GOW
        else return 4; // GOY
      }
    }
  }


  /**
   * Returns the orientation of the corners translated into an integer.
   * <p>
   * A corner's orientation can be 0, 1, or 2, depending on orientation.
   * Therefore this can be seen as a 7 digit base 3 number which we then translate.
   * This is primarily for getEncodedCorners().
   *
   * @return An int representing the orientation. Value ranges from [0, 2186].
   */
  public int getCornerOrientation(){
    int[] indexes = new int[]{0, 2, 6, 8};
    byte[] base3 = new byte[7];

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

  /**
   * Returns the permutation of edges on this cube.
   *
   * @return An int[] containing the permutation of edges.
   */
  private int[] getEdges(){
    byte[][] edges = new byte[][]{
      {cube[1], cube[52]}, {cube[3], cube[10]}, {cube[5], cube[28]}, {cube[7], cube[19]},
      {cube[12], cube[48]}, {cube[14], cube[21]}, {cube[23], cube[30]}, {cube[32], cube[50]},
      {cube[37], cube[25]}, {cube[39], cube[16]}, {cube[41], cube[34]}, {cube[43], cube[46]}
    };

    int[] output = new int[12];
    for (int i = 0; i < 12; i++){
      output[i] = getEdgesHelper(edges[i][0], edges[i][1]);
    }
    return output;
  }

  /**
   * A helper function to speed up getEdges(). Maps a given edge to a number.
   * <p>
   *   0     1     2     3     4      5     6     7    8     9     10    11
   * ("RW", "GR", "BR", "RY", "GW", "GY", "BY", "BW", "OY", "GO", "BO", "OW");
   *
   * @return The integer value of an edge.
   */
  private int getEdgesHelper(byte a, byte b){
    if (a > b){
      a = (byte)(a^b);
      b = (byte)(a^b);
      a = (byte)(a^b);
    }
    if (a == 'B'){
      switch(b){
        case 'R': return 2;
        case 'O': return 10;
        case 'Y': return 6;
        case 'W': return 7;
        default: return -1;
      }
    }
    if (a == 'G'){
      switch(b){
        case 'R': return 1;
        case 'O': return 9;
        case 'Y': return 5;
        case 'W': return 4;
        default: return -1;
      }
    }
    if (a == 'O'){
      switch(b){
        case 'Y': return 8;
        case 'W': return 11;
        default: return -1;
      }
    }
    if (a == 'R'){
      switch(b){
        case 'Y': return 3;
        case 'W': return 0;
        default: return -1;
      }
    }
    return -1;
  }


  /**
   * Returns the orientations of the edge cubies as an int[].
   * <p>
   * An edge can be oriented correctly or incorrectly, 0 or 1.
   *
   * @return An int array with the edge cubies orientations.
   */
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
    return new int[]{edges_values[11], edges_values[0], edges_values[1], edges_values[4], edges_values[9], edges_values[5], edges_values[6], edges_values[10], edges_values[7], edges_values[2], edges_values[3], edges_values[8]};
  }

  /**
   * Helper function for getEdgeOrientation.
   * <p>
   * It is the index the sticker related to a sticker on a given face and index.
   *
   * @param face  The face that was checked.
   * @param index The index that was checked.
   * @return      The index of the sticker to be checked.
   */
  public int sideFace(int face, int index){
    if (index == 3) return 1;
    if (index == 5) return 3;
    if (index == 1) return (face == 2) ? 0 : 4;
    else return (face == 2) ? 4 : 0;
  }

  /**
   * Hashing method that maps this cube's corners to an integer.
   * <p>
   * Generates the value by mapping the permutation to a unique number in the range [0, 40319].
   * Then multiples it by the total number of values the corner orientations can be and adds the
   * value of this cube's corner orientations.
   *
   * @return    The hashed value of this cube's corners.
   */
  public int getEncodedCorners(){
    int value = 0;
    int[] corners = this.getCorners();
    int[] weights = new int[]{5040, 720, 120, 24, 6, 2, 1};
    int[] sequence = new int[7];
    int index = 0;

    int j = 0;
    while(j < 7){
      int pos = 0;
      while(corners[pos] != j) pos++;
      sequence[index] = pos;
      for(int i = pos; i < corners.length - 1 - j; i++){
        corners[i] = corners[i+1]; // Collapsing array.
      }
      j++;
      index++;
    }

    for(int i = 0; i < 7; i++){
      value += sequence[i] * weights[i];
    }

    return value * 2187 + getCornerOrientation();
  }

  /**
   * Hashing method that maps this cube's edges to an integer.
   * <p>
   * Generates the value by using the position and orientation of the first six or last six
   * edge cubies and then apply a variable weight to each value.
   *
   * @param  half Integer value 0 or 1 for the first or second group.
   * @return      The hashed value of this cube's edges.
   */
  public int getEncodedEdges(int half){
    int value = 0;
    int[] edgeGroupOrientation = getEdgeOrientation(); // half is either 0 or 1.
    int[] edges = this.getEdges();

    int[] weights = new int[]{1774080, 80640, 4032, 224, 14, 1};
    int[] sequence = new int[6];
    int[] orientations = new int[6];

    int index = 0;
    int j = 0 + (6 * half);
    while(j < 6 + (6 * half)){
      int pos = 0;
      while(edges[pos] != j) pos++;
      orientations[index] = edgeGroupOrientation[pos];
      j++;
      index++;
    }

    index = 0;
    j = 0 + (6 * half);
    while(j < 6 + (6 * half)){
      int pos = 0;
      while(edges[pos] != j) pos++;
      sequence[index] = pos;
      for(int i = pos; i < edges.length - 1; i++){
        edges[i] = edges[i+1]; // Collapsing array.
      }
      j++;
      index++;
    }

    value += (sequence[0] + orientations[0] * 12) * weights[0];
    value += (sequence[1] + orientations[1] * 11) * weights[1];
    value += (sequence[2] + orientations[2] * 10) * weights[2];
    value += (sequence[3] + orientations[3] * 9) * weights[3];
    value += (sequence[4] + orientations[4] * 8) * weights[4];
    value += (sequence[5] + orientations[5] * 7) * weights[5];

    return value;
  }

}