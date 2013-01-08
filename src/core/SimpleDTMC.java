package core;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.io.DataOutputStream;

import util.Utils;

import mat.SmartMatrix;
import mat.SparseMatrix;

/**
 * @author Hongbo Liu <liuhongbo@pku.edu.cn, liuhb86@gmail.com>
 * @author Antonio Filieri 
 */
public class SimpleDTMC implements DTMC {
	public SmartMatrix trans;
	public int currentState;
	public int numTransients;

	
	public SimpleDTMC(double[][] transitionMatrix,int numTransients){
		this(transitionMatrix, new SparseMatrix<String>(), numTransients,0);
	}
	
	public SimpleDTMC(double[][] transitionMatrix, SparseMatrix<String> var,int numTransients,int initial){
		this.currentState=initial;
		this.numTransients=numTransients;
		this.trans = new SmartMatrix(transitionMatrix,var);
	}
	
	/* (non-Javadoc)
	 * @see core.DTMC#getCurrentNode()
	 */
	@Override
	public Node getCurrentNode() {
		return new Node("n"+this.currentState,this.currentState);
	}

	/* (non-Javadoc)
	 * @see core.DTMC#isAbsorbed()
	 */
	@Override
	public boolean isAbsorbed() {
		return (this.currentState>=this.numTransients);
	}

	/* (non-Javadoc)
	 * @see core.DTMC#move(double)
	 */
	@Override
	public void move(double dice) {
		if(this.isAbsorbed()) return;
		double cum=0.0;
		for(int a=0;a<trans.getDimC();a++){
			if(dice<cum+trans.getNumericEntry(currentState, a)){
				this.currentState=a;
				break;
			}
			cum+=trans.getNumericEntry(currentState, a);
		}
	}

	/* (non-Javadoc)
	 * @see core.DTMC#reset()
	 */
	@Override
	public void reset() {
		this.currentState=0;
	}

	@Override
	public Node[] getNodeSet() {
		ArrayList<Node> ret = new ArrayList<Node>();
		for(int i=0;i<trans.getDim();i++){
			ret.add(new Node("n"+i,i));
		}
		Node[] n =new Node[ret.size()];
		return ret.toArray(n);
	}

	@Override
	public void loadFile(String path) throws IOException {
		DataInputStream fIn = new DataInputStream(new FileInputStream(path));
		char c = fIn.readChar();
		if(c!='S') throw new IOException(path+" does not contain a SimpleDTMC");
		this.currentState=fIn.readInt();
		this.numTransients=fIn.readInt();
		int size=fIn.readInt();
		this.trans = new SmartMatrix(new double[size][size]);
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				trans.getBase()[i][j]=fIn.readDouble();
			}
		}
		fIn.close();
	}

	@Override
	public void toFile(String path) throws IOException {
		DataOutputStream fOut=new DataOutputStream(new FileOutputStream(path));
		fOut.writeChar('S');
		fOut.writeInt(this.currentState);
		fOut.writeInt(this.numTransients);
		fOut.writeInt(trans.getDim());
		for(int i=0;i<trans.getDimR();i++){
			for(int j=0;j<trans.getDimC();j++){
				fOut.writeDouble(trans.getNumericEntry(i, j));
			}
		}
		fOut.close();	
	}
	
	public void writeTo(Object printer) {
		PrintWriter writer = Utils.getWriter(printer);
		if (writer == null) return;
		writer.println("Transitions:");
		trans.writeTo(writer);
		writer.println("Initial:");
		writer.print('\t');
		writer.println(this.currentState);
		writer.flush();
	}
	
	public void toTransTable(OutputStream os) {
		PrintWriter fOut=new PrintWriter(os);
		for(int i=0;i<trans.getDimR();i++){
			for(int j=0;j<trans.getDimC();j++){
				fOut.print(trans.getEntry(i, j)+"\t");
			}
			fOut.println();
		}
		fOut.close();
	}
	public void toTransTable(String path) throws FileNotFoundException{
		FileOutputStream fOut = new FileOutputStream(path);
		toTransTable(fOut);
		
	}
}
