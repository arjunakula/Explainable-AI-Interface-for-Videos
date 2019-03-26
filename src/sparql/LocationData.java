package sparql;

import sparql.LocationData.LocationType;
import sparql.MseeFunction.ArgType;
import sparql.Parsedata.Homography;
import sparql.Parsedata.View;
import sparql.TimeData.TimeDataType;
import TextParser.CPositionData;

public class LocationData {
	
	public static enum LocationType { 
		GEODETIC_POINT, 
		GEODETIC_POLYGON,
		CARTESIAN_METRIC_POINT,
		CARTESIAN_METRIC_POLYGON,
		VIEW_CENTRIC_POINT,
		VIEW_CENTRIC_POLYGON,		 
		UNKNOWN };
		
		public LocationType location_type = LocationType.UNKNOWN;
				
		public GeodeticPoint geodetic_point = null;
		public GeodeticPolygon geodetic_polygon = null;
		public CartesianMetricPoint cartesian_metric_point = null; 
		public CartesianMetricPolygon cartesian_metric_polygon = null;
		public ViewCentricPoint view_centric_point = null;
		public ViewCentricPolygon view_centric_polygon = null;
				

		public LocationData() {
			// TODO Auto-generated constructor stub
		}
		

		
		public LocationData(String string) throws MseeException {			
			this.Set(string);			
		}

		
		
		// convert location data from CPositionData to LocationData
		public LocationData(LocationType target_type, CPositionData pos_data, String view_id) {
			// TODO Auto-generated constructor stub
			
			if ((target_type == LocationType.CARTESIAN_METRIC_POLYGON)					
					&& (pos_data.groundpolygon != null))
			{
				this.location_type = LocationType.CARTESIAN_METRIC_POLYGON;
				this.cartesian_metric_polygon = pos_data.groundpolygon.cartesian_metric_polygon;
			} 
			else if (((target_type == LocationType.CARTESIAN_METRIC_POINT)  
					||	(target_type == LocationType.CARTESIAN_METRIC_POLYGON) )					
					&& (pos_data.groundpos != null))
			{
				this.location_type = LocationType.CARTESIAN_METRIC_POINT;
				this.cartesian_metric_point = pos_data.groundpos.cartesian_metric_point;
			} else
			{				
				// this is for frame coordinates only 
				if (pos_data.contour.length == 2)
				{
					location_type = LocationType.VIEW_CENTRIC_POINT;
					view_centric_point = new ViewCentricPoint(pos_data.contour[0], pos_data.contour[1]);
					if (view_id!= null)
					{
						view_centric_point.SetViewId(view_id);
					}
					
					
				} else
				{	
					//TODO set view id
					location_type = LocationType.VIEW_CENTRIC_POLYGON;
					view_centric_polygon = new ViewCentricPolygon();
					// view_centric_polygon.GetViewId()
					if (view_id!= null)
					{
						view_centric_polygon.SetViewId(view_id);
					}
					
					
					for (int i = 0; i < pos_data.contour.length -1; i+=2)
					{					
						view_centric_polygon.AddPoint(pos_data.contour[i], pos_data.contour[i+1]);
					}				
				}			
			}
		}


		public LocationData(LocationType type, String string) throws MseeException {
			// TODO Auto-generated constructor stub
			this.Set(type, string);			
		}

		public LocationData(LocationType type) {
			this.SetType(type);		
			
		}


		private void SetType(LocationType type) {
			location_type = type; 
			switch (type)
			{
				case GEODETIC_POINT:					
					geodetic_point = new GeodeticPoint();										 
					break;					
				case GEODETIC_POLYGON:
					geodetic_polygon = new GeodeticPolygon();
					break;									
				case CARTESIAN_METRIC_POINT:
					cartesian_metric_point = new CartesianMetricPoint(); 
					 break;
				case CARTESIAN_METRIC_POLYGON:
					cartesian_metric_polygon = new CartesianMetricPolygon(); 
					 break;			
				case VIEW_CENTRIC_POINT:
					view_centric_point = new ViewCentricPoint(); 
					 break;
				case VIEW_CENTRIC_POLYGON:
					view_centric_polygon = new ViewCentricPolygon(); 
					 break;		
				default:
					System.err.println("DEBUG ERROR LocationData invalid argtype " + type.toString());		 
			}
		}


