package rubik;

import java.util.Arrays;
import java.nio.file.*;
import java.io.*;
import java.util.*;

class GenerateCorners {

  static byte left = (byte)240;
  static byte right = (byte)15;
  static byte[] values = new byte [44089920];

  static void errorCheck(){
    int total = 0;
    for(int i = 0; i < values.length; i++){
      int val = getValue(i);
      if(val == -1 || val == 15) {
        total += 1;
        System.out.printf("VALUE UNFILLED AT INDEX %d: %d\n", i, val);
      }
    }
    System.out.println("TOTAL UNFILLED " + total);
  }

  static void initValues(){
    for (int i = 0; i < values.length; i++){
      values[i] = (byte)255;
    }
    insertValue(new Cube(Cube.GOAL_STATE).getEncodedCorners(), 0);
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
    s.push(c);
    long found = 1;
    while(!s.empty()){
      Cube current = s.pop();
      int level = current.level;
      if (found % 100000 == 0) System.out.printf("found: %d\n", found);
      for(int face = 0; face < 6; face++){
        for(int i = 1; i < 4; i++){
          Cube node = current.rotate(face, i);
          if (getValue(node.getEncodedCorners()) == -1 || getValue(node.getEncodedCorners()) > level + 1){
            insertValue(node.getEncodedCorners(), level + 1);
            if (found < Long.MAX_VALUE) found++;
            if (level + 1 < 11){ // Max moves is 11.
              node.setLevel(level + 1);
              s.push(node);
            }
          }
        }
      }
    }
  }

  // 
  static void insertValue(int index, int level){
    byte current = values[index / 2];
    if((index & 1) == 0){
      values[index / 2] = (byte)( ((byte)level << 4) | (current & right) );
    }
    else{
      values[index / 2] = (byte)( (current & left) | ((byte)level) );
    }
  }


  static int getValue(int index){
    if((index & 1) == 0){
      return (int)((values[index / 2] & left) >> 4);
    }
    else{
      return (int) (values[index / 2] & right);
    }
  }


  static void read(){
    try {
     FileInputStream input = new FileInputStream("CornerValues");
     input.read(values);
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
     output.write(values);
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
    //System.out.println(c.getEncodedCorners());
    //write();
    read();
    errorCheck();
    //System.out.println(getValue(3));
    /*
    System.out.println(getValue(22987557));
    System.out.println(getValue(36581949));
    System.out.println(getValue(2379456));
    System.out.println(getValue(2375082));
    */
    System.out.printf("Start Time: %s\nEnd Time: %s\n", start, new java.util.Date());
  }

}