
package TextParser;
//this program implements the Earley parser. This program reads the file containing the grammer
//and the sentence to be parsed and generates the Earley chart and the parse tree
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import java.net.URL;
import java.io.IOException;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.tree.TreePath;
import java.util.*;
import java.io.*;
import java.lang.*;

import java.awt.Rectangle;



// class Earley extends JPanel
public class Earley 
{
	public Hashtable grammerhash;		//datastructure to store the grammer
	public chart charts[];				//datastructure to store the charts
	public chart lexicon[];			//the lexicon
	public String sentencefile1[];		//sentence to be parsed
	
	//private static int counter;
	//private static int counter1;
	
	private int counter;
	private int counter1;
	
	node ptree;							//parsetree
//	private JEditorPane Pane;
//	private JTree tree;
	
	// static int found;
	public int found;
	String sentence;
	
	final private boolean bDebug = false;  
	
	public Earley(int length,String grammerfile,String sentencefile, String chart_op_txt_file, boolean bVerbose) throws Exception
	{
		// super(new GridLayout(1,0));  // moved to textparserapp.java
		
		grammerhash=new Hashtable();
		lexicon=new chart[length];
		for(int kk=0;kk<length;kk++)
		{
			lexicon[kk]=new chart();
		}//endfor
		charts=new chart[length+1];
		for(int kk=0;kk<=length;kk++)
		{
			charts[kk]=new chart();
		}//endfor
		sentencefile1=new String[length];
		sentence=sentencefile;
		counter=-1;
		counter1=0;
		found=0;
		EarleyParse(grammerfile, sentencefile, chart_op_txt_file, bVerbose);	
	
	}


