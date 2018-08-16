package com.infa.eic.sample;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.infa.eic.sample.BGAssociationReport.TableBuilder;
import com.infa.products.ldm.core.rest.v2.client.invoker.ApiException;
import com.infa.products.ldm.core.rest.v2.client.invoker.ApiResponse;
import com.infa.products.ldm.core.rest.v2.client.models.LinkedObjectResponse;
import com.infa.products.ldm.core.rest.v2.client.models.ObjectIdRequest;
import com.infa.products.ldm.core.rest.v2.client.models.ObjectRefRequest;
import com.infa.products.ldm.core.rest.v2.client.models.ObjectResponse;
import com.infa.products.ldm.core.rest.v2.client.models.ObjectsResponse;
import com.infa.products.ldm.core.rest.v2.client.utils.ObjectAdapter;
import com.opencsv.CSVWriter;

@SuppressWarnings("unused")
public class ColumnDomainAssociationReport {
private static String DIVISION="com.infa.appmodels.ldm.LDM_a5922c30_42eb_40ac_bb1e_75362b67ea9c";
	
	
	List<String> attributes;
	
	
	public ColumnDomainAssociationReport(List<String> attributes) {
		this.attributes=attributes;
	}
	
	public ColumnDomainAssociationReport() {
		this.attributes=null;
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
	
	private HashMap<String, HashSet<String>> getAllColumns() throws Exception {
		int total=3000;
		int offset=0;
		final int pageSize=300;
		
		String query="* AND core.allclassTypes:\""+APIUtils.COL_CLASSTYPE+"\"";
		HashMap<String, HashSet<String>> retMap=new HashMap<String,HashSet<String>>();
		
		while (offset<total) {
			ObjectsResponse response=APIUtils.READER.catalogDataObjectsGet(query, null, BigDecimal.valueOf(offset), BigDecimal.valueOf(pageSize), false);
			total=response.getMetadata().getTotalCount().intValue();
			offset+=pageSize;
			
			for(ObjectResponse or: response.getItems()) {
				for(LinkedObjectResponse lr : or.getSrcLinks()) {
					if(lr.getClassType().equals(APIUtils.DOMAIN_CLASSTYPE) && lr.getAssociation().equals("com.infa.ldm.profiling.DataDomainColumnInferred")) {
						System.out.println(getTableName(or.getId())+":"+lr.getId());
						HashSet<String> cols=retMap.get(lr.getId());
						if(cols==null) {
							cols=new HashSet<String>();
							retMap.put(lr.getId(), cols);
						}
						cols.add(getTableName(or.getId()));
					}
				}
			}
		}
		return retMap;
	}
	
	private String getTableName(String columnID) {
		return columnID.substring(0,columnID.lastIndexOf("/"));
	}
	
	
	public String run() throws Exception {
		Map<String, HashSet<String>> cd=getAllColumns();
		
		StringBuilder sb = null;
		TableBuilder tb = null;
		String csv = "output1.csv";
		CSVWriter writer = null;
		try {
			writer = new CSVWriter(new FileWriter(csv));
			sb = new StringBuilder();
			tb = new TableBuilder();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String header = "MAIN,CONNECTED,TABLE";
//		for(String attribute: attributes) {
//			header+=attribute+",";
//		}
//		header+="BG_ASSOCIATED, BG TERM ID";
//		
		
		writer.writeNext(header.split(","));
		tb.addRow("Main Domain", "Connected Domain", "Col");
		tb.addRow("----------", "---------", "---------");
		
		for(String mainDomain : cd.keySet()) {
			for(String connectedDomain: cd.keySet()) {
				if(mainDomain.equals(connectedDomain)) {
					continue;
				}
				
				HashSet<String> cols=cd.get(mainDomain);
				for(String col : cols) {
					writer.writeNext(new String[]{mainDomain,"",col});
					//tb.addRow(mainDomain, col);
					sb.append(mainDomain + " " + col);
					if(cd.get(connectedDomain).contains(col)) {
						//writer.writeNext(new String[]{mainDomain,connectedDomain,col});
						writer.writeNext(new String[]{mainDomain,connectedDomain,""});
						tb.addRow(mainDomain, connectedDomain, col);
						sb.append(mainDomain+ " " + connectedDomain+" ");
					}
					else {
						tb.addRow(mainDomain, connectedDomain, col);
					}
				}
				
				
			}
		}
		
		try {
			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tb.toString();
		
	}
	
	
	public static String main(String URL, String USER, String PASS) {
		
		
		List<String> attributes=new ArrayList<String>();
		attributes.add("core.resourceName");
		attributes.add(DIVISION);
		
		APIUtils.setupOnce(URL, USER, PASS);
		String output = "";
		ColumnDomainAssociationReport cdar=new ColumnDomainAssociationReport();
		try {
			output = cdar.run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
		
		
		

		
	}
	

}
