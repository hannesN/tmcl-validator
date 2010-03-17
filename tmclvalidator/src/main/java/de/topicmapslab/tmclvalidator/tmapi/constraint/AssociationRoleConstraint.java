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

public class AssociationRoleConstraint extends AbstractCardinalityConstraint {

	private static final String CONSTRAINT_ROLE = "http://psi.topicmaps.org/tmcl/constrained-role";
	
	public Topic roleType;
	public int cardMin;
	public int cardMax;
	
	public AssociationRoleConstraint()
	{
		this.roleType = null;
		this.cardMin = -1;
		this.cardMax = -1;
	}
	
	public void getParameter(Topic constraintInstance) throws TMCLValidatorException {
		
		TopicMap map = constraintInstance.getTopicMap();

		roleType = Utils.getCounterPlayer(constraintInstance, map.getTopicBySubjectIdentifier(map.createLocator(CONSTRAINT_ROLE)));
		
		this.cardMin = getMinCardinality(constraintInstance);
		this.cardMax = getMaxCardinality(constraintInstance);
		
		if(this.cardMin == -1) this.cardMin = 0;
	}
}
