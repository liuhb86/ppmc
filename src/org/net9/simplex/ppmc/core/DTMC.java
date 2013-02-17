/**
 * 
 */
package org.net9.simplex.ppmc.core;

import java.util.BitSet;
import java.util.HashMap;

import org.net9.simplex.ppmc.mat.SmartMatrix;

/**
 * @author Hongbo Liu
 *
 */
public interface DTMC {
	public int size();
	public SmartMatrix getTrans();
	public int getInitState();
	public HashMap<String, BitSet> getAP();
}
