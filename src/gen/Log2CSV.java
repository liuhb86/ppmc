/**
 * 
 */
package gen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.TreeMap;

/**
 * @author Antonio Filieri
 *
 */
public class Log2CSV {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		File d = new File("/home/anfi/logs/01");
		String[] entries = d.list();
		if(entries.length==0){
			System.out.println("Empty dir. Done.");
			return;
		}
		long id=System.currentTimeMillis();
		PrintWriter fTime=new PrintWriter(new FileOutputStream(d.getAbsolutePath()+File.separator+id+"t.csv"));
		PrintWriter fSpawn=new PrintWriter(new FileOutputStream(d.getAbsolutePath()+File.separator+id+"s.csv"));
		fTime.println("RID;Nstates;Numeric;Symbolic;Java;Prism;MRMC");
		fSpawn.println("RID;Nstates;Numeric;Symbolic;Java;Prism;MRMC");

		for (int i = 0; i < entries.length; i++) {
			File f = new File(d, entries[i]);
			if(!f.getName().endsWith(".log")){
				continue;
			}
			System.out.println("Processing file "+f.getName()+" ...");
			fTime.print(f.getName().substring(0,f.getName().indexOf(".log"))+";");
			fSpawn.print(f.getName().substring(0,f.getName().indexOf(".log"))+";");
			BufferedReader input=new BufferedReader(new FileReader(f));
			String line;
			while((line=input.readLine())!=null){
				if(line.startsWith("NumStates:    ")){
					String rel=line.substring("NumStates:    ".length());
					int nstates=Integer.parseInt(rel.trim());
					fTime.print(nstates+";");
					fSpawn.print(nstates+";");
					continue;
				}
				if(line.startsWith("Matlab numeric:	")){
					String rel=line.substring("Matlab numeric: ".length());
					rel=rel.substring(rel.indexOf("\t"));
					double time=Double.parseDouble(rel.trim().substring(0,rel.trim().indexOf("\t")));
					rel=rel.trim().substring(rel.trim().indexOf("\t"));
					double spawn=Double.parseDouble(rel);
					fTime.format(Locale.ITALY,"%.0f;",time);
					fSpawn.format(Locale.ITALY,"%.0f;",spawn);
					continue;
				}
				if(line.startsWith("Matlab symbolic:")){
					String rel=line.substring("Matlab symbolic:".length());
					rel=rel.substring(rel.indexOf("\t"));
					double time=Double.parseDouble(rel.trim().substring(0,rel.trim().indexOf("\t")));
					rel=rel.trim().substring(rel.trim().indexOf("\t"));
					double spawn=Double.parseDouble(rel);
					fTime.format(Locale.ITALY,"%.0f;",time);
					fSpawn.format(Locale.ITALY,"%.0f;",spawn);
					continue;
				}
				if(line.startsWith("Java:		")){
					String rel=line.substring("Java:		".length());
					rel=rel.substring(rel.indexOf("\t"));
					double time=Double.parseDouble(rel.trim().substring(0,rel.trim().indexOf("\t")));
					rel=rel.trim().substring(rel.trim().indexOf("\t"));
					double spawn=Double.parseDouble(rel);
					fTime.format(Locale.ITALY,"%.0f;",time);
					fSpawn.format(Locale.ITALY,"%.0f;",spawn);
					continue;
				}
				if(line.startsWith("Prism:		")){
					String rel=line.substring("Prism:		".length());
					rel=rel.substring(rel.indexOf("\t"));
					double time=Double.parseDouble(rel.trim().substring(0,rel.trim().indexOf("\t")));
					rel=rel.trim().substring(rel.trim().indexOf("\t"));
					double spawn=Double.parseDouble(rel);
					fTime.format(Locale.ITALY,"%.0f;",time);
					fSpawn.format(Locale.ITALY,"%.0f;",spawn);
					continue;
				}
				if(line.startsWith("MRMC:		")){
					String rel=line.substring("MRMC:		".length());
					rel=rel.substring(rel.indexOf("\t"));
					double time=Double.parseDouble(rel.trim().substring(0,rel.trim().indexOf("\t")));
					rel=rel.trim().substring(rel.trim().indexOf("\t"));
					double spawn=Double.parseDouble(rel);
					fTime.format(Locale.ITALY,"%.0f",time);
					fSpawn.format(Locale.ITALY,"%.0f",spawn);
					fTime.println();
					fSpawn.println();
					continue;
				}
			}
		}
		fTime.close();
		fSpawn.close();
		System.out.println("Done.");
	}

}
