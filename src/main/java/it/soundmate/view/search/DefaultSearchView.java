package it.soundmate.view.search;

import it.soundmate.bean.searchbeans.UserResultBean;
import it.soundmate.constants.Style;
import it.soundmate.controller.logic.SearchController;
import it.soundmate.model.Genre;
import it.soundmate.view.UIUtils;
import it.soundmate.view.main.SearchView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DefaultSearchView extends BorderPane {

    private final SearchController searchController = new SearchController();
    private static final Logger logger = LoggerFactory.getLogger(DefaultSearchView.class);
    private final SearchView searchView;

    private final TextField searchTextField = new TextField();
    private final Button searchBtn = new Button();
    private final Button searchOnMapBtn = new Button();
    private final List<RadioButton> filters = new ArrayList<>();
    private final ScrollPane resultsScrollPane = new ScrollPane();
    private final VBox resultsVBox = new VBox();

    //Advanced Filters
    private final ComboBox<Label> genresComboBox = new ComboBox<>();
    private final ComboBox<Label> instrumentsComboBox = new ComboBox<>();
    private final ComboBox<Label> cityComboBox = new ComboBox<>();


    public DefaultSearchView(SearchView searchView) {
        this.searchView = searchView;
        buildDefaultView();
    }

    private BorderPane buildDefaultView() {
        UIUtils.setBackgroundPane("#232323", this);

        Node top = buildTopSearchBar();
        this.setTop(top);
        this.setCenter(resultsScrollPane);

        this.resultsScrollPane.setStyle("-fx-background: #232323; -fx-background-color: #232323; -fx-border-color: #232323");
        this.resultsScrollPane.setContent(this.resultsVBox);
        this.resultsScrollPane.setPrefViewportWidth(Region.USE_COMPUTED_SIZE);
        this.resultsScrollPane.setPrefViewportHeight(Region.USE_COMPUTED_SIZE);

        this.resultsVBox.setPadding(new Insets(25));
        this.resultsVBox.setSpacing(10);
        this.resultsVBox.setAlignment(Pos.CENTER_LEFT);
        this.resultsVBox.setPrefHeight(USE_COMPUTED_SIZE);
        this.resultsVBox.setPrefWidth(USE_COMPUTED_SIZE);

        return this;
    }

    private Node buildTopSearchBar() {
        VBox topVBox = new VBox();
        topVBox.setSpacing(10);
        topVBox.setPadding(new Insets(25));
        topVBox.setAlignment(Pos.CENTER);
        topVBox.setPrefWidth(USE_COMPUTED_SIZE);
        topVBox.setPrefHeight(USE_COMPUTED_SIZE);

        Label searchLabel = new Label("Search");
        searchLabel.setStyle(Style.HEADER_TEXT);

        HBox searchBarHBox = buildSearchBarHBox();
        HBox filtersHBox = buildFiltersHBox();
        HBox advancedFiltersHBox = buildAdvancedFiltersHBox();

        topVBox.getChildren().addAll(searchLabel, searchBarHBox, filtersHBox, advancedFiltersHBox);
        return topVBox;
    }

    private HBox buildAdvancedFiltersHBox() {
        HBox filtersHBox = new HBox();
        filtersHBox.setPrefHeight(USE_COMPUTED_SIZE);
        filtersHBox.setPrefWidth(USE_COMPUTED_SIZE);
        filtersHBox.setAlignment(Pos.CENTER);
        filtersHBox.setSpacing(20);

        //City comboBox
        this.cityComboBox.setPromptText("City");
        this.cityComboBox.getItems().add(new Label("Rome, IT"));

        //Genres ComboBox
        buildGenresComboBox();

        //Instrument ComboBox
        this.instrumentsComboBox.setPromptText("Instruments");
        this.instrumentsComboBox.getItems().add(new Label("Guitar"));
        this.instrumentsComboBox.getItems().add(new Label("Drums"));


        Label label = new Label("Filter by: ");
        label.setStyle(Style.MID_LABEL);
        filtersHBox.getChildren().addAll(label, this.genresComboBox);
        UIUtils.addRegion(null, filtersHBox);
        filtersHBox.getChildren().add(this.instrumentsComboBox);
        UIUtils.addRegion(null, filtersHBox);
        filtersHBox.getChildren().add(this.cityComboBox);
        return filtersHBox;
    }

    private void buildGenresComboBox() {
        this.genresComboBox.setPromptText("Genres");
        this.genresComboBox.setVisibleRowCount(5);
        for (Genre genre : Genre.values()) {
            Label genreLabel = new Label(genre.name());
            this.genresComboBox.getItems().add(genreLabel);
        }
    }

    private HBox buildFiltersHBox() {
        HBox filtersHBox = new HBox();
        filtersHBox.setPrefHeight(USE_COMPUTED_SIZE);
        filtersHBox.setPrefWidth(USE_COMPUTED_SIZE);
        filtersHBox.setAlignment(Pos.CENTER);
        filtersHBox.setSpacing(20);

        this.filters.add(UIUtils.createRadioButton("Musicians"));
        this.filters.add(UIUtils.createRadioButton("Bands"));
        this.filters.add(UIUtils.createRadioButton("Rooms"));

        Label filterLabel = new Label("Filter Results");
        filterLabel.setStyle(Style.MID_LABEL);

        this.searchOnMapBtn.setText("Search Room On Map");
        this.searchOnMapBtn.setStyle(Style.BTN_STYLE_1);
        this.searchOnMapBtn.setOnAction(new SearchMapAction());

        filtersHBox.getChildren().add(filterLabel);
        filtersHBox.getChildren().addAll(this.filters);
        UIUtils.addRegion(null, filtersHBox);
        filtersHBox.getChildren().add(this.searchOnMapBtn);
        return filtersHBox;
    }

    private HBox buildSearchBarHBox() {
        HBox hBox = new HBox();
        hBox.setSpacing(15);
        hBox.setAlignment(Pos.CENTER);
        hBox.setPrefWidth(USE_COMPUTED_SIZE);
        hBox.setPrefHeight(USE_COMPUTED_SIZE);

        //Prima riga (textField e button)
        this.searchTextField.setStyle(Style.TEXT_FIELD);
        this.searchTextField.setPromptText("Search...");
        this.searchBtn.setText("Search");
        this.searchBtn.setStyle(Style.BTN_STYLE_1);
        this.searchBtn.setOnAction(new SearchAction());

        hBox.getChildren().addAll(this.searchTextField, this.searchBtn);
        HBox.setHgrow(this.searchTextField, Priority.ALWAYS);
        return hBox;
    }


    private class SearchAction implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            resultsVBox.getChildren().clear();
            resultsVBox.setPrefHeight(USE_COMPUTED_SIZE);
            logger.info("Search Clicked");
            if (searchTextField.getText().isEmpty()) {  //Se la textfield è vuota non fa nessuna ricerca
                logger.error("Empty text fields");
                searchTextField.setPromptText("Try to search for Musicians, Bands or Rooms...");
                return;
            }

            Label loadingLabel = new Label("Loading results...");
            loadingLabel.setStyle(Style.LOW_LABEL);
            resultsVBox.getChildren().add(loadingLabel);

            List<UserResultBean> results = searchController.performSearch(searchTextField.getText(), getFilterValues(filters));
            logger.info("Done Search");
            if (results == null || results.isEmpty()) {
                resultsVBox.getChildren().remove(loadingLabel);
                buildNoResultsScreen();
            } else {
                buildResultsScreen(results);
            }
        }

        private boolean[] getFilterValues(List<RadioButton> filters) {
            boolean[] values = new boolean[3];
            for (int i = 0; i < 3; i++) {
                values[i] = filters.get(i).isSelected();
            }
            return values;
        }

        private void buildResultsScreen(List<UserResultBean> results) {
            ResultsView resultsView = new ResultsView(results, searchView);
            resultsVBox.getChildren().clear();

            Label bandResultsLabel = new Label("Bands");
            Label soloResultsLabel = new Label("Musicians");
            Label roomResultsLabel = new Label("Rooms");
            Label resultsLabel = new Label("Results for: "+"\""+searchTextField.getText()+"\"");
            soloResultsLabel.setStyle(Style.MID_LABEL);
            resultsLabel.setStyle(Style.HIGH_LABEL);
            bandResultsLabel.setStyle(Style.MID_LABEL);
            roomResultsLabel.setStyle(Style.MID_LABEL);

            resultsVBox.getChildren().addAll(resultsLabel, soloResultsLabel);

            //Results for SOLO
            SoloResults soloResults = resultsView.getSoloResults();
            if (soloResults.isEmpty()) {
                Label noSoloResultsLabel = new Label("No musicians found");
                noSoloResultsLabel.setStyle(Style.LOW_LABEL);
                resultsVBox.getChildren().add(noSoloResultsLabel);
            } else {
                soloResults.setStyle("-fx-background-color: #232323");
                logger.info("Results Length: {}", soloResults.length());
                resultsVBox.getChildren().addAll(soloResults, bandResultsLabel);
            }

            //Results for BAND
        }

        private void buildNoResultsScreen() {
            Label noResultsLabel = new Label("No Results Found");
            noResultsLabel.setStyle(Style.LOW_LABEL);
            resultsVBox.getChildren().add(noResultsLabel);
        }
    }


    private class SearchMapAction implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            logger.info("Search on Map clicked");
            searchView.setMapView();
        }
    }
}
