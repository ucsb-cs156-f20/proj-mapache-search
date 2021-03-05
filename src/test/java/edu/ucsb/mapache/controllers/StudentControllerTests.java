package edu.ucsb.mapache.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ucsb.mapache.advice.AuthControllerAdvice;
import edu.ucsb.mapache.entities.Student;
import edu.ucsb.mapache.entities.Team;
import edu.ucsb.mapache.repositories.StudentRepository;
import edu.ucsb.mapache.repositories.TeamRepository;
import edu.ucsb.mapache.services.CSVToObjectService;

@WebMvcTest(value = StudentController.class)
@WithMockUser
public class StudentControllerTests {
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @MockBean
  StudentRepository mockStudentRepository;
  @MockBean
  TeamRepository mockTeamRepository;
  @MockBean
  AuthControllerAdvice mockAuthControllerAdvice;
  @MockBean
  CSVToObjectService mockCSVToObjectService;

  @MockBean
  Reader mockReader;

  private String userToken() {
    return "blah";
  }

  @Test
  public void createStudent_teamExists() throws Exception {
    Student expectedStudent = new Student(1L, "email", "team1");
    ObjectMapper mapper = new ObjectMapper();
    String requestBody = mapper.writeValueAsString(expectedStudent);
    Team team1 = new Team(1L, "team1", "team description");
    List<Team> teams = new ArrayList<Team>();
    teams.add(team1);
    when(mockAuthControllerAdvice.getIsAdmin(anyString())).thenReturn(true);
    when(mockStudentRepository.save(any())).thenReturn(expectedStudent);
    when(mockTeamRepository.findByTeamName(any())).thenReturn(teams);

    MvcResult response = mockMvc
        .perform(post("/api/students").with(csrf()).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
            .content(requestBody).header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken()))
        .andExpect(status().isOk()).andReturn();
    verify(mockStudentRepository, times(1)).save(expectedStudent);
    String responseString = response.getResponse().getContentAsString();
    Student actualStudent = objectMapper.readValue(responseString, Student.class);
    assertEquals(actualStudent, expectedStudent);
  }

  @Test
  public void createStudent_newTeam() throws Exception {
    Student expectedStudent = new Student(1L, "email", "team");
    ObjectMapper mapper = new ObjectMapper();
    String requestBody = mapper.writeValueAsString(expectedStudent);
    List<Team> empty = new ArrayList<Team>();
    Team expectedTeam = new Team("team", "");
    when(mockAuthControllerAdvice.getIsAdmin(anyString())).thenReturn(true);
    when(mockStudentRepository.save(any())).thenReturn(expectedStudent);
    when(mockTeamRepository.save(any())).thenReturn(expectedTeam);
    when(mockTeamRepository.findByTeamName(any())).thenReturn(empty);
    MvcResult response = mockMvc
        .perform(post("/api/students").with(csrf()).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
            .content(requestBody).header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken()))
        .andExpect(status().isOk()).andReturn();
    verify(mockStudentRepository, times(1)).save(expectedStudent);
    verify(mockTeamRepository, times(1)).save(expectedTeam);
    String responseString = response.getResponse().getContentAsString();
    Student actualStudent = objectMapper.readValue(responseString, Student.class);
    assertEquals(actualStudent, expectedStudent);
  }

  @Test
  public void test_createStudent_unauthorizedIfNotAdmin() throws Exception {
    Student expectedStudent = new Student(1L, "email", "team");
    ObjectMapper mapper = new ObjectMapper();
    String requestBody = mapper.writeValueAsString(expectedStudent);
    mockMvc
        .perform(post("/api/students").with(csrf()).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
            .content(requestBody).header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void deleteStudent() throws Exception {
    Student expectedStudent = new Student(1L, "email", "team");
    when(mockStudentRepository.findById(1L)).thenReturn(Optional.of(expectedStudent));
    when(mockAuthControllerAdvice.getIsAdmin(anyString())).thenReturn(true);
    MvcResult response = mockMvc
        .perform(delete("/api/students/1").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .characterEncoding("utf-8").header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken()))
        .andExpect(status().isNoContent()).andReturn();
    verify(mockStudentRepository, times(1)).findById(expectedStudent.getId());
    verify(mockStudentRepository, times(1)).deleteById(expectedStudent.getId());

    String responseString = response.getResponse().getContentAsString();

    assertEquals(responseString.length(), 0);
  }

  @Test
  public void deleteAllStudents() throws Exception {
    List<Student> expectedStudents = new ArrayList<Student>();
    expectedStudents.add(new Student(1L, "email", "team"));
    when(mockStudentRepository.findAll()).thenReturn(expectedStudents);
    when(mockAuthControllerAdvice.getIsAdmin(anyString())).thenReturn(true);
    MvcResult response = mockMvc.perform(delete("/api/students").with(csrf()).contentType(MediaType.APPLICATION_JSON)
        .characterEncoding("utf-8").header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken()))
        .andExpect(status().isNoContent()).andReturn();
    verify(mockStudentRepository, times(1)).deleteAll();

    String responseString = response.getResponse().getContentAsString();
    assertEquals(responseString.length(), 0);
  }

  @Test
  public void testDeleteAllStudents_unauthorizedIfNotAdmin() throws Exception {
    Student expectedStudent = new Student(1L, "email", "team");
    ObjectMapper mapper = new ObjectMapper();
    String requestBody = mapper.writeValueAsString(expectedStudent);
    when(mockAuthControllerAdvice.getIsAdmin(anyString())).thenReturn(false);
    mockMvc
        .perform(delete("/api/students").with(csrf()).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
            .content(requestBody).header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void testDeleteStudent_unauthorizedIfNotAdmin() throws Exception {
    Student expectedStudent = new Student(1L, "email", "team");
    ObjectMapper mapper = new ObjectMapper();
    String requestBody = mapper.writeValueAsString(expectedStudent);
    when(mockAuthControllerAdvice.getIsAdmin(anyString())).thenReturn(false);
    mockMvc
        .perform(delete("/api/students/1").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .characterEncoding("utf-8").content(requestBody).header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void testDeleteStudent_ifNoStudents() throws Exception {
    Optional<Student> expectedStudents = Optional.ofNullable(null);
    when(mockStudentRepository.findById(1L)).thenReturn(expectedStudents);
    ObjectMapper mapper = new ObjectMapper();
    String requestBody = mapper.writeValueAsString(expectedStudents);
    when(mockAuthControllerAdvice.getIsAdmin(anyString())).thenReturn(true);
    mockMvc
        .perform(delete("/api/students/1").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .characterEncoding("utf-8").content(requestBody).header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken()))
        .andExpect(status().isNotFound());
  }

  @Test
  public void testGetANonExistingStudent() throws Exception {
    when(mockAuthControllerAdvice.getIsAdmin(anyString())).thenReturn(true);
    when(mockStudentRepository.findById(99999L)).thenReturn(Optional.ofNullable(null));
    mockMvc.perform(get("/api/students/99999").contentType("application/json").header(HttpHeaders.AUTHORIZATION,
        "Bearer " + userToken())).andExpect(status().isNotFound());
  }

  @Test
  public void getStudent() throws Exception {
    Student expectedStudent = new Student(1L, "email", "team");
    ObjectMapper mapper = new ObjectMapper();
    String requestBody = mapper.writeValueAsString(expectedStudent);
    when(mockAuthControllerAdvice.getIsAdmin(anyString())).thenReturn(true);
    when(mockStudentRepository.findById(1L)).thenReturn(Optional.of(expectedStudent));
    MvcResult response = mockMvc.perform(get("/api/students/1").contentType("application/json")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken())).andExpect(status().isOk()).andReturn();
    verify(mockStudentRepository, times(1)).findById(1L);
    String responseString = response.getResponse().getContentAsString();
    Student actualStudent = objectMapper.readValue(responseString, Student.class);
    assertEquals(actualStudent, expectedStudent);
  }

  @Test
  public void testGetStudent_notAdmin() throws Exception {
    Student expectedStudent = new Student(1L, "email", "team");
    ObjectMapper mapper = new ObjectMapper();
    String requestBody = mapper.writeValueAsString(expectedStudent);
    when(mockAuthControllerAdvice.getIsAdmin(anyString())).thenReturn(false);
    MvcResult response = mockMvc.perform(get("/api/students/1").contentType("application/json")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken())).andExpect(status().isUnauthorized()).andReturn();
  }

  @Test
  public void getAllStudents() throws Exception {
    List<Student> expectedStudents = new ArrayList<Student>();
    expectedStudents.add(new Student(1L, "email", "team"));
    ObjectMapper mapper = new ObjectMapper();
    String requestBody = mapper.writeValueAsString(expectedStudents);
    when(mockStudentRepository.findAll()).thenReturn(expectedStudents);
    when(mockAuthControllerAdvice.getIsAdmin(anyString())).thenReturn(true);
    MvcResult response = mockMvc.perform(
        get("/api/students").contentType("application/json").header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken()))
        .andExpect(status().isOk()).andReturn();
    verify(mockStudentRepository, times(1)).findAll();
    String responseString = response.getResponse().getContentAsString();
    List<Student> actualStudents = objectMapper.readValue(responseString, new TypeReference<List<Student>>() {
    });
    assertEquals(actualStudents, expectedStudents);
  }

  @Test
  public void testGetStudents_notAdmin() throws Exception {
    List<Student> expectedStudents = new ArrayList<Student>();
    ObjectMapper mapper = new ObjectMapper();
    String requestBody = mapper.writeValueAsString(expectedStudents);
    when(mockStudentRepository.findAll()).thenReturn(expectedStudents);
    when(mockAuthControllerAdvice.getIsAdmin(anyString())).thenReturn(false);
    MvcResult response = mockMvc.perform(
        get("/api/students").contentType("application/json").header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken()))
        .andExpect(status().isUnauthorized()).andReturn();
  }

  @Test
  public void updateStudent() throws Exception {
    Student inputStudent = new Student(1L, "email", "team");
    Student savedStudent = new Student(1L, "email2", "team");
    String body = objectMapper.writeValueAsString(inputStudent);
    when(mockAuthControllerAdvice.getIsAdmin(anyString())).thenReturn(true);
    when(mockStudentRepository.findById(any(Long.class))).thenReturn(Optional.of(savedStudent));
    when(mockStudentRepository.save(inputStudent)).thenReturn(inputStudent);
    MvcResult response = mockMvc
        .perform(put("/api/students/1").with(csrf()).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken()).content(body))
        .andExpect(status().isOk()).andReturn();
    verify(mockStudentRepository, times(1)).findById(inputStudent.getId());
    verify(mockStudentRepository, times(1)).save(inputStudent);
    String responseString = response.getResponse().getContentAsString();
    assertEquals(body, responseString);
  }

  @Test
  public void testUpdateStudent_studentAtPathOwned_butTryingToOverwriteAnotherStudent() throws Exception {
    Student inputStudent = new Student(1L, "email", "team");
    Student savedStudent = new Student(2L, "email", "team");
    String body = objectMapper.writeValueAsString(inputStudent);
    when(mockAuthControllerAdvice.getIsAdmin(anyString())).thenReturn(false);
    when(mockStudentRepository.findById(any(Long.class))).thenReturn(Optional.of(savedStudent));
    mockMvc
        .perform(put("/api/students/2").with(csrf()).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken()).content(body))
        .andExpect(status().isBadRequest()).andReturn();
    verify(mockStudentRepository, times(1)).findById(2L);
    verify(mockStudentRepository, times(0)).save(any(Student.class));
  }

  @Test
  public void testUpdateStudent_studentExists_updateValues() throws Exception {
    Student inputStudent = new Student(1L, "email", "team");
    Student savedStudent = new Student(1L, "email2", "team2");
    String body = objectMapper.writeValueAsString(inputStudent);
    when(mockAuthControllerAdvice.getIsAdmin(anyString())).thenReturn(true);
    when(mockStudentRepository.findById(any(Long.class))).thenReturn(Optional.of(savedStudent));
    when(mockStudentRepository.save(inputStudent)).thenReturn(inputStudent);
    MvcResult response = mockMvc
        .perform(put("/api/students/1").with(csrf()).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken()).content(body))
        .andExpect(status().isOk()).andReturn();

    verify(mockStudentRepository, times(1)).findById(inputStudent.getId());
    verify(mockStudentRepository, times(1)).save(inputStudent);

    String responseString = response.getResponse().getContentAsString();

    assertEquals(body, responseString);
  }

  @Test
  public void testUpdateStudent_ifNotPresent() throws Exception {
    Optional<Student> expectedStudents = Optional.ofNullable(null);
    when(mockStudentRepository.findById(2L)).thenReturn(expectedStudents);
    ObjectMapper mapper = new ObjectMapper();
    String requestBody = mapper.writeValueAsString(expectedStudents);

    when(mockAuthControllerAdvice.getIsAdmin(anyString())).thenReturn(true);
    mockMvc
        .perform(put("/api/students/2").with(csrf()).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
            .content(requestBody).header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken()))
        .andExpect(status().isNotFound());
  }

  @Test
  public void testUploadFile() throws Exception {
    List<Student> expectedStudents = new ArrayList<Student>();
    expectedStudents.add(new Student(1L, "email", "team1"));
    expectedStudents.add(new Student(2L, "email", "team2"));
    List<Team> teams = new ArrayList<Team>();
    Team team1 = new Team("team1", "");
    Team team2 = new Team("team2", "");
    teams.add(team1);
    when(mockCSVToObjectService.parse(any(Reader.class), eq(Student.class))).thenReturn(expectedStudents);
    when(mockTeamRepository.findByTeamName("team1")).thenReturn(teams);
    when(mockTeamRepository.findByTeamName("team2")).thenReturn(new ArrayList<Team>());
    MockMultipartFile mockFile = new MockMultipartFile("csv", "test.csv", MediaType.TEXT_PLAIN_VALUE,
        "value,done\ntodo,false".getBytes());
    MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    MvcResult response = mockMvc
        .perform(
            multipart("/api/students/upload").file(mockFile).header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken()))
        .andExpect(status().isOk()).andReturn();
    verify(mockStudentRepository, times(1)).saveAll(expectedStudents);
    verify(mockTeamRepository, times(1)).save(team2);
  }

  @Test
  public void testUploadFileThrowsRuntime() throws Exception {
    StudentController studentController = mock(StudentController.class);
    when(mockCSVToObjectService.parse(any(Reader.class), eq(Student.class))).thenThrow(RuntimeException.class);
    MockMultipartFile mockFile = new MockMultipartFile("csv", "test.csv", MediaType.TEXT_PLAIN_VALUE,
        "value,done\ntodo,false".getBytes());
    MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    MvcResult response = mockMvc
        .perform(
            multipart("/api/students/upload").file(mockFile).header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken()))
        .andExpect(status().isBadRequest()).andReturn();

    verify(mockStudentRepository, never()).saveAll(any());
  }
}