		public void Set(String locationStr) throws MseeException {
			int i =  locationStr.indexOf(',');
			if (i == -1)
			{
				System.err.println("DEBUG ERROR Invalid locationStr, expects a commas" + locationStr);
				return; 
			}
			String str_type = locationStr.substring(1,i-1);
			String str_value = locationStr.substring(i+2,locationStr.length()-1);
			
			ArgType argType = MseeFunction.GetArgType(str_type );
					
			
			switch (argType)
			{
				case ARG_GEODETIC_POINT:
					Set(LocationType.GEODETIC_POINT, str_value); 
					break;
				case ARG_GEODETIC_POLYGON:
					Set(LocationType.GEODETIC_POLYGON, str_value); 
					 break;
				case ARG_VIEW_CENTRIC_POINT:
					Set(LocationType.VIEW_CENTRIC_POINT, str_value);					
					break;
				case ARG_VIEW_CENTRIC_POLYGON:
					Set(LocationType.VIEW_CENTRIC_POLYGON, str_value);					
					break;
				case ARG_CARTESIAN_METRIC_POINT:
					Set(LocationType.CARTESIAN_METRIC_POINT, str_value); 
					break;
				case ARG_CARTESIAN_METRIC_POLYGON:
					Set(LocationType.CARTESIAN_METRIC_POLYGON, str_value); 
					break;			 
				default:
					System.err.println("ERROR LocationData Set  invalid argtype " + argType.toString());
					throw new MseeException("ERROR LocationData Set invalid argtype " + argType.toString());				 
			}		
			
		}
		

		public void Set(LocationType type, String locationStr) throws MseeException {
			/*
			 * reference:
		IsAtLocation("VIEW_CENTRIC_POINT","0,0;?view1","OBJECT_ID",?object1)
		IsAtLocation("VIEW_CENTRIC_POLYGON","0,0;100,0;100,100;0,100;?view1","OBJECT_ID",?object1)
		IsAtLocation(“CARTESIAN_METRIC_POINT","0,0,0","OBJECT_ID",?object1)
		IsAtLocation(“CARTESIAN_METRIC_POLYGON","0,0,0;5.25,0,0;5.25,4.32,10;0,4.32,10","OBJECT_ID",?object1)
		IsAtLocation(“GEODETIC_POINT","39.778661,-84.079399,0","OBJECT_ID",?object1)
		IsAtLocation(“GEODETIC_POLYGON","39.778661,-84.079399,0","39.778958,-84.085214,0","39.784647,-84.085064,10","OBJECT_ID",?object1)
			 */

			location_type = type; 
			
			switch (type)
			{
				case GEODETIC_POINT:					
					geodetic_point = new GeodeticPoint(locationStr);										 
					break;					
				case GEODETIC_POLYGON:
					geodetic_polygon = new GeodeticPolygon(locationStr);
					break;									
				case CARTESIAN_METRIC_POINT:
					cartesian_metric_point = new CartesianMetricPoint(locationStr); 
					 break;
				case CARTESIAN_METRIC_POLYGON:
					cartesian_metric_polygon = new CartesianMetricPolygon(locationStr); 
					 break;			
				case VIEW_CENTRIC_POINT:
					view_centric_point = new ViewCentricPoint(locationStr); 
					 break;
				case VIEW_CENTRIC_POLYGON:
					view_centric_polygon = new ViewCentricPolygon(locationStr); 
					 break;		
				default:
					System.err.println("DEBUG ERROR LocationData invalid argtype " + type.toString());
					throw new MseeException("DEBUG ERROR LocationData invalid argtype " + type.toString());				 
			}
			/*
			 * reference:
		IsAtLocation("VIEW_CENTRIC_POINT","0,0;?view1","OBJECT_ID",?object1)
		IsAtLocation("VIEW_CENTRIC_POLYGON","0,0;100,0;100,100;0,100;?view1","OBJECT_ID",?object1)
		IsAtLocation(“CARTESIAN_METRIC_POINT","0,0,0","OBJECT_ID",?object1)
		IsAtLocation(“CARTESIAN_METRIC_POLYGON","0,0,0;5.25,0,0;5.25,4.32,10;0,4.32,10","OBJECT_ID",?object1)
		IsAtLocation(“GEODETIC_POINT","39.778661,-84.079399,0","OBJECT_ID",?object1)
		IsAtLocation(“GEODETIC_POLYGON","39.778661,-84.079399,0;39.778958,-84.085214,0;39.784647,-84.085064,10","OBJECT_ID",?object1)
			 */
			
		}

