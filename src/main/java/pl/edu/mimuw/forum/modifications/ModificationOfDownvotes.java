package pl.edu.mimuw.forum.modifications;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;

import static jdk.nashorn.internal.objects.NativeFunction.bind;

/**
 * Created by piternet on 11.06.16.
 */
public class ModificationOfDownvotes extends Modification {
    Integer oldValue, newValue;

    Button downVoteButton;

    public ModificationOfDownvotes(Button upVoteButton, Integer oldValue, Integer newValue) {
        this.downVoteButton = upVoteButton;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public void undo() {
        bind(downVoteButton.textProperty(), downVoteButton, new SimpleIntegerProperty(oldValue));
    }

    public void redo() {
        bind(downVoteButton.textProperty(), downVoteButton, new SimpleIntegerProperty(newValue));
    }

    private void bind(StringProperty stringProperty, Button button, IntegerProperty property) {
        stringProperty.bind(Bindings.createStringBinding(() -> String.valueOf(property.get()), property));
        button.setOnAction(evt -> property.set(property.get() + 1));
    }
}
