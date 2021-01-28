/*
 * Copyright (c) 2021.
 * Created by Lorenzo Pantano on 20/01/21, 17:56
 * Last edited: 20/01/21, 17:56
 */

package it.soundmate.view.profiles.band;

import it.soundmate.constants.Style;
import it.soundmate.controller.graphic.profiles.BandProfileGraphicController;
import it.soundmate.exceptions.InputException;
import it.soundmate.model.Band;
import it.soundmate.model.Genre;
import it.soundmate.view.UIUtils;
import it.soundmate.view.main.ProfileView;
import it.soundmate.view.uicomponents.SocialLinks;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class BandProfileView extends VBox {

    private static final Logger logger = LoggerFactory.getLogger(BandProfileView.class);
    BandProfileGraphicController bandProfileGraphicController = new BandProfileGraphicController();
    private final ProfileView profileView;
    private final Band band;

    //UI
    private final Rectangle coverImg = new Rectangle();
    private HBox genresList = new HBox();

    //Buttons
    private final Button addCoverImgBtn = UIUtils.createStyledButton("Add cover image", new AddImageAction());
    private final Button searchSolosBtn = UIUtils.createStyledButton("Search Solos", new SearchSoloAction());
    private final Button adSocialBtn = UIUtils.createStyledButton("Add social links", new AddSocialAction());
    private final Button editProfileBtn = UIUtils.createStyledButton("Edit profile Info", new EditProfileAction());
    private final Button addGenresBtn = UIUtils.createStyledButton("Add Genres", new AddGenreAction());

    public BandProfileView(ProfileView profileView, Band user) {
        this.profileView = profileView;
        this.band = user;
        buildBandProfileView(user);
    }

    private void buildBandProfileView(Band band) {
        StackPane stackPane = this.profileView.buildStackPane(band, addCoverImgBtn, coverImg);
        HBox userInfoVBox = buildUserInfoVBox(band);
        HBox photosHBox = this.profileView.buildMediaHBox(band, new ManageMediaAction());
        HBox membersHBox = buildMembersHBox(band);
        HBox socialLinksHBox = buildSocialLinksHBox(band);

        Label nameLabel = new Label(band.getBandName());
        nameLabel.setStyle(Style.HEADER_TEXT);
        nameLabel.setPadding(new Insets(0, 0,0, 25));

        this.getChildren().addAll(stackPane, nameLabel, userInfoVBox, membersHBox, photosHBox, socialLinksHBox);
    }

    private HBox buildSocialLinksHBox(Band band) {
        HBox[] hBoxes = this.profileView.buildProfileSection("Social Links", "", adSocialBtn);
        HBox mainHBox = hBoxes[0];
        HBox socialHBox = hBoxes[1];
        for (int i = 0; i < SocialLinks.values().length; i++) {
            socialHBox.getChildren().add(buildSocialVBox(band.getSocialLinks()[i]));
        }
        return mainHBox;
    }

    private VBox buildSocialVBox(SocialLinks socialLink) {
        VBox socialVBox = new VBox();
        socialVBox.setSpacing(10);
        socialVBox.setAlignment(Pos.CENTER);
        socialVBox.setPrefWidth(USE_COMPUTED_SIZE);
        socialVBox.setPrefHeight(USE_COMPUTED_SIZE);

        Rectangle rectangle = new Rectangle();
        rectangle.setHeight(24);
        rectangle.setWidth(24);
        rectangle.setFill(new ImagePattern(socialLink.getSource()));

        Label nameLabel = new Label(socialLink.getName());
        nameLabel.setStyle(Style.MID_LABEL);
        if (socialLink.getLink() != null){
            nameLabel.setUnderline(true);
            nameLabel.setOnMouseClicked(new LinkAction(socialLink.getLink()));
        }
        socialVBox.getChildren().addAll(rectangle, nameLabel);
        return socialVBox;
    }

    private HBox buildMembersHBox(Band band) {
        HBox[] hBoxes = this.profileView.buildProfileSection("Members", "Send requests to Solos to join your Band", searchSolosBtn);
        HBox mainHBox = hBoxes[0];
        HBox membersHBox = hBoxes[1];
        if (band.getMembers() != null) {
            //Add members to membersHBox
        }
        return mainHBox;
    }

    private HBox buildUserInfoVBox(Band band) {
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(20);

        HBox[] hBoxes = this.profileView.buildProfileSection("Genres", "Add genres", editProfileBtn);
        HBox mainHBox = hBoxes[0];
        this.genresList = hBoxes[1];
        this.genresList.getChildren().remove(0);
        this.genresList.getChildren().add(addGenresBtn);
        for (Genre genre : band.getGenres()) {
            Label genreLabel = new Label(genre.name());
            genreLabel.setStyle(Style.FAV_GENRE_LABEL);
            this.genresList.getChildren().add(genreLabel);
        }
        return mainHBox;
    }


    private class AddImageAction implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            logger.info("Add image clicked");
            bandProfileGraphicController.navigateToEditView(profileView, band);
        }
    }

    private class ManageMediaAction implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            logger.info("Manage media click");
        }
    }

    private class AddGenreAction implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            logger.info("Add genre click");
            try {
                Genre genre = bandProfileGraphicController.addGenre(band);
                updateGenreUI(genre);
            } catch (InputException inputException) {
                logger.error("Input Exception: {}", inputException.getMessage());
            }
        }

        private void updateGenreUI(Genre genre) {
            Label genreLabel = new Label(genre.name());
            genreLabel.setStyle(Style.FAV_GENRE_LABEL);
            genresList.getChildren().add(genreLabel);
        }
    }

    private class EditProfileAction implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            logger.info("Edit profile info clicked");
            bandProfileGraphicController.navigateToEditView(profileView, band);
        }
    }

    private class SearchSoloAction implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            logger.info("Search solo click");
        }
    }

    private class AddSocialAction implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            logger.info("Social action clicked");
            bandProfileGraphicController.navigateToSocialView(profileView, band);
        }
    }

    private static class LinkAction implements EventHandler<MouseEvent> {

        private final String url;

        public LinkAction(String link) {
            this.url = link;
        }

        @Override
        public void handle(MouseEvent event) {
            URI uri = null;
            try {
                uri = new URI(this.url);
                java.awt.Desktop.getDesktop().browse(uri);
            } catch (URISyntaxException | IOException e) {
                logger.error("Url exception browser");
                e.printStackTrace();
            }

        }
    }
}