		public static boolean IsTypeComparable(LocationType loc1,
				LocationType loc2) {
			// TODO Auto-generated method stub
			if (((loc1 == LocationType.GEODETIC_POINT) ||(loc1 == LocationType.GEODETIC_POLYGON)) &&
				((loc2 == LocationType.GEODETIC_POINT) ||(loc2 == LocationType.GEODETIC_POLYGON)))
				return true; 
			
			if (((loc1 == LocationType.CARTESIAN_METRIC_POINT) ||(loc1 == LocationType.CARTESIAN_METRIC_POLYGON)) &&
				((loc2 == LocationType.CARTESIAN_METRIC_POINT) ||(loc2 == LocationType.CARTESIAN_METRIC_POLYGON)))
				return true; 
				
			
			if (((loc1 == LocationType.GEODETIC_POINT) ||(loc1 == LocationType.GEODETIC_POLYGON)) &&
				((loc2 == LocationType.CARTESIAN_METRIC_POINT) ||(loc2 == LocationType.CARTESIAN_METRIC_POLYGON)))
				return true; 
				
			if (((loc1 == LocationType.CARTESIAN_METRIC_POINT) ||(loc1 == LocationType.CARTESIAN_METRIC_POLYGON)) &&
				((loc2 == LocationType.GEODETIC_POINT) ||(loc2 == LocationType.GEODETIC_POLYGON)))
				return true; 
				
				
			if (((loc1 == LocationType.VIEW_CENTRIC_POINT) ||(loc1 == LocationType.VIEW_CENTRIC_POLYGON)) &&
				((loc2 == LocationType.VIEW_CENTRIC_POINT) ||(loc2 == LocationType.VIEW_CENTRIC_POLYGON)))
				return true; 
					
			return false;
		}

		public SimpleBoundingBox GetEnclosingBox()  {
			// TODO Auto-generated method stub
			switch (location_type)
			{
				case GEODETIC_POINT:					
					return geodetic_point.GetEnclosingBox();	 
								
				case GEODETIC_POLYGON:
					return geodetic_polygon.GetEnclosingBox();		
					
				case CARTESIAN_METRIC_POINT:
					return cartesian_metric_point.GetEnclosingBox(); 
					
				case CARTESIAN_METRIC_POLYGON:
					return cartesian_metric_polygon.GetEnclosingBox(); 
								
				case VIEW_CENTRIC_POINT:
					return view_centric_point.GetEnclosingBox();
					
				case VIEW_CENTRIC_POLYGON:
					return view_centric_polygon.GetEnclosingBox();
							
				default:
					System.err.println("ERROR GetEnclosingBox invalid argtype " + location_type.toString());
					return new SimpleBoundingBox(0,0);
					
//					throw new MseeException("ERROR GetEnclosingBox invalid argtype " + location_type.toString());				 
			}			
			
		}

