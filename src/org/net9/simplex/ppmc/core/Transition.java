/**
 * 
 */
package org.net9.simplex.ppmc.core;

/**
 * @author Antonio Filieri
 *
 */
public class Transition {
	private Node[] from;
	private Node to;
	private double prob;
	
	public Transition(Node[] from, Node to, double prob){
		this.from=from;
		this.to=to;
		this.prob=prob;
	}
	
	/**
	 * @return the from
	 */
	public Node[] getFrom() {
		return from;
	}
	/**
	 * @param from the from to set
	 */
	public void setFrom(Node[] from) {
		this.from = from;
	}
	/**
	 * @return the to
	 */
	public Node getTo() {
		return to;
	}
	/**
	 * @param to the to to set
	 */
	public void setTo(Node to) {
		this.to = to;
	}
	/**
	 * @return the prob
	 */
	public double getProb() {
		return prob;
	}
	/**
	 * @param prob the prob to set
	 */
	public void setProb(double prob) {
		this.prob = prob;
	}
	
	public String toString(){
		String ret="(";
		for(int i=0;i<this.from.length;i++){
			ret=ret+this.from[i].getName()+",";
		}
		ret=ret.substring(0, ret.length()-1)+")-"+this.prob+"-"+this.to.toString();
		return ret;
	}
	
}
