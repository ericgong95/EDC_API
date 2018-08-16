package com.infa.eic.sample;
import java.math.BigDecimal;
import java.util.ArrayList;

import com.infa.products.ldm.core.rest.v2.client.invoker.ApiException;
import com.infa.products.ldm.core.rest.v2.client.models.AttributeResponse;
import com.infa.products.ldm.core.rest.v2.client.models.AttributesResponse;
import com.infa.products.ldm.core.rest.v2.client.models.FactRequest;
import com.infa.products.ldm.core.rest.v2.client.models.ObjectIdRequest;
import com.infa.products.ldm.core.rest.v2.client.models.ObjectRefResponse;
import com.infa.products.ldm.core.rest.v2.client.models.ObjectResponse;
import com.infa.products.ldm.core.rest.v2.client.models.ObjectsResponse;
import com.infa.products.ldm.core.rest.v2.client.models.RefAttributeResponse;
import com.infa.products.ldm.core.rest.v2.client.utils.ObjectAdapter;

/**
 * 
 */

/**
 * This program uses the EIC REST API to copy values from one custom attribute to the other.
 * @author lntrapad
 *
 */
@SuppressWarnings("unused")
public class CustomAttributeValuesCopier {
	
	private static String srcCustomAttrName="Business Description";
	private static String tgtCustomAttrName="Rich Business Description";
	// set this flag to true if you want to override existing values in the target custom attribute
	private static Boolean overrideValue=false;

	public CustomAttributeValuesCopier() {
	}

	/**
	 * @param args
	 */
	public static void main(String URL, String USER, String PASS) {
		// TODO Auto-generated method stub
		
		CustomAttributeValuesCopier b=new CustomAttributeValuesCopier();
		
		//Connect to the EIC REST Instance 
		try {
			APIUtils.setupOnce(URL, USER, PASS);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			
			String srcCustomAttrID=b.getCustomAttributeID(srcCustomAttrName);
			System.out.println("Source Attribute ID for "+srcCustomAttrName+" is "+srcCustomAttrID);
			String tgtCustomAttrID=b.getCustomAttributeID(tgtCustomAttrName);
			System.out.println("Target Attribute ID for "+tgtCustomAttrName+" is "+tgtCustomAttrID);
			
			
			
			System.out.println("Updating Resources");
			b.bulkCopier(srcCustomAttrID, tgtCustomAttrID, "*" +" AND core.allclassTypes:\"core.Resource\"");
			
			System.out.println("Updating Tables");
			b.bulkCopier(srcCustomAttrID, tgtCustomAttrID, "*" +" AND core.allclassTypes:\""+ APIUtils.TABLE_CLASSTYPE+"\"");
			
			System.out.println("Updating Columns");
			b.bulkCopier(srcCustomAttrID, tgtCustomAttrID, "*" +" AND core.allclassTypes:\""+ APIUtils.COL_CLASSTYPE+"\"");
			
			
			
		} catch(Exception e) {
			e.printStackTrace();
			
		}
		

	}
	
	
	
	public void bulkCopier(String srcCustomAttributeID, String tgtCustomAttributeID, String query) throws Exception  {
		int total=1000;
		int offset=0;
		final int pageSize=300;
		
		while (offset<total) {
			ObjectsResponse response=APIUtils.READER.catalogDataObjectsGet(query, null, BigDecimal.valueOf(offset), BigDecimal.valueOf(pageSize), false);
			
			total=response.getMetadata().getTotalCount().intValue();
			offset+=pageSize;
			
			
			for(ObjectResponse or: response.getItems()) {
				
				ObjectIdRequest request=ObjectAdapter.INSTANCE.copyIntoObjectIdRequest(or);
				String srcVal=APIUtils.getValue(or,srcCustomAttributeID);
				String tgtVal=APIUtils.getValue(or,tgtCustomAttributeID);
				// only update value if targetValue is not set of if override flag set to true
				if(srcVal!=null && (tgtVal==null || overrideValue)) {
					//request.getFacts().remove(new FactRequest().attributeId(tgtCustomAttributeID).value(srcVal));
					request.addFactsItem(new FactRequest().attributeId(tgtCustomAttributeID).value(srcVal));							
														
					String ifMatch;
					try {
						ifMatch = APIUtils.READER.catalogDataObjectsIdGetWithHttpInfo(or.getId()).getHeaders().get("ETag").get(0);
					
						ObjectResponse newor=APIUtils.WRITER.catalogDataObjectsIdPut(or.getId(), request, ifMatch);
						System.out.println(or.getId()+":"+srcVal);
					} catch (ApiException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} 
			}
			if(offset >= total) System.out.println(total+"/"+total);
			else System.out.println(offset+"/"+total);
		}
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
		
		if (customAttributeId.equals("")) { 			
			throw new Exception("Custom Attribute ID not found");
		} else if (dup) {
			throw new Exception("Duplicate Attribute ID found");
  		} else {
			return customAttributeId;
		}
  }
	
	

}
