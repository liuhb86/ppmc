package mat;

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
}
