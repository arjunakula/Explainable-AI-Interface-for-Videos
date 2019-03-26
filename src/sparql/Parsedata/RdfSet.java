package sparql.Parsedata;

import java.util.ArrayList;

public class RdfSet {

	String rdf_file = "";
	ArrayList<String> obs_id_list = new ArrayList<String>(); 
	ArrayList<String> view_list = new ArrayList<String>();
	public void SetRdf(String string) {
		// TODO Auto-generated method stub
		rdf_file  = string;
		
	}
	public void AddObsId(String string) {
		// TODO Auto-generated method stub
		obs_id_list.add(string);
	}
	public ArrayList<String> GetObsIds() {
		return obs_id_list;
	}
	public void AddView(String view) {
		view_list.add(view);
	}
	public ArrayList<String> getViews() {
		return view_list;
	}
	public String GetRdfFile() {
		return rdf_file; 
	}

}
