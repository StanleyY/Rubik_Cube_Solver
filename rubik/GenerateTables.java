package rubik;

import java.nio.file.*;
import java.io.*;
import java.util.*;

/**
 * Generates the pattern databases as decribed in Korf's Algorithm using variations
 * of Depth-First Search, particularly Depth-Limited and Iterative-Deepening.
 *
 * @author     Stanley Yang
 * @version    1.0
 * @since      2014-11-04
 */
class GenerateTables {
  /**
   * Useful Global Variables.
   * GOAL_STATE is a String representation of the goal state cube. This is used 
   * to start generating cubes for placement.
   * left and right are for bitwise operations for getting and inserting values
   * into the pattern databases.
   * corner_values, edge0_values, edge1_values are the pattern databases.
   * edgesFound and cornersFound are used for keeping track of progress.
   */
  public static final String GOAL_STATE = "RRRRRRRRRGGGYYYBBBGGGYYYBBBGGGYYYBBBOOOOOOOOOWWWWWWWWW";

  static byte left = (byte)0xF0;
  static byte right = (byte)0x0F;
  static byte[] corner_values = new byte [44089920];
  static byte[] edge0_values = new byte [21288960];
  static byte[] edge1_values = new byte [21288960];
  static long edgesFound = 1;
  static long cornersFound = 1;

  /**
   * Utility function that prints out how many unfilled indexes there are
   * in the pattern databases.
   */
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

  /**
   * Initializes the pattern databases to 15 in every index. This is effectively
   * infinity because the maximum moves needed to solve the corners on a cube is 11
   * and the maximum moves for solving the edges is 10.
   */
  static void initValues(){
    for (int i = 0; i < corner_values.length * 2; i++){
      insertCornerValue(i, 15);
    }
    for (int i = 0; i < edge0_values.length * 2; i++){
      insertEdgeValue(i, 15, 0);
      insertEdgeValue(i, 15, 1);
    }
  }


  /**
   * Generates every permutation of the corners and the number of moves used to reach this state.
   * <p>
   * The values generated should be the lowest number of moves necessary to solve that particular
   * permutation of the corners. This search is done by doing a Depth-Limited Search to the defined
   * limit and storing those nodes at the limit, similar to a fringe in Breadth-First Search. Once
   * all the nodes above the limit are exhausted. It does a Depth-Limited Search up to 11 moves of
   * the nodes in this fringe.
   */
  static void generateCornerValues(){
    Cube c = new Cube(GOAL_STATE);
    c.setLevel(0);
    Stack<Cube> s = new Stack<Cube>();
    Stack<Cube> next = new Stack<Cube>();
    s.push(c);
    int limit = 5; // limit of 5 uses less than 500MB of RAM.
    long found = 1;
    while(!s.empty()){
      Cube current = s.pop();
      int level = current.level;
      int corner_index = current.getEncodedCorners();

      if (getCornerValue(corner_index) > level) {
        insertCornerValue(corner_index, level);
        if (found < Long.MAX_VALUE) found++;
        if (found % 1000000 == 0) System.out.printf("Passed %d Corners\n", found);

        if (level < 11){ // Max moves is 11.
          for(int face = 0; face < 6; face++){
            if (face != current.last_face){
              for(int i = 1; i < 4; i++){
                Cube node = current.rotate(face, i);
                if (getCornerValue(node.getEncodedCorners()) > level + 1){
                  node.setLevel(level + 1);
                  node.setFace(face);
                  if (level + 1 < limit) {
                    s.push(node);
                  }
                  else {
                    next.push(node);
                  }
                }
              }
            }
          }
        }
      }
      if(s.empty() && !next.empty()){
        System.out.println("Starting level: " + limit);
        s = next;
        limit = 12;
        next = new Stack<Cube>();
      }
    }
  }


  static void generateCornerValuesID(){
    Cube goal = new Cube(GOAL_STATE);
    goal.setLevel(0);
    goal.setFace(7);
    insertCornerValue(goal.getEncodedCorners(), 0);
    Stack<Cube> s = new Stack<Cube>();
    s.push(goal);
    int limit = 1;
    int found = 1;
    while (limit < 12){
      while(!s.empty()){
        Cube current = s.pop();
        int level = current.level + 1;
        for(int face = 0; face < 6; face++){
          if (face != current.last_face){
            for(int i = 1; i < 4; i++){
              Cube node = current.rotate(face, i);
              int existing_node_corner_value = getCornerValue(node.getEncodedCorners());
              if (level == limit){
                if(existing_node_corner_value > level){
                  found++;
                  if(found % 100000 == 0) System.out.printf("Passed %d corners found.\n", found);
                  insertCornerValue(node.getEncodedCorners(), level);
                }
              }
              else if (existing_node_corner_value == level){
                node.setLevel(level);
                node.setFace(face);
                s.push(node);
              }
            }
          }
        }
      }
      limit++;
      System.out.println("Current Limit: " + limit);
      s.push(goal);
    }
    System.out.println("Corners Found: " + found);
  }


