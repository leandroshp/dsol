package org.dsol.management;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.dsol.Instance;
import org.dsol.api.ResourceImpl;
import org.dsol.api.management.InstanceManagement;
import org.dsol.api.management.util.Serializer;
import org.dsol.config.MethodsInfo;
import org.dsol.planner.api.AbstractAction;
import org.dsol.planner.api.Planner;

import com.google.gson.JsonArray;

public class InstanceManagmentImpl extends ResourceImpl implements InstanceManagement {

	private ManagementCallback managementCallback;
	private Planner planner;
	
	
	public InstanceManagmentImpl(ManagementCallback ManagementCallback, Planner planner) {
		this.managementCallback = ManagementCallback;
		initPlanner(planner);
	}
	
	private void initPlanner(Planner planner){
		this.planner = planner;

		try{
			InputStream initialStateStream = new ByteArrayInputStream(Planner.EMPTY_INITIAL_STATE.getBytes());
			InputStream goalStream = new ByteArrayInputStream(Planner.EMPTY_GOAL.getBytes());
			InputStream actions = new ByteArrayInputStream(new byte[0]);
			planner.initialize(actions,initialStateStream, goalStream);
			
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	@Override
	public String getInstances() {
		JsonArray instances = new JsonArray();
		for (Instance instance : managementCallback.getInstances()) {
			instances.add(instance.toJSON());
		}
		return instances.toString();
	}

	@Override
	public String getInstance(String refId) {
		Instance instance = managementCallback.getInstance(refId);
		return instance.toJSON().toString();
	}

	@Override
	public void updateInstance(	String refId, 
								boolean applyToAllInstances, 
								Actions actions) {
			managementCallback.updateModel(refId,applyToAllInstances,actions);
	}
	
	@Override
	public String testPlan(String refId, PlannerInfo plannerInfo) {
		
		InputStream initialState = new ByteArrayInputStream(plannerInfo.getInitial_state().getBytes());
		InputStream goal = new ByteArrayInputStream(plannerInfo.getGoal().getBytes());
		
		synchronized (planner) {
			//planner.initialize();
			planner.updateActions(plannerInfo.getActions());
			
			try {
				planner.setInitialStateAndGoal(initialState,goal);
			} catch (Exception e1) {
				return "{\"error\":\"Invalid initial state or goal.\"}";
			}
			
			try {
				for(AbstractAction action:plannerInfo.getRemoved_actions()){
					planner.removeOperation(action);	
				}				
				//TODO:Return more than one plan
				return Serializer.toJSON(planner.plan().get(0)).toString();
			} catch (Exception e) {
				return "{\"error\":\"Plan not found.\"}";
			}			
		}
		
	}

	@Override
	public Response changeMethodsInfo(String refId, boolean applyForAllInstances, MethodsInfo methodsInfo) {

		try {
			managementCallback.updateOrchestrationInterface(refId, applyForAllInstances, methodsInfo);
			return Response.ok().build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return Response.status(Status.NOT_FOUND).build();
	}
}
