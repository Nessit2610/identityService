package com.TienLe.identityService.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.TienLe.identityService.entity.UserMongo;



@Repository
public interface UserMongoRepository extends MongoRepository<UserMongo, String> {

}
