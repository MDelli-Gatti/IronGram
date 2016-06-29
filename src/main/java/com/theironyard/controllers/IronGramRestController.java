package com.theironyard.controllers;

import com.theironyard.entities.Photo;
import com.theironyard.entities.User;
import com.theironyard.services.PhotoRepository;
import com.theironyard.services.UserRepository;
import com.theironyard.utils.PasswordStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.time.LocalDateTime;

/**
 * Created by michaeldelli-gatti on 6/28/16.
 */
@RestController
public class IronGramRestController {
    @Autowired
    UserRepository users;

    @Autowired
    PhotoRepository photos;

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public User login(@RequestBody User user, HttpSession session) throws Exception {
        User userFromDb = users.findFirstByName(user.getName());
        if (userFromDb == null){
            user.setPassword(PasswordStorage.createHash(user.getPassword()));
            users.save(user);
        }
        else if(!PasswordStorage.verifyPassword(user.getPassword(), userFromDb.getPassword())){
            throw new Exception("Wrong Password!!!");
        }
        session.setAttribute("username", user.getName());
        return user;
    }

    @RequestMapping(path = "/logout", method = RequestMethod.POST)
    public void logout(HttpSession session){
        session.invalidate();
    }

    @RequestMapping(path = "/photos", method = RequestMethod.GET)
    public Iterable<Photo> getPhotos(HttpSession session){
        String username = (String) session.getAttribute("username");
        User user = users.findFirstByName(username);
        Iterable<Photo> p = photos.findByRecipient(user);
        LocalDateTime now = LocalDateTime.now();
        for (Photo photo: p){
            if (photo.getDeleteTime() == null) {
                LocalDateTime d = LocalDateTime.now().plusSeconds(photo.getTimer());
                photo.setDeleteTime(d);
                photos.save(photo);
            }
            if (now.isAfter(photo.getDeleteTime())) {
                File f = new File("public/photos/" + photo.getFilename());
                f.delete();
                photos.delete(photo);
            }
        }
        return photos.findByRecipient(user);
    }
    @RequestMapping(path = "/public-photos", method = RequestMethod.GET)
    public Iterable<Photo> getPublicPhotos(String username){
        User sender = users.findFirstByName(username);
        return photos.findByIsPublicTrueAndSender(sender);
    }
}