  static void generateEdgeValuesID(int group){
    Cube goal = new Cube(GOAL_STATE);
    goal.setLevel(0);
    goal.setFace(7);
    insertEdgeValue(goal.getEncodedEdges(0), 0, 0);
    insertEdgeValue(goal.getEncodedEdges(1), 0, 1);
    Stack<Cube> s = new Stack<Cube>();
    s.push(goal);
    int limit = 1;

    while (limit < 8){
      while(!s.empty()){
        Cube current = s.pop();
        int level = current.level + 1;
        for(int face = 0; face < 6; face++){
          if (face != current.last_face){
            for(int i = 1; i < 4; i++){
              Cube node = current.rotate(face, i);
              int node_edge = node.getEncodedEdges(group);
              int existing_edge_value = getEdgeValue(node_edge, group);
              if (node_edge == 31805264 && group == 1) node.printCube();
              if (level == limit){
                if (limit == 7) {
                  node.setLevel(level);
                  node.setFace(face);
                  edgeHelper(node, group);
                }
                else {
                  if (existing_edge_value > level){
                    edgesFound++;
                    if(edgesFound % 1000000 == 0) System.out.printf("Passed %d edges found.\n", edgesFound);
                    insertEdgeValue(node_edge, level, group);
                  }
                }
              }
              else {
                if (existing_edge_value == level){
                  node.setLevel(level);
                  node.setFace(face);
                  s.push(node);
                }
              }
            }
          }
        }
      }
      limit++;
      System.out.println("Current Edge Limit: " + limit);
      s.push(goal);
    }
    System.out.println("Edges Found: " + edgesFound);
  }

  static void edgeHelper(Cube c, int group){
    Stack<Cube> s = new Stack<Cube>();
    s.push(c);
    while (!s.empty()){
      Cube current = s.pop();
      int level = current.level;
      int current_edge = current.getEncodedEdges(group);
      if (current_edge == 31805264 && group == 1) current.printCube();
      if (getEdgeValue(current_edge, group) > level){
        edgesFound++;
        if(edgesFound % 1000000 == 0) System.out.printf("Passed %d edges found.\n", edgesFound);
        insertEdgeValue(current_edge, level, group);

        if (level < 10){
          for(int face = 0; face < 6; face++){
            if (face != current.last_face){
              for(int i = 1; i < 4; i++){
                Cube node = current.rotate(face, i);
                int node_edge = node.getEncodedEdges(group);
                int existing_edge_value = getEdgeValue(node_edge, group);

                if (existing_edge_value > level + 1){
                  node.setLevel(level + 1);
                  node.setFace(face);
                  s.push(node);
                }
              }
            }
          }
        }
      }
      else {
        current.printCube();
      }
    }
  }


  static void generateAllValuesID(){
    Cube goal = new Cube(GOAL_STATE);
    goal.setLevel(0);
    goal.setFace(7);
    insertCornerValue(goal.getEncodedCorners(), 0);
    insertEdgeValue(goal.getEncodedEdges(0), 0, 0);
    insertEdgeValue(goal.getEncodedEdges(1), 0, 1);
    Stack<Cube> s = new Stack<Cube>();
    s.push(goal);
    int limit = 1;

    while (limit < 8){
      while(!s.empty()){
        Cube current = s.pop();
        int level = current.level + 1;
        for(int face = 0; face < 6; face++){
          if (face != current.last_face){
            for(int i = 1; i < 4; i++){
              Cube node = current.rotate(face, i);
              int node_edge0 = node.getEncodedEdges(0);
              int node_edge1 = node.getEncodedEdges(1);
              int node_corner = node.getEncodedCorners();
              int existing_edge0_value = getEdgeValue(node_edge0, 0);
              int existing_edge1_value = getEdgeValue(node_edge1, 1);
              int existing_corner_value = getCornerValue(node_corner);

              if (level == limit){
                if (limit == 7) {
                  node.setLevel(level);
                  node.setFace(face);
                  allHelper(node);
                }
                else {
                  if (existing_edge0_value > level){
                    edgesFound++;
                    if(edgesFound % 1000000 == 0) System.out.printf("Passed %d edges found.\n", edgesFound);
                    insertEdgeValue(node_edge0, level, 0);
                  }
                  if (existing_edge1_value > level){
                    edgesFound++;
                    if(edgesFound % 1000000 == 0) System.out.printf("Passed %d edges found.\n", edgesFound);
                    insertEdgeValue(node_edge1, level, 1);
                  }
                  if (existing_corner_value > level){
                    cornersFound++;
                    if(cornersFound % 1000000 == 0) System.out.printf("Passed %d corners found.\n", cornersFound);
                    insertCornerValue(node_corner, level);
                  }
                }
              }
              else {
                if (existing_edge0_value == level || existing_edge1_value == level || existing_corner_value == level){
                  node.setLevel(level);
                  node.setFace(face);
                  s.push(node);
                }
              }
            }
          }
        }
      }
      limit++;
      System.out.println("Current Limit: " + limit);
      s.push(goal);
    }
    System.out.println("Edges Found: " + edgesFound);
    System.out.println("Corners Found: " + cornersFound);
  }


