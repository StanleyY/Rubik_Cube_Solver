package rubik;

import java.io.*;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Stack;

/**
 * Rubik's Cube solver using IDA* based on Korf's Algorithm.
 *
 * @author     Stanley Yang
 * @version    1.0
 * @since      2014-11-04
 */
class Solve {
  /**
   * Useful Global Variables.
   * left and right are for bitwise operations for getting and inserting values
   * into the pattern databases.
   * corner_values, edge0_values, edge1_values are the pattern databases.
   * input_cube will be where the original inputted cube will be stored.
   * goal_cube is the byte representation of the goal state.
   */
  static byte left = (byte)0xF0;
  static byte right = (byte)0x0F;
  static byte[] corner_values = new byte [44089920];
  static byte[] edge0_values = new byte [21288960];
  static byte[] edge1_values = new byte [21288960];
  static Cube input_cube = new Cube();
  static byte[] goal_cube = new Cube(GenerateTables.GOAL_STATE).cube;

  /**
   * This method is responsible for the iterative deepening portion of the
   * algorithm. It starts the first bound f value of the inputted cube. However
   * because the first actual step cost is 0, the f value is simply the heuristic
   * value. The bound is incremented by 1 every time the search completes without
   * finding a solution.
   * <p>
   * Note: Proper IDA* search would set the new bound to the lowest f value over
   * the existing bound because this allows for at least 1 more node to be analyzed.
   * However in a Rubik's cube case, due to the step cost being 1, an increment of 
   * just 1 allow many more nodes to be considered.
   */
  static void solveCube(){
    int threshold = h(input_cube);
    while (true) {
      search(new CubeNode(input_cube, h(input_cube)), threshold, "");
      threshold++;
      System.out.println("New Threshold: " + threshold);
    }
  }

  /**
   * A recursive A* algorithm implementation.
   * <p>
   * If the goal state is found, this method will print out the moves used and ends 
   * the program. Otherwise it will expand the current node for every child that is 
   * under the current bound.
   *
   * @param cn      A node containing a cube and its heuristic value.
   * @param bound   The f value a cube needs to be below to be considered.
   * @param moves   The moves used to reach this node.
   */
  static void search(CubeNode cn, int bound, String moves){
    if (goalTest(cn.cube)) {System.out.println("FOUND IT: " + translateMoves(moves) + " Length: " + moves.length() / 2); System.out.println(new java.util.Date()); System.exit(0);}
    PriorityQueue<CubeNode> children = generateChildren(cn.cube, bound);
    while (children.size() > 0){
      CubeNode n = children.poll();
      search(n, bound, moves.concat(n.move));
    }
  }

  /**
   * Generates all the children of a given Cube that are under a given bound.
   * <p>
   * Rotates every face to generate three children per face. It will not rotate face
   * that was previously rotated to get the input Cube because it will generate a
   * Cube that was previously generated with the input Cube, an uncle so to speak.
   * If a child's f value is under the bound, then it will be added to the priority
   * queue based on its f value.
   *
   * @param input   The Cube to be rotated.
   * @param bound   The f value a cube needs to be below to be considered.
   * @return        A Priority Queue of all the valid children of the given Cube.
   */
  static PriorityQueue<CubeNode> generateChildren(Cube input, int bound){
    PriorityQueue<CubeNode> queue = new PriorityQueue<CubeNode>(18, new CubeNodeComparator());
    for(int face = 0; face < 6; face++){
      if (face != input.last_face){
        for(int move = 1; move < 4; move++){
          Cube node = input.rotate(face, move);
          int h_val = h(node);
          if (input.level + 1 + h_val < bound){
            node.setLevel(input.level + 1);
            node.setFace(face);
            queue.offer(new CubeNode(node, input.level + 1 + h(node), Integer.toString(face).concat(Integer.toString(move))));
          }
        }
      }
    }
    return queue;
  }

  /**
   * Generates the heuristic value of the given Cube.
   * <p>
   * Korf's algorithm says that the highest of the value out of the moves needed to
   * solve a cube's corners, 6 of its edges, and the remaining 6 edges is an admissible
   * heuristic.
   *
   * @param c   The Cube to be rotated.
   * @return    The heuristic value of the given Cube.
   */
  static int h(Cube c){
    return Math.max(getCornerValue(c.getEncodedCorners()),
                    Math.max(getEdgeValue(c.getEncodedEdges(0), 0),
                             getEdgeValue(c.getEncodedEdges(1), 1)));
  }

  /**
   * Checks if the given Cube is the goal state.
   *
   * @param c   The Cube to be checked.
   * @return    True if it is the goal state, false otherwise.
   */
  static boolean goalTest(Cube c){
    if (!Arrays.equals(c.cube, goal_cube)) return false;
    return true;
  }

