package com.infa.eic.sample;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import org.apache.commons.lang3.StringUtils;

import com.infa.products.ldm.core.rest.v2.client.invoker.ApiResponse;
import com.infa.products.ldm.core.rest.v2.client.models.ObjectIdRequest;
import com.infa.products.ldm.core.rest.v2.client.models.ObjectRefRequest;
import com.infa.products.ldm.core.rest.v2.client.models.ObjectResponse;
import com.infa.products.ldm.core.rest.v2.client.models.ObjectsResponse;
import com.infa.products.ldm.core.rest.v2.client.utils.ObjectAdapter;
import com.opencsv.CSVWriter;


/**
 * This program uses the EIC REST API to generate a coverage report of BG terms against specified resources. 
 * Using this program, data stewards can quickly get a report on # of columns not associated with BG terms yet. 
 * @author gpathak
 *
 */
public class BGAssociationReport {
	
	private static String DIVISION="com.infa.appmodels.ldm.LDM_a5922c30_42eb_40ac_bb1e_75362b67ea9c";
	
	
	List<String> resources;
	List<String> attributes;
	List<String> types;
	
	
	/**
	 * Instantiate class with resource name list, attribute list and class type list.
	 * @param resources
	 * @param attributes
	 * @param types
	 */
	public BGAssociationReport(List<String> resources, List<String> attributes, List<String> types) {
		this.resources=resources;
		this.attributes=attributes;
		this.types=types;
	}
	
	/**
	 * Get BG/Custom Attribute Associations for a given object
	 * @param objectID
	 * @return
	 * @throws Exception
	 */
	public List<String> getAssociations(String objectID) throws Exception {
		ApiResponse<ObjectResponse> apiResponse=APIUtils.READER.catalogDataObjectsIdGetWithHttpInfo(objectID);
		ObjectIdRequest request=ObjectAdapter.INSTANCE.copyIntoObjectIdRequest(apiResponse.getData());
		//Get Associated BG Terms
		ArrayList<ObjectRefRequest> bgterms=request.getBusinessTerms();
		List<String> retList=new ArrayList<String>();
		retList.add(objectID);
		retList.add(APIUtils.getValue(apiResponse.getData(),"core.classType"));
		retList.add(apiResponse.getData().getHref());
		String cval;
		for(String customAttribute: attributes) {
			cval=APIUtils.getValue(apiResponse.getData(),customAttribute);
			if(cval!=null) {
				retList.add(cval);
			} else {
				retList.add("");
			}
		}
		
		//If no BG term is associated add FALSE to the report
		if(bgterms==null || bgterms.isEmpty()) {
			retList.add("FALSE");
			retList.add("");
			
		} else {
			retList.add("TRUE");
			retList.add(request.getBusinessTerms().get(0).getId());
			
		}
		return retList;
	}
	
	
	
	public List<List<String>> run(Boolean Preview) throws Exception {
		List<List<String>> retList=new ArrayList<List<String>>();
			for(String resource: resources) {
				for(String type: types) {
					for(String objectID: APIUtils.getAssetsByType(resource, type, Preview).keySet()) {
						retList.add(getAssociations(objectID));
					}
				}
			}
		
		return retList;
	}
	
	public String replaceCommas(String a){
	    String result = "";
	    Scanner scan = new Scanner(a);
	    scan.useDelimiter(",");
	    while(scan.hasNext()){
	        result += scan.next() + "\n";
	    }
	    return result;
	}
	
	public static class TableBuilder
	{
	    List<String[]> rows = new LinkedList<String[]>();
	 
	    public void addRow(String... cols)
	    {
	        rows.add(cols);
	    }
	 
	    private int[] colWidths()
	    {
	        int cols = -1;
	 
	        for(String[] row : rows)
	            cols = Math.max(cols, row.length);
	 
	        int[] widths = new int[cols];
	 
	        for(String[] row : rows) {
	            for(int colNum = 0; colNum < row.length; colNum++) {
	                widths[colNum] =
	                    Math.max(
	                        widths[colNum],
	                        StringUtils.length(row[colNum]));
	            }
	        }
	 
	        return widths;
	    }
	 
	    @Override
	    public String toString()
	    {
	        StringBuilder buf = new StringBuilder();
	 
	        int[] colWidths = colWidths();
	 
	        for(String[] row : rows) {
	            for(int colNum = 0; colNum < row.length; colNum++) {
	                buf.append(
	                    StringUtils.rightPad(
	                        StringUtils.defaultString(
	                            row[colNum]), colWidths[colNum]));
	                buf.append(' ');
	            }
	 
	            buf.append('\n');
	        }
	 
	        return buf.toString();
	    }
	 
	}

