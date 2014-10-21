package rubik;

import java.util.Arrays;
import java.nio.file.*;
import java.io.*;
import java.util.*;

class GenerateCorners {

  static byte left = (byte)0xF0;
  static byte right = (byte)0x0F;
  static byte[] corner_values = new byte [44089920];

  static void errorCheck(){
    int total = 0;
    for(int i = 0; i < corner_values.length; i++){
      int val = getValue(i);
      if(val < 0 || val > 11) {
        total += 1;
        //System.out.printf("VALUE UNFILLED AT INDEX %d: %d\n", i, val);
      }
    }
    System.out.println("TOTAL UNFILLED " + total);
  }

  static void initValues(){
    for (int i = 0; i < corner_values.length * 2; i++){
      insertValue(i, 15);
    }
  }

  // BFS attempt
/*
  static void generateValues(){
    Cube c = new Cube(Cube.GOAL_STATE);
    Queue<Cube> q = new LinkedList<Cube>();
    Queue<Cube> next = new LinkedList<Cube>();
    next.add(c);
    int level = 0;
    while(!next.isEmpty()){
      System.out.println("current level: " + level);
      q = next;
      next = new LinkedList<Cube>();
      while(!q.isEmpty()){
        Cube current = q.poll();
        for(int face = 0; face < 6; face++){
          for(int i = 1; i < 4; i++){
            Cube node = current.rotate(face, i);
            if (getValue(node.getEncodedCorners()) == 15 || getValue(node.getEncodedCorners()) == -1){
              insertValue(node.getEncodedCorners(), level);
              next.add(node);
            }
          }
        }
      }
      level++;
    }
  }
*/

  // Limited DFS attempt
  static void generateValues(){
    Cube c = new Cube(Cube.GOAL_STATE);
    c.setLevel(0);
    Stack<Cube> s = new Stack<Cube>();
    Stack<Cube> next = new Stack<Cube>();
    s.push(c);
    int limit = 6;
    long found = 1;
    while(!s.empty()){
      Cube current = s.pop();
      int level = current.level;
      if (found % 100000 == 0) System.out.printf("found: %d\n", found);

      if (getValue(current.getEncodedCorners()) > level) {
        insertValue(current.getEncodedCorners(), level);

        if (level < 11){ // Max moves is 11.
          for(int face = 0; face < 6; face++){
            for(int i = 1; i < 4; i++){
              Cube node = current.rotate(face, i);
              if (getValue(node.getEncodedCorners()) > level + 1){
                if (found < Long.MAX_VALUE) found++;

                  node.setLevel(level + 1);
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
        limit = limit + 6;
        next = new Stack<Cube>();
      }
    }
  }

  // 
  static void insertValue(int index, int level){
    byte current = corner_values[index / 2];
    if((index & 1) == 0){
      corner_values[index / 2] = (byte)((level << 4) | (current & right) );
    }
    else{
      corner_values[index / 2] = (byte)( (current & left) | level );
    }
  }


  static int getValue(int index){
    if((index & 1) == 0){
      return (( (corner_values[index / 2] & left) >> 4) & right);
    }
    else{
      return (corner_values[index / 2] & right);
    }
  }


  static void read(){
    try {
     FileInputStream input = new FileInputStream("CornerValues");
     input.read(corner_values);
     input.close();
    } catch (java.io.IOException e) {
     e.printStackTrace();
     System.exit(1);
    }
   }

   static void write(){
    System.out.println("Starting writing process");
    try {
     FileOutputStream output = new FileOutputStream("CornerValues");
     initValues();
     generateValues();
     output.write(corner_values);
     output.close();
    } catch (java.io.IOException e) {
     e.printStackTrace();
     System.exit(1);
    }
    System.out.println("Done writing");
   }

  public static void main(String[] args){
    Date start = new java.util.Date();
    //Cube c = new Cube(Cube.GOAL_STATE);
    write();
    //read();
    //errorCheck();
    //System.out.println(getValue(3));

    //System.out.println(getValue(22987557));
    //System.out.println(getValue(0));
    //System.out.println(getValue(2379456));
    //System.out.println(getValue(14171760));

    System.out.printf("Start Time: %s\nEnd Time: %s\n", start, new java.util.Date());
  }

}