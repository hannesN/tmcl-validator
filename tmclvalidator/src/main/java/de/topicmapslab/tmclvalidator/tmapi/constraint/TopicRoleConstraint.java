/**
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Christian Haß
 */
package de.topicmapslab.tmclvalidator.tmapi.constraint;

import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

import de.topicmapslab.tmclvalidator.TMCLValidatorException;
import de.topicmapslab.tmclvalidator.tmapi.utils.Utils;

public class TopicRoleConstraint extends AbstractCardinalityConstraint {

	private static final String CONSTRAINT_STATEMENT = "http://psi.topicmaps.org/tmcl/constrained-statement"; // the association type
	private static final String CONSTRAINT_ROLE = "http://psi.topicmaps.org/tmcl/constrained-role"; // the role type
	
	public Topic associationType;
	public Topic roleType;
	public int cardMin;
	public int cardMax;
	
	public TopicRoleConstraint() {
	 	
    	this.associationType = null;
    	this.roleType = null;
    	this.cardMin = -1;
    	this.cardMax = -1;
    }
	
	public void getParameter(Topic constraintInstance) throws TMCLValidatorException {
		
		TopicMap map = constraintInstance.getTopicMap();
		
		associationType = Utils.getCounterPlayer(constraintInstance, map.getTopicBySubjectIdentifier(map.createLocator(CONSTRAINT_STATEMENT)));
		roleType = Utils.getCounterPlayer(constraintInstance, map.getTopicBySubjectIdentifier(map.createLocator(CONSTRAINT_ROLE)));
		
		this.cardMin = getMinCardinality(constraintInstance);
		this.cardMax = getMaxCardinality(constraintInstance);
		
		if(this.cardMin == -1) this.cardMin = 0;
	}
	
}
