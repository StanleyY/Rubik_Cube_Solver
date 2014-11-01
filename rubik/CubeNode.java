package rubik;

public class CubeNode {
  public Cube cube;
  public int value;

  public CubeNode(){
    this.cube = new Cube();
    this.value = 0;
  }

  public CubeNode(Cube c, int val){
    this.cube = c;
    this.value = val;
  }
}