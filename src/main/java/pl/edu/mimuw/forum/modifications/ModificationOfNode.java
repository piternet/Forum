package pl.edu.mimuw.forum.modifications;

import com.sun.org.apache.xpath.internal.operations.Mod;
import javafx.scene.control.TreeItem;
import pl.edu.mimuw.forum.data.Node;
import pl.edu.mimuw.forum.ui.models.NodeViewModel;

/**
 * Created by piternet on 09.06.16.
 */
public class ModificationOfNode extends Modification {

    private NodeViewModel model;

    public ModificationOfNode(NodeViewModel model) {
        this.model = model;
    }

    public void undo() {
        NodeViewModel parent = model.getParent();
        if(parent.getChildren().contains(model))
            parent.getChildren().remove(model);
    }

    public void redo() {
        NodeViewModel parent = model.getParent();
        parent.getChildren().add(model);
    }
}
