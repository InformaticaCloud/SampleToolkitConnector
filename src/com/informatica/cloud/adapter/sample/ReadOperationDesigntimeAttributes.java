package com.informatica.cloud.adapter.sample;

import java.util.ArrayList;
import java.util.List;

import com.informatica.cloud.api.adapter.metadata.RecordAttribute;
import com.informatica.cloud.api.adapter.metadata.RecordAttributeScope;

/**
 * @author anair
 *
 */
public enum ReadOperationDesigntimeAttributes {
	SOURCE_FILTER(1, "Source Filter","Specifies filter to be applied while querying Entity","STRING", "",""),
	SORTED_PORTS(2, "Sorted Ports","Order of ports based on which the data would be sorted","STRING", "","");
	
	private int id;
	private String name;
	private String desc;
	private String datatype;
	private String defaultValue;
	private String groupName;
	private static List<RecordAttribute> list;
	
	private ReadOperationDesigntimeAttributes(int id, String name, String desc, String datatype, String defaultValue, String groupName) {
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.datatype = datatype;
		this.defaultValue = defaultValue;
		this.groupName = groupName;
	}
	public static List<RecordAttribute> getAsList() {
		if(list == null) {
			list = new ArrayList<RecordAttribute>();
			for(ReadOperationDesigntimeAttributes attr : ReadOperationDesigntimeAttributes.values()) {
				RecordAttribute rAttrib = new RecordAttribute(
						RecordAttributeScope.DESIGNTIME);
				rAttrib.setId(attr.id);
				rAttrib.setName(attr.name);
				rAttrib.setDescription(attr.desc);
				rAttrib.setDatatype(attr.datatype);
				rAttrib.setDefaultValue(attr.defaultValue);
				rAttrib.setGroupName(attr.groupName);
				list.add(rAttrib);
			}
		}
		return list;
	}	

}
