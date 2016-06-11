package pl.edu.mimuw.forum.modifications;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

/**
 * Created by piternet on 11.06.16.
 */
public class ModificationOfContent extends Modification {

    String oldValue, newValue;

    StringProperty textProperty;

    public ModificationOfContent(StringProperty textProperty, String oldValue, String newValue) {
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
