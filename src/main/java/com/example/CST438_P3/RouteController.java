package com.example.CST438_P3;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RouteController {

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
	public String createGroup() {
		return "This is the route for creating groups!";
	}

	/**
	 * This will eventually be the route that tells the database to delete groups
	 * Input is the group ID
	 * @return the number of rows affected
	 */
	@GetMapping("/deleteGroup")
	public String deleteGroup() {
		return "This is the route for deleting groups!";
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
	public String joinGroup() {
		return "This is the route for adding a user to a group!";
	}

	/**
	 * This will eventually be the route to remove a user from a group or waitlist.
	 * If it's actually removing them from the group, it'll notify members on the waitlist.
	 * Inputs are the group ID to leave and the user ID who's leaving.
	 * @return either a success or error code
	 */
	@GetMapping ("/leaveGroup")
	public String leaveGroup() {
		return "This is the route for leaving a group.";
	}

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