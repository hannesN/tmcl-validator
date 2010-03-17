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

public class ReifierConstraint extends AbstractCardinalityConstraint {

	private static final String ALLOWED_REIFIER = "http://psi.topicmaps.org/tmcl/allowed-reifier";
	
	public int cardMin;
	public int cardMax;
	public Topic allowedReifier;
		
	public ReifierConstraint() {
		this.cardMin = -1;
		this.cardMax = -1;
		this.allowedReifier = null;
	}
		
	public void getParameter(Topic constraintInstance) throws TMCLValidatorException {
		
		TopicMap map = constraintInstance.getTopicMap();

		allowedReifier = Utils.getCounterPlayer(constraintInstance, map.getTopicBySubjectIdentifier(map.createLocator(ALLOWED_REIFIER)));
		
		this.cardMin = getMinCardinality(constraintInstance);
		this.cardMax = getMaxCardinality(constraintInstance);
		
		if(this.cardMin == -1) this.cardMin = 0;
	}
	
}
