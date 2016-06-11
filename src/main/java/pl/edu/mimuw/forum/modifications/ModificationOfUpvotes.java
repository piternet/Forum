package pl.edu.mimuw.forum.modifications;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;

import static jdk.nashorn.internal.objects.NativeFunction.bind;

public class ModificationOfUpvotes extends Modification {
    Integer oldValue, newValue;

    Button upVoteButton;

    public ModificationOfUpvotes(Button upVoteButton, Integer oldValue, Integer newValue) {
        this.upVoteButton = upVoteButton;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public void undo() {
        bind(upVoteButton.textProperty(), upVoteButton, new SimpleIntegerProperty(oldValue));
    }

    public void redo() {
        bind(upVoteButton.textProperty(), upVoteButton, new SimpleIntegerProperty(newValue));
    }

    private void bind(StringProperty stringProperty, Button button, IntegerProperty property) {
        stringProperty.bind(Bindings.createStringBinding(() -> String.valueOf(property.get()), property));
        button.setOnAction(evt -> property.set(property.get() + 1));
    }
}
