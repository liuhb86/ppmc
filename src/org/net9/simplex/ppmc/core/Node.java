/**
 * 
 */
package org.net9.simplex.ppmc.core;

/**
 * @author Antonio Filieri
 *
 */
public class Node {
	private String name;
	private int id=-1;
	private String description;
	
	public Node(String name,int id){
		this(name,id,"");
	}
	
	public Node(String name,int id,String description){
		this.name=name;
		this.id=id;
		this.description=description;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		if(this.id==-1)
			this.id = id;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return this.name;
	}
}
