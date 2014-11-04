package rubik;

import java.io.*;
import java.util.*;

class TestTables {

  static byte left = (byte)0xF0;
  static byte right = (byte)0x0F;
  static byte[] corner_values = new byte [44089920];
  static byte[] edge0_values = new byte [21288960];
  static byte[] edge1_values = new byte [21288960];


  static void errorCheck(){
    int corners = 0;
    int edge0 = 0;
    int edge0_u = 0;
    int edge1 = 0;
    int edge1_u = 0;
    for(int i = 0; i < corner_values.length * 2; i++){
      int val = getCornerValue(i);
      if(val < 0 || val > 11) {
        corners += 1;
      }
    }
    for(int i = 0; i < edge0_values.length * 2; i++){
      int val1 = getEdgeValue(i, 0);
      int val2 = getEdgeValue(i, 1);
      if(val1 > 10){
        if(val1 > 11){
          edge0_u++;
        }
        else edge0++;
      }
      if(val2 > 10){
        if(val2 > 11){
          edge1_u++;
        }
        else edge1++;
      }
    }
    System.out.println("Total Unfilled Corners: " + corners);
    System.out.println("Edge 0, Unadmissible: " + edge0);
    System.out.println("Edge 1, Unadmissible: " + edge1);
    System.out.println("Edge 0, Unfilled: " + edge0_u);
    System.out.println("Edge 1: Unfilled: " + edge1_u);
  }


  static int getCornerValue(int index){
    if((index & 1) == 0){
      return (( (corner_values[index / 2] & left) >> 4) & right);
    }
    else{
      return (corner_values[index / 2] & right);
    }
  }


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


  static void readNew(){
    System.out.println("Reading New Files");
    try {
      FileInputStream input = new FileInputStream("CornerValues");
      input.read(corner_values);
      input.close();

      input = new FileInputStream("Edge0Values");
      input.read(edge0_values);
      input.close();

      input = new FileInputStream("Edge1Values");
      input.read(edge1_values);
      input.close();
      errorCheck();
    } catch (java.io.IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
    System.out.println("Done loading files");
  }


  static void readOld(){
    System.out.println("Reading Old Files");
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
      errorCheck();
    } catch (java.io.IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
    System.out.println("Done loading files");
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
      if(getEdgeValue(current.getEncodedEdges(0), 0) > level || getEdgeValue(current.getEncodedEdges(1), 1) > level || getCornerValue(current.getEncodedCorners()) > level){
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


  static void printCubeInformation(Cube c){
    System.out.printf("Corners: %d, Edge 0: %d , Edge 1, %d\n", getCornerValue(c.getEncodedCorners()), getEdgeValue(c.getEncodedEdges(0), 0), getEdgeValue(c.getEncodedEdges(1), 1));
  }

  public static void main(String[] args){
    readOld();
    Cube c = new Cube("YOORRRRRRBWWGYYBBGBGOGYYBBGRGOGWWOGGYBBROYWOYBYROWWOWW");
    c.printCube();
    System.out.println("Corner: " + c.getEncodedCorners());
    System.out.println("Edge0: " + c.getEncodedEdges(0));
    System.out.println("Edge1: " + c.getEncodedEdges(1));
    System.out.printf("Corners: %d, Edge 0: %d , Edge 1, %d\n", getCornerValue(c.getEncodedCorners()), getEdgeValue(c.getEncodedEdges(0), 0), getEdgeValue(c.getEncodedEdges(1), 1));
  }
}