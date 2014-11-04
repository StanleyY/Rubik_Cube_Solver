package rubik;

import java.util.Arrays;
import java.nio.file.*;
import java.io.*;
import java.util.*;

/** Rubik's Cube solver using IDA* based on Korf's Algorithm.
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
   * @param input   The Cube to be rotated
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
   * Korf's algorithm says that the maximum of the number of moves needed to solve
   * a cube's corners, 6 of its edges, and the remaining 6 edges is an admissible
   * heuristic.
   *
   * @param c   The Cube to be rotated
   * @return    The heuristic value of the given Cube.
   */
  static int h(Cube c){
    return Math.max(getCornerValue(c.getEncodedCorners()),
                    Math.max(getEdge0Value(c.getEncodedEdges(0)),
                             getEdge1Value(c.getEncodedEdges(1))));
  }


  static boolean goalTest(Cube c){
    if (!Arrays.equals(c.cube, goal_cube)) return false;
    return true;
  }


  static String translateMoves(String s){
    char[] letters = new char[] {'R','G','Y','B','O','W'};
    char[] temp = s.toCharArray();
    for (int i = 0; i < temp.length; i = i + 2){
      temp[i] = letters[Character.getNumericValue(temp[i])];
    }
    return new String(temp);
  }


  static void printCubeInformation(Cube c){
    System.out.printf("Corners: %d, Edge 1: %d , Edge 2, %d\n", getCornerValue(c.getEncodedCorners()), getEdge0Value(c.getEncodedEdges(0)), getEdge1Value(c.getEncodedEdges(1)));
  }


  static int getCornerValue(int index){
    if((index & 1) == 0){
      return (( (corner_values[index / 2] & left) >> 4) & right);
    }
    else{
      return (corner_values[index / 2] & right);
    }
  }


  static int getEdge0Value(int index){
    if((index & 1) == 0){
      return (( (edge0_values[index / 2] & left) >> 4) & right);
    }
    else{
      return (edge0_values[index / 2] & right);
    }
  }


  static int getEdge1Value(int index){
    if((index & 1) == 0){
      return (( (edge1_values[index / 2] & left) >> 4) & right);
    }
    else{
      return (edge1_values[index / 2] & right);
    }
  }


  static void read(){
    System.out.println("Starting reading process");
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

      forceAdmission();
    } catch (java.io.IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
    System.out.println("Done loading files");
  }


  static void readInput(String filename){
    try{
      FileInputStream fs = new FileInputStream(filename);
      byte[] buffer = new byte[fs.available()];
      fs.read(buffer);
      char[] temp = new String(buffer).replaceAll("\\s+", "").toCharArray(); //Remove whitespace
      input_cube = new Cube(temp);
      input_cube.setFace(7);
      input_cube.setLevel(0);
      fs.close();
    }catch(Exception e){
      e.printStackTrace();
    }
  }


  static void forceAdmission(){
    for (int i = 0; i < edge0_values.length; i++){
      if(getEdge0Value(i) > 10) insertEdge0Value(i, 0);
      if(getEdge1Value(i) > 10) insertEdge1Value(i, 0);
    }
  }


  static void insertEdge0Value(int index, int level){
    byte current = edge0_values[index / 2];
    if((index & 1) == 0){
      edge0_values[index / 2] = (byte)((level << 4) | (current & right) );
    }
    else{
      edge0_values[index / 2] = (byte)( (current & left) | level );
    }
  }


  static void insertEdge1Value(int index, int level){
    byte current = edge1_values[index / 2];
    if((index & 1) == 0){
      edge1_values[index / 2] = (byte)((level << 4) | (current & right) );
    }
    else{
      edge1_values[index / 2] = (byte)( (current & left) | level );
    }
  }


  // Generate every cube up to 11 moves and sees if there are any unadmissable heuristics.
  static void randomCubeTest(){
    Stack<Cube> s = new Stack<Cube>();
    Cube goal = new Cube(GenerateTables.GOAL_STATE);
    goal.setLevel(0);
    goal.setFace(7);
    s.push(goal);
    int level = 0;
    int limit = 5;
    while (!s.empty()){
      Cube current = s.pop();
      level = current.level;
      if(getEdge0Value(current.getEncodedEdges(0)) > level || getEdge1Value(current.getEncodedEdges(1)) > level || getCornerValue(current.getEncodedCorners()) > level){
        System.out.println("Cube information, Level: " + current.level);
        printCubeInformation(current);
        current.printCube();
      }
      if (level < 11){
        for(int face = 0; face < 6; face++){
          if (face != current.last_face){
            for(int i = 1; i < 4; i++){
              Cube node = current.rotate(face, i);
              node.setLevel(level + 1);
              node.setFace(face);
              s.push(node);
            }
          }
        }
      }
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
    //randomCubeTest();
  }

}