package rubik;

import java.util.Arrays;
import java.nio.file.*;
import java.io.*;
import java.util.*;

class Solve {

  static byte left = (byte)0xF0;
  static byte right = (byte)0x0F;
  static byte[] corner_values = new byte [44089920];
  static byte[] edge0_values = new byte [21288960];
  static byte[] edge1_values = new byte [21288960];
  static Cube input_cube = new Cube();
  static Cube goal_cube = new Cube(GenerateTables.GOAL_STATE);


  static void solveCube(){
    int threshold = h(input_cube);
    while (true) {
      search(new CubeNode(input_cube, h(input_cube)), threshold, "");
      threshold++;
      System.out.println("New Threshold: " + threshold);
    }
  }


  static void search(CubeNode cn, int bound, String s){
    if (goalTest(cn.cube)) {System.out.println("FOUND IT: " + translateMoves(s) + " Length: " + s.length() / 2); System.out.println(new java.util.Date()); System.exit(0);}
    PriorityQueue<CubeNode> neighbors = generateNeighbors(cn.cube, bound);
    while (neighbors.size() > 0){
      CubeNode n = neighbors.poll();
    search(n, bound, s.concat(n.move));
    }
  }


  static PriorityQueue<CubeNode> generateNeighbors(Cube c, int bound){
    PriorityQueue<CubeNode> queue = new PriorityQueue<CubeNode>(18, new CubeNodeComparator());
    for(int face = 0; face < 6; face++){
      if (face != c.last_face){
        for(int move = 1; move < 4; move++){
          Cube node = c.rotate(face, move);
          int h_val = h(node);
          if (c.level + 1 + h_val < bound){
            node.setLevel(c.level + 1);
            node.setFace(face);
            queue.offer(new CubeNode(node, c.level + 1 + h(node), Integer.toString(face).concat(Integer.toString(move))));
          }
        }
      }
    }
    return queue;
  }


  static int h(Cube c){
    return Math.max(getCornerValue(c.getEncodedCorners()),
                    Math.max(getEdge0Value(c.getEncodedEdges(0)),
                             getEdge1Value(c.getEncodedEdges(1))));
  }


  static boolean goalTest(Cube c){
    for (int i = 0; i < 6; i++){
      if (!Arrays.equals(c.cube[i], goal_cube.cube[i])) return false;
    }
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