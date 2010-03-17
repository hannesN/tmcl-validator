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

public class RoleCombinationConstraint extends AbstractConstraint {

	private static final String CONSTRAINED_ROLE = "http://psi.topicmaps.org/tmcl/constrained-role";
	private static final String CONSTRAINED_TOPIC_TYPE = "http://psi.topicmaps.org/tmcl/constrained-topic-type";
	private static final String OTHER_CONSTRAINED_ROLE = "http://psi.topicmaps.org/tmcl/other-constrained-role";
	private static final String OTHER_CONSTRAINED_TOPIC_TYPE = "http://psi.topicmaps.org/tmcl/other-constrained-topic-type";
	
	
	public Topic roleType;
	public Topic topicType;
	public Topic otherRoleType;
	public Topic otherTopicType;
	
	public RoleCombinationConstraint()
	{
		this.roleType = null;
		this.topicType = null;
		this.otherRoleType = null;
		this.otherTopicType = null;
	}
	
	
	public void getParameter(Topic constraintInstance) throws TMCLValidatorException {
		
		TopicMap map = constraintInstance.getTopicMap();

		this.roleType = Utils.getCounterPlayer(constraintInstance, map.getTopicBySubjectIdentifier(map.createLocator(CONSTRAINED_ROLE)));
		this.topicType = Utils.getCounterPlayer(constraintInstance, map.getTopicBySubjectIdentifier(map.createLocator(CONSTRAINED_TOPIC_TYPE)));
		this.otherRoleType = Utils.getCounterPlayer(constraintInstance, map.getTopicBySubjectIdentifier(map.createLocator(OTHER_CONSTRAINED_ROLE)));
		this.otherTopicType = Utils.getCounterPlayer(constraintInstance, map.getTopicBySubjectIdentifier(map.createLocator(OTHER_CONSTRAINED_TOPIC_TYPE)));
	}
}
