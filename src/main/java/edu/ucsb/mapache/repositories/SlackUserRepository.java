package edu.ucsb.mapache.repositories;

import edu.ucsb.mapache.documents.SlackUser;
import edu.ucsb.mapache.entities.AppUser;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface SlackUserRepository extends MongoRepository<SlackUser, ObjectId> {
    @Query("{ 'profile.email': ?0}")
    List<SlackUser> findByEmail(String email);

    public List<SlackUser> findByName(String name);

    @Query("{ 'id': ?0}")
    List<SlackUser> findByID(String id);

    @Query("{ 'is_admin': true, 'profile.email': ?0}")
    List<SlackUser> findAdminByEmail(String email);
    
    @Query("{ 'is_admin': true}")
    List<SlackUser> findAdmins();

}
