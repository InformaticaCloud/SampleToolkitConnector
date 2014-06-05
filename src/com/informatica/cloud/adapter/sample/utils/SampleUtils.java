package com.informatica.cloud.adapter.sample.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.informatica.cloud.adapter.sample.SampleConstants;
import com.informatica.cloud.api.adapter.runtime.exception.FatalRuntimeException;
import com.informatica.cloud.api.adapter.typesystem.JavaDataType;

/**
 * @author anair
 *
 */
public class SampleUtils {
	
	public static int getPrecisionForDatatype(final String datatype)
	{
		int precision = 15;
		
		if ( datatype.equalsIgnoreCase(SampleConstants.BOOLEAN) )
			precision = 10;
		else if ( datatype.equalsIgnoreCase(SampleConstants.STRING) )
			precision = getOverridenStringLength();
		else if ( datatype.equalsIgnoreCase(SampleConstants.DECIMAL) )
			precision = 28;
		else if (datatype.equalsIgnoreCase(SampleConstants.INTEGER) )
			precision = 10;
		else if ( datatype.equalsIgnoreCase(SampleConstants.DATETIME) )
			precision = 26;
		else if ( datatype.equalsIgnoreCase(SampleConstants.DATE) )
			precision = 19;
		else if ( datatype.equalsIgnoreCase(SampleConstants.BINARY) )
			precision = 10;
		else if ( datatype.equalsIgnoreCase(SampleConstants.LONG) )
			precision = 19;
		else if ( datatype.equalsIgnoreCase(SampleConstants.SHORT) )
			precision = 10;
		else if ( datatype.equalsIgnoreCase(SampleConstants.BIGINT) )
			precision = 19;
		else if ( datatype.equalsIgnoreCase(SampleConstants.DOUBLE) )
			precision = 15;
		else if ( datatype.equalsIgnoreCase(SampleConstants.FLOAT) )
			precision = 10;

		return precision;
	}
	
	public static int getOverridenStringLength()
	{
		int defaultStringLen = 256;
		return defaultStringLen;
	}

	public static Object createJavaObjectFromString(String filterValue,
			JavaDataType jdt) throws FatalRuntimeException {
		Object retObj = null;
		try {
			switch (jdt) {

			case JAVA_TIMESTAMP:
				retObj = java.sql.Timestamp.valueOf(filterValue);
				break;
			case JAVA_BOOLEAN:
				
					int intValue = Integer.parseInt(filterValue);
					switch(intValue){
					case 0: retObj = Boolean.FALSE;
							break;
					case 1: retObj = Boolean.TRUE;
							break;
					default:
						throw new FatalRuntimeException("Invalid Boolean value "+filterValue+". Expected 1 or 0 ");		
					}
				 
				break;
			//case JAVA_BYTES:
			// break;

			case JAVA_BIGDECIMAL:
			case JAVA_BIGINTEGER:
			case JAVA_DOUBLE:
			case JAVA_FLOAT:
			case JAVA_INTEGER:
			case JAVA_LONG:
			case JAVA_SHORT:
			case JAVA_STRING:
				Class[] strArgsClass = new Class[] { String.class };

				Class<?> bd = Class.forName(jdt.getFullClassName());
				Constructor<?> stringArgConstructor = bd
						.getConstructor(strArgsClass);
				retObj = stringArgConstructor.newInstance(filterValue);
				break;
				
			default: throw new FatalRuntimeException("Filer not allowed for Java data type : "+jdt);

			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new FatalRuntimeException(
					"Error Occurred when creating Java Object from String: "+filterValue+" for "
							+ jdt);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new FatalRuntimeException(
					"Error Occurred when creating Java Object from String: "+filterValue+" for "
							+ jdt);
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new FatalRuntimeException(
					"Error Occurred when creating Java Object from String: "+filterValue+" for "
							+ jdt);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new FatalRuntimeException(
					"Error Occurred when creating Java Object from String: "+filterValue+" for "
							+ jdt);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new FatalRuntimeException(
					"Error Occurred when creating Java Object from String: "+filterValue+" for "
							+ jdt);
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new FatalRuntimeException(
					"Error Occurred when creating Java Object from String: "+filterValue+" for "
							+ jdt);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw new FatalRuntimeException(
					"Error Occurred when creating Java Object from String: "+filterValue+" for "
							+ jdt);
		}
		return retObj;

	}
}
