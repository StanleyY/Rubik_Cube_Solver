package rubik;

/**
 * A node class to be used in conjunction with CubeNodeComparator in a PriorityQueue.
 *
 * @author     Stanley Yang
 * @version    1.0
 * @since      2014-11-04
 */
public class CubeNode {
  /**
   * Public variables of a CubeNode.
   * cube is the Cube object of this CubeNode.
   * value is the f value of this cube and used for comparisions in the IDA* search.
   * move is a String containing the moves used to get to this CubeNode.
   */
  public Cube cube;
  public int value;
  public String move;

  /**
   * Basic constructor for CubeNode.
   */
  public CubeNode(){
    this.cube = new Cube();
    this.value = 0;
  }

  /**
   * Creates a CubeNode with a given Cube and f value.
   *
   * @param c     A Cube object.
   * @param val   f value.
   */
  public CubeNode(Cube c, int val){
    this.cube = c;
    this.value = val;
  }

  /**
   * Creates a CubeNode with a given Cube, f value, and moves used.
   *
   * @param c     A Cube object.
   * @param val   f value.
   * @param s     The moves used to get to this CubeNode.
   */
  public CubeNode(Cube c, int val, String s){
    this.cube = c;
    this.value = val;
    this.move = s;
  }
}