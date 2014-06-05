package com.informatica.cloud.adapter.sample.plugin;

import java.util.ArrayList;
import java.util.List;

import com.informatica.cloud.adapter.sample.connection.SampleConnection;
import com.informatica.cloud.adapter.sample.metadata.SampleMetadata;
import com.informatica.cloud.adapter.sample.metadata.SampleRegistrationInfo;
import com.informatica.cloud.adapter.sample.read.SampleRead;
import com.informatica.cloud.adapter.sample.write.SampleWrite;
import com.informatica.cloud.api.adapter.common.ILogger;
import com.informatica.cloud.api.adapter.common.OperationContext;
import com.informatica.cloud.api.adapter.connection.IConnection;
import com.informatica.cloud.api.adapter.metadata.Capability;
import com.informatica.cloud.api.adapter.metadata.IMetadata;
import com.informatica.cloud.api.adapter.metadata.IRegistrationInfo;
import com.informatica.cloud.api.adapter.plugin.IPlugin;
import com.informatica.cloud.api.adapter.plugin.PluginVersion;
import com.informatica.cloud.api.adapter.runtime.IRead;
import com.informatica.cloud.api.adapter.runtime.IWrite;

/**
 * @author anair
 *
 */
public class SamplePlugin implements IPlugin {

	private IRegistrationInfo dummyRegInfo;
	private IConnection dummyConnection;
	private IMetadata dummyMetadata;
	private ILogger logger;
	private OperationContext context;
	private IRead dummyReader;
	private IWrite sampleWriter;

	@Override
	public IConnection getConnection() {
		if(dummyConnection == null){
			dummyConnection = new SampleConnection();
		}
		return dummyConnection;
	}

	@Override
	public IMetadata getMetadata(IConnection conn) {
		if (dummyMetadata == null) {
			dummyMetadata = new SampleMetadata(this, (SampleConnection)conn);
		}
		return dummyMetadata;	}

	@Override
	public IRegistrationInfo getRegistrationInfo() {
		if(dummyRegInfo == null){
			dummyRegInfo = new SampleRegistrationInfo();
		}
		return dummyRegInfo;
	}

	@Override
	public void setContext(OperationContext context) {
		this.context = context;
	}

	@Override
	public void setLogger(ILogger logger) {
		this.logger = logger;
	}

	@Override
	public IRead getReader(IConnection conn) {
		if (dummyReader == null) {
			dummyReader = new SampleRead(this, (SampleConnection)conn);
		}
		return dummyReader;
	}
	
	@Override
	public IWrite getWriter(IConnection conn){
		if (sampleWriter == null) {
			sampleWriter = new SampleWrite(this, (SampleConnection)conn);
		}
		return sampleWriter;
	}
	
	/**
	 * This method is added to give access to ILogger to other classes.
	 * @return ILogger instance.
	 */
	public ILogger getLogger() {
		return this.logger;
	}

	@Override
	public PluginVersion getVersion() {
		return new PluginVersion(1, 0, 1);
	}

	@Override
	public List<Capability> getCapabilities() {
		List<Capability> capabilities = new ArrayList<Capability>();
		capabilities.add(Capability.SINGLE_OBJECT_READ);
		capabilities.add(Capability.SINGLE_OBJECT_WRITE);
		capabilities.add(Capability.SUPPORTS_CREATE_RECORD);
		return capabilities;
	}

}
