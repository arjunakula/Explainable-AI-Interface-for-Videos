package TextParser;
import java.util.Vector;



//each rule in a chart
public class rules
{
	int dot;		//position of dot
	int start;		//starting position
	int end;		//ending position
	int rno;		//rule number
	Vector rule;	//vector to store the rule
	Vector pointer;	//vector to store the pointer to previous rule
	char ch;
	public rules()	//constructor
	{
		rule=new Vector();
		pointer=new Vector();

	}//endrules constructor
	
	public rules(int i,int j,int k, String s,char c)	//constructor
	{
		String sep[]=s.split(" ");
		dot=i;
		start=j;
		end=k;
		rule=new Vector();
		pointer=new Vector();
		for(int m=0;m<sep.length;m++)
		{
			rule.addElement(sep[m]);

		}//endfor
		ch=c;
	}//endrules constructor
	public rules(int i,int j,int k,Vector vv,Vector pp,char c)	//constructor
	{
		dot=i;
		start=j;
		end=k;
		rule=vv;
		pointer=pp;
		ch=c;
	}//endrules constructor
}//end rules class

