package pl.edu.mimuw.forum.modifications;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by piternet on 11.06.16.
 */
public class ModificationOfSuggestionAcceptable extends Modification {

    Boolean oldValue, newValue;

    BooleanProperty booleanProperty;

    public ModificationOfSuggestionAcceptable(BooleanProperty booleanProperty, Boolean oldValue, Boolean newValue) {
        this.booleanProperty = booleanProperty;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public void undo() {
        booleanProperty.bindBidirectional(new SimpleBooleanProperty(oldValue));
    }

    public void redo() {
        booleanProperty.bindBidirectional(new SimpleBooleanProperty(newValue));
    }

}