  static void allHelper(Cube c){
    Stack<Cube> s = new Stack<Cube>();
    s.push(c);
    while (!s.empty()){
      Cube current = s.pop();
      int level = current.level;
      int current_corner = current.getEncodedCorners();
      int current_edge0 = current.getEncodedEdges(0);
      int current_edge1 = current.getEncodedEdges(1);

      if (getEdgeValue(current_edge0, 0) > level){
        edgesFound++;
        if(edgesFound % 1000000 == 0) System.out.printf("Passed %d edges found.\n", edgesFound);
        insertEdgeValue(current_edge0, level, 0);
      }
      if (getEdgeValue(current_edge1, 1) > level){
        edgesFound++;
        if(edgesFound % 1000000 == 0) System.out.printf("Passed %d edges found.\n", edgesFound);
        insertEdgeValue(current_edge1, level, 1);
      }
      if (getCornerValue(current_corner) > level){
        cornersFound++;
        if(cornersFound % 1000000 == 0) System.out.printf("Passed %d corners found.\n", cornersFound);
        insertCornerValue(current_corner, level);
      }

      if (level < 11){
        for(int face = 0; face < 6; face++){
          if (face != current.last_face){
            for(int i = 1; i < 4; i++){
              Cube node = c.rotate(face, i);
              int node_edge0 = node.getEncodedEdges(0);
              int node_edge1 = node.getEncodedEdges(1);
              int node_corner = node.getEncodedCorners();
              int existing_edge0_value = getEdgeValue(node_edge0, 0);
              int existing_edge1_value = getEdgeValue(node_edge1, 1);
              int existing_corner_value = getCornerValue(node_corner);

              if (existing_edge0_value > level + 1 || existing_edge1_value > level + 1 || existing_corner_value > level + 1){
                node.setLevel(level + 1);
                node.setFace(face);
                s.push(node);
              }
            }
          }
        }
      }
    }
  }


  static void insertCornerValue(int index, int level){
    byte current = corner_values[index / 2];
    if((index & 1) == 0){
      corner_values[index / 2] = (byte)((level << 4) | (current & right) );
    }
    else{
      corner_values[index / 2] = (byte)( (current & left) | level );
    }
  }


  static int getCornerValue(int index){
    if((index & 1) == 0){
      return (( (corner_values[index / 2] & left) >> 4) & right);
    }
    else{
      return (corner_values[index / 2] & right);
    }
  }


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


  static void read(){
    System.out.println("Starting reading process");
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

  static void write(){
    System.out.println("Starting writing process");
    try {
      initValues();
      //generateEdgeValuesID(0);
      //generateEdgeValuesID(1);
      Cube c = new Cube(GOAL_STATE);
      c.setLevel(0);
      c.setFace(7);
      edgeHelper(c, 1);
      //generateCornerValues();
      //generateAllValuesID();
      FileOutputStream output = new FileOutputStream("CornerValues");
      output.write(corner_values);
      output.close();

      output = new FileOutputStream("Edge0Values");
      output.write(edge0_values);
      output.close();

      output = new FileOutputStream("Edge1Values");
      output.write(edge1_values);
      output.close();
      } catch (java.io.IOException e) {
        e.printStackTrace();
        System.exit(1);
      }
    System.out.println("Done writing to files");
  }


  public static void main(String[] args){
    Date start = new java.util.Date();
    write();
    //read();
    Cube c = new Cube("ORWORRGGYWWRWORGYRGGYBYBOBGOOBWYRBWGOGYYOBYROBWYRWWGBB");
    c.printCube();
    System.out.println("Corner: " + c.getEncodedCorners());
    System.out.println("Edge0: " + c.getEncodedEdges(0));
    System.out.println("Edge1: " + c.getEncodedEdges(1));
    System.out.printf("Corners: %d, Edge 0: %d , Edge 1, %d\n", getCornerValue(c.getEncodedCorners()), getEdgeValue(c.getEncodedEdges(0), 0), getEdgeValue(c.getEncodedEdges(1), 1));
    //errorCheck();

    System.out.printf("Start Time: %s\nEnd Time: %s\n", start, new java.util.Date());
  }

}
