package com.connectedworldservices.nectr.v2.api.rest.repository;

import org.springframework.data.repository.CrudRepository;

import com.connectedworldservices.nectr.v2.api.rest.model.User;

public interface UserRepository extends CrudRepository<User, String> {

}
