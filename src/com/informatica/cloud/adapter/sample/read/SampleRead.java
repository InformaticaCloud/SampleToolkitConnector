package com.informatica.cloud.adapter.sample.read;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;

import com.informatica.cloud.adapter.sample.SampleConstants;
import com.informatica.cloud.adapter.sample.connection.SampleConnection;
import com.informatica.cloud.adapter.sample.plugin.SamplePlugin;
import com.informatica.cloud.adapter.sample.utils.AttributeTypeCode;
import com.informatica.cloud.adapter.sample.utils.SampleUtils;
import com.informatica.cloud.api.adapter.common.ELogMsgLevel;
import com.informatica.cloud.api.adapter.common.ILogger;
import com.informatica.cloud.api.adapter.connection.ConnectionFailedException;
import com.informatica.cloud.api.adapter.logging.LoggerMessageHandler;
import com.informatica.cloud.api.adapter.metadata.AdvancedFilterInfo;
import com.informatica.cloud.api.adapter.metadata.Field;
import com.informatica.cloud.api.adapter.metadata.FilterInfo;
import com.informatica.cloud.api.adapter.metadata.FilterOperation;
import com.informatica.cloud.api.adapter.metadata.RecordInfo;
import com.informatica.cloud.api.adapter.plugin.PluginVersion;
import com.informatica.cloud.api.adapter.runtime.IRead;
import com.informatica.cloud.api.adapter.runtime.exception.DataConversionException;
import com.informatica.cloud.api.adapter.runtime.exception.FatalRuntimeException;
import com.informatica.cloud.api.adapter.runtime.exception.InitializationException;
import com.informatica.cloud.api.adapter.runtime.exception.ReadException;
import com.informatica.cloud.api.adapter.runtime.exception.ReflectiveOperationException;
import com.informatica.cloud.api.adapter.runtime.utils.IOutputDataBuffer;
import com.informatica.cloud.api.adapter.typesystem.DataType;
import com.informatica.cloud.api.adapter.typesystem.JavaDataType;
import com.sample.wsproxy.Exception_Exception;
import com.sample.wsproxy.Record;


public class SampleRead implements IRead {
	private static final String MSG_BUNDLE_NAME = "SampleAdapterResourceBundle";
	public static final String SAMPLE_READER_PREFIX = "SAMPLE_READER";

	private final SamplePlugin dummyPlugin;
	private final SampleConnection dummyConn;
	private Map<String,Map<String, String>> recordAttributes = new HashMap<String, Map<String,String>>();
	private Map<String, String> readOperationAttributes = new HashMap<String, String>();
	private List<RecordInfo> secondaryRecordInfoList = new ArrayList<RecordInfo>();
	private RecordInfo primaryRecordInfo;
	private List<Field> fieldList = new ArrayList<Field>();
	private Random randomGenerator = new Random();
	private List<FilterInfo> filterInfoList = new ArrayList<FilterInfo>();
	private AdvancedFilterInfo advancedFilterInfo;
	private List<Record> recordList = new ArrayList<Record>();
	private Object[] rowData;
	private LoggerMessageHandler logger;
	public SampleRead(SamplePlugin plugin, SampleConnection conn) {
		dummyPlugin = plugin;
		dummyConn = conn;
		logger = new LoggerMessageHandler(ResourceBundle.getBundle(
				MSG_BUNDLE_NAME, Locale.getDefault()), plugin.getLogger(),
				SAMPLE_READER_PREFIX);
		advancedFilterInfo = new AdvancedFilterInfo();
		
	}
	
	LinkedHashMap<String,Field> displayNameFieldMap = null;

