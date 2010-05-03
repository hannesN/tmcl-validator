/**
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Christian Haß
 */
package de.topicmapslab.tmclvalidator;

/**
 * Exception class for the TMCL validator.
 */
public class TMCLValidatorException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * @param message - The error message.
     */
	public TMCLValidatorException(String message) {
	    super(message);
    }
}
