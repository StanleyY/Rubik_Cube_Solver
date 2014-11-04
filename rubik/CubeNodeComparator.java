package rubik;

import java.util.Comparator;

/**
 * An implementation of the Comparator class for CubeNode.
 *
 * @author     Stanley Yang
 * @version    1.0
 * @since      2014-11-04
 */
public class CubeNodeComparator implements Comparator<CubeNode>{
    /** 
     * Returns a negative integer if CubeNode a is less than CubeNode b,
     * zero if equal, positive integer a is greater.
     */
    @Override
    public int compare(CubeNode a, CubeNode b){
        return a.value - b.value;
    }
}