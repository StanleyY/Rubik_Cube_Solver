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
  static Cube goal_cube = new Cube(Cube.GOAL_STATE);


  static boolean goalTest(Cube c){
    for (int i = 0; i < 6; i++){
      if (!Arrays.equals(c.cube[i], goal_cube.cube[i])) return false;
    }
    return true;
  }


  static void printCubeInformation(Cube c){
    System.out.println("Cube information");
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
      FileInputStream input = new FileInputStream("CornerValues");
      input.read(corner_values);
      input.close();

      input = new FileInputStream("Edge0Values");
      input.read(edge0_values);
      input.close();

      input = new FileInputStream("Edge1Values");
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
      fs.close();
    }catch(Exception e){
      e.printStackTrace();
    }
  }


  public static void main(String[] args){
    if (args.length < 1) {
      System.out.println("Please Input Filename.");
      System.exit(1);
    }

    Date start = new java.util.Date();
    read();
    readInput(args[0]);

    System.out.printf("Start Time: %s\nEnd Time: %s\n", start, new java.util.Date());
  }

}