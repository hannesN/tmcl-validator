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

public class OccurrenceDatatypeConstraint extends AbstractConstraint {

	private static final String DATATYPE = "http://psi.topicmaps.org/tmcl/datatype";
	
	public String datatype;
	
	public OccurrenceDatatypeConstraint()
	{
		this.datatype = "";
	}
	
	public void getParameter(Topic constraintInstance) throws TMCLValidatorException {
		
		TopicMap map = constraintInstance.getTopicMap();
		this.datatype = getOccurrenceValue(constraintInstance, map.getTopicBySubjectIdentifier(map.createLocator(DATATYPE)));

		if(this.datatype == "") throw new TMCLValidatorException("No datatype specified in occurrence datatype constraint.");
	}
	
}
