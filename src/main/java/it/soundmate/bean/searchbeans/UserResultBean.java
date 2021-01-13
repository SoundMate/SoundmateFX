/*
 * Copyright (c) 2021.
 * Created by Lorenzo Pantano on 12/01/21, 13:34
 * Last edited: 12/01/21, 13:34
 */

package it.soundmate.bean.searchbeans;

import it.soundmate.model.UserType;
import it.soundmate.utils.Cache;
import javafx.scene.image.Image;

import java.io.InputStream;

public class UserResultBean {

    private final int id;
    private final String email;
    private final InputStream profileImgIs;
    private final Image profileImg;
    private final UserType userType;

    public UserResultBean(int id, String email, String encodedImg, UserType userType) {
        this.id = id;
        this.email = email;
        this.profileImgIs = Cache.getInstance().buildProfileImg(id, encodedImg);
        this.profileImg = new Image(this.profileImgIs);
        this.userType = userType;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public InputStream getProfileImgIs() {
        return profileImgIs;
    }

    public Image getProfileImg() {
        return profileImg;
    }

    public UserType getUserType() {
        return userType;
    }
}
