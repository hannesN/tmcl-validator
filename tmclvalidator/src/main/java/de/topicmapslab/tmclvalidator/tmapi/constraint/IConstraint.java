/**
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Christian Haß
 */
package de.topicmapslab.tmclvalidator.tmapi.constraint;

import org.tmapi.core.Topic;

import de.topicmapslab.tmclvalidator.TMCLValidatorException;

public interface IConstraint {

	public void getParameter(Topic constraintInstance) throws TMCLValidatorException;
}
