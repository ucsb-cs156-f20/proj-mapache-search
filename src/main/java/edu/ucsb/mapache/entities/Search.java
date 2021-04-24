package edu.ucsb.mapache.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
public class Search {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false)
  private String searchTerm;
  @Column(nullable = false)
  private int count;

  public Search(Long id, String searchTerm, int count){
      this.id = id;
      this.searchTerm = searchTerm;
      this.count = count;
  }

  public void incrementCount(){
    this.count += 1;
  }

}