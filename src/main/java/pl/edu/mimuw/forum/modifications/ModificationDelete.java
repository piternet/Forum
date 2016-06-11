package pl.edu.mimuw.forum.modifications;

import pl.edu.mimuw.forum.ui.models.NodeViewModel;

/**
 * Created by piternet on 11.06.16.
 */
public class ModificationDelete extends Modification {

    private NodeViewModel model;

    public ModificationDelete(NodeViewModel model) {
        this.model = model;
    }

    public void undo() {
        NodeViewModel parent = model.getParent();
        parent.getChildren().add(model);
    }

    public void redo() {
        NodeViewModel parent = model.getParent();
        if(parent.getChildren().contains(model))
            parent.getChildren().remove(model);
    }

}
