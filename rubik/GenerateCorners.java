package rubik;

import java.util.Arrays;
import java.nio.file.*;
import java.io.*;
import java.util.*;

class GenerateCorners {

  static byte left = (byte)240;
  static byte right = (byte)15;
  //static byte[] values = new byte [44089920];
  static byte[] values = new byte [50];

  static void errorCheck(byte[] values){
    byte x = (byte)15;
    for(int i = 0; i < values.length; i++){
      if((values[i] & right) == x || ((values[i] & left) >> 4) == x) {
        System.out.println("VALUE UNFILLED AT INDEX " + i);
      }
    }
  }


  static void generateValues(){
    Cube c = new Cube(Cube.GOAL_STATE);
    Queue<Cube> q = new LinkedList<Cube>();
    Queue<Cube> next = new LinkedList<Cube>();
    next.add(c);
    int level = 0;
    while(!next.isEmpty() && level < 2){
      System.out.println("next queue size: " + next.size());
      q = next;
      next = new LinkedList<Cube>();
      while(!q.isEmpty()){
        Cube current = q.poll();
        current.printCube();
        System.out.println(current.getEncodedCorners());
        for(int face = 0; face < 6; face++){
          for(int i = 1; i < 4; i++){
            next.add(current.rotate(face, i));
          }
        }
      }
      level++;
    }
  }

  public static void main(String[] args){
    //System.out.println(c.getEncodedCorners());
    try {
     FileOutputStream output = new FileOutputStream("CornerValues");
     values[0] = (byte)205;
     values[1] = (byte)255;
     generateValues();
     output.write(values);
     output.close();
    } catch (java.io.IOException e) {
     e.printStackTrace();
     System.exit(1);
    }
  }

}