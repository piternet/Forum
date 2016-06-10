package pl.edu.mimuw.forum.modifications;

import pl.edu.mimuw.forum.ui.models.NodeViewModel;

/**
 * Created by piternet on 09.06.16.
 */
public class ModificationOfRedo extends Modification {

    Modification undoMod;

    public ModificationOfRedo(Modification undoMod) {
        this.undoMod = undoMod;
    }

    public void undo() {
        undoMod.undo();
    }

    public void redo() {
        undoMod.redo();
    }
}
