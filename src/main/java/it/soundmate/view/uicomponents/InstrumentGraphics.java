package it.soundmate.view.uicomponents;

import it.soundmate.model.Genre;
import javafx.scene.image.Image;

import java.util.Arrays;

public enum InstrumentGraphics {
    GUITAR("Guitar", new Image("soundmate/icons/instruments/electric-guitar.png")),
    DRUM("Drums", new Image("soundmate/icons/instruments/drum-set.png")),
    BASS("Bass", new Image("soundmate/icons/instruments/bass.png")),
    KEYBOARD("Keyboard", new Image("soundmate/icons/instruments/organ.png")),
    MIC("Microphone", new Image("soundmate/icons/instruments/microphone.png"));

    private final String name;
    private final Image source;

    InstrumentGraphics(String name, Image source) {
        this.name = name;
        this.source = source;
    }

    public static InstrumentGraphics returnInsrument(String instrument) {
        InstrumentGraphics[] instrumentGraphics = InstrumentGraphics.values();
        return Arrays.stream(instrumentGraphics)
                .filter(currentGenre -> currentGenre.toString().equals(instrument))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Genre not found:"));

    }

    public Image getSource() {
        return source;
    }

    public String getName() {
        return name;
    }
}
