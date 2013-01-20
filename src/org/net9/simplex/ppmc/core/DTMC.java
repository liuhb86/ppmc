/**
 * 
 */
package org.net9.simplex.ppmc.core;

import java.io.IOException;

/**
 * @author Antonio Filieri
 *
 */
public interface DTMC {
	public void reset();
	
	public boolean isAbsorbed();
	
	public void move(double dice);
	
	public void toFile(String path) throws IOException;
	
	public void loadFile(String path) throws IOException;
}
