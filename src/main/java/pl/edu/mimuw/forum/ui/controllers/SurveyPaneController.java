package pl.edu.mimuw.forum.ui.controllers;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import pl.edu.mimuw.forum.ui.models.SurveyViewModel;

public class SurveyPaneController extends BasePaneController {

	private SurveyViewModel model;

	public Button getUpVoteButton() {
		return upVoteButton;
	}

	public Button getDownVoteButton() {
		return downVoteButton;
	}

	@FXML
	private Button upVoteButton;

	@FXML
	private Button downVoteButton;

	public void setModel(SurveyViewModel model) {
		if (this.model != null) {
			upVoteButton.textProperty().unbind();
			downVoteButton.textProperty().unbind();
		}

		this.model = model;

		if (this.model != null) {
			bind(upVoteButton.textProperty(), upVoteButton, this.model.getLikes());
			bind(downVoteButton.textProperty(), downVoteButton, this.model.getDislikes());
		}

		setHasModel(model != null);
	}

	private void bind(StringProperty stringProperty, Button button, IntegerProperty property) {
		stringProperty.bind(Bindings.createStringBinding(() -> String.valueOf(property.get()), property));
		button.setOnAction(evt -> property.set(property.get() + 1));
	}
}
