package com.infa.eic.sample;


import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.infa.products.ldm.core.rest.v2.client.invoker.ApiException;
import com.infa.products.ldm.core.rest.v2.client.models.AttributeResponse;
import com.infa.products.ldm.core.rest.v2.client.models.AttributesResponse;
import com.infa.products.ldm.core.rest.v2.client.models.FactResponse;
import com.infa.products.ldm.core.rest.v2.client.models.LinkedObjectResponse;
import com.infa.products.ldm.core.rest.v2.client.models.ObjectResponse;
import com.infa.products.ldm.core.rest.v2.client.models.ObjectsResponse;
import com.opencsv.CSVWriter;

/**
 * 
 */

/**
 * This program uses the EIC REST API to copy values from one custom attribute to the other.
 * @author lntrapad
 *
 */
@SuppressWarnings("unused")
public class ObjectFilteredByCustomAttributeValueReport {
	
	private static String srcCustomAttrName="PartyType";
	private static String srcCustomAttrValue="Legal Entity";

	public ObjectFilteredByCustomAttributeValueReport() {
	}

	/**
	 * @param args
	 */
	public static HashMap<String, String> main(String URL, String USER, String PASS) {
		// TODO Auto-generated method stub
		
		ObjectFilteredByCustomAttributeValueReport b=new ObjectFilteredByCustomAttributeValueReport();
		HashMap<String, String> map = new HashMap<String, String>();
		StringBuilder sb = null;
		//Connect to the EIC REST Instance 
		try {
			APIUtils.setupOnce(URL, USER, PASS);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			String attrName="";
			String attrValue="";
		
			//attrName = srcCustomAttrName;
			//attrValue = srcCustomAttrValue;
			
			sb = new StringBuilder();
			map = getCustomAttributeList();
//			String csv = "output1.csv";
//			CSVWriter writer = null;
//			
//			writer = new CSVWriter(new FileWriter(csv));
//			
//			
//			for (Entry<String, String> entry : map.entrySet()) {
//				attrName = entry.getKey();
//				attrValue = entry.getValue();
//				System.out.println(attrName + " : " + attrValue);
//				
////				String srcCustomAttrID=b.getCustomAttributeID(attrName); 
////				System.out.println("Source Attribute ID for "+srcCustomAttrName+" is "+srcCustomAttrID);
////				sb.append("Source Attribute ID for "+srcCustomAttrName+" is "+srcCustomAttrID);
////				sb.append("\n");
////				HashMap<String,HashMap<String, String>> objMap = b.getObjectFilteredByCustomAttrValue(srcCustomAttrID, attrValue);
////				
////
////				String header = "Object ID,Object Type,Object Name,Resource Name,Parent Object Name";
////				writer.writeNext(header.split(","));
////				//sb.append(header.split(","));
////				//sb.append("\n");
////				for(String colId : objMap.keySet()) {			
////					HashMap<String, String> col=objMap.get(colId);
////					writer.writeNext(new String[]{colId, col.get("objectType"),col.get("objectName"),col.get("resourceName"),col.get("parentObjectName")});
////					sb.append(colId+ "             "+col.get("objectType")+ " "+col.get("objectName")+ " "+col.get("resourceName")+ " "+col.get("parentObjectName")+"\n");
////					//sb.append("\n");
//				//}
//				
//				
//			}
			//writer.close();

			
									
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
			
		}
		for (Entry<String, String> entry : map.entrySet()) {
			String attrName = entry.getKey();
			String attrValue = entry.getValue();
			System.out.println(attrName + " : " + attrValue);
		}
		return map;
	}
	
	
	
