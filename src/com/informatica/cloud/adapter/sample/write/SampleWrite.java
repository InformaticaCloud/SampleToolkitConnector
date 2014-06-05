package com.informatica.cloud.adapter.sample.write;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.informatica.cloud.adapter.sample.SampleConstants;
import com.informatica.cloud.adapter.sample.connection.SampleConnection;
import com.informatica.cloud.adapter.sample.plugin.SamplePlugin;
import com.informatica.cloud.api.adapter.common.ELogMsgLevel;
import com.informatica.cloud.api.adapter.common.ILogger;
import com.informatica.cloud.api.adapter.connection.ConnectionFailedException;
import com.informatica.cloud.api.adapter.logging.LoggerMessageHandler;
import com.informatica.cloud.api.adapter.metadata.Field;
import com.informatica.cloud.api.adapter.metadata.RecordInfo;
import com.informatica.cloud.api.adapter.plugin.PluginVersion;
import com.informatica.cloud.api.adapter.runtime.IWrite;
import com.informatica.cloud.api.adapter.runtime.exception.DataConversionException;
import com.informatica.cloud.api.adapter.runtime.exception.FatalRuntimeException;
import com.informatica.cloud.api.adapter.runtime.exception.InitializationException;
import com.informatica.cloud.api.adapter.runtime.exception.ReflectiveOperationException;
import com.informatica.cloud.api.adapter.runtime.exception.WriteException;
import com.informatica.cloud.api.adapter.runtime.utils.IInputDataBuffer;
import com.informatica.cloud.api.adapter.runtime.utils.OperationResult;
import com.informatica.cloud.api.adapter.typesystem.JavaDataType;
import com.sample.wsproxy.Record;

public class SampleWrite implements IWrite {
	
	private static final String MSG_BUNDLE_NAME = "SampleAdapterResourceBundle";
	public static final String SAMPLE_WRITER_PREFIX = "SAMPLE_READER";


	private final SamplePlugin dummyPlugin;
	private final SampleConnection dummyConn;
	private final LoggerMessageHandler logger;
	
	private List<RecordInfo> secondaryRecordInfoList = new ArrayList<RecordInfo>();
	private RecordInfo primaryRecordInfo;
	private List<Field> fieldList = new ArrayList<Field>();
	private Map<String,Map<String, String>> recordAttributes = new HashMap<String, Map<String,String>>();
	private Map<String, String> runtimeAttributes = new HashMap<String, String>();
	private com.sample.wsproxy.SampleData port;
	public SampleWrite(SamplePlugin samplePlugin, SampleConnection conn) {
		dummyPlugin = samplePlugin;
		dummyConn = conn;
		logger = new LoggerMessageHandler(ResourceBundle.getBundle(
				MSG_BUNDLE_NAME, Locale.getDefault()), samplePlugin.getLogger(),
				SAMPLE_WRITER_PREFIX);

	}

	@Override
	public void setChildRecords(List<RecordInfo> secondaryRecordInfoList) {
		if(secondaryRecordInfoList != null && secondaryRecordInfoList.size() != 0)
			this.secondaryRecordInfoList.addAll(secondaryRecordInfoList);
	}

	@Override
	public List<OperationResult> delete(IInputDataBuffer arg0)
			throws ConnectionFailedException, ReflectiveOperationException,
			WriteException, DataConversionException, FatalRuntimeException {
		return new ArrayList<OperationResult>();
	}

