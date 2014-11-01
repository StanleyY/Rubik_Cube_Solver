package rubik;

public class CubeNode {
  public Cube cube;
  public int value;
  public String move;

  public CubeNode(){
    this.cube = new Cube();
    this.value = 0;
  }

  public CubeNode(Cube c, int val){
    this.cube = c;
    this.value = val;
  }

  public CubeNode(Cube c, int val, String s){
    this.cube = c;
    this.value = val;
    this.move = s;
  }
}