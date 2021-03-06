package org.net9.simplex.ppmc.mat;

import java.util.TreeMap;

/**
 * @author Hongbo Liu <liuhongbo@pku.edu.cn, liuhb86@gmail.com>
 */
public class SparseMatrix<T> implements Cloneable{
	TreeMap<MatrixIndex, T> data;
	T defaultValue;
	public SparseMatrix(){
		this(null);
	}
	public SparseMatrix(T defaultValue){
		data = new TreeMap<MatrixIndex, T>();
		this.defaultValue = defaultValue;
	}
	public TreeMap<MatrixIndex, T> getMap() {
		return data;
	}
	public T getN(int r, int c) {
		return data.get(new MatrixIndex(r,c));
	}
	public T get(int r,int c) {
		T v = getN(r,c);
		return (v==null) ? defaultValue : v;
	}
	public void put(int r,int c, T v) {
		data.put(new MatrixIndex(r,c), v);
	}
	public void put(MatrixIndex i, T v) {
		data.put(i, v);
	}
	public void remove(int r, int c){
		data.remove(new MatrixIndex(r,c));
	}
	public int size() {
		return data.size();
	}

	public SparseMatrix<T> clone() {
		SparseMatrix<T> c = new SparseMatrix<T>(defaultValue);
		c.data.putAll(data);
		return c;
	}
}