	@Override
	public List<OperationResult> insert(IInputDataBuffer inputDataBuffer)
			throws ConnectionFailedException, ReflectiveOperationException,
			WriteException, DataConversionException, FatalRuntimeException {
		init();
		List<OperationResult> rowStatus = new ArrayList<OperationResult>();
		try {
			String primaryRecordClassName = recordAttributes.get(primaryRecordInfo.getRecordName())
			.get(SampleConstants.CLASS_NAME);
			Class<?> primaryClass = Class.forName(primaryRecordClassName);
			Method[] primaryRecordMethods = primaryClass.getMethods();
			Object primaryRecordInstance = primaryClass.newInstance();
			while(inputDataBuffer.hasMoreRows()) {
				Object[] data = inputDataBuffer.getData();
				int j = 0;
				for (Field aField : fieldList) {
					Object value = data[j];
					if(aField.getContainingRecord().getRecordName().equals(primaryRecordInfo.getRecordName())) {
						for (Method aMethod : primaryRecordMethods) {
							if(aMethod.getName().equalsIgnoreCase("set" + aField.getDisplayName())) {
								JavaDataType javaDT = aField.getJavaDatatype();
								
								switch(javaDT) {
								case JAVA_STRING : aMethod.invoke(primaryRecordInstance, (String)value);
									break;
								case JAVA_INTEGER : aMethod.invoke(primaryRecordInstance, (Integer)value);
									break;
								case JAVA_DOUBLE : aMethod.invoke(primaryRecordInstance, (Double)value);
									break;
								case JAVA_TIMESTAMP : 
									XMLGregorianCalendar xmlGregDate = null;
									if(value != null) {
										GregorianCalendar c = new GregorianCalendar();
										Timestamp ts = (Timestamp) value;
										c.setTimeInMillis(ts.getTime());
										xmlGregDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
									}
										aMethod.invoke(primaryRecordInstance, xmlGregDate);
									
									break;
								case JAVA_BOOLEAN : 
									aMethod.invoke(primaryRecordInstance, (Boolean)value);
									break;
								case JAVA_BIGDECIMAL : aMethod.invoke(primaryRecordInstance, (BigDecimal)value);
									break;
								case JAVA_PRIMITIVE_BYTEARRAY:
									byte[] byteArray = (byte[])value;
									aMethod.invoke(primaryRecordInstance, byteArray);
									break;
								case JAVA_SHORT: aMethod.invoke(primaryRecordInstance, (Short)value);
									break;
								case JAVA_LONG: aMethod.invoke(primaryRecordInstance, (Long)value);
									break;
								case JAVA_BIGINTEGER: aMethod.invoke(primaryRecordInstance, (BigInteger)value);
									break;
								case JAVA_FLOAT: aMethod.invoke(primaryRecordInstance, (Float)value);
									break;
								}
								break;
							}
						}
					} 
					j++;
				}
				boolean status = port.insert((Record)primaryRecordInstance);
				rowStatus.add(new OperationResult(status, null));
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rowStatus;
	}

	private void init() {
		com.sample.wsproxy.SampleDataService service = new com.sample.wsproxy.SampleDataService();
		port = service.getSampleDataPort();
		
	}

	@Override
	public void setFieldList(List<Field> fieldList) {
		this.fieldList.addAll(fieldList);
	}

	@Override
	public void setPrimaryRecord(RecordInfo primaryRecordInfo) {
		logger.logMsg("10004_PrimaryRecord", ELogMsgLevel.INFO, primaryRecordInfo.getRecordName());		
		this.primaryRecordInfo = primaryRecordInfo;
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
	public void setRecordAttributes(RecordInfo recordInfo, Map<String, String> tgtDesigntimeAttribs) {
		logger.logMsg("10001_LogRecordAttributes", ELogMsgLevel.INFO, recordInfo.getRecordName());		
		this.recordAttributes.put(recordInfo.getRecordName(), tgtDesigntimeAttribs);
		logMap(tgtDesigntimeAttribs);
	}

	@Override
	public void setOperationAttributes(Map<String, String> runtimeAttribs) {
		logger.logMsg("10003_LogOperationAttributes", ELogMsgLevel.INFO);	
		this.runtimeAttributes.putAll(runtimeAttribs);
		logMap(runtimeAttribs);
	}

	@Override
	public List<OperationResult> update(IInputDataBuffer arg0)
			throws ConnectionFailedException, ReflectiveOperationException,
			WriteException, DataConversionException, FatalRuntimeException {
		return new ArrayList<OperationResult>();
	}

	@Override
	public List<OperationResult> upsert(IInputDataBuffer arg0)
			throws ConnectionFailedException, ReflectiveOperationException,
			WriteException, DataConversionException, FatalRuntimeException {
		return new ArrayList<OperationResult>();
	}

	@Override
	public void setMetadataVersion(PluginVersion arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initializeAndValidate() throws InitializationException {
		// TODO Auto-generated method stub
		
	}


}
