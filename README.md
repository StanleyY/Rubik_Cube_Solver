Rubik_Cube_Solver
=================

Rubik's Cube solver using Korf's Algorithm.  
Solves cubes that require 15 moves in about 2 hours.  
Cubes that require 14 moves takes a few minutes.

Input
=========
The cube is inputted as if it was unfolded with Yellow being the front face.  
Green to the left and Blue to the right.  
See GOAL_STATE for an example.

Running
=========
- Compile the everything in the rubik folder.
- Run GenerateTables to create the heuristic tables.  This should take about an hour or so on a reasonably modern computer.
- Run Solve with the location of the input cube file passed in as a commandline argument.  
 `java Solve Location_of_File`
