package com.informatica.cloud.adapter.sample.connection;

import java.util.HashMap;
import java.util.Map;

import com.informatica.cloud.api.adapter.connection.ConnectionFailedException;
import com.informatica.cloud.api.adapter.connection.IConnection;
import com.informatica.cloud.api.adapter.connection.InsufficientConnectInfoException;
import com.informatica.cloud.api.adapter.plugin.InvalidArgumentException;

/**
 * @author anair
 *
 */
public class SampleConnection implements IConnection{

	Map<String, String> connAttribs = new HashMap<String, String>();;
	@Override
	public boolean connect() throws InsufficientConnectInfoException,
			ConnectionFailedException {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean disconnect() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setConnectionAttributes(Map<String, String> connParams) {
		this.connAttribs.putAll(connParams);

	}

	@Override
	public boolean validate() throws InvalidArgumentException {
		// TODO Auto-generated method stub
		return true;
	}

}
