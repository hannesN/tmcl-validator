/**
 * @file Utils
 * @author Christian Haß
 */
package de.topicmapslab.tmclvalidator.tmapi.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.tmapi.core.Locator;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.Typed;

import de.topicmapslab.tmclvalidator.TMCLValidatorException;

/**
 * Utils class for common functionality
 *
 */
public class Utils {

	private static String tmclTopicType = "http://psi.topicmaps.org/tmcl/topic-type";
	
	private static Set<String> tmclTypes = new HashSet<String>(); 
	
	static {
		tmclTypes.add("http://psi.topicmaps.org/tmcl/name-type");
		tmclTypes.add("http://psi.topicmaps.org/tmcl/occurrence-type");
		tmclTypes.add("http://psi.topicmaps.org/tmcl/association-type");
		tmclTypes.add("http://psi.topicmaps.org/tmcl/role-type");
	};
	

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
	
	public static Set<Topic> getCounterPlayers(Topic topic, Topic associationType){
		
		Set<Topic> result = new HashSet<Topic>();
		
		Set<Role> roles = topic.getRolesPlayed();
		
		for(Role role:roles)
		{
			if(role.getParent().getType().equals(associationType))
			{
				Set<Role> allRoles = role.getParent().getRoles();
				
				for(Role associationRole:allRoles)
				{
					if(!associationRole.getPlayer().equals(topic))
						result.add(associationRole.getPlayer()); 
				}
			}
		}
		
		return result;
		
	}
	
	public static boolean hasType(Typed typedConstruct, Topic type){
		
		Set<Topic> superTypes = new HashSet<Topic>();

		if(typedConstruct.getType().equals(type))
			return true;
				
		getSupertypes(typedConstruct.getType(), superTypes);
		
		if(superTypes.contains(type))
			return true;
		
		return false;
	}
	
	private static void getSupertypes(Topic type, Set<Topic> superTypes){
		
		if(superTypes == null)
			return;
		
		Topic supertype = type.getTopicMap().getTopicBySubjectIdentifier(type.getTopicMap().createLocator("http://psi.topicmaps.org/iso13250/model/supertype"));
		Topic subtype = type.getTopicMap().getTopicBySubjectIdentifier(type.getTopicMap().createLocator("http://psi.topicmaps.org/iso13250/model/subtype"));
		Topic supertype_subtype = type.getTopicMap().getTopicBySubjectIdentifier(type.getTopicMap().createLocator("http://psi.topicmaps.org/iso13250/model/supertype-subtype"));
		
		if(supertype_subtype == null || supertype == null || subtype == null)
			return;
		
		Set<Role> roles = type.getRolesPlayed(subtype);
		
		for(Role SubRole:roles){
			
			if(!SubRole.getParent().getType().equals(supertype_subtype))
				continue;
			
			for(Role superRole:SubRole.getParent().getRoles(supertype)){
				superTypes.add(superRole.getPlayer());
				getSupertypes(superRole.getPlayer(), superTypes);
			}
		}
	}

	public static boolean isTMCLTopicType(Topic type){
		
		for(Locator l:type.getSubjectIdentifiers())
			if(l.getReference().equals(tmclTopicType))
				return true;
				
		return false;
	}
	
	public static boolean isTMCLType(Topic type){
		
		for(Locator l:type.getSubjectIdentifiers())
			if(tmclTypes.contains(l.getReference()))
				return true;
		
		return false;
	}
}