	@Override
	public boolean read(IOutputDataBuffer dataBufferInstance) throws 
		ConnectionFailedException, ReflectiveOperationException, ReadException,
		DataConversionException, FatalRuntimeException{
		boolean status = true;
		try {
			//List<List<Object>> recordData = new ArrayList<List<Object>>();
			//batchSize = dataBufferInstance.getBatchSize();
			
			//Assuming that the web service returns the entire data in one call
			//page size is ignored
			com.sample.wsproxy.SampleDataService service = new com.sample.wsproxy.SampleDataService();
			com.sample.wsproxy.SampleData dummyReadData = service.getSampleDataPort();
			
			//read the data from the service
			recordList = dummyReadData.read(primaryRecordInfo.getRecordName());
			
			//get the primaryRecord Class Name from the record Attributes
			//fetch the methods supported on the primary Record
			String primaryRecordClassName = recordAttributes.get(primaryRecordInfo.getRecordName())
				.get(SampleConstants.CLASS_NAME);
			Class<?> primaryClass = Class.forName(primaryRecordClassName);
			Method[] primaryRecordMethods = primaryClass.getMethods();
			
			//ASSUMPTION only single child
			//get the child record name and get its corresponding 'list' method
			//defined in the primary record class
			Method secListMethod = null;
			Method[] secondaryRecordMethods = null;
			if(secondaryRecordInfoList != null && secondaryRecordInfoList.size() > 0) {
				String secondaryClassSimpleName = secondaryRecordInfoList.get(0).getRecordName();
				String secondaryClassFullyQualifiedName = recordAttributes.get(secondaryRecordInfoList.get(0).getRecordName())
					.get(SampleConstants.CLASS_NAME);
				String secondaryListMethod = "get" + secondaryClassSimpleName + "List";
				secListMethod = primaryClass.getMethod(secondaryListMethod);
				//fetch the methods supported on child record
				secondaryRecordMethods = Class.forName(secondaryClassFullyQualifiedName).getMethods();
			}
			
			
			for(Record record : recordList) {
				
				List data = null;
				//if we have a secondary record list defined then invoke the 'list' method
				//corresponding to the secondary record on the primary record instance
				//to get a list of secondary record
				if(secondaryRecordInfoList != null && secondaryRecordInfoList.size()>0) {
					Object listVal = secListMethod.invoke(record);
					data = (List)listVal;
				}
				
				//if there is no data for the secondary record or a secondary record is not
				//specified then set data only for primary record
				//otherwise we need to repeat the primary row data for each row in the 
				//list of secondary row data
				//hence we iterate over the secondary record data and for each iteration
				//repeat the primary record data
				if(data != null && data.size() > 0 ) {
					for(int i = 0; i < data.size(); i++) {
						int j = 0;
						clearRowData();
						Object child = data.get(i);
						for (Field aField : fieldList) {
							if(aField.getContainingRecord().getRecordName().equals(primaryRecordInfo.getRecordName())) {
								for (Method aMethod : primaryRecordMethods) {
									if(aMethod.getName().equalsIgnoreCase("get" + aField.getDisplayName())) {
										Object value = aMethod.invoke(record);
										DataType toolkitDatatype = aField.getDatatype();
										//convert to Java datatype
										value = convertToJavaDatatype(value, toolkitDatatype);
										rowData[j] = value;
										break;
									}
								}
							} else {
								for(Method aMethod : secondaryRecordMethods) {
									if(aMethod.getName().equalsIgnoreCase("get" + aField.getDisplayName())
											|| aMethod.getName().equalsIgnoreCase("is" + aField.getDisplayName())) {
										Object value = aMethod.invoke(child);
										DataType toolkitDatatype = aField.getDatatype();
										//convert to Java datatype
										value = convertToJavaDatatype(value, toolkitDatatype);
										rowData[j] = value;
										break;
									}
								}
							}
							j++;
						}
						dataBufferInstance.setData(rowData);
					}
				} else {
					int j = 0;
					//boolean validAfterFilter = true;
					clearRowData();
					for (Field aField : fieldList) {
						
						if(aField.getContainingRecord().getRecordName().equals(primaryRecordInfo.getRecordName())) {
							for (Method aMethod : primaryRecordMethods) {
								if(aMethod.getName().equalsIgnoreCase("get" + aField.getDisplayName())
										|| aMethod.getName().equalsIgnoreCase("is" + aField.getDisplayName())) {
									
									Object value = aMethod.invoke(record);
									DataType toolkitDatatype = aField.getDatatype();
									//convert to Java datatype
									value = convertToJavaDatatype(value, toolkitDatatype);
									rowData[j] = value;
									break;
									
								}
							}
						} 
						j++;
					}
					if(validAfterAplyingFilterToRowData())
						dataBufferInstance.setData(rowData);
				}
				
				
			}
		} catch(ClassNotFoundException e) {
			throw new ReflectiveOperationException(e.getMessage());
		} catch (Exception_Exception e) {
			throw new ReadException(e.getMessage());
		} catch (SecurityException e) {
			throw new ReflectiveOperationException(e.getMessage());
		} catch (NoSuchMethodException e) {
			throw new ReflectiveOperationException(e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new ReflectiveOperationException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new ReflectiveOperationException(e.getMessage());
		} catch (InvocationTargetException e) {
			throw new ReflectiveOperationException(e.getMessage());
		}
		return status;
	}

	
	private boolean validAfterAplyingFilterToRowData() throws FatalRuntimeException {
		
		boolean toBeAdded = true;
		
		//check if the target fields have all the filter fields
		
		for(FilterInfo filterInfo : filterInfoList) {
			if (!isFilteredFieldProjected(filterInfo))
				throw new FatalRuntimeException("The Target fields do not have the filter column " + filterInfo.getField().getDisplayName());
		}
			
		for(int filterCounter = 0; filterCounter < filterInfoList.size(); filterCounter++){
			FilterInfo filterInfo =  filterInfoList.get(filterCounter);
			boolean hasFilterPassed = validateFilter(filterInfo,displayNameFieldMap);
			if(filterCounter == 0){
				//ignore the filter condition
				toBeAdded = toBeAdded && hasFilterPassed;
				
			}else{
				switch(filterInfo.getCondition()){
				
				case AND:
					toBeAdded = toBeAdded && hasFilterPassed;	
					break;
				case OR:
					toBeAdded = toBeAdded || hasFilterPassed;
					break;
				}

			}
				 
		}
		return toBeAdded;
	}

	private boolean isFilteredFieldProjected(FilterInfo filterInfo) {
		return displayNameFieldMap.containsKey(filterInfo.getField().getDisplayName());
	}

	private boolean checkStringDataType(JavaDataType jdt,FilterOperation operation) throws FatalRuntimeException{
		if(jdt.equals(JavaDataType.JAVA_STRING))
			return true;
		else
			throw new FatalRuntimeException(operation +" can only work with String datatype");
	}
	
	private boolean validateFilter(FilterInfo filterInfo,LinkedHashMap<String, Field> displayNameFieldMap) throws FatalRuntimeException {
		Object[] displayNameArr = (Object[]) displayNameFieldMap.keySet().toArray();
		//this list is useful for getting the index of a field and map it to rowData[index] for its value
		List<Object> displayNameList =  Arrays.asList(displayNameArr);
		
		Field field= displayNameFieldMap.get(filterInfo.getField().getDisplayName());
		JavaDataType jdt = field.getJavaDatatype();
		
		List<String> filterValues = filterInfo.getValues();
		String filterValue = null ;
		Object objFilterValue = null;
		
		if( filterValues!=null && !filterValues.isEmpty()){
			//assumption : the filtervalue list has a single value
			filterValue = filterValues.get(0);
			
			/* In case of a lookup tranform, the filter value may come from another table and may be null.
			 * In such a  case, instead of throwing a FatalRunTimeException, we return false in the later 
			 * switch case statements 
			 */
			
			if(filterValue != null) 
				objFilterValue = SampleUtils.createJavaObjectFromString(filterValue,jdt);	

		}
		
		Object rowDataValue = rowData[displayNameList.indexOf(field.getDisplayName())];
		boolean retValue = true;
		FilterOperation operation = filterInfo.getOperator();
		Comparable filterVal = null;
		Comparable rowVal = null;
		switch(operation){
		//unary operations
		case isNotNull: 
				retValue = (rowDataValue != null) ? true : false;
				break;
		case isNull:
				retValue = (rowDataValue == null) ? true : false;
				break;

		//string specific operations		
		case contains:
			//should work only with String
			if(rowDataValue == null || filterValue == null)
				retValue = false;
			else if(checkStringDataType(jdt, operation)){
				String rowValue = rowDataValue.toString();
				retValue = rowValue.contains(filterValue);
			}
			break;	
			
		case endsWith:
			//should work only with String
			if(rowDataValue == null || filterValue == null)
				retValue = false;
			else if (checkStringDataType(jdt, operation)) {
				String rowValue = rowDataValue.toString();
				retValue = rowValue.endsWith(filterValue);
			}
			break;
			
		case startsWith:
			//should work only with String
			if(rowDataValue == null || filterValue == null)
				retValue = false;
			else if(checkStringDataType(jdt, operation)){
				String rowValue = rowDataValue.toString();
				retValue = rowValue.startsWith(filterValue);
			}
			break;
		
		//general operations	
		case equals:
			if(rowDataValue == null || objFilterValue == null) 
				retValue = false;
			else
				retValue = objFilterValue.equals(rowDataValue);
			break;
			
		case notEquals:
			if(rowDataValue == null || objFilterValue == null)
				retValue = false;
			else
				retValue = !objFilterValue.equals(rowDataValue);
			break;
			
		case greaterOrEquals:
			if(rowDataValue == null || objFilterValue == null){ 
				retValue = false;
			}else {
				filterVal = (Comparable) objFilterValue;
				rowVal = (Comparable) rowDataValue;
				
				retValue = (rowVal.compareTo(filterVal) >= 0);
			}
				
			break;
			
		case greaterThan:
			if(rowDataValue == null || objFilterValue == null){
				retValue = false;
			}else{
				filterVal = (Comparable)objFilterValue;
				rowVal = (Comparable)rowDataValue;
				
				retValue = (rowVal.compareTo(filterVal) > 0) ;
			}
			break;
			
		case lessOrEquals:
			if(rowDataValue == null || objFilterValue == null){
				retValue = false;
			}else{
				filterVal = (Comparable)objFilterValue;
				rowVal = (Comparable)rowDataValue;
				
				retValue = (rowVal.compareTo(filterVal) <= 0) ;
			}
			break;
			
		case lessThan:
			if(rowDataValue == null || objFilterValue == null){
				retValue = false;
			}else{
				filterVal = (Comparable)objFilterValue;
				rowVal = (Comparable)rowDataValue;

				retValue = (rowVal.compareTo(filterVal) < 0) ;
			}
			break;

		default: throw new FatalRuntimeException("Invalid operator ! "+operation);
		
		}
		
		return retValue;
	}

	
	private LinkedHashMap<String,Field> createMapFromDisplaynameToFieldList() {
		displayNameFieldMap = new LinkedHashMap<String, Field>();
		
		for(Field field: fieldList){
			displayNameFieldMap.put(field.getDisplayName(), field);
		}
		return displayNameFieldMap;
	}

	private void clearRowData() {
		for(int i = 0; i < rowData.length ; i++) {
			rowData[i] = null;
		}
		
	}

	public static Object convertToJavaDatatype(Object value, DataType toolkitDatatype) {
		Object returnValue = null;
		//TODO handle other datatypes
		switch(AttributeTypeCode.fromValue(toolkitDatatype.getName())) {
		case INTEGER : returnValue = (Integer)value;
						break;
		case STRING : returnValue = value == null ? null : value.toString();
						break;
		case DOUBLE : returnValue = (Double) value;
						break;
		case DATE :
		case DATETIME : 
		case TIME : returnValue = value == null ? null : new Timestamp(((XMLGregorianCalendar)value).toGregorianCalendar().getTime().getTime());
						break;
		case DECIMAL : returnValue = (BigDecimal) value;
						break;
						
		case BOOLEAN : returnValue = (Boolean) value;
						break;
		case BINARY :
			// Note: This is a sample implementation. Ideally we should be getting data from our webservice.
			// our current version of the webservice does not return binary data.
			String s = "abcd";
			returnValue = s.getBytes();
			break;
		case SHORT : returnValue = (Short) value;
						break;
		case LONG : returnValue = (Long) value;
						break;
		case BIGINT : returnValue = (BigInteger) value;
						break;
						
		case FLOAT : returnValue = (Float) value;
		break;
		
						
		}
		return returnValue;
		
	}

	private void logMap(Map<String, String> map) {
		Set<String> keys = map.keySet();
		Iterator<String> iter = keys.iterator();
		while(iter.hasNext()) {
			String key = iter.next();
			String value = map.get(key);
			logger.logMsg("10002_KeyValue", ELogMsgLevel.INFO, key, value);
		}
	}
	
	@Override
	public void setRecordAttributes(RecordInfo recordInfo, Map<String, String> srcDesigntimeAttribs) {		
		logger.logMsg("10001_LogRecordAttributes", ELogMsgLevel.INFO, recordInfo.getRecordName());
		this.recordAttributes.put(recordInfo.getRecordName(), srcDesigntimeAttribs);
		logMap(srcDesigntimeAttribs);		
	}

	@Override
	public void setOperationAttributes(Map<String, String> roAttribs) {
		logger.logMsg("10003_LogOperationAttributes", ELogMsgLevel.INFO);
		this.readOperationAttributes.clear();
		this.readOperationAttributes.putAll(roAttribs);
		logMap(roAttribs);
	}

	@Override
	public void setFieldList(List<Field> fieldList) {
		this.fieldList = fieldList;
		rowData = new Object[this.fieldList.size()];
		createMapFromDisplaynameToFieldList();// is used in filtering
		
	}

	@Override
	public void setPrimaryRecord(RecordInfo primaryRecordInfo) {
		logger.logMsg("10004_PrimaryRecord", ELogMsgLevel.INFO, primaryRecordInfo.getRecordName());
		this.primaryRecordInfo = primaryRecordInfo;		
	}
	
	@Override
	public void setRelatedRecords(List<RecordInfo> secondaryRecordInfoList) {
		if(secondaryRecordInfoList != null && secondaryRecordInfoList.size() != 0)
			this.secondaryRecordInfoList.addAll(secondaryRecordInfoList);
	}

	@Override
	public void setFilters(List<FilterInfo> filterInfoList) {
		if(filterInfoList != null && filterInfoList.size() != 0)
			this.filterInfoList.addAll(filterInfoList);
	}

	@Override
	public void setAdvancedFilters(AdvancedFilterInfo advancedFilterInfo) {
		this.advancedFilterInfo = advancedFilterInfo;
		
	}

	@Override
	public void setMetadataVersion(PluginVersion arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initializeAndValidate() throws InitializationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setChildRecords(List<RecordInfo> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	/*public static void main(String args[]) {
		SampleRead reader = new SampleRead (new SamplePlugin(), null);
		Map<String, String> readOpAttribs = new HashMap<String, String>();
		readOpAttribs.put("Test ReadOp", "Test ReadOp");
		Map<String, String> recAttribs = new HashMap<String, String>();
		recAttribs.put("Test Record", "Test Record");
		reader.setReadOperationAttributes(readOpAttribs);
		RecordInfo recInfo = new RecordInfo();
		recInfo.setRecordName("TEST_OBJECT");
		reader.setRecordAttributes(recInfo, recAttribs);
		List<Field> fieldList = new ArrayList<Field>();
		Field f1 = new Field();
		f1.setDisplayName("Field1");
		f1.setDefaultValue("");
		f1.setUniqueName("Field1");
		DataType dt1 = new DataType("Integer", 1);
		List<FieldAttribute> fieldAttribs1 = new ArrayList<FieldAttribute>();
		FieldAttribute aFieldAttrib1 = new FieldAttribute();
		aFieldAttrib1.setName(SampleConstants.REQUIRED_LEVEL);
		aFieldAttrib1.setValue("true");
		fieldAttribs1.add(aFieldAttrib1);
		f1.setCustomAttributes(fieldAttribs1);
		f1.setDatatype(dt1);
		f1.setPrecision(15);
		fieldList.add(f1);
		Field f2 = new Field();
		f2.setDisplayName("Field2");
		f2.setDefaultValue("");
		f2.setUniqueName("Field2");
		DataType dt2 = new DataType("String", 2);
		List<FieldAttribute> fieldAttribs2 = new ArrayList<FieldAttribute>();
		FieldAttribute aFieldAttrib2 = new FieldAttribute();
		aFieldAttrib2.setName(SampleConstants.REQUIRED_LEVEL);
		aFieldAttrib2.setValue("false");
		fieldAttribs2.add(aFieldAttrib2);
		f2.setCustomAttributes(fieldAttribs2);
		f2.setDatatype(dt2);
		f2.setPrecision(15);
		fieldList.add(f2);
		reader.setFieldList(fieldList);
		reader.setPageSize(100);
		while(reader.hasMoreData()) {
			List<List<Object>> readData = reader.read();
			for(List<Object> objects : readData) {
				for(Object anObject : objects) {
					System.out.println(anObject.toString());
				}
			}
		}
		
		
	}*/

}

