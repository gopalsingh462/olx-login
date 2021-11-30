package com.zensar.olx.actuator;

import java.util.Map;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

@Component
@Endpoint(id="advertise-stats")
public class CustomInfoActuator {

	@ReadOperation
	public Map<String, String> getStats(){
		return null;
	}

}
