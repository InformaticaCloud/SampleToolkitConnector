package com.informatica.cloud.adapter.sample;

import java.util.ArrayList;
import java.util.List;

import com.informatica.cloud.api.adapter.metadata.RecordAttribute;
import com.informatica.cloud.api.adapter.metadata.RecordAttributeScope;

/**
 * @author anair
 *
 */
public enum RecordAttributes {
	
	PARENT_RECORD_NAME(1, "Parent Record Name","Parent Record Name","STRING", "","", true, true, "10000000"),
	CLASS_NAME(2, "Class Name","Class Nameof Primary Record","STRING", "","", true, true, "10000000");

	private int id;
	private String name;
	private String desc;
	private String datatype;
	private String defaultValue;
	private String groupName;
	private boolean isClientEditable;
	private boolean isClientVisible; 
	private String maxLength;
	private static List<RecordAttribute> list;
	private RecordAttributes(int id, String name, String desc, String datatype, String defaultValue, String groupName, boolean isClientEditable, boolean isClientVisible, String maxLength) {
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.datatype = datatype;
		this.defaultValue = defaultValue;
		this.groupName = groupName;
		this.isClientEditable = isClientEditable;
		this.isClientVisible = isClientVisible;
		this.maxLength = maxLength;
	}
	
	public static List<RecordAttribute> getAsList() {
		if(list == null) {
			list = new ArrayList<RecordAttribute>();
			for(RecordAttributes attr : RecordAttributes.values()) {
				RecordAttribute rAttrib = new RecordAttribute(
						RecordAttributeScope.DESIGNTIME);
				rAttrib.setId(attr.id);
				rAttrib.setName(attr.name);
				rAttrib.setDescription(attr.desc);
				rAttrib.setDatatype(attr.datatype);
				rAttrib.setDefaultValue(attr.defaultValue);
				rAttrib.setGroupName(attr.groupName);
				rAttrib.setClientEditable(attr.isClientEditable);
				rAttrib.setClientVisible(attr.isClientVisible);
				rAttrib.setMaxLength(attr.maxLength);
				list.add(rAttrib);
			}
		}
		return list;
	}	

}