	//function for early parser
	void EarleyParse(String grammerfile,String sentencefile, String chart_op_txt_file, boolean bVerbose) throws Exception
	{
		FileReader fr = new FileReader(grammerfile);
		FileReader fr1 = new FileReader(grammerfile);
		BufferedReader br1 = new BufferedReader(fr);
		BufferedReader br2 = new BufferedReader(fr1);
		String grammer=null,tgrammer=null,grammer1=null,tgrammer1=null;
		Vector terminals=new Vector();			//vector to store terminals
		Vector nonterminals=new Vector();		//vector to store nonterminals
		Vector pos=new Vector();				//vector to store parts of speech
		sentencefile1=sentencefile.split(" ");	//split the input sentence
		int length=sentencefile1.length;		//number of words in the sentence


		//function for early parser
		while((tgrammer=br1.readLine())!=null)   	//read the grammer file
		{
			grammer=tgrammer.replace('|','&');
			String tempgrammer[]=grammer.split(" ");
			if(nonterminals.contains(tempgrammer[0]))
			{
			}//endif
			else
			{
				nonterminals.addElement(tempgrammer[0]);  	//add to nonterminals
			}//endelseif
			

			String tempgrammer1[]=grammer.split(" -> ");
			
			Vector grammerrule=new Vector();
			
			//mw
			if (bDebug)
			{	System.out.println("grammar " + grammer);			
				System.out.println("tempgrammer1[0] " + tempgrammer1[0]);
				System.out.println("tempgrammer1[1] " + tempgrammer1[1]);
			}
			
			if(tempgrammer1[1].indexOf('&')!=-1)
			{
				String tempgrammer2[]=tempgrammer1[1].split(" & ");
				for(int u=0;u<tempgrammer2.length;u++)
				{
					grammerrule.addElement(tempgrammer2[u]);
				}//endfor
				grammerhash.put(tempgrammer1[0],grammerrule);		//extract the grammer
			}//endif
			else
			{
				grammerrule.addElement(tempgrammer1[1]);
				grammerhash.put(tempgrammer1[0],grammerrule);
			}//endelseif
		}//endwhile



		while((tgrammer1=br2.readLine())!=null)			//read the file
		{

			grammer1=tgrammer1.replace('|','&');
			String tempgrammer3[]=grammer1.split(" ");
			for(int u=1;u<tempgrammer3.length;u++)
			if(nonterminals.contains(tempgrammer3[u]))
			{
			}//endif
			else
			{
				if((tempgrammer3[u].equals("->"))|(tempgrammer3[u].equals("&")))
				{
				}//endif
				else
				{
					terminals.addElement(tempgrammer3[u]);
				}//endelseif
			}//endelseif
		}//endwhile


		
		for(int s=0;s<length;s++)		//construct the lexicon
		{
			for(int t=0;t<nonterminals.size();t++)
			{
				Vector v1=(Vector)grammerhash.get(nonterminals.elementAt(t));
				
				
				if (bDebug)
				{	// debug
					System.out.println("DEBUG nonterminals.elementAt(t) "+ nonterminals.elementAt(t));
					System.out.println("DEBUG v1 "+ v1);	
					System.out.println("DEBUG sentencefile1 "+ sentencefile1.length);				
					System.out.println("DEBUG s "+ s);
					System.out.println("DEBUG length "+ length);
				}
				
				if(v1.indexOf(sentencefile1[s])==-1)
				{
				}//endif
				else
				{
					//System.out.println(nonterminals.elementAt(t)+"   "+s);
					lexicon[s].table.addElement(nonterminals.elementAt(t));
					//t=nonterminals.size();
				}//endelseif
			}//endfor
		}//endfor



		for(int i=0;i<nonterminals.size();i++)		//construct part of speech
		{
			int flag=0;
			Vector v1=(Vector)grammerhash.get(nonterminals.elementAt(i));
			for(int j=0;j<v1.size();j++)
			{
				String tempstr[]=((String)v1.elementAt(j)).split(" ");
				if(tempstr.length!=1)
				{
					flag=1;
				}//endif
				for(int k=0;k<tempstr.length;k++)
				{
					if((tempstr[k].equals("->"))|(tempstr[k].equals("&")))
					{
					}//endif
					else
					{
						if(terminals.contains(tempstr[k]))
						{
						}//endif
						else
						{
							flag=1;
						}//endelseif
					}//endelseif
				}//endfor
			}//endfor
			if(flag==0)
			{
				pos.addElement(nonterminals.elementAt(i));
			}//endif
		}//endfor
		for(int i=0;i<pos.size();i++)
		{
			nonterminals.removeElement(pos.elementAt(i));
			grammerhash.remove(pos.elementAt(i));
		}//endfor

		if (bDebug) 
		{
			for(int i=0;i<pos.size();i++)
			{
				System.out.println("pos " + pos.elementAt(i));						
			}
			for(int i=0;i<nonterminals.size();i++)
			{
				System.out.println("nonterminals " + nonterminals.elementAt(i));						
			}
			for(int i=0;i<terminals.size();i++)
			{
				System.out.println("terminals " + terminals.elementAt(i));						
			}
		}

		enqueue(new rules(1,0,0,"@ S",'D'),0,0);		//enqueue dummy street

		for(int x=0;x<(length+1);x++)				//implement the parser
		{
			for(int z=0;z<charts[x].table.size();z++)
			{
				rules temprule=(rules)charts[x].table.elementAt(z);
				if(temprule.dot!=temprule.rule.size())
				{
					// debug 
					int itmp = nonterminals.indexOf((String)temprule.rule.elementAt(temprule.dot));
					
					if (bDebug) 
					{
						System.out.println("\tfind non-terminals rule itmp" + itmp);
					}
							
					if((nonterminals.indexOf((String)temprule.rule.elementAt(temprule.dot)))!=-1)
					{
						predictor(temprule); //call the predictor
					}//endif
					else
					{												;
						if(x!=length)
						{
							//System.out.println("yyyyyyyyyyyyyyy"+"  "+x+"   "+z);
							scanner(temprule); // call the scanner
						}//endif
					}//endelseif
				}//endif
				else
				{
					if(charts[x].table.size()==1)
					{
						counter=counter+1;
						temprule.rno=counter;
					}//endif
					completor(temprule);		//call the completor
				}//endelseif
			}//endfor
		}//endfor

		//display the chart
		displaychart(chart_op_txt_file, bVerbose);
		

		//check if parsing is successful
		int ruleno=-1;
		for(int y=0;y<charts[charts.length-1].table.size();y++)
		{
			rules temprules=(rules)(charts[charts.length-1].table.elementAt(y));
			if((((String)temprules.rule.elementAt(0)).equals("S"))&(temprules.dot==temprules.rule.size()))
			{
				ruleno=temprules.rno;
				y=charts[charts.length-1].table.size();

			}//endif
		}//endfor
		if(ruleno!=-1)
		{
			found=1;
			ptree=tree(ruleno);		//generate the parsetree
		}//endif
	}//end EarleyParse


