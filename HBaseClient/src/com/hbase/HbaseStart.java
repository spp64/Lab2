package com.hbase;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

public class HbaseStart
{
	static public void main(String args[]) throws IOException {
		
		//createTable();
		insertTable();
		//retrieveTable();
		//deleteTable();
		}
	
	
	public static void createTable() throws IOException
	{
		Configuration config = HBaseConfiguration.create();		 
		config.clear();
		config.set("hbase.zookeeper.quorum", "134.193.136.147");
        config.set("hbase.zookeeper.property.clientPort","2181");
        config.set("hbase.master", "134.193.136.147:7180");
		
		HBaseAdmin admin = new HBaseAdmin(config);
		
		try {
			// HBaseConfiguration hc = new HBaseConfiguration(new Configuration());
			
			  HTableDescriptor ht = new HTableDescriptor("SensorTagTablePra"); 
			  
/*			  ht.addFamily( new HColumnDescriptor("latitude"));

			  ht.addFamily( new HColumnDescriptor("longitude"));
			  */
			  ht.addFamily( new HColumnDescriptor("Date"));
			  
			  ht.addFamily( new HColumnDescriptor("x"));
			  
			  ht.addFamily( new HColumnDescriptor("y"));
			  
			  ht.addFamily( new HColumnDescriptor("z"));
			  
			  ht.addFamily( new HColumnDescriptor("Humidity"));
			  
			  ht.addFamily( new HColumnDescriptor("Gyroscope"));
			  
			/*  ht.addFamily( new HColumnDescriptor("Pressure"));*/
			  
			 
			  
			  System.out.println( "connecting" );

			  HBaseAdmin hba = new HBaseAdmin( config );

			  System.out.println( "Creating Table" );

			  hba.createTable( ht );

			  System.out.println("Done......");
			  
			  	
        } finally {
            admin.close();
        }
		
		
	}
	
	
	public static void insertTable() throws IOException{
	
		Configuration config = HBaseConfiguration.create();		 
		config.clear();
         config.set("hbase.zookeeper.quorum", "134.193.136.147");
         config.set("hbase.zookeeper.property.clientPort","2181");
         config.set("hbase.master", "134.193.136.147:60010");
         
         
         String Date="",x="",y="",z="",Humidity="",Gyroscope="";
         

		  HTable table = new HTable(config, "SensorTagTablePra");
	
		  Put p = new Put(Bytes.toBytes("row1"));
		  
		  int count=1;
         
        BufferedReader br = null;
         
 		try {
  
 			String sCurrentLine;
  
 			br = new BufferedReader(new FileReader("C:\\Users\\Praneeth\\Android\\HBaseClient\\sensortag.txt"));
  
 			while ((sCurrentLine = br.readLine()) != null) {
 				
 				if(sCurrentLine.equals(""))
 				{
 					continue;
 				}
 				
 				String[] array = sCurrentLine.split("\t");
 			/*	latitude = array[0];
 				longitude=array[1];*/
 				Date=array[0];
 				x=array[1];
 				y=array[2];
 				z=array[3];
 				Humidity=array[4];
 				Gyroscope=array[5];
 				/*Pressure=array[7];*/
 				
 				
 				/*  p.add(Bytes.toBytes("latitude"), Bytes.toBytes("col"+count),Bytes.toBytes(latitude));
 				  
 				 p.add(Bytes.toBytes("longitude"), Bytes.toBytes("col"+(count+1)),Bytes.toBytes(longitude));
 				 */
 				 p.add(Bytes.toBytes("Date"), Bytes.toBytes("col"+(count)),Bytes.toBytes(Date));
 				 
 				 p.add(Bytes.toBytes("x"), Bytes.toBytes("col"+(count+1)),Bytes.toBytes(x));
 				 
 				p.add(Bytes.toBytes("y"), Bytes.toBytes("col"+(count+2)),Bytes.toBytes(y));
 				
 				p.add(Bytes.toBytes("z"), Bytes.toBytes("col"+(count+3)),Bytes.toBytes(z));
 				
 				p.add(Bytes.toBytes("Humidity"), Bytes.toBytes("col"+(count+4)),Bytes.toBytes(Humidity));
 				
 				p.add(Bytes.toBytes("Gyroscope"), Bytes.toBytes("col"+(count+5)),Bytes.toBytes(Gyroscope));
 				
 		/*		p.add(Bytes.toBytes("Pressure"), Bytes.toBytes("col"+(count+7)),Bytes.toBytes(Pressure));
 				*/
 				

 			      table.put(p);
 			      
 			      count=count+1;
 			     //System.out.println( "Creating Table" );
 				
 			}
  
 		} catch (IOException e) {
 			e.printStackTrace();
 		} finally {
 			try {
 				if (br != null)br.close();
 			} catch (IOException ex) {
 				ex.printStackTrace();
 			}
 		}
         
         
		
		
	  
	    
	}
	
	
	public static void retrieveTable() throws IOException{
		
		Configuration config = HBaseConfiguration.create();		 
		config.clear();
		  config.set("hbase.zookeeper.quorum", "134.193.136.147");
	         config.set("hbase.zookeeper.property.clientPort","2181");
	         config.set("hbase.master", "134.193.136.147:60010");
		
		
		  HTable table = new HTable(config, "SensorTagTablePra");
		
		 Get g = new Get(Bytes.toBytes("row1"));

		  Result r = table.get(g);

		/*  byte [] value = r.getValue(Bytes.toBytes("latitude"),Bytes.toBytes("col1"));

		  byte [] value1 = r.getValue(Bytes.toBytes("longitude"),Bytes.toBytes("col2"));
*/
		  byte [] value = r.getValue(Bytes.toBytes("Date"),Bytes.toBytes("col1"));
		  
		  byte [] value1 = r.getValue(Bytes.toBytes("x"),Bytes.toBytes("col2"));
		  
		  byte [] value2 = r.getValue(Bytes.toBytes("y"),Bytes.toBytes("col3"));
		  
		  byte [] value3 = r.getValue(Bytes.toBytes("z"),Bytes.toBytes("col4"));
		  
		  byte [] value4 = r.getValue(Bytes.toBytes("Humidity"),Bytes.toBytes("col5"));
		  
		  byte [] value5 = r.getValue(Bytes.toBytes("Gyroscope"),Bytes.toBytes("col6"));
		  
		  
		  
		/*  byte [] value7 = r.getValue(Bytes.toBytes("Pressure"),Bytes.toBytes("col8"));*/
		  
		 
		  
		  String valueStr = Bytes.toString(value);

		  String valueStr1 = Bytes.toString(value1);
		  
		  String valueStr2 = Bytes.toString(value2);
		  
		  String valueStr3 = Bytes.toString(value3);
		  
		  String valueStr4 = Bytes.toString(value4);
		  
		  String valueStr5 = Bytes.toString(value5);
		  
/*		  String valueStr6 = Bytes.toString(value6);
		  
		  String valueStr7 = Bytes.toString(value7);
		  
		  String valueStr8 = Bytes.toString(value8);*/

		/*  System.out.println("GET: " +"latitude: "+ valueStr+"longitude: "+valueStr1);*/
		  System.out.println("GET: " +"Date: "+ valueStr);
		  System.out.println("GET: " +"x: "+ valueStr1);
		  System.out.println("GET: " +"y: "+ valueStr2);
		  System.out.println("GET: " +"z: "+ valueStr3);
		  System.out.println("GET: " +"Humidity: "+ valueStr4);
		  System.out.println("GET: " +"Gyroscope: "+ valueStr5);
		  /*System.out.println("GET: " +"Pressure: "+ valueStr7);*/
		  

		  

	/*	  Scan s = new Scan();

		  s.addColumn(Bytes.toBytes("latitude"), Bytes.toBytes("col1"));

		  s.addColumn(Bytes.toBytes("longitude"), Bytes.toBytes("col2"));

		  ResultScanner scanner = table.getScanner(s);

		  try
		  {
		   for (Result rr = scanner.next(); rr != null; rr = scanner.next())
		   {
		    System.out.println("Found row : " + rr);
		   }
		  } finally
		  {
		   // Make sure you close your scanners when you are done!
		   scanner.close();
		  }
		*/
	}
	
	
	public static void deleteTable() throws IOException{
		
		Configuration config = HBaseConfiguration.create();		 
		config.clear();
		  config.set("hbase.zookeeper.quorum", "134.193.136.147");
	         config.set("hbase.zookeeper.property.clientPort","2181");
	         config.set("hbase.master", "134.193.136.147:60010");
         
         HBaseAdmin admin = new HBaseAdmin(config);
         admin.disableTable("User");
         admin.deleteTable("User");

	}
}

