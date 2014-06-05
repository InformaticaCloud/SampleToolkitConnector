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
public enum WriteOperationRuntimeAttributes {
	
	REPLACE_ALL(1, "Replace All","Replace all the entries","BOOLEAN", null, null),
	ERROR_THRESHOLD(2, "Error Threshold","Error Threshold","NUMBER", "10", null),
	TARGET_LOAD_TYPE(3, "Target Load Type","Specified how to load the Target","STRING", "NORMAL", "NORMAL/BULK/STANDARD");

	private int id;
	private String name;
	private String desc;
	private String datatype;
	private String defaultValue;
	private List<String> listOfValues = new ArrayList<String>();
	private static List<RecordAttribute> list;
	
	private WriteOperationRuntimeAttributes(int id, String name, String desc, String datatype, String defaultValue, String listOfValues) {
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
		if(list == null){
			list = new ArrayList<RecordAttribute>();
			for(WriteOperationRuntimeAttributes attr : WriteOperationRuntimeAttributes.values()) {
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
