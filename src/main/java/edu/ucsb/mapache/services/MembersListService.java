package edu.ucsb.mapache.services;

import java.util.List;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.ucsb.mapache.entities.Student;
import edu.ucsb.mapache.repositories.StudentRepository;

@Service
public class MembersListService {
    
    @Autowired
    private StudentRepository studentRepository;
    public String getListOfMembers(String teamName) {
        List<Student> studentsAll = studentRepository.findByTeamName(teamName);
        Collections.sort(studentsAll);
        String teamMembersList = "";
        for (Student s : studentsAll) {
            teamMembersList += s + "\n";
    }
        return teamMembersList;
  }
}
