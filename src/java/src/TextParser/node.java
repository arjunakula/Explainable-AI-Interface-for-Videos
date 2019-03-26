
package TextParser;
import java.util.Vector;


//each node in the parsetree
public class node
{
	String name;	//name of the node
	Vector pointers;	//pointers to other nodes
	public node()
	{
		pointers=new Vector();
	}//endnode constructor
	public node(String s)
	{
		name=s;
		pointers=new Vector();
	}//end node constructor
}//end node class