	public void enqueue(rules r,int i,int check)	//insert rule in the chart
	{
		String tempstr1=null;
		for(int y=0;y<r.rule.size();y++)
		{
			if(y==0)
			{
				tempstr1=(String)r.rule.elementAt(y);
			}//endif
			else
			{
				tempstr1=tempstr1+" "+((String)r.rule.elementAt(y));
			}//endelseif
		}//endfor
		int flag=0;
		String tempstr3=null;
		for(int t=0;t<charts[i].table.size();t++)	//check whether the rule already exists in the chart
		{
			rules temp=(rules)charts[i].table.elementAt(t);
			String tempstr2=null;

			for(int y=0;y<temp.rule.size();y++)
			{
				if(y==0)
				{
					tempstr2=(String)temp.rule.elementAt(y);
					tempstr3=(String)temp.rule.elementAt(y);
				}//endif
				else
				{
					tempstr2=tempstr2+" "+((String)temp.rule.elementAt(y));
				}//endelseif
			}//endfor
			if((r.dot==temp.dot)&&(tempstr1.equals(tempstr2)))
			{
				flag=1;
				if(check==2)
				{
					if(temp.pointer.size()==r.pointer.size())
					{
						int flag1=0;
						for(int k=0;k<temp.pointer.size();k++)
						{
							if(((Integer)temp.pointer.elementAt(k)).intValue()!=((Integer)r.pointer.elementAt(k)).intValue())
							{
								flag1=1;
							}//endif
						}//endfor
						if(flag1==1)
						{
							flag=0;
						}//endif
					}//endif
				}//endif
				t=charts[i].table.size();
			}//endif
		}//endfor
		if(flag==0)
		{
			if((check==0)|(check==2))
			{
				counter=counter+1;
				r.rno=counter;
			}//endif
			charts[i].table.addElement(r);
		}//endif
	}//end enqueue


	public void predictor(rules temprule)		// the predictor
	{
		Vector v2=(Vector)grammerhash.get(temprule.rule.elementAt(temprule.dot));
		for(int i=0;i<(v2.size());i++)
		{
			rules temprule1=new rules(1,temprule.end,temprule.end,((String)temprule.rule.elementAt(temprule.dot))+" "+((String)v2.elementAt(i)),'P');
			enqueue(temprule1,temprule.end,0);
		}//endfor
	}//end predictor


	public void scanner(rules temprule)			// the scanner
	{
		for(int i=0;i<lexicon[temprule.end].table.size();i++)
		{
			if(((String)temprule.rule.elementAt(temprule.dot)).equals((String)lexicon[temprule.end].table.elementAt(i)))
			{
				//System.out.println("ttttttttttttttttttttttt"+temprule.end+lexicon[temprule.end].table.size());
				enqueue(new rules(2,temprule.end,temprule.end+1,((String)lexicon[temprule.end].table.elementAt(i))+" "+sentencefile1[temprule.end],'S'),temprule.end+1,1);
			}//endif
		}
	}//end scanner


	public void completor(rules temprule)  // the completor
	{
		for(int t=0;t<charts[temprule.start].table.size();t++)
		{
			rules temprule1=(rules)charts[temprule.start].table.elementAt(t);
			if(temprule1.rule.size()!=temprule1.dot)
			if(((String)temprule.rule.elementAt(0)).equals((String)temprule1.rule.elementAt(temprule1.dot)))
			{
				Vector v2=(Vector)temprule1.pointer.clone();
				v2.addElement(new Integer(temprule.rno));
				enqueue(new rules(temprule1.dot+1,temprule1.start,temprule.end,temprule1.rule,v2,'C'),temprule.end,2);
			}//endif
		}//endfor
	}//end completor