		public void SetPoint(LocationType loc_type, double fx, double fy)  {
			// TODO Auto-generated method stub
			
			switch (loc_type)
			{
				case GEODETIC_POINT:					
					location_type = loc_type;
					geodetic_point = new GeodeticPoint(fx, fy);
					break;					
				case CARTESIAN_METRIC_POINT:
					location_type = loc_type;
					cartesian_metric_point = new CartesianMetricPoint(fx, fy);
					break;		
								
				case VIEW_CENTRIC_POINT:
					location_type = loc_type;
					view_centric_point = new ViewCentricPoint(fx, fy);
					break;							
	
				default:
					System.err.println("ERROR SetPoint invalid argtype " + loc_type.toString());					 
			}				
		}


		public void AddPointToCartesianMetricPolygon(
				CartesianMetricPoint pt) {
			// TODO Auto-generated method stub
			cartesian_metric_polygon.AddPoint(pt);
			
		}		
		


		public void AddPointToGeodeticPolygon(
				GeodeticPoint pt) {
			this.geodetic_polygon.AddPoint(pt);
			
		}		

		public void AddPointToCartesianMetricPolygon(double x, double y) {
			AddPointToCartesianMetricPolygon(new CartesianMetricPoint(x,y));
			
		}	


		// compute weighted average of two points in Cartesian Metric
		public static LocationData GetWeightedAverageCartesianMetricPoint(
				double r1, LocationData pos1, double r2,
				LocationData pos2) {
			// TODO Auto-generated method stub
			LocationData new_data = new LocationData();
			new_data.SetType(LocationType.CARTESIAN_METRIC_POINT);
			
			if ((pos1.GetType() != LocationType.CARTESIAN_METRIC_POINT) ||
					(pos2.GetType() != LocationType.CARTESIAN_METRIC_POINT))
			{
				System.err.println("ERROR, LocationData, GetWeightedAverageCartesianMetricPoint, pos.GetType is not CARTESIAN_METRIC_POINT");
				return null;
			}
			double fx = (r1 * pos1.cartesian_metric_point.x) + 	(r2 * pos2.cartesian_metric_point.x);
			double fy = (r1 * pos1.cartesian_metric_point.y) + 	(r2 * pos2.cartesian_metric_point.y);
			new_data.cartesian_metric_point.Set(fx,fy);
			
			return new_data;
			
		}


		public LocationType GetType() {
			// TODO Auto-generated method stub
			return this.location_type;
		}
		
		/**
		 * Returns a string that uniquely identifies this particular location.  For a different location (so long as it is valid), this method should return a different string.
		 * @return
		 */
		public String getUniqueStringDescription( ) {
			switch (location_type)
			{
				case GEODETIC_POINT:
					return "GEODETIC_POINT:" + geodetic_point.latitude + ","+ geodetic_point.longitude;
					
				case GEODETIC_POLYGON:
					String retval = "GEODETIC_POLYGON:";
					for ( GeodeticPoint pt : this.geodetic_polygon.points){
						retval += "\n\t"+pt.latitude+","+pt.longitude;
					}
					return retval;
					
				case CARTESIAN_METRIC_POINT:
					return "CARTESIAN_METRIC_POINT:" + cartesian_metric_point.x + ","+ cartesian_metric_point.y;
					
				case CARTESIAN_METRIC_POLYGON:
					retval = "CARTESIAN_METRIC_POLYGON:" + cartesian_metric_polygon.points.size();
					for ( CartesianMetricPoint pt : cartesian_metric_polygon.points){
						retval += "\n\t"+pt.x+","+pt.y;
					} 	
					return retval;
					//break;
					
				case VIEW_CENTRIC_POINT:
					return "VIEW_CENTRIC_POINT:" + view_centric_point.x() + ","+ view_centric_point.y();		
					
				case VIEW_CENTRIC_POLYGON:
					retval = "VIEW_CENTRIC_POLYGON:";
					for ( ViewCentricPoint pt : this.view_centric_polygon.points){
						retval += "\n\t" + pt.x()+","+pt.y();
					} 
					return retval;
					//break;
			}
			
			return "\t\t\t unknown location type";
		}
		
		
		public void Printf() {
			System.out.println( getUniqueStringDescription() );			
		}

