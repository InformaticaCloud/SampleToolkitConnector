package com.informatica.cloud.adapter.sample;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.informatica.cloud.api.adapter.metadata.RecordAttribute;
import com.informatica.cloud.api.adapter.metadata.RecordAttributeScope;

/**
 * @author anair
 *
 */
public enum ReadOperationRuntimeAttributes {

	SOURCE_FILTER(1, "Source Filter","Specifies filter to be applied while querying Entity","STRING", null, null),
	SORTED_PORTS(2, "Sorted Ports","Specifies sort specification to be applied while querying for Entity Attribute values","STRING", null, null),
	MAXIMUM_PAGE_SIZE(3, "Maximum Page Size","Maximum Page size for creating Message Buffer","STRING", "50", null),
	GET_DELETED(4,"Get Deleted", "Fetch the Deleted Entries", "BOOLEAN", "NO", null),
	FIELD_TYPE(5,"Field Type", "Get the Type Of the Field To be Fetched", "STRING", "ALL","ALL/STANDARD/CUSTOM");

	private int id;
	private String name;
	private String desc;
	private String datatype;
	private String defaultValue;
	private List<String> listOfValues = new ArrayList<String>();
	private static List<RecordAttribute> list;
	private ReadOperationRuntimeAttributes(int id, String name, String desc, String datatype, String defaultValue, String listOfValues) {
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.datatype = datatype;
		this.defaultValue = defaultValue;
		if(listOfValues != null) {
			StringTokenizer token = new StringTokenizer(listOfValues, "/");
			while(token.hasMoreTokens()) {
				this.listOfValues.add(token.nextToken());
			}
		}
	}

	public static List<RecordAttribute> getAsList() {
		if(list == null) {
			list = new ArrayList<RecordAttribute>();
			for(ReadOperationRuntimeAttributes attr : ReadOperationRuntimeAttributes.values()) {
				RecordAttribute rAttrib = new RecordAttribute(
						RecordAttributeScope.RUNTIME);
				rAttrib.setId(attr.id);
				rAttrib.setName(attr.name);
				rAttrib.setDescription(attr.desc);
				rAttrib.setDatatype(attr.datatype);
				rAttrib.setDefaultValue(attr.defaultValue);
				rAttrib.setListOfValues(attr.listOfValues);
				list.add(rAttrib);
			}
		}
		return list;
	}	
}
