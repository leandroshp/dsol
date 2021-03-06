package org.dsol.api.monitoring;

import java.util.List;

import org.dsol.api.ResourceImpl;
import org.dsol.monitoring.MonitorCounter;
import org.dsol.monitoring.State;


public class MonitorImpl extends ResourceImpl implements Monitor{
	
	private MonitorCounter monitorCounter;
	
	public MonitorImpl(MonitorCounter monitorCounter){
		this.monitorCounter = monitorCounter;
	}
	
	public String getActions(){
		List<State> states = monitorCounter.getStates();
		
		StringBuilder result = new StringBuilder();
		result.append("[");
		for(int i = 0;i < states.size();i++){
			State state = states.get(i);
			if(i != 0){
				result.append(",");
			}
			result.append("{");
			result.append("\"action\":");
			result.append("\"").append(state.getAbstractAction().getName()).append("\"");
			result.append(",");
			result.append("\"reliability\":");
			//result.append(state.getAbstractAction().getReliability());
			result.append(",");
			result.append("\"success\":");
			result.append(state.getNumberOfSuccessRequests());
			result.append(",");
			result.append("\"fail\":");
			result.append(state.getNumberOfFaultyRequests());
			result.append("}");
			
		}
		result.append("]");
		
		return result.toString();
	}

	@Override
	public String index() {
		return getResource("/html/monitoring.html");
	}
	

}
