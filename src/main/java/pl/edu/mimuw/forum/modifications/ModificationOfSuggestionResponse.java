package pl.edu.mimuw.forum.modifications;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by piternet on 11.06.16.
 */
public class ModificationOfSuggestionResponse extends Modification {

    String oldValue, newValue;

    StringProperty textProperty;

    public ModificationOfSuggestionResponse(StringProperty textProperty, String oldValue, String newValue) {
        this.textProperty = textProperty;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public void undo() {
        textProperty.bindBidirectional(new SimpleStringProperty(oldValue));
    }

    public void redo() {
        textProperty.bindBidirectional(new SimpleStringProperty(newValue));
    }
}