	//display the chart
	public void displaychart(String chart_op_txt_file, boolean bVerbose) throws Exception
	{
		int chartlength=charts.length;
		// FileWriter f1=new FileWriter("chart.txt",false);
		FileWriter f1=new FileWriter(chart_op_txt_file,false);
		String str2="Earley chart for = "+"("+sentence+")"+"\n";
		char buffer1[]=new char[str2.length()];
		str2.getChars(0,str2.length(),buffer1,0);
		f1.write(buffer1);
		for(int x=0;x<chartlength;x++)
		{
			if (bVerbose)
			{
				System.out.println("CHART============== "+x);
			}
			String str1="CHART=============="+x+"\n";
			char buffer2[]=new char[str1.length()];
			str1.getChars(0,str1.length(),buffer2,0);
			f1.write(buffer2);
			for(int y=0;y<charts[x].table.size();y++)
			{

				rules trules=(rules)charts[x].table.elementAt(y);
				String str=null;
				str="S"+trules.rno+"  ";
				for(int a1=str.length();a1<=8;a1++)
				{
					str=str+" ";
				}
				str=trules.rule.elementAt(0)+" "+"->";
				for(int z=1;z<trules.dot;z++)
				{
					str=str+" "+trules.rule.elementAt(z);
				}//endfor
				str=str +".";
				for(int z=trules.dot;z<trules.rule.size();z++)
				{
					str=str+" "+trules.rule.elementAt(z);
				}//endfor
				for(int a1=str.length();a1<=35;a1++)
				{
					str=str+" ";
				}
				str=str+" ["+trules.start+","+trules.end+"]"+"   "+trules.rno+"    ";
				for(int a1=str.length();a1<=45;a1++)
				{
					str=str+" ";
				}
				str=str+"[";
				if(trules.pointer.size()>0)
				{
					for(int z=0;z<trules.pointer.size();z++)
					{
						str=str+"S"+trules.pointer.elementAt(z);
						if(z!=trules.pointer.size()-1)
						{
							str=str+",";
						}
					}//endfor
				}//endif
				str=str+"]";
				for(int a1=str.length();a1<=67;a1++)
				{
					str=str+" ";
				}
				if(trules.ch=='D')
				{
					str=str+"Dummy Start State";
				}
				else
				{
					if(trules.ch=='C')
					{
						str=str+"Completer";
					}
					else
					{
						if(trules.ch=='S')
						{
						str=str+"Scanner";
						}
						else
						{
							if(trules.ch=='P')
							{
								str=str+"Predictor";
							}
						}
					}
				}
				str=str+"\n";
				
				if (bVerbose)
				{
					System.out.print(str);
				}
				char buffer[]=new char[str.length()];
				str.getChars(0,str.length(),buffer,0);
				f1.write(buffer);
			}//endfor
		}//endfor
		f1.close();
	}//end displaychart


	//generate the data structure to store the tree
	public node tree(int rlno)
	{
		int length=charts.length;
		for(int x=0;x<(length);x++)
		{
			for(int z=0;z<charts[x].table.size();z++)
			{
				rules temprule=(rules)charts[x].table.elementAt(z);
				if(temprule.rno==rlno)
				{
					node n=new node((String)temprule.rule.elementAt(0));
					int flag=0;
					for(int y=temprule.pointer.size()-1;y>=0;y--)
					{
						flag=1;
						n.pointers.addElement(tree(((Integer)temprule.pointer.elementAt(y)).intValue()));
					}//endfor
					if(flag==0)
					{

						n.pointers.addElement(new node((String)temprule.rule.elementAt(1)));
					}//endif
					return n;
				}//endif
			}//endfor
		}//endfor
		return new node();
	}//end tree

}
