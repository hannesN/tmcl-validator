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

public class SubjectIdentifierConstraint extends AbstractCardinalityConstraint {

	public int cardMin;
	public int cardMax;
	public String regExp;
	
	public SubjectIdentifierConstraint() {
	    this.cardMax = -1;
	    this.cardMin = -1;
	    this.regExp = "";
    }
	
	public void getParameter(Topic constraintInstance) throws TMCLValidatorException {
		
		TopicMap map = constraintInstance.getTopicMap();
		
		this.cardMin = getMinCardinality(constraintInstance);
		this.cardMax = getMaxCardinality(constraintInstance);
	
		if(this.cardMin == -1) this.cardMin = 0;
		
		this.regExp = getOccurrenceValue(constraintInstance, map.getTopicBySubjectIdentifier(map.createLocator(REGEXP)));

		if(this.regExp == "") this.regExp = ".*";
	}
	
}
