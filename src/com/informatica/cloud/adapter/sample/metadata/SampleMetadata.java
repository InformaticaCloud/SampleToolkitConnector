package com.informatica.cloud.adapter.sample.metadata;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Pattern;

import com.informatica.cloud.adapter.sample.SampleConstants;
import com.informatica.cloud.adapter.sample.connection.SampleConnection;
import com.informatica.cloud.adapter.sample.plugin.SamplePlugin;
import com.informatica.cloud.adapter.sample.read.SampleRead;
import com.informatica.cloud.adapter.sample.utils.AttributeTypeCode;
import com.informatica.cloud.adapter.sample.utils.SampleUtils;
import com.informatica.cloud.api.adapter.common.ELogMsgLevel;
import com.informatica.cloud.api.adapter.logging.LoggerMessageHandler;
import com.informatica.cloud.api.adapter.metadata.CreateRecordResult;
import com.informatica.cloud.api.adapter.metadata.DataPreviewException;
import com.informatica.cloud.api.adapter.metadata.Field;
import com.informatica.cloud.api.adapter.metadata.FieldAttribute;
import com.informatica.cloud.api.adapter.metadata.FieldInfo;
import com.informatica.cloud.api.adapter.metadata.FilterInfo;
import com.informatica.cloud.api.adapter.metadata.IDefineMetadata;
import com.informatica.cloud.api.adapter.metadata.IMetadata;
import com.informatica.cloud.api.adapter.metadata.MetadataCreateException;
import com.informatica.cloud.api.adapter.metadata.MetadataReadException;
import com.informatica.cloud.api.adapter.metadata.RecordInfo;
import com.informatica.cloud.api.adapter.plugin.IPlugin;
import com.informatica.cloud.api.adapter.typesystem.DataType;
import com.sample.wsproxy.Exception_Exception;
import com.sample.wsproxy.Record;

/**
 * @author anair
 *
 */
public class SampleMetadata implements IMetadata, IDefineMetadata {

	private HashMap<String, String> classNameMap;
	private String jarFileLoc;
	private static final String metadataJarName = "sample-metadata.jar";
	
	private Map<String,Class<?>> builtInMap = new HashMap<String,Class<?>>();
	private IPlugin plugin;
	private LoggerMessageHandler logger;
	private static final String MSG_BUNDLE_NAME = "SampleAdapterResourceBundle";
	private static final String PREFIX = "SampleAdapter";
	
	private Map<String, List<Field>> customRecordFieldMap = new HashMap<String, List<Field>>();
	
 

