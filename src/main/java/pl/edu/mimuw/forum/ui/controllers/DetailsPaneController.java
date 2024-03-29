package pl.edu.mimuw.forum.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import pl.edu.mimuw.forum.ui.models.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Kontroler widoku do edycji wezlow forum.
 * Zawiera i koordynuje dzialanie podkontrolerow widokow
 * dla poszczegolnych typow wezlow (zadania, sugestii, etc.).
 * W zaleznosci od typu aktualnie edytowanego wezla wyswietlane
 * sa tylko te widoki, ktore odpowiadaja temu wezlowi.
 * 
 * @author konraddurnoga
 *
 */
public class DetailsPaneController implements Initializable {

	public ContentPaneController getContentController() {
		return contentController;
	}

	@FXML
	private ContentPaneController contentController;

	public SuggestionPaneController getSuggestionController() {
		return suggestionController;
	}

	@FXML
	private SuggestionPaneController suggestionController;
	
	@FXML
	private TaskPaneController taskController;

	public SurveyPaneController getSurveyController() {
		return surveyController;
	}

	public TaskPaneController getTaskController() {
		return taskController;
	}

	@FXML
	private SurveyPaneController surveyController;

	
	public void setModel(NodeViewModel model) {
		contentController.setModel(null);
		suggestionController.setModel(null);
		taskController.setModel(null);
		surveyController.setModel(null);
		
		if (model != null) {
			model.presentOn(this);
		}
	}
	
	public void present(NodeViewModel model) {
		contentController.setModel(model);
	}
	
	public void present(CommentViewModel comment) {
		present((NodeViewModel) comment);
	}
	
	public void present(SuggestionViewModel suggestion) {
		present((NodeViewModel) suggestion);
		suggestionController.setModel(suggestion);
	}
	
	public void present(TaskViewModel task) {
		present((NodeViewModel) task);
		taskController.setModel(task);
	}
	
	public void present(SurveyViewModel survey) {
		present((NodeViewModel) survey);
		surveyController.setModel(survey);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setModel(null);
	}

}