	public HashMap<String,HashMap<String, String>> getObjectFilteredByCustomAttrValue(String srcCustomAttributeID, String srcCustomAttributeValue) throws Exception  {
		int total=1000;
		int offset=0;
		final int pageSize=300;
		
		HashMap<String,HashMap<String,String>> retMap=new HashMap<String,HashMap<String,String>>();
		
		String query = srcCustomAttributeID+":\""+srcCustomAttributeValue+"\"";
		
		while (offset<total) {
			ObjectsResponse response=APIUtils.READER.catalogDataObjectsGet(query, null, BigDecimal.valueOf(offset), BigDecimal.valueOf(pageSize), false);
			
			total=response.getMetadata().getTotalCount().intValue();
			offset+=pageSize;
			
			
			for(ObjectResponse or: response.getItems()) {
				System.out.println(or.getId());
				retMap.put(or.getId(), this.getObjectFullDetails(or.getId()));
				
			}
			if(offset >= total) System.out.println(total+"/"+total);
			else System.out.println(offset+"/"+total);
		}
		
		return retMap;
	}

	
	
	public static HashMap<String, String> getCustomAttributeList() throws Exception {
		int total = 1000;
		int offset = 0;
		final int pageSize = 300;
		
		String customAttributeName = new String();
		String customAttributeId = new String();
		
		//hashmap 
		HashMap<String, String> retMap = new HashMap<String, String>();

		while (offset < total) {
			try {
				AttributesResponse response = APIUtils.MODEL_READER.catalogModelsAttributesGet(null, null,
						BigDecimal.valueOf(offset), BigDecimal.valueOf(pageSize));
				total = response.getMetadata().getTotalCount().intValue();
				offset += pageSize;

				for (AttributeResponse ar : response.getItems()) {
					customAttributeName = ar.getName();
					customAttributeId = ar.getId();
					retMap.put(customAttributeName, customAttributeId);
				}
			} catch (ApiException e) {
				e.printStackTrace();
			}
		}

		return retMap;
	}
	
  public String getCustomAttributeID(String customAttributeName) throws Exception {
		int total=1000;
		int offset=0;
		final int pageSize=300;
		
		String customAttributeId = new String();
		boolean dup = false;
		
		while (offset<total) {
			try {			
				AttributesResponse response=APIUtils.MODEL_READER.catalogModelsAttributesGet(null, null, BigDecimal.valueOf(offset), BigDecimal.valueOf(pageSize));
				total=response.getMetadata().getTotalCount().intValue();
				offset+=pageSize;
				
				for(AttributeResponse ar: response.getItems()) {					
					if(ar.getName().equals(customAttributeName)) {
						if (customAttributeId != null && ! customAttributeId.equals("")) dup = true;
						customAttributeId=ar.getId();					
					}
				}
			} catch (ApiException e) {
				e.printStackTrace();
			}
		}
		
//		if (customAttributeId.equals("")) { 			
//			throw new Exception("Custom Attribute ID not found");
//		} else if (dup) {
//			throw new Exception("Duplicate Attribute ID found");
//  		} else {
//			return customAttributeId;
//		}
		return customAttributeId;
  }
	

	private HashMap<String, String> getObjectFullDetails(String Id) throws Exception {
		
		ObjectResponse or=APIUtils.READER.catalogDataObjectsIdGet(Id);			
						
		HashMap<String,String> hashtemp= new HashMap<String,String>();
		hashtemp.put("ID",or.getId());
		
		for(LinkedObjectResponse lor : or.getSrcLinks()) {
			if(lor.getAssociation().equals("com.infa.ldm.relational.TableColumn")) {
				hashtemp.put("parentObjectName",lor.getName());
				//HashMap<String,String> tMap = getObjectFullDetails(lor.getId());
				//hashtemp.put("schemaName",tMap.get("schemaName"));				
			}	
			if(lor.getAssociation().equals("com.infa.ldm.relational.SchemaTable")) {
				hashtemp.put("parentObjectName",lor.getName());								
			}
		}
		
		for( FactResponse f : or.getFacts()) {
			if (f.getAttributeId().equals("core.resourceName")) hashtemp.put("resourceName",f.getValue());
			if (f.getAttributeId().equals("core.name")) hashtemp.put("objectName",f.getValue());
			if (f.getAttributeId().equals("core.classType")) hashtemp.put("objectType",f.getValue());
		}
		
		return hashtemp;
	}

}
