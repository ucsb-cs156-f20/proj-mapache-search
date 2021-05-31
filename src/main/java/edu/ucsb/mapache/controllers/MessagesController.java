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
@RequestMapping("/api/members/messages")
public class MessagesController {
    private final Logger logger = LoggerFactory.getLogger(MessagesController.class);

    @Autowired
    ChannelRepository channelRepository;
    @Autowired
    MessageRepository messageRepository;

    @Autowired
    private AuthControllerAdvice authControllerAdvice;

    private ObjectMapper mapper = new ObjectMapper();

    private ResponseEntity<String> getUnauthorizedResponse(String roleRequired) throws JsonProcessingException {
        Map<String, String> response = new HashMap<String, String>();
        response.put("error", String.format("Unauthorized; only %s may access this resource.", roleRequired));
        String body = mapper.writeValueAsString(response);
        return new ResponseEntity<String>(body, HttpStatus.UNAUTHORIZED);
    }

    // search messages by a particular sender
    @GetMapping("/usersearch")
    public ResponseEntity<String> getMessages(@RequestHeader("Authorization") String authorization,
            @RequestParam String searchUser) throws JsonProcessingException {
        String decoded = searchUser.replaceAll("%20", " ");
        if (!authControllerAdvice.getIsMemberOrAdmin(authorization))
            return getUnauthorizedResponse("member");
        if (decoded.equals("")) {
            return ResponseEntity.ok().body("[]");
        }
        // Set ensures no duplicate messages
        Set<Message> messages = new HashSet<>(messageRepository.findByUser(decoded));
        String body = mapper.writeValueAsString(messages);
        return ResponseEntity.ok().body(body);
    }

    // search between two dates
    @GetMapping("/datesearch")
    public ResponseEntity<String> getMessagesBetweenDates(@RequestHeader("Authorization") String authorization,
            @RequestParam String startDate, @RequestParam String endDate) throws JsonProcessingException {
        if (!authControllerAdvice.getIsMemberOrAdmin(authorization))
            return getUnauthorizedResponse("member");
        if (startDate.equals("") || endDate.equals("")) {
            return ResponseEntity.ok().body("[]");
        }
        // Set ensures no duplicate messages
        Set<Message> messages = new HashSet<>(messageRepository.findByDate(startDate, endDate));
        String body = mapper.writeValueAsString(messages);
        return ResponseEntity.ok().body(body);
    }

    @GetMapping("/contentsearch")
    public ResponseEntity<String> getMessageOfChannel(@RequestHeader("Authorization") String authorization,
            @RequestParam String searchString) throws JsonProcessingException {
        if (!authControllerAdvice.getIsMember(authorization))
            return getUnauthorizedResponse("member");
        if (searchString.equals("")) {
            return ResponseEntity.ok().body("[]");
        }
        Set<Message> messages = new HashSet<>(messageRepository.findByText(searchString));
        String body = mapper.writeValueAsString(messages);
        return ResponseEntity.ok().body(body);
    }

    @GetMapping("/reactionsearch")
    public ResponseEntity<String> getReactionOfChannel(@RequestHeader("Authorization") String authorization,
            @RequestParam String searchReaction) throws JsonProcessingException {
        if (!authControllerAdvice.getIsMember(authorization))
            return getUnauthorizedResponse("member");
        logger.info("searchReaction=" + searchReaction);
        if (searchReaction.equals("")) {
            return ResponseEntity.ok().body("[]");
        }
        Set<Message> messages = new HashSet<>(messageRepository.findByReactionName(searchReaction));
        String body = mapper.writeValueAsString(messages);
        return ResponseEntity.ok().body(body);
    }

    @GetMapping("/allmessages")
    public ResponseEntity<String> getMessages(@RequestHeader("Authorization") String authorization)
            throws JsonProcessingException {
        if (!authControllerAdvice.getIsMember(authorization))
            return getUnauthorizedResponse("member");
        Set<Message> messages = new HashSet<>(messageRepository.findAll());
        String body = mapper.writeValueAsString(messages);
        return ResponseEntity.ok().body(body);
    }
}