	public static String main(String METHOD, String URL, String USER, String PASS) {
		// Method will either be Preview or Download
		
		//Connect to the EIC REST Instance 
		StringBuilder sb = null;
		TableBuilder tb = null;
		try {
			APIUtils.setupOnce(URL, USER, PASS);
		} catch(Exception e) {
			e.printStackTrace();
		}
		//List of resources
		List<String> resources = new ArrayList<String>();
		resources.add("Hive_Atlas");
		//resources.add("ORACLE_CRM");
		//resources.add("OrderEntry");
		
		//List of attributes (provide ids)
		List<String> attributes=new ArrayList<String>();
		attributes.add("core.resourceName");
		attributes.add(DIVISION);
		
		//List of asset types
		List<String> types=new ArrayList<String>();
		types.add("com.infa.ldm.relational.Column");
		
		//Path to the output report
		//String csv = "output.csv";
		//CSVWriter writer = null;
		//try {
		//	writer = new CSVWriter(new FileWriter(csv));
		//} catch (IOException e1) {
			// TODO Auto-generated catch block
		//	e1.printStackTrace();
		//}

		String header = "ID,TYPE,LINK,";
		for(String attribute: attributes) {
			header+=attribute+",";
		}
		header+="BG_ASSOCIATED, BG TERM ID";
		
		
		//writer.writeNext(header.split(","));

		if(METHOD.equals("Download")) {
			BGAssociationReport rep=new BGAssociationReport(resources,attributes,types);
			try {
				sb = new StringBuilder();
				//sb.append("....start process....." + "\n");
				for(List<String> l: rep.run(false)) {
					//writer.writeNext(l.toArray(new String[l.size()]));
					System.out.println();
					//sb.append("\n");
					for(String s: l) {
						if (s.startsWith("Hive_Atlas:")) {
							sb.append("BG Term: ");
						}
						else if (s.startsWith("com")) {
							sb.append("Catalog ID: ");
						}
						else if (s.startsWith("/2/")) {
							sb.append("API Call: ");
						}
						else if (s.startsWith("Hive_Atlas")) {
							sb.append("Resource Name: ");
						}
						
						sb.append(s);
						sb.append("\n");
					}
					sb.append("\n");
				}
				sb.append("....end process....." + "\n");
				//writer.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return sb.toString();
			
			
		}
		else if(METHOD.equals("Preview")) {
			//int total=3000;
			//int offset=0;
			//final int pageSize=300;
			//String query="* AND core.allclassTypes:\""+APIUtils.COL_CLASSTYPE+"\"";
			
			BGAssociationReport rep=new BGAssociationReport(resources,attributes,types);
			try {
				sb = new StringBuilder();
				tb = new TableBuilder();
				//sb.append("....start process....." + "\n");
				//while (offset<300) {
					//ObjectsResponse response=APIUtils.READER.catalogDataObjectsGet(query, null, BigDecimal.valueOf(offset), BigDecimal.valueOf(pageSize), false);
					//total=response.getMetadata().getTotalCount().intValue();
					//offset+=150;
				ArrayList<String> BGTerm  = new ArrayList<String>();
				ArrayList<String> Catalog = new ArrayList<String>();
				ArrayList<String> API = new ArrayList<String>();
				ArrayList<String> Resource = new ArrayList<String>();
				
					for(List<String> l: rep.run(true)) {
						//writer.writeNext(l.toArray(new String[l.size()]));
						System.out.println();
						for(String s: l) {
							
							//System.out.println(s.replaceAll(",", "\n"));
							//sb.append("Resource Name: ");
							if (s.equals("FALSE")) {
								continue;
							}
							else {
								if (s.startsWith("Hive_Atlas:")) {
									//sb.append("BG Term: ");
									BGTerm.add(s);
								}
								else if (s.startsWith("com")) {
									//sb.append("Catalog ID: ");
									Catalog.add(s);
								}
								else if (s.startsWith("/2/")) {
									//sb.append("API Call: ");
									API.add(s);
								}
								else if (s.startsWith("Hive_Atlas")) {
									//sb.append("Resource Name: ");
									Resource.add(s);
								}
								
								//sb.append(s);
								//sb.append("\n");
							}
							
							
						}
					}
					//sb.append("....end process....." + "\n");
					//writer.close();
					tb.addRow("Resource", "Catalog", "BGTerm");
					tb.addRow("-----", "----", "-----");
					String test = null;
					for (int i =0; i < BGTerm.size(); i++) {
//						test = String.format("%90s%100s%132s%32s", BGTerm.get(i), Catalog.get(i), API.get(i),Resource.get(i));
//						System.out.format(test);
//						System.out.println("\n");
						tb.addRow(Resource.get(i)+"   ", Catalog.get(i), BGTerm.get(i).substring(27,BGTerm.get(i).length()));
						//sb.append(test+"\n"+"\n"+"\n");
					}
					
				}
				
			 catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return tb.toString();
		}
		else {
			return "Did not specify method";
		}
	}
	
	

}
