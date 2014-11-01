package rubik;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class CubeSpeedTest {
    public static void main(String[] args){
      long startTime = System.nanoTime();
      Cube c = new Cube(Cube.GOAL_STATE);
      Stack<Cube> s = new Stack<Cube>();
      for(int j = 0; j < 11; j++){
        for(int face = 0; face < 6; face++){
          for(int i = 1; i < 4; i++){
            Cube node = c.rotate(face, i);
            s.push(node);
          }
        }
        System.out.println(s.size());
      }
      long elapsedTime = System.nanoTime() - startTime;
      System.out.println("NanoSeconds Taken: " + elapsedTime);
      System.out.println("Seconds Taken: " + TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS));
    }
}