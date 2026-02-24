package oshrik.shidech_stable_match.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import oshrik.shidech_stable_match.datamodels.User;

@Repository
public interface UserRepository extends MongoRepository<User, String>
{
    
    
} 
