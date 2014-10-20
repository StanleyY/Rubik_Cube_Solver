package rubik;

import java.util.Arrays;
import java.nio.file.*;
import java.io.*;
import java.util.*;

class GenerateCorners {

  static byte left = (byte)240;
  static byte right = (byte)15;
  static byte[] values = new byte [44089920];

  static void errorCheck(byte[] values){
    byte x = (byte)15;
    for(int i = 0; i < values.length; i++){
      if((values[i] & right) == x || ((values[i] & left) >> 4) == x) {
        System.out.println("VALUE UNFILLED AT INDEX " + i);
      }
    }
  }

  static void initValues(){
    for (int i = 0; i < values.length; i++){
      values[i] = (byte)255;
    }
    insertValue(new Cube(Cube.GOAL_STATE).getEncodedCorners(), 0);
  }


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
    /*
    System.out.println(getValue(22987557));
    System.out.println(getValue(36581949));
    System.out.println(getValue(2379456));
    System.out.println(getValue(2375082));
    */
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


  public static void main(String[] args){
    //System.out.println(c.getEncodedCorners());
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
  }

}