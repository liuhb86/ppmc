package prop;

public class PropOr extends StateProp {
	public StateProp p1;
	public StateProp p2;
	public PropOr (StateProp p1, StateProp p2) {
		this.p1 = p1;
		this.p2 = p2;
	}
	public void print(){
		System.out.println("OR{");
		p1.print();
		System.out.println(",");
		p2.print();
		System.out.println("}");
	}
}