	public SampleMetadata(SamplePlugin plugin, SampleConnection connection){
		this.plugin = plugin;
		logger = new LoggerMessageHandler(ResourceBundle.getBundle(SampleMetadata.MSG_BUNDLE_NAME, Locale.getDefault()), 
				plugin.getLogger(), SampleMetadata.PREFIX );
		String jarFilePath;
		try {
			jarFilePath = this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
			int lastIndexOfFileSeperator = jarFilePath.lastIndexOf('/');
			jarFileLoc = jarFilePath.substring(0, lastIndexOfFileSeperator + 1) + SampleMetadata.metadataJarName;
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.classNameMap = new HashMap<String, String>();
		builtInMap.put("int", Integer.class);
		builtInMap.put("long", Long.class);
		builtInMap.put("double", Double.class );
		builtInMap.put("float", Float.class);
		builtInMap.put("bool", Boolean.class);
		builtInMap.put("short", Short.class);
		builtInMap.put("byte", Byte.class);

	}
	
	@Override
	public List<RecordInfo> filterRecords(Pattern arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RecordInfo> getAllRecords() {
		logger.logMsg("20001_GetRecords", ELogMsgLevel.INFO);
		List<RecordInfo> recInfoList = new ArrayList<RecordInfo>();		
		try {
			JarInputStream jarFile = new JarInputStream(new FileInputStream(jarFileLoc));
			while(true) {
				JarEntry entry = jarFile.getNextJarEntry();
				if (entry == null)
					break;
				if (entry.getName().endsWith(".class")) {
					String className = entry.getName().replaceAll("/", ".").replace(".class", "").trim();
					Class<?> clz = Class.forName(className);
					Class<?> superClz = clz.getSuperclass();
					if (superClz != null && superClz.getSimpleName().equals("Record")) {
						classNameMap.put(clz.getSimpleName(), className);
						RecordInfo info = new RecordInfo();
						info.setRecordName(clz.getSimpleName());
						info.setCatalogName("");
						info.setRecordType("Standard Records");
						recInfoList.add(info);						
					}
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return recInfoList;
	}
/*
	@Override
	public List<RecordInfo> getContainedRecords(RecordInfo aRecordInfo) {
		
		List<RecordInfo> recInfoList = new ArrayList<RecordInfo>();
		
		if(aRecordInfo.getRecordName().equalsIgnoreCase("account")) {
			RecordInfo recInfo1 = new RecordInfo();
			recInfo1.setRecordName("Contact");
			recInfo1.setCatalogName("Standard Records");
			recInfo1.setLabel("Contact");
			RecordInfo recInfo2 = new RecordInfo();
			recInfo2.setRecordName("Opportunity");
			recInfo2.setCatalogName("Standard Records");
			recInfo2.setLabel("Opportunity");
			recInfoList.add(recInfo2);
			recInfoList.add(recInfo1);
		}
		
		return recInfoList;
	}
	
	private String generateRandomString() {
		String str=new  String("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
	 	StringBuffer sb=new StringBuffer();
	 	Random r = new Random();
	 	int te=0;
	 	for(int i=1;i<=10;i++){
	 		te=r.nextInt(9);
	 		sb.append(str.charAt(te));
	 	}
	 	return sb.toString();
	}
	
	private Object getRandomValue(DataType toolkitDatatype) {
		Object returnValue = null;
		Random r = new Random();
		//TODO handle other datatypes
		switch(AttributeTypeCode.fromValue(toolkitDatatype.getName())) {
		case INTEGER : returnValue = Math.abs(r.nextInt());
						break;
		case STRING : returnValue = generateRandomString();
						break;
		case DOUBLE : returnValue = Math.abs(r.nextDouble()) * 1000;
						break;
		case DATE :
		case DATETIME : 
		case TIME : returnValue = new Timestamp(Math.round(r.nextDouble()*(System.currentTimeMillis()-1L)+1L));
						break;
		case DECIMAL : returnValue = new BigDecimal(Math.abs(r.nextDouble() * 100));
						break;
						
		case BOOLEAN : returnValue = r.nextBoolean();
						break;
		case BINARY : returnValue = new Byte(generateRandomString());
						break;
						
		}
		return returnValue;
		
	}
*/
	@Override
	public String[][] getDataPreview(RecordInfo aRecordInfo, int pageSize,
			List<FieldInfo> fieldInfoList) throws DataPreviewException{
		FieldInfo aFieldInfo = null;
		boolean refreshFields = false;
		List<Field> fieldList = null;
		try {
			fieldList = getFields(aRecordInfo, refreshFields);
		} catch (MetadataReadException e1) {
			throw new DataPreviewException(e1.getMessage());
		}
		for (Field f : fieldList) {
			aFieldInfo = new FieldInfo();
			aFieldInfo.setDisplayName(f.getDisplayName());
			aFieldInfo.setUniqueName(f.getUniqueName());
			fieldInfoList.add(aFieldInfo);
		}
		
		String[][] dataRows = new String[pageSize][fieldList.size()];
		

		/*int i = 0;
		while (i < pageSize) {
			int j = 0;
			for (Field aField : fieldList) {
				dataRows[i][j++] = getRandomValue(aField.getDatatype()).toString();
			}
			i++;
		}*/

		try {

			com.sample.wsproxy.SampleDataService service = new com.sample.wsproxy.SampleDataService();
			com.sample.wsproxy.SampleData dummyReadData = service
					.getSampleDataPort();

			// read the data from the service
			List<Record> recordList = dummyReadData.read(aRecordInfo
					.getRecordName());

			Class<?> recordClass = Class.forName(classNameMap.get(aRecordInfo.getRecordName()));
			// for standard records the classname should be not null
			if (recordClass == null) {
				// this is a custom record, so return empty.
				return dataRows;
			}

			Method[] recordMethods = recordClass.getMethods();
			int i = 0;
			for (Record aRecord : recordList) {
				int j = 0;
				for (Field aField : fieldList) {
					for (Method aMethod : recordMethods) {
						if (aMethod.getName().equalsIgnoreCase(
								"get" + aField.getDisplayName())
								|| aMethod.getName().equalsIgnoreCase(
										"is" + aField.getDisplayName())) {
							Object value = aMethod.invoke(aRecord);
							DataType toolkitDatatype = aField.getDatatype();
							// convert to Java datatype
							value = SampleRead.convertToJavaDatatype(value,
									toolkitDatatype);
							dataRows[i][j++] = value == null ? "" : value.toString();
							break;
						}
					}
				}
				
				if(i == (pageSize - 1)) {
					break;
				}
				i++;
			}

		} catch (Exception_Exception e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return dataRows;
	}

	@Override
	public List<Field> getFields(RecordInfo aRecordInfo, boolean refreshFields) throws MetadataReadException {
		logger.logMsg("20002_GetFields", ELogMsgLevel.INFO, aRecordInfo.getRecordName());
		List<Field> fieldList = new ArrayList<Field>();
		if (classNameMap.isEmpty())
			getAllRecords();
		String className = this.classNameMap.get(aRecordInfo.getRecordName());
		
		// for standard records the classname should be not null
		if (className == null) {
			// check if Record is a custom record
			List<Field> fldList = customRecordFieldMap.get(aRecordInfo.getRecordName());
			if (fldList == null)
				throw new MetadataReadException("getFields called for unknown Record: " + aRecordInfo.getRecordName());
			else
				return fldList;
		}
		try {
			Class<?> clzName = Class.forName(className);
			java.lang.reflect.Field[] declaredFields = clzName.getDeclaredFields();
			for (java.lang.reflect.Field f : declaredFields) {
				String name = f.getName();
				String type = f.getType().getName();
				// datatype list is considered as a Child record.
				if (type.equals("java.util.List"))
					continue;
				Field infaField = new Field();
				infaField.setDisplayName(name);
				infaField.setDefaultValue("");
				// this is just for test. The system may not have specific unique names.
				infaField.setUniqueName(name + "_u");
				infaField.setContainingRecord(aRecordInfo);
				List<FieldAttribute> fieldAttribs = new ArrayList<FieldAttribute>();
				FieldAttribute aFieldAttrib1 = new FieldAttribute();
				aFieldAttrib1.setName(SampleConstants.REQUIRED_LEVEL);
				aFieldAttrib1.setValue("true");
				fieldAttribs.add(aFieldAttrib1);
				infaField.setCustomAttributes(fieldAttribs);
								
				AttributeTypeCode tc = null;
				if (f.getType().isArray()) {
					String arrayType = f.getType().getComponentType().getName();
					tc = AttributeTypeCode.fromValue(builtInMap.get(arrayType).getSimpleName());
				} else if (builtInMap.get(f.getType().getName()) != null) {
					/*
					 * this case handles the scenario where the wsdl has
					 * generated non-wrapped primitive datatypes.
					 */
					tc = AttributeTypeCode.fromValue(builtInMap.get(f.getType().getName())
							.getSimpleName());
				} else if (f.getType().getSimpleName().equals("XMLGregorianCalendar")) {
					// special case for handling XMLGregorianCalendar datatype.
					tc = AttributeTypeCode.DATETIME;
				} else {
					tc = AttributeTypeCode.fromValue(f.getType().getSimpleName());
				}
				
				DataType dt = new DataType(tc.getDataTypeName(), tc.getDataTypeId());
				
				//DataType dt = getDataType(jdt);
				infaField.setJavaDatatype(plugin.getRegistrationInfo().getTypeSystem().getDatatypeMapping().get(dt).get(0));
				infaField.setDatatype(dt);
				int prec = tc.getDefaultPrecision();
				// for fields with variable precision, default the precision to 256.
				if (prec == 0)
					prec = 256;
				infaField.setPrecision(prec);
				fieldList.add(infaField);
			}
		} catch (ClassNotFoundException e) {			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return fieldList;
	}

	@Override
	public String[] getRecordAttributeValue(String[] attrNames, RecordInfo aRecordInfo) {
		String [] attrValues = new String[attrNames.length];
		
		if(aRecordInfo.getRecordName().equalsIgnoreCase("Account")) {
			for(int i = 0 ; i < attrNames.length ; i++) {
				if(attrNames[i].equals(SampleConstants.PARENT_RECORD_NAME)) {
					attrValues[i] = "";
				} else if (attrNames[i].equals(SampleConstants.CLASS_NAME)) {
					attrValues[i] = "com.sample.wsproxy.Account";
				}
			}
		} else if(aRecordInfo.getRecordName().equalsIgnoreCase("Contact")) {
			for(int i = 0 ; i < attrNames.length ; i++) {
				if(attrNames[i].equals(SampleConstants.PARENT_RECORD_NAME)) {
					attrValues[i] = "Account";
				} else if (attrNames[i].equals(SampleConstants.CLASS_NAME)) {
					attrValues[i] = "com.sample.wsproxy.Contact";
				}
			} 
		} else if(aRecordInfo.getRecordName().equalsIgnoreCase("Opportunity")) {
			for(int i = 0 ; i < attrNames.length ; i++) {
				if(attrNames[i].equals(SampleConstants.PARENT_RECORD_NAME)) {
					attrValues[i] = "Account";
				} else if (attrNames[i].equals(SampleConstants.CLASS_NAME)) {
					attrValues[i] = "com.sample.wsproxy.Opportunity";
				}
			} 
		} else if(aRecordInfo.getRecordName().equalsIgnoreCase("Testobject")) {
			for(int i = 0 ; i < attrNames.length ; i++) {
				if(attrNames[i].equals(SampleConstants.PARENT_RECORD_NAME)) {
					attrValues[i] = "";
				} else if (attrNames[i].equals(SampleConstants.CLASS_NAME)) {
					attrValues[i] = "com.sample.wsproxy.Testobject";
				}
			} 
		}
		return attrValues;
	}

	@Override
	public String serializeFilterCriteria(List<FilterInfo> arg0, RecordInfo primaryRecord) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String[] getReadOpDesigntimeAttribValues(String[] names, RecordInfo record) {
		String[] retVal = new String[names.length];
		String emptyString = "";
		Arrays.fill(retVal, emptyString);
		return retVal;
	}

	@Override
	public String[] getWriteOpDesigntimeAttribValues(String[] names, RecordInfo record) {
		String[] retVal = new String[names.length];
		String emptyString = "";
		Arrays.fill(retVal, emptyString);
		return retVal;
	}

	@Override
	public CreateRecordResult createRecord(RecordInfo inRec, List<Field> inFldList)
			throws MetadataCreateException {
		CreateRecordResult result = new CreateRecordResult();
		// This is a dummy implementation, so we just cache the record information.
		// In actual case, the adapter developer will invoke the required APIs to define the record in
		// the target system.
		RecordInfo createdRecInfo = new RecordInfo();
		createdRecInfo.setRecordName(inRec.getRecordName());
		createdRecInfo.setRecordType("Custom");
		result.setRecordInfo(createdRecInfo);
		
		List<Field> outFieldList = new ArrayList<Field>();
		for (Field inField : inFldList) {
			Field outField = new Field();
			outField.setDisplayName(inField.getDisplayName());
			outField.setPrecision(inField.getPrecision());
			outField.setScale(inField.getScale());
			outField.setJavaDatatype(inField.getJavaDatatype());
			
			List<FieldAttribute> fieldAttribs = new ArrayList<FieldAttribute>();
			FieldAttribute aFieldAttrib1 = new FieldAttribute();
			aFieldAttrib1.setName(SampleConstants.REQUIRED_LEVEL);
			aFieldAttrib1.setValue("false");
			fieldAttribs.add(aFieldAttrib1);
			outField.setCustomAttributes(fieldAttribs);
			DataType dt = new DataType();
			switch(inField.getJavaDatatype()) {
				case JAVA_STRING:
					dt.setName(AttributeTypeCode.STRING.getDataTypeName());
					dt.setId(AttributeTypeCode.STRING.getDataTypeId());
					break;
				case JAVA_FLOAT:
					dt.setName(AttributeTypeCode.FLOAT.getDataTypeName());
					dt.setId(AttributeTypeCode.FLOAT.getDataTypeId());					
					break;
				case JAVA_DOUBLE:
					dt.setName(AttributeTypeCode.DOUBLE.getDataTypeName());
					dt.setId(AttributeTypeCode.DOUBLE.getDataTypeId());
					break;
				case JAVA_INTEGER:
					dt.setName(AttributeTypeCode.INTEGER.getDataTypeName());
					dt.setId(AttributeTypeCode.INTEGER.getDataTypeId());
					break;
				case JAVA_SHORT:
					dt.setName(AttributeTypeCode.SHORT.getDataTypeName());
					dt.setId(AttributeTypeCode.SHORT.getDataTypeId());
					break;
				case JAVA_LONG:
					dt.setName(AttributeTypeCode.LONG.getDataTypeName());
					dt.setId(AttributeTypeCode.LONG.getDataTypeId());
					break;
				case JAVA_BIGINTEGER:
					dt.setName(AttributeTypeCode.BIGINT.getDataTypeName());
					dt.setId(AttributeTypeCode.BIGINT.getDataTypeId());
					break;					
				case JAVA_BIGDECIMAL:
					dt.setName(AttributeTypeCode.BIGDECIMAL.getDataTypeName());
					dt.setId(AttributeTypeCode.BIGDECIMAL.getDataTypeId());
					break;
				case JAVA_TIMESTAMP:
					dt.setName(AttributeTypeCode.DATETIME.getDataTypeName());
					dt.setId(AttributeTypeCode.DATETIME.getDataTypeId());
					break;					
			}
			outField.setDatatype(dt);
			outFieldList.add(outField);
		}
		// store our created record in the record map.
		customRecordFieldMap.put(createdRecInfo.getRecordName(), outFieldList);
		result.setFields(outFieldList);

		return result;
	}
	
}