		// this is to convert view centric to scene centric
		// e.g. this is used during query, when the query is expressed in view centric
		// this object must be a view centric (either point or polygon)
		// the homography must be found. 
		
		public LocationData ConvertViewToSceneCentric() {
			
			// this should only be used for location is the query
			System.out.println("DEBUG LocationData ConvertViewToSceneCentric; this should only be called during query");
			
			if (	this.location_type == LocationType.VIEW_CENTRIC_POINT)
			{
				
				String str_view_id = this.view_centric_point.GetViewId();
				 View view = MseeDataset.GetView(str_view_id);
				Homography hView2Map = MseeDataset.GetHomographyView2Map(str_view_id);
				if ((hView2Map  == null) ||  (view  == null) )
				{
					System.err.println("ERROR, ConvertViewToSceneCentric: unable to get homography for view +  " + str_view_id);
					return this;
				} 			
				
				
				ViewCentricPoint new_pt = new ViewCentricPoint(	
						this.view_centric_point.GetViewId(),
						this.view_centric_point.x() * view.GetImageWidthScale(),
						this.view_centric_point.y());
				
				if (view.distortion_param!= null)
				{
					System.out.println("DEBUG distorted ");
					new_pt.Printf();
					new_pt = view.distortion_param.Undistort(
							new_pt);

					System.out.println("DEBUG undistorted ");
					new_pt.Printf();
					
				}
							
				LocationData locationData_Cartesian = hView2Map.Transform(
							LocationType.CARTESIAN_METRIC_POINT, 
							new_pt.x(),
							new_pt.y(),
							null		// no bounding box for map coordinates 
							);
				// debug
				System.out.println("DEBUG LocationData ConvertViewToSceneCentric");
				locationData_Cartesian.Printf();
				
				return locationData_Cartesian;
			} else if (this.location_type == LocationType.VIEW_CENTRIC_POLYGON) 
			{
				String str_view_id = this.view_centric_polygon.GetViewId();
				 View view = MseeDataset.GetView(str_view_id);
				Homography hView2Map = MseeDataset.GetHomographyView2Map(str_view_id);
				if ((hView2Map  == null) ||  (view  == null) )
				{
					System.err.println("ERROR, ConvertViewToSceneCentric: unable to get homography for view +  " + str_view_id);
					return this;
				} 
					
				LocationData locationData_Cartesian = new LocationData(LocationType.CARTESIAN_METRIC_POLYGON);
				
					
				for (ViewCentricPoint pt:this.view_centric_polygon.points)
				{
					
					ViewCentricPoint new_pt = new ViewCentricPoint(	
								pt.GetViewId(),
								pt.x()  * view.GetImageWidthScale(),
								pt.y());

					if (view.distortion_param!= null)
					{
						System.out.println("DEBUG distorted ");
						new_pt.Printf();
						
						new_pt = view.distortion_param.Undistort(new_pt);
						
						System.out.println("DEBUG undistorted ");
						new_pt.Printf();
					
					}
					
					locationData_Cartesian.AddPointToCartesianMetricPolygon(
							hView2Map.Transform(
									new_pt.x(), new_pt.y()));							
				}
				
				// debug
				System.out.println("DEBUG LocationData ConvertViewToSceneCentric");
				locationData_Cartesian.Printf();
				
				return locationData_Cartesian;				
			} else
			{
				System.err.println("ERROR ConvertViewToSceneCentric; unexpected location_type  " + location_type );
				return this;
			}
	}

}
		
