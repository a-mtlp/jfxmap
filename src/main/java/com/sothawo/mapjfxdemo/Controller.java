/*
 Copyright 2015-2020 Peter-Josef Meisch (pj.meisch@sothawo.com)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package com.sothawo.mapjfxdemo;

import com.sothawo.mapjfx.*;
import com.sothawo.mapjfx.event.MapLabelEvent;
import com.sothawo.mapjfx.event.MapViewEvent;
import com.sothawo.mapjfx.event.MarkerEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Controller for the FXML defined code.
 *
 * @author P.J. Meisch (pj.meisch@sothawo.com).
 */
public class Controller {

    /**
     * logger for the class.
     */
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    ArrayList<Restaurant> restaurants = new ArrayList<>();

    @FXML
    private Button buttonZoom;

    @FXML
    private MapView mapView;

    @FXML
    private HBox topControls;

    @FXML
    private Slider sliderZoom;

    @FXML
    private Accordion leftControls;

    @FXML
    private TextField lat;

    @FXML
    private TextField lieferUmkreis;

    @FXML
    private TextField restaurantName;

    @FXML
    private TextField lng;


    public void initMapAndControls(Projection projection) {


        setControlsDisable(true);

        buttonZoom.setOnAction(event -> mapView.setZoom(14));
        sliderZoom.valueProperty().bindBidirectional(mapView.zoomProperty());


        // watch the MapView's initialized property to finish initialization
        mapView.initializedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                afterMapIsInitialized();
            }
        });

        onlyFloat(lat);
        onlyFloat(lng);
        onlyFloat(lieferUmkreis);

        mapView.setMapType(MapType.OSM);

        setupEventHandlers();
        mapView.initialize(Configuration.builder()
                .projection(projection)
                .showZoomControls(false)
                .build());
        logger.debug("initialization finished");
    }


    private void onlyFloat(TextField txt) {
        txt.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                mapView.setAnimationDuration(0);
            } else {
                try {
                    Float.parseFloat(newValue);
                } catch (NumberFormatException e) {
                    txt.setText(oldValue);
                }
            }
        });
    }

    @FXML
    private void onCreateNewRestaurant(ActionEvent e) {
        Restaurant restaurant = new Restaurant(
                restaurantName.getText(),
                Double.parseDouble(lat.getText()),
                Double.parseDouble(lng.getText()),
                Double.parseDouble(lieferUmkreis.getText())
        );
        Coordinate coordinate = new Coordinate(Double.parseDouble(lat.getText()), Double.parseDouble(lng.getText()));
        Marker markerKaCastle = new Marker(Objects.requireNonNull(getClass().getResource("/restaurant.png")), -28, -28)
                .setPosition(coordinate)
                .setVisible(true);
        MapCircle circle = new MapCircle(coordinate, Double.parseDouble(this.lieferUmkreis.getText()) * 1000)
                .setVisible(true);
        mapView.addMapCircle(circle);
        mapView.addMarker(markerKaCastle);
        this.restaurants.add(restaurant);


    }

    private void setupEventHandlers() {
        // add an event handler for singleclicks, set the click marker to the new position when it's visible
        mapView.addEventHandler(MapViewEvent.MAP_CLICKED, event -> {
            event.consume();
        });

        // add an event handler for MapViewEvent#MAP_EXTENT and set the extent in the map
        mapView.addEventHandler(MapViewEvent.MAP_EXTENT, event -> {
            event.consume();
        });

        // add an event handler for extent changes and display them in the status label
        mapView.addEventHandler(MapViewEvent.MAP_BOUNDING_EXTENT, event -> {
            event.consume();
        });

        mapView.addEventHandler(MapViewEvent.MAP_RIGHTCLICKED, event -> {
            event.consume();
        });

        mapView.addEventHandler(MarkerEvent.MARKER_CLICKED, event -> {
            event.consume();
        });

        mapView.addEventHandler(MarkerEvent.MARKER_RIGHTCLICKED, event -> {
            event.consume();
        });

        mapView.addEventHandler(MapLabelEvent.MAPLABEL_CLICKED, event -> {
            event.consume();
        });

        mapView.addEventHandler(MapLabelEvent.MAPLABEL_RIGHTCLICKED, event -> {
            event.consume();
        });

        mapView.addEventHandler(MapViewEvent.MAP_POINTER_MOVED, event -> {
            logger.debug("pointer moved to " + event.getCoordinate());
        });

        logger.trace("map handlers initialized");
    }


    private void setControlsDisable(boolean flag) {
        topControls.setDisable(flag);
        leftControls.setDisable(flag);
    }

    /**
     * finishes setup after the mpa is initialzed
     */
    private void afterMapIsInitialized() {
        logger.trace("map intialized");
        logger.debug("setting center and enabling controls...");
        // start at the harbour with default zoom
        mapView.setZoom(14);

        mapView.setCenter(new Coordinate(49.015511, 8.323497));

        // now enable the controls
        setControlsDisable(false);
    }

}
