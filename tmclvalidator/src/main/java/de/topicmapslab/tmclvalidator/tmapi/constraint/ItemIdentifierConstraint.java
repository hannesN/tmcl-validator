/**
 * 
 */
package de.topicmapslab.tmclvalidator.tmapi.constraint;

import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

import de.topicmapslab.tmclvalidator.TMCLValidatorException;


public class ItemIdentifierConstraint extends AbstractCardinalityConstraint {
	
	public int cardMin;
	public int cardMax;
	public String regExp;
	
	public ItemIdentifierConstraint() {
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
