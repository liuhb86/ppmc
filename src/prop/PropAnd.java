package prop;

public class PropAnd extends StateProp {
	public StateProp p1;
	public StateProp p2;
	public PropAnd (StateProp p1, StateProp p2) {
		this.p1 = p1;
		this.p2 = p2;
	}
	public void print(){
		System.out.println("AND{");
		p1.print();
		System.out.println(",");
		p2.print();
		System.out.println("}");
	}
}
