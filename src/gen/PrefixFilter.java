package gen;

import java.io.File;
import java.io.FilenameFilter;

public class PrefixFilter implements FilenameFilter{
	private String prefix;
	
	public PrefixFilter(String prefix){
		this.prefix=prefix;
	}
	
	public boolean accept(File dir,String name){
		return name.startsWith(prefix);
	}
}