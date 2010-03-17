/**
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Christian Haß
 */
package de.topicmapslab.tmclvalidator;

/**
 * Class to store one specific validation result.
 */
public class ValidationResult {

	/**
	 * The ID of the corresponding constraint.
	 */
	private String constraint_id;
	/**
	 * The error message.
	 */
	private String message;
	
	/**
	 * Constructor
	 * @param id - The constraint ID.
	 * @param message - The error message.
	 */
    public ValidationResult(String id, String message) {
	    this.constraint_id = id;
	    this.message = message;
    }
	
    /**
     * Returns the constraint ID.
     * @return The ID string.
     */
    public String getConstraintId()
    {
    	return this.constraint_id;
    }
	
    /**
     * Returns the error message.
     * @return The error message.
     */
    public String getMessage()
    {
    	return this.message;
    }
}
