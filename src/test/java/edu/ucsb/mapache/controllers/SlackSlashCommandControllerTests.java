package edu.ucsb.mapache.controllers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;  

import edu.ucsb.mapache.config.SecurityConfig;
import edu.ucsb.mapache.models.SlackSlashCommandParams;
import edu.ucsb.mapache.repositories.ChannelRepository;
import edu.ucsb.mapache.repositories.MessageRepository;
import edu.ucsb.mapache.repositories.SlackUserRepository;



import org.springframework.http.MediaType;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.nio.file.Path;

import edu.ucsb.mapache.services.GoogleSearchService;
import edu.ucsb.mapache.services.GoogleSearchServiceHelper;
import edu.ucsb.mapache.services.NowService;
import edu.ucsb.mapache.services.TeamEmailListService;
import edu.ucsb.mapache.services.TeamListService;
import edu.ucsb.mapache.services.WhoIsService;
import edu.ucsb.mapache.services.MembersListService;


import java.util.List;  
import java.util.ArrayList; 
import edu.ucsb.mapache.documents.Message;   
import edu.ucsb.mapache.documents.SlackUser;  
import edu.ucsb.mapache.documents.SlackUserProfile;  
import edu.ucsb.mapache.documents.MessageReactions;


import me.ramswaroop.jbot.core.slack.models.RichMessage;

