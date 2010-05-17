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

public class ScopeConstraint extends AbstractCardinalityConstraint {

	private static final String CONSTRAINED_SCOPE = "http://psi.topicmaps.org/tmcl/constrained-scope";
	
	public int cardMin;
	public int cardMax;
	public Topic allowedScope;
	
    public ScopeConstraint() {
    	this.cardMin = -1;
    	this.cardMax = -1;
    	this.allowedScope = null;
    }
	
	
	public void getParameter(Topic constraintInstance) throws TMCLValidatorException {
		
		TopicMap map = constraintInstance.getTopicMap();

		allowedScope = Utils.getCounterPlayer(constraintInstance, map.getTopicBySubjectIdentifier(map.createLocator(CONSTRAINED_SCOPE)));
		
		this.cardMin = getMinCardinality(constraintInstance);
		this.cardMax = getMaxCardinality(constraintInstance);
		
		if(this.cardMin == -1) this.cardMin = 0;
		
	}
	
}
