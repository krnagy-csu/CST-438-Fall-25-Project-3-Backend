package com.example.CST438_P3;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.CST438_P3.repo.*;
import com.example.CST438_P3.model.*;

@RestController
public class RouteController {
	private GroupRepository groupRepo;
	private UserRepository userRepo;

	//The default route given with Springboot
	@GetMapping("/")
	public String index() {
		return "Greetings from Spring Boot!";
	}

	/**
	 * This will eventually be the route that tells the database to add group
	 * Input is all the group's information - name, owner, etc.
	 * @return an error code or status message
	 */
	@GetMapping("/createGroup")
	public String createGroup(
        @RequestParam("name") String name,
        @RequestParam(value = "description", required = false) String description,
        @RequestParam("activityType") String activityType,
        @RequestParam("zipCode") String zipCode,
        @RequestParam(value = "maxMembers", required = false) Integer maxMembers,
        @RequestParam(value = "eventDate", required = false) String eventDateIso, // e.g. 2025-11-12T15:00:00
        @RequestParam(value = "isRecurring", required = false) Boolean isRecurring,
        @RequestParam("creatorId") Long creatorId
	){
    	User creator = userRepo.findById(creatorId).orElse(null);
    	if (creator == null) {
        	return "Invalid creator ID";
    	}

    	Group g = new Group(name, description, activityType, zipCode, creator);
    	if (maxMembers != null) g.setMaxMembers(maxMembers);
    	if (isRecurring != null) g.setIsRecurring(isRecurring);
    	if (eventDateIso != null && !eventDateIso.isBlank()) {
        	try {
            	g.setEventDate(LocalDateTime.parse(eventDateIso));
        	} catch (Exception e) {
            	return "Invalid date";
        	}
    	}
    	g.setUpdatedAt(LocalDateTime.now());

    	return "Success";
	}


	/**
	 * This will eventually be the route that tells the database to delete groups
	 * Input is the group ID
	 * @return the number of rows affected
	 */
	@GetMapping("/deleteGroup")
	public String deleteGroup(
		@RequestParam ("Group ID") Long groupId
	) {
		Group target = groupRepo.findById(groupId).orElse(null);
		if (target == null){
			return "Failed to find group";
		}
		groupRepo.delete(target);
		return "Success!";
		//probably want to add *some* degree of security but eh, that's a stretch goal
	}

	/**
	 * Editing a group's tags
	 * Input is the number of tags followed by the strings for the tags themselves
	 * @return the number of tags added
	 */
	@GetMapping("/addTags")
	public String addTags() {
		return "This is the route for adding tags to a group.";
	}

	/**
	 * Editing a group's tags
	 * Input is the number of tags to remove followed by the strings for them
	 * @return the number of tags deleted, and a small error for each not-matching tag
	 * i.e. if the object has tag "A" but you remove tag "B" it doesn't crash but it warns
	 */
	@GetMapping("/removeTags")
	public String removeTags() {
		return "This is the route for removing tags from a group.";
	}

	/**
	 * This will eventually be the route to add a user to a group.
	 * If the group's capacity is full, the user will be added to the waitlist instead.
	 * Inputs are user and group IDs.
	 * @return the IDs of the user and group affected, and whether they were added to the waitlist or the group directly.
	 */
	@GetMapping("/joinGroup")
	public String joinGroup(
		@RequestParam("User ID") Long userID,
		@RequestParam("Group ID") Long groupID
	) {
		User user = userRepo.findById(userID).orElse(null);
		if (user == null){
			return "Failed to get user.";
		}
		Group group = groupRepo.findById(groupID).orElse(null);
		if (group == null){
			return "Failed to get group.";
		}
		Set<User> temp = group.getMembers();
		temp.add(user);
		group.setMembers(temp);
		if (group.getMembers().contains(user)){
			return "Success!";
		} else {
			return "An error occured";
		}
	}

	/**
	 * This will eventually be the route to remove a user from a group or waitlist.
	 * If it's actually removing them from the group, it'll notify members on the waitlist.
	 * Inputs are the group ID to leave and the user ID who's leaving.
	 * @return either a success or error code
	 */
	@GetMapping ("/leaveGroup")
	public String leaveGroup(
		@RequestParam("User ID") Long userID,
		@RequestParam("Group ID") Long groupID
	) {
		User user = userRepo.findById(userID).orElse(null);
		if (user == null){
			return "Failed to get user.";
		}
		Group group = groupRepo.findById(groupID).orElse(null);
		if (group == null){
			return "Failed to get group.";
		}
		group.removeMember(user);
		
		if (!group.getMembers().contains(user)){
			return "Success!";
		} else {
			return "An error occured";
		}	}
	
	/**
	 * This will eventually be the route to add timeslots to a group.
	 * I'm still not sure how this will functionally work; this may need to be split up into multiple routes.
	 * @return something lol
	 */
	@GetMapping ("/addTimetoGroup")
	public String addTimetoGroup(){
		return "This is the route for adding timeslots to a group.";
	}

	/**
	 * This will eventually be the route to remove timeslots from a group.
	 * Again, I'm unsure of the actual implementation details - a problem for another cycle
	 * @return something lol
	 */
	@GetMapping ("/removeTimeFromGroup")
	public String removeTimeFromGroup(){
		return "This is the route from removing timeslots from a group.";
	}
	//Will refrain from making user routes for now pending Oauth implementation on frontend
}