package graphplan.domain.qos;

import java.util.List;

import graphplan.PlanResult;
import graphplan.domain.Operator;

public abstract class SumQoSAggregator implements QoSAggregator {

	@Override
	public Double getPlanQoSAggregatedValue(PlanResult plan) {
		Double planAggregatedValue = 0d;
		for (List<Operator> level : plan.getSteps()) {
			planAggregatedValue = planAggregatedValue + getLevelValue(level);
		}
		
		return planAggregatedValue;
	}
	
	protected double getLevelValue(List<Operator> level){
		double levelValue = 0;
		for (Operator operator : level) {
			levelValue = levelValue + getQoSValue(operator);
		}
		return levelValue;
	}
	
	private Double getQoSValue(Operator operator){
		Double value = operator.getQoSValue(getMetricName());
		if(value == null){
			return 0d;
		}
		return value;
	}

}
