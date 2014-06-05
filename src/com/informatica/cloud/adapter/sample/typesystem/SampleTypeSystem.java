package com.informatica.cloud.adapter.sample.typesystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.informatica.cloud.adapter.sample.utils.AttributeTypeCode;
import com.informatica.cloud.adapter.sample.utils.SampleUtils;
import com.informatica.cloud.api.adapter.typesystem.DataType;
import com.informatica.cloud.api.adapter.typesystem.ITypeSystem;
import com.informatica.cloud.api.adapter.typesystem.JavaDataType;

/**
 * @author anair
 *
 */
public class SampleTypeSystem implements ITypeSystem{
	
	HashMap<DataType, List<JavaDataType>> dataTypeMap;
	List<DataType> nativeDataTypes;

	@Override
	public Map<DataType, List<JavaDataType>> getDatatypeMapping() {
		if(dataTypeMap == null){
			dataTypeMap = new HashMap<DataType, List<JavaDataType>>();
			for (DataType dt : getNativeDataTypes()) {
				try {
					dataTypeMap.put(dt, getJavaDataTypesFor(dt.getName()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return dataTypeMap;	}

	private List<JavaDataType> getJavaDataTypesFor(String dataTypeName) throws Exception {
		ArrayList<JavaDataType> listOfJavaDataTypes = new ArrayList<JavaDataType>();
		switch(AttributeTypeCode.fromValue(dataTypeName)){
		case INTEGER:
			listOfJavaDataTypes.add(JavaDataType.JAVA_INTEGER);
			listOfJavaDataTypes.add(JavaDataType.JAVA_BIGINTEGER);
			break;
			
		case BOOLEAN:
			listOfJavaDataTypes.add(JavaDataType.JAVA_BOOLEAN);
			break;
			
		case DATE:
			listOfJavaDataTypes.add(JavaDataType.JAVA_TIMESTAMP);
			break;
			
		case DATETIME:
			listOfJavaDataTypes.add(JavaDataType.JAVA_TIMESTAMP);
			break;
			
		case TIME:
			listOfJavaDataTypes.add(JavaDataType.JAVA_TIMESTAMP);
			break;
			
		case BIGDECIMAL:
		case DECIMAL:
			listOfJavaDataTypes.add(JavaDataType.JAVA_BIGDECIMAL);
			listOfJavaDataTypes.add(JavaDataType.JAVA_DOUBLE);
			listOfJavaDataTypes.add(JavaDataType.JAVA_FLOAT);
			break;
			
		case STRING:
			listOfJavaDataTypes.add(JavaDataType.JAVA_STRING);
			break;
		case DOUBLE:
			listOfJavaDataTypes.add(JavaDataType.JAVA_DOUBLE);
			break;
		case BINARY:
			listOfJavaDataTypes.add(JavaDataType.JAVA_PRIMITIVE_BYTEARRAY);
			break;
		case SHORT:
			listOfJavaDataTypes.add(JavaDataType.JAVA_SHORT);
			break;
		case BIGINT:
			listOfJavaDataTypes.add(JavaDataType.JAVA_BIGINTEGER);
			break;
		case LONG:
			listOfJavaDataTypes.add(JavaDataType.JAVA_LONG);
			break;
		case FLOAT:
			listOfJavaDataTypes.add(JavaDataType.JAVA_FLOAT);
			break;
			
		default:
			throw new Exception();

		}
		return listOfJavaDataTypes;
	}

	@Override
	public List<DataType> getNativeDataTypes() {
		if (nativeDataTypes == null) {
			nativeDataTypes = new ArrayList<DataType>();
			for (AttributeTypeCode type : AttributeTypeCode.values()) {
				int id = type.getDataTypeId();
				String name = type.getDataTypeName();
				DataType dt = new DataType(name, id); 
				dt.setDefaultPrecision(type.getDefaultPrecision());
				dt.setHasScale(type.hasScale());
				nativeDataTypes.add(dt);
			}
		}
		return nativeDataTypes;	}

}