@WebMvcTest(value = SlackSlashCommandController.class)
@Import(SecurityConfig.class)
public class SlackSlashCommandControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    SlackSlashCommandController slackSlashCommandController;

    @MockBean
    NowService nowService;  

    @MockBean 
    MessageRepository messageRepository; 

    @MockBean
    ChannelRepository channelRepository;

    @MockBean
    SlackUserRepository slackUserRepository;

    @MockBean
    GoogleSearchServiceHelper googleSearchServiceHelper;

    @MockBean
    TeamEmailListService teamEmailListService;

    @MockBean
    TeamListService teamListService;

    @MockBean
    WhoIsService whoIsService;
    
    @Autowired
    MembersListService membersListService;

    private final String testURL = "/api/public/slash-command";

    @Test
    public void test_postSlashMessage() throws Exception {
        // content type: https://api.slack.com/interactivity/slash-commands
        mockMvc.perform(post(testURL).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("token", slackSlashCommandController.getSlackToken())
                .param("team_id", "value").param("team_domain", "value").param("channel_id", "value")
                .param("channel_name", "value").param("user_id", "value").param("user_name", "value")
                .param("command", "value").param("text", "value").param("response_url", "value"))
                .andExpect(status().is(200));
    }

    @Test
    public void test_badToken() throws Exception {
        // content type: https://api.slack.com/interactivity/slash-commands
        mockMvc.perform(post(testURL).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("token", "BADTOKEN")
                .param("team_id", "value").param("team_domain", "value").param("channel_id", "value")
                .param("channel_name", "value").param("user_id", "value").param("user_name", "value")
                .param("command", "value").param("text", "value").param("response_url", "value"))
                .andExpect(status().is(200));
    }

    @Test
    public void test_emptyCommand() throws Exception {
        // content type: https://api.slack.com/interactivity/slash-commands
        mockMvc.perform(post(testURL).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("token", slackSlashCommandController.getSlackToken())
                .param("team_id", "value").param("team_domain", "value").param("channel_id", "value")
                .param("channel_name", "value").param("user_id", "value").param("user_name", "value")
                .param("command", "value").param("text", "").param("response_url", "value"))
                .andExpect(status().is(200));
    }

    @Test
    public void test_emptyCommand2() throws Exception {
        // content type: https://api.slack.com/interactivity/slash-commands
        mockMvc.perform(post(testURL).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("token", slackSlashCommandController.getSlackToken())
                .param("team_id", "value").param("team_domain", "value").param("channel_id", "value")
                .param("channel_name", "value").param("user_id", "value").param("user_name", "value")
                .param("command", "value").param("text", "             ").param("response_url", "value"))
                .andExpect(status().is(200));
    }

    @Test
    public void test_statusCommand() throws Exception {
        // content type: https://api.slack.com/interactivity/slash-commands
        mockMvc.perform(post(testURL).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("token", slackSlashCommandController.getSlackToken())
                .param("team_id", "value").param("team_domain", "value").param("channel_id", "value")
                .param("channel_name", "value").param("user_id", "value").param("user_name", "value")
                .param("command", "/mapache").param("text", "status").param("response_url", "value"))
                .andExpect(status().is(200));
    }

    @Test
    public void test_timeCommand() throws Exception {
        // content type: https://api.slack.com/interactivity/slash-commands

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        java.util.Date now = sdf.parse("06/24/2017");

        when(nowService.now()).thenReturn(now);
        mockMvc.perform(post(testURL).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("token", slackSlashCommandController.getSlackToken())
                .param("team_id", "value").param("team_domain", "value").param("channel_id", "value")
                .param("channel_name", "value").param("user_id", "value").param("user_name", "value")
                .param("command", "/mapache").param("text", "time").param("response_url", "value"))
                .andExpect(status().is(200));
    }

    @Test
    public void test_debugCommand() throws Exception {
        // content type: https://api.slack.com/interactivity/slash-commands
        mockMvc.perform(post(testURL).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("token", slackSlashCommandController.getSlackToken())
                .param("team_id", "value").param("team_domain", "value").param("channel_id", "value")
                .param("channel_name", "value").param("user_id", "value").param("user_name", "value")
                .param("command", "/mapache").param("text", "debug").param("response_url", "value"))
                .andExpect(status().is(200));
    }

    // google search test below
    @Test
    public void test_googleSearch() throws Exception {
        // content type: https://api.slack.com/interactivity/slash-commands
        Path jsonPath = Paths.get("src/test/java/edu/ucsb/mapache/google/sample.json");
        String retval = Files.readString(jsonPath);
        when(googleSearchServiceHelper.getJSON(any(), any())).thenReturn(retval);
        mockMvc.perform(post(testURL).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("token", slackSlashCommandController.getSlackToken())
                .param("team_id", "value").param("team_domain", "value").param("channel_id", "value")
                .param("channel_name", "value").param("user_id", "value").param("user_name", "value")
                .param("command", "/mapache").param("text", "search google 0").param("response_url", "value"))
                .andExpect(status().is(200));
    }

    @Test
    public void test_googleSearchNotSearchGoogle() throws Exception {
        // content type: https://api.slack.com/interactivity/slash-commands
        mockMvc.perform(post(testURL).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("token", slackSlashCommandController.getSlackToken())
                .param("team_id", "value").param("team_domain", "value").param("channel_id", "value")
                .param("channel_name", "value").param("user_id", "value").param("user_name", "value")
                .param("command", "/mapache").param("text", "notSeach notGoogle").param("response_url", "value"))
                .andExpect(status().is(200));
    }

    @Test
    public void test_googleSearchMultipleArguments() throws Exception {
        // content type: https://api.slack.com/interactivity/slash-commands
        Path jsonPath = Paths.get("src/test/java/edu/ucsb/mapache/google/sample.json");
        String retval = Files.readString(jsonPath);
        when(googleSearchServiceHelper.getJSON(any(), any())).thenReturn(retval);
        mockMvc.perform(post(testURL).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("token", slackSlashCommandController.getSlackToken()).param("team_id", "value")
                .param("team_domain", "value").param("channel_id", "value").param("channel_name", "value")
                .param("user_id", "value").param("user_name", "value").param("command", "/mapache")
                .param("text", "search google two words").param("response_url", "value")).andExpect(status().is(200));
    }

    @Test
    public void test_googleSearch_empty_items() throws Exception {
        // content type: https://api.slack.com/interactivity/slash-commands
        Path jsonPath = Paths.get("src/test/java/edu/ucsb/mapache/google/sample.json");
        String retval = Files.readString(jsonPath);
        int index_trim = retval.indexOf("\"items\"");
        retval = retval.substring(0, index_trim) + "\"items\": []}";
        when(googleSearchServiceHelper.getJSON(any(), any())).thenReturn(retval);
        SlackSlashCommandParams params = new SlackSlashCommandParams();
        params.setText("search google test");
        RichMessage result = slackSlashCommandController.googleSearch(params);
        assertEquals("No results found!", result.getAttachments()[0].getText());
    }

    @Test
    public void test_googleSearch_search_google() throws Exception {
        // content type: https://api.slack.com/interactivity/slash-commands
        Path jsonPath = Paths.get("src/test/java/edu/ucsb/mapache/google/sample.json");
        String retval = Files.readString(jsonPath);
        when(googleSearchServiceHelper.getJSON(any(), any())).thenReturn(retval);
        mockMvc.perform(post(testURL).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("token", slackSlashCommandController.getSlackToken())
                .param("team_id", "value").param("team_domain", "value").param("channel_id", "value")
                .param("channel_name", "value").param("user_id", "value").param("user_name", "value")
                .param("command", "/mapache").param("text", "search google").param("response_url", "value"))
                .andExpect(status().is(200));
    }

    // testing google search

    @Test
    public void test_googleSearch_placeholder_google() throws Exception {
        // content type: https://api.slack.com/interactivity/slash-commands
        mockMvc.perform(post(testURL).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("token", slackSlashCommandController.getSlackToken())
                .param("team_id", "value").param("team_domain", "value").param("channel_id", "value")
                .param("channel_name", "value").param("user_id", "value").param("user_name", "value")
                .param("command", "/mapache").param("text", "placeholder google").param("response_url", "value"))
                .andExpect(status().is(200));
    }

    @Test
    public void test_googleSearch_search_placeholder() throws Exception {
        // content type: https://api.slack.com/interactivity/slash-commands
        mockMvc.perform(post(testURL).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)

                .param("token", slackSlashCommandController.getSlackToken())

                .param("team_id", "value").param("team_domain", "value").param("channel_id", "value")
                .param("channel_name", "value").param("user_id", "value").param("user_name", "value")
                .param("command", "/mapache").param("text", "search placeholder").param("response_url", "value"))
                .andExpect(status().is(200));
    }

    @Test
    public void test_googleSearch_placeholder_placeholder() throws Exception {
        // content type: https://api.slack.com/interactivity/slash-commands
        mockMvc.perform(post(testURL).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("token", slackSlashCommandController.getSlackToken())
                .param("team_id", "value").param("team_domain", "value").param("channel_id", "value")
                .param("channel_name", "value").param("user_id", "value").param("user_name", "value")
                .param("command", "/mapache").param("text", "placeholder placeholder").param("response_url", "value"))
                .andExpect(status().is(200));
    }

    @Test
    public void test_whoisCommand_correct_response() throws Exception {
        when(whoIsService.getOutput("@user")).thenReturn("name, team, email");
        mockMvc.perform(post(testURL).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("token", slackSlashCommandController.getSlackToken())
                .param("team_id", "value").param("team_domain", "value").param("channel_id", "value")
                .param("channel_name", "value").param("user_id", "value").param("user_name", "value")
                .param("command", "/mapache").param("text", "whois @user").param("response_url", "value"))
                .andExpect(status().is(200));
    }
    
    @Test
    public void test_MembersCommand_correct_response() throws Exception {
        when(membersListService.getListOfMembers("team")).thenReturn("name");
        mockMvc.perform(post(testURL).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("token", slackSlashCommandController.getSlackToken())
                .param("team_id", "value").param("team_domain", "value").param("channel_id", "value")
                .param("channel_name", "value").param("user_id", "value").param("user_name", "value")
                .param("command", "/mapache").param("text", "members team").param("response_url", "value"))
                .andExpect(status().is(200));
    }

    @Test
    public void test_teamlistCommandRegular() throws Exception {
        // content type: https://api.slack.com/interactivity/slash-commands
        when(teamListService.getListOfTeams()).thenReturn("team1");
        mockMvc.perform(post(testURL).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("token", slackSlashCommandController.getSlackToken())
                .param("team_id", "value").param("team_domain", "value").param("channel_id", "value")
                .param("channel_name", "value").param("user_id", "value").param("user_name", "value")
                .param("command", "/mapache").param("text", "teamlist").param("response_url", "value"))
                .andExpect(status().is(200));
    }

    @Test
    public void test_teamlistCommandWithInputtedTeamName() throws Exception {
        // fixes null error
        when(teamEmailListService.getEmailsStringFromTeamname("team")).thenReturn("email");
        mockMvc.perform(post(testURL).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("token", slackSlashCommandController.getSlackToken())
                .param("team_id", "value").param("team_domain", "value").param("channel_id", "value")
                .param("channel_name", "value").param("user_id", "value").param("user_name", "value")
                .param("command", "/mapache").param("text", "teamlist team").param("response_url", "value"))
                .andExpect(status().is(200));
    }   
    
    @Test 
    public void test_slackSearchCommand() throws Exception {  
      mockMvc.perform(post(testURL).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("token", slackSlashCommandController.getSlackToken())
                .param("team_id", "value").param("team_domain", "value").param("channel_id", "value")
                .param("channel_name", "value").param("user_id", "value").param("user_name", "value")
                .param("command", "/mapache").param("text", "search slack").param("response_url", "value"))
                .andExpect(status().is(200));
    }    

    @Test public void test_slackSlashTextInChannel() throws Exception{       
        List<Message> message = new ArrayList<Message>();
        when(messageRepository.findByTextInChannel(any(), any(), any())).thenReturn(message);
        mockMvc.perform(post(testURL).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("token", slackSlashCommandController.getSlackToken())
                .param("team_id", "value").param("team_domain", "value").param("channel_id", "value")
                .param("channel_name", "value").param("user_id", "value").param("user_name", "value")
                .param("command", "/mapache").param("text","search slack computer").param("response_url", "value"))
                .andExpect(status().is(200));
    }     

    @Test public void test_find_slackMessages() throws Exception{    
        List<Message> message = new ArrayList<Message>();   
        List<MessageReactions> reactions = new ArrayList<MessageReactions>(); 
        SlackUserProfile profile =  new SlackUserProfile("email");   
        Message m = new Message("type","subtype","ts","user","text","channel",profile,reactions);   
        List<SlackUser> users = new ArrayList<SlackUser>();    
        SlackUser user = new SlackUser("id","name","real_name",profile);
        users.add(user);  
        message.add(m);    
        when(messageRepository.findByTextInChannel(any(), any(), any())).thenReturn(message);  
        when(slackUserRepository.findByID(any())).thenReturn(users);
        mockMvc.perform(post(testURL).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("token", slackSlashCommandController.getSlackToken())
                .param("team_id", "value").param("team_domain", "value").param("channel_id", "value")
                .param("channel_name", "value").param("user_id", "value").param("user_name", "value")
                .param("command", "/mapache").param("text","search slack computer").param("response_url", "value"))
                .andExpect(status().is(200));
    }     
  
}
