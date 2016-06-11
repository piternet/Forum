package pl.edu.mimuw.forum.modifications;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Date;

/**
 * Created by piternet on 11.06.16.
 */
public class ModificationOfTaskDate extends Modification {

    Date oldValue, newValue;

    ObjectProperty<Date> dateObjectProperty;

    public ModificationOfTaskDate(ObjectProperty<Date> dateObjectProperty, Date oldValue, Date newValue) {
        this.dateObjectProperty = dateObjectProperty;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public void undo() {
        dateObjectProperty.bindBidirectional(new SimpleObjectProperty<>(oldValue));
    }

    public void redo() {
        dateObjectProperty.bindBidirectional(new SimpleObjectProperty<>(newValue));
    }

}