  /**
   * Decodes the original move String from digits to the face letters and move numbers.
   * <p>
   * Even indexes on the input String are actually the faces encoded. The odd indexes contain
   * the number of clockwise rotations of the face. It is far more efficent to do decoding once
   * here on the final solution string instead of doing the same decoding during 
   * generateNeighbors().
   *
   * @param input   The String to be decoded.
   * @return        The String with the faces decoded.
   */
  static String translateMoves(String input){
    char[] letters = new char[] {'R','G','Y','B','O','W'};
    char[] temp = input.toCharArray();
    for (int i = 0; i < temp.length; i = i + 2){
      temp[i] = letters[Character.getNumericValue(temp[i])];
    }
    return new String(temp);
  }

  /**
   * Returns the number of moves needed to solve a cube's corners.
   * <p>
   * The maximum moves needed to solve any cube's corners is a maximum of 11.
   * This means every state can be mapped to four bits so an index in the byte
   * array corner_values contains two states because a byte is 8 bits.
   * <p>
   * An even state hash is mapped to the first four bits while an odd hash is
   * mapped to the last four bits. These values are extracted using bitwise
   * operations.
   *
   * @param index   The hash value of this cube state's corners.
   * @return        The moves needed to solve this state's corners.
   */
  static int getCornerValue(int index){
    if((index & 1) == 0){
      return (( (corner_values[index / 2] & left) >> 4) & right);
    }
    else{
      return (corner_values[index / 2] & right);
    }
  }

  /**
   * Returns the number of moves needed to solve the requested group of edges.
   * <p>
   * The maximum moves needed to solve any cube's edges is a maximum of 10.
   * See getCornerValue() for more information.
   *
   * @param index   The hash value of this cube state's first group of edges.
   * @param group   The requested edge group, 0 or 1.
   * @return        The moves needed to solve this group of edges.
   * @see           getCornerValue
   */
  static int getEdgeValue(int index, int group){
    byte[] edge_values;
    if (group == 0) {
      edge_values = edge0_values;
    }
    else {
      edge_values = edge1_values;
    }
    if((index & 1) == 0){
      return (( (edge_values[index / 2] & left) >> 4) & right);
    }
    else{
      return (edge_values[index / 2] & right);
    }
  }

  /**
   * Reads in the pattern databases from the binary files on disk.
   */
  static void read(){
    try {
      FileInputStream input = new FileInputStream("oldCornerValues");
      input.read(corner_values);
      input.close();

      input = new FileInputStream("oldEdge0Values");
      input.read(edge0_values);
      input.close();

      input = new FileInputStream("oldEdge1Values");
      input.read(edge1_values);
      input.close();

      forceAdmissibility();
    } catch (java.io.IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
    System.out.println("Done loading files");
  }

  /**
   * Reads in the cube's state from a given file and generates a Cube object to input_cube.
   */
  static void readInput(String filename){
    try{
      FileInputStream fs = new FileInputStream(filename);
      byte[] buffer = new byte[fs.available()];
      fs.read(buffer);
      char[] temp = new String(buffer).replaceAll("\\s+", "").toCharArray(); //Remove any whitespace
      input_cube = new Cube(temp);
      input_cube.setFace(7);
      input_cube.setLevel(0);
      fs.close();
    }catch(Exception e){
      e.printStackTrace();
    }
  }

  /**
   * A failsafe check for my edge tables. Certain index values have not yet been
   * initialized so they were defaulted to 15. This is an unadmissible heuristic.
   * By setting those values to 0 instead, it becomes admissible, albeit a poor one.
   * Hopefully this function will soon be unnecessary.
   */
  static void forceAdmissibility(){
    for (int i = 0; i < edge0_values.length * 2; i++){
      if(getEdgeValue(i, 0) > 10) insertEdgeValue(i, 0, 0);
      if(getEdgeValue(i, 1) > 10) insertEdgeValue(i, 0, 1);
    }
  }

  /**
   * Inserts a value into an index in the specified edge group.
   * <p>
   * See getCornerValue() for more information on how the pattern databases are
   * formatted. This method inserts a value into the proper bits in the byte
   * at the index without modifying the other bits.
   *
   * @param index   The hash value of this cube state's specified group of edges.
   * @param level   The value to be inserted.
   * @param group   The requested edge group, 0 or 1.
   * @see           getCornerValues()
   */
  static void insertEdgeValue(int index, int level, int group){
    byte[] edge_values;
    if (group == 0) {
      edge_values = edge0_values;
    }
    else {
      edge_values = edge1_values;
    }
    byte current = edge_values[index / 2];
    if((index & 1) == 0){
      edge_values[index / 2] = (byte)((level << 4) | (current & right) );
    }
    else{
      edge_values[index / 2] = (byte)( (current & left) | level );
    }
  }


  public static void main(String[] args){
    if (args.length < 1) {
      System.out.println("Please Input Filename.");
      System.exit(1);
    }

    System.out.println(new java.util.Date());
    read();
    readInput(args[0]);
    solveCube();
  }

}