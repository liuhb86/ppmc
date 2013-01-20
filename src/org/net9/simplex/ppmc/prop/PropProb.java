package org.net9.simplex.ppmc.prop;

import java.util.HashMap;

import org.net9.simplex.ppmc.util.BinaryPredicator;

public class PropProb extends StateProperty {
	public PathProperty p1;
	String strComparator;
	BinaryPredicator<Double, Double> comparator;
	double prob;
	static final HashMap<String, BinaryPredicator<Double, Double>> 
		comparatorMap =  new HashMap<String, BinaryPredicator<Double, Double>>();
	
	public PropProb(PathProperty path, String comparator, double prob) {
		this.p1 = path;
		this.strComparator = comparator;
		this.prob = prob;
		this.comparator = comparatorMap.get(comparator);
	}

	@Override
	public void accept(PropertyVisitor visitor) {
		visitor.visit(this);
	}
	
	{
		comparatorMap.put("<", new BinaryPredicator<Double,Double>(){
			public boolean execute(Double d1, Double d2) {
				return d1<d2;
			}
		});
		comparatorMap.put("<=", new BinaryPredicator<Double,Double>(){
			public boolean execute(Double d1, Double d2) {
				return d1<=d2;
			}
		});
		comparatorMap.put(">", new BinaryPredicator<Double,Double>(){
			public boolean execute(Double d1, Double d2) {
				return d1>d2;
			}
		});
		comparatorMap.put(">=", new BinaryPredicator<Double,Double>(){
			public boolean execute(Double d1, Double d2) {
				return d1>=d2;
			}
		});
	}
}
