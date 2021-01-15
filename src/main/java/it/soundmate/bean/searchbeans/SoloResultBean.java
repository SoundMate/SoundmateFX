/*
 * Copyright (c) 2021.
 * Created by Lorenzo Pantano on 12/01/21, 13:19
 * Last edited: 12/01/21, 13:19
 */

package it.soundmate.bean.searchbeans;


import it.soundmate.model.UserType;

import java.util.ArrayList;
import java.util.List;

public class SoloResultBean extends UserResultBean {

    private final String firstName;
    private final String lastName;
    private final List<String> genreList = new ArrayList<>();

    public SoloResultBean(int id, String email, String encodedProfileImg, String firstName, String lastName) {
        super(id, email, encodedProfileImg, UserType.SOLO);
        this.firstName = firstName;
        this.lastName = lastName;
    }


    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setGenres(List<String> genreList) {
        this.genreList.addAll(genreList);
    }

    public List<String> getGenreList() {
        return genreList;
    }
}
