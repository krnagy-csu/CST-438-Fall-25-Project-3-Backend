package com.example.CST438_P3.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.CST438_P3.repo.GroupRepository;
import com.example.CST438_P3.repo.UserRepository;
import com.example.CST438_P3.model.User;
import com.example.CST438_P3.model.Group;
import java.util.Optional;



@Service
public class GroupService {
    
    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Group getGroupById(Long id) throws Exception {
        return groupRepository.findById(id)
                .orElseThrow(() -> new Exception("Group not found with id: " + id));
    }

    public Group createGroup(String name, String description, String activityType, String zipCode, Integer maxMembers, LocalDateTime eventDate, Boolean isRecurring, Long creatorId) throws Exception {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new Exception("User not found with id: " + creatorId));

        Group group = new Group(name, description, activityType, zipCode, creator);

        if(maxMembers != null) {
            group.setMaxMembers(maxMembers);
        }

        if(eventDate != null) {
            group.setEventDate(eventDate);
        }

        if(isRecurring != null) {
            group.setIsRecurring(isRecurring);
        }

        group.setUpdatedAt(LocalDateTime.now());

        return groupRepository.save(group);
    }

    public Group joinGroup(Long groupId, Long userId) throws Exception {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new Exception("Group not found with id: " + groupId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found with id: " + userId));

        if(group.getMembers().contains(user)) {
            throw new Exception("User is already a member of the group");
        }

        if(group.getMaxMembers() != null && group.getCurrentMemberCount() >= group.getMaxMembers()) {
            throw new Exception("Group is full");
        }

        group.getMembers().add(user);
        group.setUpdatedAt(LocalDateTime.now());

        return groupRepository.save(group);
    }

    public List<Group> searchByActivityType(String activityType){
        return groupRepository.findByActivityType(activityType);
    }

    public List<Group> searchByZipCode(String zipCode){
        return groupRepository.findByZipCode(zipCode);
    }

    public List<Group> getGroupsByUser(Long userId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found with id: " + userId));
        
        return groupRepository.findByMembersContaining(user);
    }

    public boolean leaveGroup(Long userId, Long groupId) {
        Optional<Group> groupOpt = groupRepository.findById(groupId);
        Optional<User> userOpt = userRepository.findById(userId);
    
        if (groupOpt.isPresent() && userOpt.isPresent()) {
            Group group = groupOpt.get();
            User user = userOpt.get();
    
            if (group.getMembers().contains(user)) {
                group.getMembers().remove(user);
                groupRepository.save(group);
                return true;
            }
        }
        return false;
    }
    
        
}
