/**
 * 
 */
package core;

import java.io.IOException;

/**
 * @author Antonio Filieri
 *
 */
public interface DTMC {
	public void reset();
	
	public boolean isAbsorbed();
	
	public Node getCurrentNode();
	
	public void move(double dice);
	
	public Node[] getNodeSet();
	
	public void toFile(String path) throws IOException;
	
	public void loadFile(String path) throws IOException;
}
