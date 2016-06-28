package com.theironyard.services;

import com.theironyard.entities.Photo;
import com.theironyard.entities.User;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by michaeldelli-gatti on 6/28/16.
 */
public interface PhotoRepository extends CrudRepository<Photo, Integer> {
    public Iterable<Photo> findByRecipient(User recipient);
    public Iterable<Photo> findByIsPublicTrue();
    public Iterable<Photo> findByIsPublicTrueAndName(String username);
}
