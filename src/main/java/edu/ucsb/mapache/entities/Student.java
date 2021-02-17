package edu.ucsb.mapache.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import org.apache.commons.lang3.builder.EqualsBuilder;

@Entity
public class Student {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  @CsvBindByPosition(position = 0)
  private String email;

  @Column(nullable = false)
  @CsvBindByPosition(position = 1)
  private String teamName;

  public Student() {
  }

  public Student(Long id, String email, String teamName) {
    this.id = id;
    this.email = email;
    this.teamName = teamName;
  }

  @Override
  public String toString() {
    return String.format("Student[id=%d, email=%s, teamName=%s]", id, email, teamName);
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;
    Student other = (Student) obj;
    EqualsBuilder builder = new EqualsBuilder();
    builder.append(id, other.id).append(email, other.email).append(teamName, other.teamName);
    return builder.isEquals();
  }
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }
  public String getTeamName() {
    return teamName;
  }
  public void setTeamName(String teamName) {
    this.teamName = teamName;
  }
}