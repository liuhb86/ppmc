package mat;

import util.Utils;

public class MatrixIndex implements Comparable<MatrixIndex>{
	public final int row;
	public final int col;

	public MatrixIndex(int i, int j){
		this.row = i;
		this.col = j;
	}

	public int compareTo(MatrixIndex o) {
		if (row<o.row) return -1;
		if (row>o.row) return 1;
		
		if (col<o.col) return -1;
		if (col>o.col) return 1;
		
		return 0;
	}
	
	public boolean equals(Object o) {
		if (o==null || !(o instanceof MatrixIndex)) return false;
		MatrixIndex t = (MatrixIndex) o;
		return (row == t.row && col ==t.col);
	}
	
	public int hashCode() {
		return Utils.hashPair(row, col);
	}
}
