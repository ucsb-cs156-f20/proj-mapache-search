package edu.ucsb.mapache.services;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.ucsb.mapache.documents.SlackUser;
import edu.ucsb.mapache.repositories.SlackUserRepository;
import edu.ucsb.mapache.entities.Student;
import edu.ucsb.mapache.repositories.StudentRepository;
@Service
public class WhoIsService {
  @Autowired
  private SlackUserRepository slackUserRepository;
  @Autowired
  private StudentRepository studentRepository;
  public String getOutput(String username) {
    List<SlackUser> user = slackUserRepository.findByName(username);
    if(user.size()==0){
      return "/mapache whois failed: unknown user "+username;
    }
    SlackUser output = user.get(0);
    String name = output.getProfile().getReal_name();
    String email = output.getProfile().getEmail();
    String team = "";

    List<Student> stu = studentRepository.findByEmail(email);
    if(stu.size()!=0) {
      Student outputStudent = stu.get(0);
      team = outputStudent.getTeamName() + ", ";
    }

    String response = name + ", " + team + email;
    
    return response;
    }
  }