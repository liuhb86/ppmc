package org.net9.simplex.ppmc.core;

import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import org.lsmp.djep.xjep.XJep;
import org.net9.simplex.ppmc.mat.SmartMatrix;
import org.net9.simplex.ppmc.mat.SparseMatrix;

public class GeneralDTMC implements DTMC {

	SmartMatrix trans;
	int initialState;
	HashMap<String, BitSet> ap;
	public SimpleDTMC absorbingTrans;
	public int[] reorderMap;
	public BSCC[] bsccMap;
	public HashSet<BSCC> bsccSet = new HashSet<BSCC>();
	public BSCC[] ergoric;

	public GeneralDTMC(SmartMatrix trans,int initial, HashMap<String, BitSet> ap){
		this.initialState = initial;
		this.ap = ap;
		this.trans = trans;
		this.init();
	}
	
	void tarjan() {
		Stack<Integer> stack = new Stack<Integer>();
		Stack<Integer> varStack = new Stack<Integer>();
		Stack<Integer> sStack = new Stack<Integer>();
		HashSet<Integer>  sSet = new HashSet<Integer>();
		int[] label = new int[trans.getDim()];
		int[] group = new int[trans.getDim()];
		boolean [] visited = new boolean[trans.getDim()];
		for(int i=0;i<visited.length;++i) visited[i]=false;
		int curLabel = 0;
		if (trans.getDim()>0){
			stack.push(0);
			varStack.push(null);
		}
		
recursive:	
		while(!stack.empty()){
			int u = stack.peek();
			int v;
			Integer nv = varStack.peek();
			if (nv==null) {
				visited[u] = true;
				label[u]=curLabel;
				group[u]=curLabel;
				++curLabel;
				sStack.push(u);
				sSet.add(u);
				v=0;
			} else {
				v = nv;
				if (group[v]<group[u]) group[u]=group[v];
				v = v+1;
			}
			for (;v<trans.getDim();++v){
				if (trans.getNumericEntry(u, v)==0) continue;
				if (!visited[v]){
					stack.push(v);
					varStack.pop();
					varStack.push(v);
					varStack.push(null);
					continue recursive;
				} else if (sSet.contains(v)) {
					if (label[v]<group[u]) {
						group[u]=label[v];
					}
				}
			}
			if (label[u]==group[u]){
				BSCC bscc = new BSCC();
				do {
					v = sStack.pop();
					sSet.remove(v);
					bscc.states.add(v);
					bsccMap[v]=bscc;
				} while(u!=v);
				bsccSet.add(bscc);
			}
			stack.pop();
			varStack.pop();
		}
	}
	
	void buildAbsorbingTrans(){
		int nErgoricStates = 0;
		for(BSCC bscc:ergoric){
			nErgoricStates+=bscc.states.size();
		}
		int nTransients = this.size()-nErgoricStates;
		int nStates = nTransients +this.ergoric.length;
		this.reorderMap = new int[this.size()];
		int ni=0;
		for(int i=0;i<this.size();++i){
			if (bsccMap[i].isErgoric()) { 
				reorderMap[i] = nTransients + bsccMap[i].index;
			} else {
				reorderMap[i] = ni;
				++ni;
			}
		}
		XJep jep = new XJep();
		double[][] transition = new double[nStates][nStates];
		SparseMatrix<String> vars = new SparseMatrix<String>();
		for(int i=0;i<nStates;++i)
			for(int j=0;j<nStates;++j)
				transition[i][j]=0;
		for(int i=0;i<this.size();++i){
			int s = reorderMap[i];
			if (s>=nTransients) continue;
			for(int j=0;j<this.size();++j){
				int t=reorderMap[j];
				if (t>=nTransients){
					if (trans.isSymbolicEntry(i, j)){
						if (vars.getN(s, t) == null){
							vars.put(s, t, trans.getEntry(i, j, true));
						} else {
							vars.put(s, t, vars.get(s, t)+"+"+trans.getEntry(0, 0, false));
						}
					} else {
						transition[s][t]+=trans.getNumericEntry(i, j);
					}
				} else {
					if(trans.isSymbolicEntry(i, j)){
						transition[s][t] = trans.getNumericEntry(i, j);
						vars.put(s, t, trans.getSymbolicEntry(i, j));
					} else {
						transition[s][t]=trans.getNumericEntry(i, j);
					}
				}
			}
			for(int t=nTransients;t<nStates;++t){
				if (vars.getN(s, t)!=null){
					String sp =null;
					try {
						sp = jep.toString(jep.simplify(jep.parse(
								vars.get(s, t)+"+"+transition[s][t])));
					} catch (org.nfunk.jep.ParseException e) {
						e.printStackTrace();
					}
					vars.put(s, t, sp);
				}
			}
		}
		this.absorbingTrans = new SimpleDTMC(transition,vars, nTransients, reorderMap[this.initialState],new HashMap<String, BitSet>());
	}
	void init() {
		this.bsccMap = new BSCC[trans.getDim()];
		tarjan();
		HashSet<BSCC> visited = new HashSet<BSCC>();
		ArrayList<BSCC> ergoric = new ArrayList<BSCC>();
		for(BSCC bscc:bsccMap){
			if (visited.contains(bscc)) continue;
			visited.add(bscc);
			for(int s:bscc.states){
				for(int t=0;t<this.size();++t){
					if (trans.getNumericEntry(s, t)!=0){
						bscc.next.add(bsccMap[t]);
					}
				}
			}
			bscc.next.remove(bscc);
			if (bscc.next.size()==0) {
				ergoric.add(bscc);
			}
		}

		this.ergoric = (BSCC[]) ergoric.toArray(new BSCC[ergoric.size()]);
		for(int i=0;i<this.ergoric.length;++i) {
			this.ergoric[i].index = i;
		}
		this.buildAbsorbingTrans();
	}

	@Override
	public int size() {
		return trans.getDim();
	}
	
	public static GeneralDTMC loadFrom(Reader in) throws ParseException{
		SimpleDTMC dtmc = SimpleDTMC.loadFrom(in);
		return new GeneralDTMC(dtmc.trans, dtmc.currentState, dtmc.ap);
	}

	@Override
	public SmartMatrix getTrans() {
		return this.trans;
	}

	@Override
	public int getInitState() {
		return this.initialState;
	}

	@Override
	public HashMap<String, BitSet> getAP() {
		return this.ap;
	}

}