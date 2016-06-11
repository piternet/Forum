package pl.edu.mimuw.forum.ui.controllers;

import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import pl.edu.mimuw.forum.ui.helpers.DateTimePicker;
import pl.edu.mimuw.forum.ui.models.TaskViewModel;

import java.util.Date;

public class TaskPaneController extends BasePaneController {

	private TaskViewModel model;
	
	@FXML
	private DateTimePicker dateTimeField;
	
	public void setModel(TaskViewModel model) {
		if (this.model != null) {
			dateTimeField.dateTimeValueProperty().unbindBidirectional(this.model.getDueDate());
		}
		
		this.model = model;
		
		if (this.model != null) {
			dateTimeField.dateTimeValueProperty().bindBidirectional(this.model.getDueDate());
		}
		
		setHasModel(this.model != null);
	}

	public ObjectProperty<Date> getDateTimeProperty() {
		return dateTimeField.dateTimeValueProperty();
	}
}
