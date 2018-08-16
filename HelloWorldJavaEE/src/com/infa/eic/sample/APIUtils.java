/**
 * 
 */
package com.infa.eic.sample;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;

import com.infa.products.ldm.core.rest.v2.client.api.ModelInfoApi;
import com.infa.products.ldm.core.rest.v2.client.api.ObjectInfoApi;
import com.infa.products.ldm.core.rest.v2.client.api.ObjectModificationApi;
import com.infa.products.ldm.core.rest.v2.client.invoker.ApiException;
import com.infa.products.ldm.core.rest.v2.client.models.AttributeResponse;
import com.infa.products.ldm.core.rest.v2.client.models.AttributesResponse;
import com.infa.products.ldm.core.rest.v2.client.models.FactResponse;
import com.infa.products.ldm.core.rest.v2.client.models.LinkedObjectResponse;
import com.infa.products.ldm.core.rest.v2.client.models.ObjectResponse;
import com.infa.products.ldm.core.rest.v2.client.models.ObjectsResponse;

/**
 * @author gpathak
 *
 */
public final class APIUtils {

	public static final String TABLE_CLASSTYPE = "com.infa.ldm.relational.Table";
	public static final String COL_CLASSTYPE = "com.infa.ldm.relational.Column";
	public static final String DOMAIN_CLASSTYPE = "com.infa.ldm.profiling.DataDomain";
	public static final String CORE_NAME = "core.name";
	public static final String CORE_RESOURCE_NAME = "core.resourceName";
	public static final String BGTERM = "com.infa.ldm.bg.BGTerm";

	public static final String DATASET_FLOW = "core.DataSetDataFlow";

	/**
	 * Access URL of the EIC Instance
	 */
	private static String URL = ""; //http://psvrh7iwcmg1001.informatica.com:9085/access/2

	/**
	 * Credentials.
	 */
	private static String USER = ""; // Enter Username: gpathak
	private static String PASS = ""; // Enter password: welcome1

	public final static ObjectInfoApi READER = new ObjectInfoApi();
	public final static ObjectModificationApi WRITER = new ObjectModificationApi();

	public final static ModelInfoApi MODEL_READER = new ModelInfoApi();

	public final static void setupOnce(String URL, String USER, String PASS) {
		READER.getApiClient().setUsername(USER);
		READER.getApiClient().setPassword(PASS);
		READER.getApiClient().setBasePath(URL);

		WRITER.getApiClient().setUsername(USER);
		WRITER.getApiClient().setPassword(PASS);
		WRITER.getApiClient().setBasePath(URL);
		MODEL_READER.getApiClient().setUsername(USER);
		MODEL_READER.getApiClient().setPassword(PASS);
		MODEL_READER.getApiClient().setBasePath(URL);
	}

	public static final String getValue(ObjectResponse obj, String name) {
		for (FactResponse fact : obj.getFacts()) {
			if (name.equals(fact.getAttributeId())) {
				return fact.getValue();
			}
		}
		return null;
	}

	/**
	 * Returns a hashmap of <assetID, assetName> where data assets belong to the
	 * provided type and resource
	 * 
	 * @param resourceName EIC Resource Name
	 * @param type         Class ID of the asset type
	 * @return hashmap of <assetID,assetName>
	 * @throws Exception
	 */
	public static final HashMap<String, String> getAssetsByType(String resourceName, String type, Boolean Preview) throws Exception {
			
		int total = 1000;
		int offset = 0;
		// Get objects in increments of 300
		final int pageSize = 300;

		// Standard Lucene style object query to get assets of a given type from a given
		// resource.
		String query = CORE_RESOURCE_NAME + ":\"" + resourceName + "\" AND core.allclassTypes:\"" + type + "\"";

		HashMap<String, String> retMap = new HashMap<String, String>();

		while (offset < total) {
			
			// Query the Object READER
			ObjectsResponse response = READER.catalogDataObjectsGet(query, null, BigDecimal.valueOf(offset),
					BigDecimal.valueOf(pageSize), false);

			total = response.getMetadata().getTotalCount().intValue();
			offset += pageSize;

			// Iterate over returned objects and add them to the return hashmap
			for (ObjectResponse or : response.getItems()) {
				String curVal = getValue(or, CORE_NAME);
				if (curVal != null) {
					// Hashkey is the object ID.
					retMap.put(or.getId(), curVal);
				}
			}
			if(Preview == true ) {
				System.out.println("-------------");
				System.out.println(offset);
				total = 0;
				//break;
			}			
		}
		return retMap;
	}

	public static final HashMap<String, HashSet<String>> getTableColumnMap() throws Exception {
		int total = 1000;
		int offset = 0;
		final int pageSize = 300;

		String query = "* AND core.allclassTypes:\"" + TABLE_CLASSTYPE + "\"";
		HashMap<String, HashSet<String>> retMap = new HashMap<String, HashSet<String>>();

		while (offset < total) {
			ObjectsResponse response = READER.catalogDataObjectsGet(query, null, BigDecimal.valueOf(offset),
					BigDecimal.valueOf(pageSize), false);

			total = response.getMetadata().getTotalCount().intValue();
			offset += pageSize;

			for (ObjectResponse or : response.getItems()) {
				HashSet<String> colSet = new HashSet<String>();
				retMap.put(or.getId(), colSet);
				for (LinkedObjectResponse lr : or.getDstLinks()) {
					if (lr.getAssociation().equals("com.infa.ldm.relational.TableColumn")) {
						colSet.add(lr.getId());
					}
				}

			}
		}
		return retMap;
	}

	public static String getCustomAttributeID(String customAttributeName) throws Exception {
		int total = 1000;
		int offset = 0;
		final int pageSize = 300;

		String customAttributeId = new String();
		boolean dup = false;

		while (offset < total) {
			try {
				AttributesResponse response = APIUtils.MODEL_READER.catalogModelsAttributesGet(null, null,
						BigDecimal.valueOf(offset), BigDecimal.valueOf(pageSize));
				total = response.getMetadata().getTotalCount().intValue();
				offset += pageSize;

				for (AttributeResponse ar : response.getItems()) {
					if (ar.getName().equals(customAttributeName)) {
						if (customAttributeId != null && !customAttributeId.equals(""))
							dup = true;
						customAttributeId = ar.getId();
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
					customAttributeName = ar.toString();
					customAttributeId = ar.getId();
					retMap.put(customAttributeName, customAttributeId);
				}
			} catch (ApiException e) {
				e.printStackTrace();
			}
		}

		return retMap;
	}

}
