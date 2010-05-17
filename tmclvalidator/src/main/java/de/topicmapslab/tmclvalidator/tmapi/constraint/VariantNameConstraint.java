
package de.topicmapslab.tmclvalidator.tmapi.constraint;

import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

import de.topicmapslab.tmclvalidator.TMCLValidatorException;
import de.topicmapslab.tmclvalidator.tmapi.utils.Utils;


public class VariantNameConstraint extends AbstractCardinalityConstraint {

	private static final String CONSTRAINT_STATEMENT = "http://psi.topicmaps.org/tmcl/constrained-statement";
	private static final String CONSTRAINT_SCOPE_TOPIC = "http://psi.topicmaps.org/tmcl/constrained-scope-topic";
	
	public Topic nameType;
	public Topic scopeTopic;
	public int cardMin;
	public int cardMax;
	
    public VariantNameConstraint() {
	   
    }
	
	
	public void getParameter(Topic constraintInstance) throws TMCLValidatorException {
	    
		TopicMap map = constraintInstance.getTopicMap();
		
		nameType = Utils.getCounterPlayer(constraintInstance, map.getTopicBySubjectIdentifier(map.createLocator(CONSTRAINT_STATEMENT)));
		scopeTopic = Utils.getCounterPlayer(constraintInstance, map.getTopicBySubjectIdentifier(map.createLocator(CONSTRAINT_SCOPE_TOPIC)));
		
		this.cardMin = getMinCardinality(constraintInstance);
		this.cardMax = getMaxCardinality(constraintInstance);
		
		if(this.cardMin == -1) 	this.cardMin = 0;
	    
	}
	
	
	
}
