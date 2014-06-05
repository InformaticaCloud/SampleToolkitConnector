package com.informatica.cloud.adapter.sample.utils;

/**
 * @author anair
 *
 */
public enum AttributeTypeCode {
	STRING("String",1,0,false),
	INTEGER("Integer",2, 10,true),
	DOUBLE("Double",3, 15,true),
	BOOLEAN("Boolean",4,10,true),
	DATETIME("DateTime",5, 26,true),
	DATE("Date",6, 19,true),
	DECIMAL("Decimal",7,28,true),
	BINARY("Byte",8,0,true),
	TIME("Time",9,15,true),
	SHORT("Short", 10, 10,true),
	LONG("Long", 11, 10,true),
	BIGINT("BigInteger", 12, 19,true),
	FLOAT("Float", 13, 10,true),
	BIGDECIMAL("BigDecimal", 14, 15,true);

    private String dataTypeName;
    private  int dataTypeId;
    private int defaultPrecision;
    private boolean hasScale; 

    public boolean hasScale() {
		return hasScale;
	}
	AttributeTypeCode(String dataTypeName, int dataTypeId, int defaultPrecision, boolean hasScale) {
        this.dataTypeName = dataTypeName;
        this.dataTypeId = dataTypeId;
        this.defaultPrecision = defaultPrecision;
        this.hasScale = hasScale;
    }
    public String getDataTypeName() {
        return this.dataTypeName;
    }
  
    public int getDataTypeId() {
        return this.dataTypeId;
    }
    
    public static AttributeTypeCode fromValue(String value) {
        for(AttributeTypeCode c : AttributeTypeCode.values()) {
              if(c.getDataTypeName().equals(value)) {
                    return c;
              }
        }
        throw new IllegalArgumentException(value);
    }
    public int getDefaultPrecision() {
    	return defaultPrecision;
    }

	
}
