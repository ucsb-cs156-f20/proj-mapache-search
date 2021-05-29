package edu.ucsb.mapache.controllers;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ucsb.mapache.advice.AuthControllerAdvice;
import edu.ucsb.mapache.documents.Message;
import edu.ucsb.mapache.repositories.ChannelRepository;
import edu.ucsb.mapache.repositories.MessageRepository;
import edu.ucsb.mapache.repositories.UserSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/members/searchhistory")
public class SearchHistoryController {
    private final Logger logger = LoggerFactory.getLogger(SearchHistoryController.class);

    @Autowired
    UserSearchRepository usersearchRepository;
    @Autowired
    private AuthControllerAdvice authControllerAdvice;

    private ObjectMapper mapper = new ObjectMapper();

    private ResponseEntity<String> getUnauthorizedResponse(String roleRequired) throws JsonProcessingException {
        Map<String, String> response = new HashMap<String, String>();
        response.put("error", String.format("Unauthorized; only %s may access this resource.", roleRequired));
        String body = mapper.writeValueAsString(response);
        return new ResponseEntity<String>(body, HttpStatus.UNAUTHORIZED);
    }

 
    @GetMapping("/allusersearches")
    public ResponseEntity<String> getMessages(@RequestHeader("Authorization") String authorization)
            throws JsonProcessingException {
        if (!authControllerAdvice.getIsMember(authorization))
            return getUnauthorizedResponse("member");
        Set<UserSearch> usersearch = new HashSet<>(usersearchRepository.findAll());
        String body = mapper.writeValueAsString(usersearch);
        return ResponseEntity.ok().body(body);
    }
}
