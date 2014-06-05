package com.informatica.cloud.adapter.sample.metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.informatica.cloud.adapter.sample.ReadOperationDesigntimeAttributes;
import com.informatica.cloud.adapter.sample.ReadOperationRuntimeAttributes;
import com.informatica.cloud.adapter.sample.RecordAttributes;
import com.informatica.cloud.adapter.sample.SampleConstants;
import com.informatica.cloud.adapter.sample.WriteOperationRuntimeAttributes;
import com.informatica.cloud.adapter.sample.typesystem.SampleTypeSystem;
import com.informatica.cloud.api.adapter.connection.ConnectionAttribute;
import com.informatica.cloud.api.adapter.connection.ConnectionAttributeType;
import com.informatica.cloud.api.adapter.connection.StandardAttributes;
import com.informatica.cloud.api.adapter.metadata.FieldAttribute;
import com.informatica.cloud.api.adapter.metadata.IRegistrationInfo;
import com.informatica.cloud.api.adapter.metadata.RecordAttribute;
import com.informatica.cloud.api.adapter.metadata.RecordAttributeScope;
import com.informatica.cloud.api.adapter.metadata.TransformationInfo;
import com.informatica.cloud.api.adapter.typesystem.ITypeSystem;

/**
 * @author anair
 *
 */
public class SampleRegistrationInfo implements IRegistrationInfo {

	private ArrayList<ConnectionAttribute> connAttribs;
	private ITypeSystem dummyTypeSystem;
	private UUID id = UUID.fromString("798f88a7-91ad-4730-8dca-05505182cbe0");

	@Override
	public String getPluginDescription() {
		return SampleConstants.PLUGIN_DESC;
	}

	@Override
	public UUID getPluginUUID() {
		return this.id;
	}

	@Override
	public String getPluginShortName() {
		return SampleConstants.PLUGIN_SHORT_NAME;
	}
	
	@Override
	public List<ConnectionAttribute> getConnectionAttributes() {
		if (connAttribs == null) {
			connAttribs = new ArrayList<ConnectionAttribute>();
			connAttribs.add(StandardAttributes.username);
			connAttribs.add(StandardAttributes.password);
			connAttribs.add(StandardAttributes.connectionUrl);
			connAttribs.add(new ConnectionAttribute(SampleConstants.DOMAIN_NAME,
					ConnectionAttributeType.ALPHABET_TYPE
							| ConnectionAttributeType.NUMERIC_TYPE
							| ConnectionAttributeType.SYMBOLS_TYPE, null, null,	true, SampleConstants.DOMAIN_NUMBER));
			connAttribs.add(new ConnectionAttribute(SampleConstants.IS_ON_PREMISE,
					ConnectionAttributeType.BOOLEAN, "true", null, true, SampleConstants.IS_ON_PREMISE_NUMBER));

		}
		return connAttribs;
	}

	@Override
	public List<FieldAttribute> getFieldAttributes() {
		ArrayList<FieldAttribute> listOfFieldAttrs = new ArrayList<FieldAttribute>();
		FieldAttribute a = new FieldAttribute();

		a.setName(SampleConstants.REQUIRED_LEVEL);
		a.setDescription("Specifies the data entry requirement level of data entry enforced for the attribute.");
		listOfFieldAttrs.add(a);

		return listOfFieldAttrs;
	}

	@Override
	public String getName() {
		return SampleConstants.PLUGIN_NAME;
	}


	@Override
	public List<RecordAttribute> getReadOperationAttributes() {
		ArrayList<RecordAttribute> listOfReadOpAttribs = new ArrayList<RecordAttribute>();
		listOfReadOpAttribs.addAll(ReadOperationRuntimeAttributes.getAsList());
		listOfReadOpAttribs.addAll(ReadOperationDesigntimeAttributes.getAsList());
		return listOfReadOpAttribs;
	}

	@Override
	public List<RecordAttribute> getRecordAttributes() {
		ArrayList<RecordAttribute> listOfRecordAttribs =
			new ArrayList<RecordAttribute>();
		listOfRecordAttribs.addAll(RecordAttributes.getAsList());
		return listOfRecordAttribs;	}

	@Override
	public ITypeSystem getTypeSystem() {
		if(dummyTypeSystem == null){
			dummyTypeSystem = new SampleTypeSystem();
		}
		return dummyTypeSystem;
	}

	@Override
	public List<RecordAttribute> getWriteOperationAttributes() {
		ArrayList<RecordAttribute> listOfWriteOpAttribs = new ArrayList<RecordAttribute>();
		listOfWriteOpAttribs.addAll(WriteOperationRuntimeAttributes.getAsList());
		return listOfWriteOpAttribs;
	}
	
	public List<TransformationInfo> getTransformationOperations() {
		return null;
	}
	
	public List<RecordAttribute> getTransformationAttributes(TransformationInfo transform) {
		return null;
	}

}
