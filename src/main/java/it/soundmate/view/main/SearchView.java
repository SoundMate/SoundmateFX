package it.soundmate.view.main;

import it.soundmate.model.User;
import it.soundmate.view.UIUtils;
import it.soundmate.view.search.DefaultSearchView;
import it.soundmate.view.search.MapSearchView;
import it.soundmate.view.search.SoloSearchView;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SearchView extends Pane {

    private static final Logger logger = LoggerFactory.getLogger(SearchView.class);

    private final BorderPane searchBorderPane;
    private final VBox contentVBox;

    public BorderPane getSearchBorderPane() {
        return searchBorderPane;
    }

    public VBox getContentVBox() {
        return contentVBox;
    }

    public SearchView(User searcher){
        this.contentVBox = new VBox();
        this.contentVBox.setAlignment(Pos.TOP_CENTER);
        UIUtils.setBackgroundPane("#232323", this.contentVBox);
        this.searchBorderPane = new DefaultSearchView(this, searcher);
        this.contentVBox.getChildren().add(this.searchBorderPane);
    }

    public void setMapView() {
        this.contentVBox.getChildren().set(0, new MapSearchView(this));
        logger.info("Search Map Page set");
    }

    public void setDefaultView() {
        this.contentVBox.getChildren().set(0, this.searchBorderPane);
        logger.info("Default Search Page set");
    }

    public void setDetailViewSolo(SoloSearchView soloSearchView) {
        this.contentVBox.getChildren().set(0, soloSearchView);
    }
}
