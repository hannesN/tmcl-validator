/**
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Christian Haß
 */
package de.topicmapslab.tmclvalidator.tmapi.utils;

import java.util.Set;

import org.tmapi.core.Role;
import org.tmapi.core.Topic;

import de.topicmapslab.tmclvalidator.TMCLValidatorException;

/**
 * Utils class for common functionality
 */
public class Utils {

	/**
	 * Gets the counter player of an binary association.
	 * @param topic - The topic with represents the first player.
	 * @param associationType - The type of the association.
	 * @return The counter player.
	 * @throws TMCLValidatorException
	 */
	public static Topic getCounterPlayer(Topic topic, Topic associationType) throws TMCLValidatorException{
		
		Set<Role> roles = topic.getRolesPlayed();

		for(Role role:roles)
		{
			if(role.getParent().getType().equals(associationType))
			{
				Set<Role> allRoles = role.getParent().getRoles();
				
				for(Role associationRole:allRoles)
				{
					if(!associationRole.getPlayer().equals(topic)) return associationRole.getPlayer(); 
				}
			}
		}
		
		throw new TMCLValidatorException("No counter player found.");
	}
	
	
}
