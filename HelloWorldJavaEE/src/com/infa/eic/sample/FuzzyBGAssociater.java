/**
 * 
 */
package com.infa.eic.sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

import com.infa.products.ldm.core.rest.v2.client.invoker.ApiResponse;
import com.infa.products.ldm.core.rest.v2.client.models.ObjectIdRequest;
import com.infa.products.ldm.core.rest.v2.client.models.ObjectRefRequest;
import com.infa.products.ldm.core.rest.v2.client.models.ObjectResponse;
import com.infa.products.ldm.core.rest.v2.client.utils.ObjectAdapter;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;

/**
 * Sample REST API Program that associates data assets with business glossary terms based on fuzzy name matches.
 * @author gpathak
 *
 */
public class FuzzyBGAssociater {
	
	
	
	/**
	 * Thresholds go from 1-100 where 100 stands for exact match.
	 */
	private static int THRESHOLD=80;
	
	private static String BG_RESOURCE_NAME="BG_DEFAULT_RESOURCE";
	private static String RESOURCE="OrderEntry";
	
	

	/**
	 * @param args
	 */
	public static String main(String URL, String USER, String PASS) {
		FuzzyBGAssociater fbg=new FuzzyBGAssociater();
		StringBuilder sb = null;
		TableBuilder tb = null;
		//Connect to the EIC REST Instance 
		try {
			APIUtils.setupOnce(URL, USER, PASS);
		}catch(Exception e) {
			throw e;
		}
		try {
			sb = new StringBuilder();
			tb = new TableBuilder();
			
			//Get all business terms. The boolean flag false indicates that a full run is conducted
			HashMap<String, String> termMap=APIUtils.getAssetsByType(BG_RESOURCE_NAME, APIUtils.BGTERM,false);
			//Get all Columns from the specified resource
			HashMap<String, String> columnMap=APIUtils.getAssetsByType(RESOURCE, APIUtils.COL_CLASSTYPE,false);
			System.out.println(termMap.size()+":"+columnMap.size());
			tb.addRow("Number of Terms: " + termMap.size(), "Number of Columns: " + columnMap.size());
			
			tb.addRow("Number", "Column Name", "Result");
			tb.addRow("----------","----------", "---------");
			int i=1;
			int j=1;
			for(String columnID: columnMap.keySet()) {
				
				//Remove all _ for a better fuzzy match
				String colName=columnMap.get(columnID).replaceAll("_", " ");
				
				sb.append(colName);
				//List<ExtractedResult> results= FuzzySearch.extractAll(colName, termMap.values(), THRESHOLD);
                List<ExtractedResult> results= FuzzySearch.extractSorted(colName, termMap.values(), THRESHOLD);
                //System.out.println(results);
				
				if(!results.isEmpty()) {
				//System.out.println(columnName+":"+term);
					j = i;
					System.out.println(i++ +":"+colName+":"+results.get(0).getString());
					
					
					//sb.append(i++ +" : "+colName+" : "+results.get(0).getString());
					
					String termID=getKeyByValue(termMap, results.get(0).getString());
					sb.append(termID);
					sb.append("\n");
					
					//Perform BG Term Associations
					String test = fbg.associateBGTerm(columnID,termID);
					
					//Use the method below to remove BG terms from a given column
					//fbg.resetTerms(columnID);
					tb.addRow(String.valueOf(j),colName,results.get(0).getString());
				}
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return tb.toString();

	}
	
	/**
	 * Utility method to get hash key from value. Works best with unique values.
	 * @param map
	 * @param value
	 * @return
	 */
	public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
	    for (Entry<T, E> entry : map.entrySet()) {
	        if (Objects.equals(value, entry.getValue())) {
	            return entry.getKey();
	        }
	    }
	    return null;
	}
	
	/**
	 * Associates a bg term to a given object.
	 * @param objectID
	 * @param bgTermID
	 * @throws Exception
	 */
	public String associateBGTerm(String objectID, String bgTermID) throws Exception {
		System.out.println(objectID+":"+bgTermID);
		ApiResponse<ObjectResponse> apiResponse=APIUtils.READER.catalogDataObjectsIdGetWithHttpInfo(objectID);
		ObjectIdRequest request=ObjectAdapter.INSTANCE.copyIntoObjectIdRequest(apiResponse.getData());
		ArrayList<ObjectRefRequest> bgterms=request.getBusinessTerms();
		String ifMatch = null;
		if(bgterms==null || bgterms.isEmpty()) {
			ObjectRefRequest bg=new ObjectRefRequest();
			bg.setId(bgTermID);
			request.addBusinessTermsItem(bg);
			ifMatch=APIUtils.READER.catalogDataObjectsIdGetWithHttpInfo(objectID).getHeaders().get("ETag").get(0);
			System.out.println(ifMatch);
			APIUtils.WRITER.catalogDataObjectsIdPut(objectID, request, ifMatch);
		} else {
			System.out.println("Existing bg term");
			ifMatch="Existing bg term";
			
		}
		return ifMatch;
	}
	
	/**
	 * Removes BG term association from a given object
	 * @param objectID
	 * @throws Exception
	 */
	public void resetTerms(String objectID) throws Exception {
		ApiResponse<ObjectResponse> apiResponse=APIUtils.READER.catalogDataObjectsIdGetWithHttpInfo(objectID);
		ObjectIdRequest request=ObjectAdapter.INSTANCE.copyIntoObjectIdRequest(apiResponse.getData());
		
		
		ArrayList<ObjectRefRequest> bgterms=request.getBusinessTerms();
		if(bgterms!=null && !bgterms.isEmpty()) {
			request.setBusinessTerms(new ArrayList<ObjectRefRequest>());
			//ObjectRefRequest bg=new ObjectRefRequest();
			//bg.setId(bgTermID);
			//request.addBusinessTermsItem(bg);
			String ifMatch=APIUtils.READER.catalogDataObjectsIdGetWithHttpInfo(objectID).getHeaders().get("ETag").get(0);
			//System.out.println(ifMatch);
			System.out.println(request.toString());
			
			APIUtils.WRITER.catalogDataObjectsIdPut(objectID, request, ifMatch);
		} else {
			System.out.println("BG Term Not Found");
		}
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

}
