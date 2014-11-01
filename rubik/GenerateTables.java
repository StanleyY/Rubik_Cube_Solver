package rubik;

import java.nio.file.*;
import java.io.*;
import java.util.*;

class GenerateTables {

  static byte left = (byte)0xF0;
  static byte right = (byte)0x0F;
  static byte[] corner_values = new byte [44089920];
  static byte[] edge0_values = new byte [21288960];
  static byte[] edge1_values = new byte [21288960];

  static void errorCheck(){
    int total = 0;
    for(int i = 0; i < corner_values.length; i++){
      int val = getCornerValue(i);
      if(val < 0 || val > 11) {
        total += 1;
        //System.out.printf("VALUE UNFILLED AT INDEX %d: %d\n", i, val);
      }
    }
    System.out.println("TOTAL UNFILLED " + total);
  }

  static void initValues(){
    for (int i = 0; i < corner_values.length * 2; i++){
      insertCornerValue(i, 15);
    }
    for (int i = 0; i < edge0_values.length * 2; i++){
      insertEdge0Value(i, 15);
      insertEdge1Value(i, 15);
    }
  }

  // Limited DFS attempt,
  //TODO: reimplement as interative DFS later.
  // Currently takes ~1 hour 20 minutes.
  static void generateEdgeValues(){
    Cube c = new Cube(Cube.GOAL_STATE);
    c.setLevel(0);
    c.setFace(7);
    Stack<Cube> s = new Stack<Cube>();
    Stack<Cube> next = new Stack<Cube>();
    s.push(c);
    int limit = 4; // limit of 6 needs ~1.7 gb of RAM to run reasonably. limit of 7 needs >4gb of RAM.
    long found = 1;
    while(!s.empty()){
      Cube current = s.pop();
      int level = current.level;
      if (found % 100000 == 0) System.out.printf("found: %d\n", found);
      int edge0_index = current.getEncodedEdges(0);
      int edge1_index = current.getEncodedEdges(1);

      if (getEdge0Value(edge0_index) > level) {
        insertEdge0Value(edge0_index, level);
      }
      if (getEdge1Value(edge1_index) > level) {
        insertEdge1Value(edge1_index, level);
      }

      if (level < 10){ // Max moves is 11.
        for(int face = 0; face < 6; face++){
          if (face != current.last_face){
            for(int i = 1; i < 4; i++){
              Cube node = current.rotate(face, i);
              if (getEdge0Value(node.getEncodedEdges(0)) > level + 1 || getEdge1Value(node.getEncodedEdges(1)) > level + 1){
                if (found < Long.MAX_VALUE) found++;
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
      if(s.empty() && !next.empty()){
        System.out.println("Starting level: " + limit);
        s = next;
        limit = 12;
        next = new Stack<Cube>();
      }
    }
  }


  static void generateValuesID(){
    Cube goal = new Cube(Cube.GOAL_STATE);
    goal.setLevel(0);
    goal.setFace(7);
    Stack<Cube> s = new Stack<Cube>();
    s.push(goal);
    int limit = 0;
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


  // 
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


  static void insertEdge0Value(int index, int level){
    byte current = edge0_values[index / 2];
    if((index & 1) == 0){
      edge0_values[index / 2] = (byte)((level << 4) | (current & right) );
    }
    else{
      edge0_values[index / 2] = (byte)( (current & left) | level );
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


  static void insertEdge1Value(int index, int level){
    byte current = edge1_values[index / 2];
    if((index & 1) == 0){
      edge1_values[index / 2] = (byte)((level << 4) | (current & right) );
    }
    else{
      edge1_values[index / 2] = (byte)( (current & left) | level );
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
     generateEdgeValues();
     FileOutputStream output = new FileOutputStream("CornerValues");
     //output.write(corner_values);
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
    //Cube c = new Cube("ORRORRROOYYYBGGWWWBGGWYYGBBYYYGBBWWWROOROOORRGBBYWWBGG"); // 5 move cube
    write();
    //read();
    //System.out.printf("Corners: %d, Edge 1: %d , Edge 2, %d\n", getCornerValue(c.getEncodedCorners()), getEdge0Value(c.getEncodedEdges(0)), getEdge1Value(c.getEncodedEdges(1)));
    //errorCheck();

    System.out.printf("Start Time: %s\nEnd Time: %s\n", start, new java.util.Date());
  }

}