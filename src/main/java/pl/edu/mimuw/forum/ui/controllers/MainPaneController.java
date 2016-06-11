   package pl.edu.mimuw.forum.ui.controllers;

   import com.thoughtworks.xstream.XStream;
   import javafx.beans.binding.Bindings;
   import javafx.beans.binding.BooleanBinding;
   import javafx.fxml.FXML;
   import javafx.fxml.FXMLLoader;
   import javafx.fxml.Initializable;
   import javafx.scene.Node;
   import javafx.scene.control.TreeItem;
   import javafx.scene.control.TreeView;
   import pl.edu.mimuw.forum.exceptions.ApplicationException;
   import pl.edu.mimuw.forum.modifications.*;
   import pl.edu.mimuw.forum.ui.bindings.MainPaneBindings;
   import pl.edu.mimuw.forum.ui.helpers.DialogHelper;
   import pl.edu.mimuw.forum.ui.models.*;
   import pl.edu.mimuw.forum.ui.tree.ForumTreeItem;
   import pl.edu.mimuw.forum.ui.tree.TreeLabel;

   import java.io.*;
   import java.net.URL;
   import java.util.ArrayList;
   import java.util.List;
   import java.util.Optional;
   import java.util.ResourceBundle;

/**
 * Kontroler glownego widoku reprezentujacego forum.
 * Widok sklada sie z drzewa zawierajacego wszystkie wezly forum oraz
 * panelu z polami do edycji wybranego wezla.
 * @author konraddurnoga
 */
public class MainPaneController implements Initializable {

	/**
	 * Korzen drzewa-modelu forum.
	 */
	private NodeViewModel document;

	/**
	 * Lista modyfikacji.
	 */
	private List<Modification> modifications = new ArrayList<Modification>();
	private List<Modification> undos = new ArrayList<Modification>();

	/**
	 * Wiazania stosowane do komunikacji z {@link pl.edu.mimuw.forum.ui.controllers.ApplicationController }.
	 */
	private MainPaneBindings bindings;

	/**
	 * Widok drzewa forum (wyswietlany w lewym panelu).
	 */
	@FXML
	private TreeView<NodeViewModel> treePane;

	/**
	 * Kontroler panelu wyswietlajacego pola do edycji wybranego wezla w drzewie.
	 */
	@FXML
	private DetailsPaneController detailsController;

	private boolean wasUndo = false;

	public MainPaneController() {
		bindings = new MainPaneBindings();
	}

	public void addModification(Modification modification) {
		modifications.add(modification);
		bindings.undoAvailableProperty().set(true);
		bindings.hasChangesProperty().set(true);
	}

	public void setUndo(boolean b) {
		bindings.undoAvailableProperty().set(b);
	}

	public void setRedo(boolean b) {
		bindings.redoAvailableProperty().set(b);
	}

	public void noModifications() {
		bindings.undoAvailableProperty().set(false);
		bindings.redoAvailableProperty().set(false);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		BooleanBinding nodeSelectedBinding = Bindings.isNotNull(treePane.getSelectionModel().selectedItemProperty());
		bindings.nodeAdditionAvailableProperty().bind(nodeSelectedBinding);
		bindings.nodeRemovaleAvailableProperty()
				.bind(nodeSelectedBinding.and(
						Bindings.createBooleanBinding(() -> getCurrentTreeItem().orElse(null) != treePane.getRoot(),
								treePane.rootProperty(), nodeSelectedBinding)));
		
		bindings.hasChangesProperty().set(false);		// TODO Nalezy ustawic na true w przypadku, gdy w widoku sa zmiany do
														// zapisania i false wpp, w odpowiednim miejscu kontrolera (niekoniecznie tutaj)
														// Spowoduje to dodanie badz usuniecie znaku '*' z tytulu zakladki w ktorej
														// otwarty jest plik - '*' oznacza niezapisane zmiany
		bindings.undoAvailableProperty().set(false);
		bindings.redoAvailableProperty().set(false);		// Podobnie z undo i redo




	}

	public MainPaneBindings getPaneBindings() {
		return bindings;
	}

	/**
	 * Otwiera plik z zapisem forum i tworzy reprezentacje graficzna wezlow forum.
	 * @param file
	 * @return
	 * @throws ApplicationException
	 */
	public Node open(File file) throws ApplicationException {
		if (file != null) {
			XStream xstream = new XStream();
			xstream.addImplicitCollection(pl.edu.mimuw.forum.data.Node.class, "children");

			String fileName = file.getPath();
			pl.edu.mimuw.forum.data.Node node = null;
			try {
				Reader rdr = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
				ObjectInputStream in = xstream.createObjectInputStream(rdr);
				node = (pl.edu.mimuw.forum.data.Node) in.readObject();
				in.close();
			}
			catch (Exception e){
				e.printStackTrace();
			}

			document = node.getModel();
		} else {
			NodeViewModel node = new CommentViewModel("Welcome to a new forum", "Admin");
			addContentListener(node);
			addAuthorListener(node);
			document = node;
		}

		/** Dzieki temu kontroler aplikacji bedzie mogl wyswietlic nazwe pliku jako tytul zakladki.
		 * Obsluga znajduje sie w {@link pl.edu.mimuw.forum.ui.controller.ApplicationController#createTab }
		 */
		getPaneBindings().fileProperty().set(file);

		return openInView(document);
	}

	/**
	 * Zapisuje aktualny stan forum do pliku.
	 * @throws ApplicationException
	 */
	public void save() throws ApplicationException {
		/**
		 * Obiekt pliku do ktorego mamy zapisac drzewo znajduje sie w getPaneBindings().fileProperty().get()
		 */
		if (document != null) {
			bindings.hasChangesProperty().set(false);
			XStream xstream = new XStream();
			xstream.addImplicitCollection(pl.edu.mimuw.forum.data.Node.class, "children");

			pl.edu.mimuw.forum.data.Node node = document.toNode();
			String fileName = getPaneBindings().fileProperty().get().getPath();

			try {
				PrintWriter pw = new PrintWriter(fileName, "UTF-8");
				ObjectOutputStream out = xstream.createObjectOutputStream(pw, "Forum");
				out.writeObject(node);
				out.close();
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Cofa ostatnio wykonana operacje na forum.
	 * @throws ApplicationException
	 */
	public void undo() throws ApplicationException {
		System.out.println("On undo");
		if(modifications.isEmpty()) {
			setUndo(false);
			return;
		}
		wasUndo = true;
		int last = modifications.size() - 1;
		Modification lastElement = modifications.get(last);
		modifications.remove(last);

		lastElement.undo();
		undos.add(lastElement);
		setRedo(true);

		if(modifications.isEmpty())
			setUndo(false);
		wasUndo = false;
	}

	/**
	 * Ponawia ostatnia cofnieta operacje na forum.
	 * @throws ApplicationException
	 */
	public void redo() throws ApplicationException {
		System.out.println("On redo");

		if(undos.isEmpty()) {
			setRedo(false);
			return;
		}

		int last = undos.size() - 1;
		Modification lastElement = undos.get(last);
		undos.remove(last);

		lastElement.redo();
		modifications.add(lastElement);
		setUndo(true);

		if(undos.isEmpty())
			setRedo(false);

	}

	void addContentListener(NodeViewModel node) {
		node.getContent().addListener((observable, oldValue, newValue) -> {
			if(wasUndo)
				return;
			addModification(new ModificationOfContent(detailsController.getContentController().commentProperty(), oldValue, newValue));
		});
	}

	void addAuthorListener(NodeViewModel node) {
		node.getAuthor().addListener((observable, oldValue, newValue) -> {
			if(wasUndo)
				return;
			addModification(new ModificationOfAuthor(detailsController.getContentController().userProperty(), oldValue, newValue));
		});
	}

	void addSuggestionResponseListener(SuggestionViewModel node) {
		node.getResponse().addListener((observable, oldValue, newValue) -> {
			if(wasUndo)
				return;
			addModification(new ModificationOfSuggestionResponse(detailsController.getSuggestionController().responseProperty(), oldValue, newValue));
		});
	}

	void addSuggestionAcceptableListener(SuggestionViewModel node) {
		node.getIsResponseAccepted().addListener((observable, oldValue, newValue) -> {
			if(wasUndo)
				return;
			addModification(new ModificationOfSuggestionAcceptable(detailsController.getSuggestionController().acceptableProperty(), oldValue, newValue));
		});
	}

	void addSurveyUpvotesListener(SurveyViewModel node) {
		node.getLikes().addListener((observable, oldValue, newValue) -> {
			if(wasUndo)
				return;
			addModification(new ModificationOfUpvotes(detailsController.getSurveyController().getUpVoteButton(), oldValue.intValue(), newValue.intValue()));
		});
	}

	void addSurveyDownvotesListener(SurveyViewModel node) {
		node.getDislikes().addListener((observable, oldValue, newValue) -> {
			if(wasUndo)
				return;
			addModification(new ModificationOfDownvotes(detailsController.getSurveyController().getDownVoteButton(), oldValue.intValue(), newValue.intValue()));
		});
	}

	void addTaskDateListener(TaskViewModel node) {
		node.getDueDate().addListener((observable, oldValue, newValue) -> {
			if(wasUndo)
				return;
			addModification(new ModificationOfTaskDate(detailsController.getTaskController().getDateTimeProperty(), oldValue, newValue));
		});
	}

	/**
	 * Podaje nowy wezel jako ostatnie dziecko aktualnie wybranego wezla.
	 * @param node
	 * @throws ApplicationException
	 */
	public void addNode(NodeViewModel node) throws ApplicationException {
		getCurrentNode().ifPresent(currentlySelected -> {
			currentlySelected.getChildren().add(node);		// Zmieniamy jedynie model, widok (TreeView) jest aktualizowany z poziomu
															// funkcji nasluchujacej na zmiany w modelu (zob. metode createViewNode ponizej)
			node.setParent(currentlySelected);
			addModification(new ModificationOfNode(node));
			addContentListener(node);
			addAuthorListener(node);
			if(node.getName().equals("Suggestion")) {
				addSuggestionResponseListener((SuggestionViewModel) node);
				addSuggestionAcceptableListener((SuggestionViewModel) node);
			}
			if(node.getName().equals("Survey")) {
				addSurveyUpvotesListener((SurveyViewModel) node);
				addSurveyDownvotesListener((SurveyViewModel) node);
			}
			if(node.getName().equals("Task")) {
				addTaskDateListener((TaskViewModel) node);
			}
		});
	}

	/**
	 * Usuwa aktualnie wybrany wezel.
	 */
	public void deleteNode() {
		getCurrentTreeItem().ifPresent(currentlySelectedItem -> {
			TreeItem<NodeViewModel> parent = currentlySelectedItem.getParent();

			NodeViewModel parentModel;
			NodeViewModel currentModel = currentlySelectedItem.getValue();
			if (parent == null) {
				return; // Blokujemy usuniecie korzenia - TreeView bez kobrzenia jest niewygodne w obsludze
			} else {
				parentModel = parent.getValue();
				parentModel.getChildren().remove(currentModel); // Zmieniamy jedynie model, widok (TreeView) jest aktualizowany z poziomu
																// funkcji nasluchujacej na zmiany w modelu (zob. metode createViewNode ponizej)
				addModification(new ModificationDelete(currentModel));
			}

		});
	}

	private Node openInView(NodeViewModel document) throws ApplicationException {
		Node view = loadFXML();

		treePane.setCellFactory(tv -> {
			try {
				//Do reprezentacji graficznej wezla uzywamy niestandardowej klasy wyswietlajacej 2 etykiety
				return new TreeLabel();
			} catch (ApplicationException e) {
				DialogHelper.ShowError("Error creating a tree cell.", e);
				return null;
			}
		});

		ForumTreeItem root = createViewNode(document);
		root.addEventHandler(TreeItem.<NodeViewModel> childrenModificationEvent(), event -> {
			if (event.wasAdded()) {
				System.out.println("Adding to " + event.getSource());
			}
			
			if (event.wasRemoved()) {
				System.out.println("Removing from " + event.getSource());
			}
		});

		treePane.setRoot(root);

		for (NodeViewModel w : document.getChildren()) {
			addToTree(w, root);
		}

		expandAll(root);

		treePane.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> onItemSelected(oldValue, newValue));

		return view;
	}
	
	private Node loadFXML() throws ApplicationException {
		FXMLLoader loader = new FXMLLoader();
		loader.setController(this);
		loader.setLocation(getClass().getResource("/fxml/main_pane.fxml"));

		try {
			return loader.load();
		} catch (IOException e) {
			throw new ApplicationException(e);
		}
	}
	
	private Optional<? extends NodeViewModel> getCurrentNode() {
		return getCurrentTreeItem().<NodeViewModel> map(TreeItem::getValue);
	}

	private Optional<TreeItem<NodeViewModel>> getCurrentTreeItem() {
		return Optional.ofNullable(treePane.getSelectionModel().getSelectedItem());
	}

	private void addToTree(NodeViewModel node, ForumTreeItem parentViewNode, int position) {
		ForumTreeItem viewNode = createViewNode(node);

		List<TreeItem<NodeViewModel>> siblings = parentViewNode.getChildren();
		siblings.add(position == -1 ? siblings.size() : position, viewNode);

		node.getChildren().forEach(child -> addToTree(child, viewNode));
	}

	private void addToTree(NodeViewModel node, ForumTreeItem parentViewNode) {
		addToTree(node, parentViewNode, -1);
	}

	private void removeFromTree(ForumTreeItem viewNode) {
		viewNode.removeChildListener();
		TreeItem<NodeViewModel> parent = viewNode.getParent();
		if (parent != null) {
			viewNode.getParent().getChildren().remove(viewNode);
		} else {
			treePane.setRoot(null);
		}
	}

	private ForumTreeItem createViewNode(NodeViewModel node) {
		ForumTreeItem viewNode = new ForumTreeItem(node);
		viewNode.setChildListener(change -> {	// wywolywanem, gdy w modelu dla tego wezla zmieni sie zawartosc kolekcji dzieci
			while (change.next()) {
				if (change.wasAdded()) {
					int i = change.getFrom();
					for (NodeViewModel child : change.getAddedSubList()) {
						addToTree(child, viewNode, i);	// uwzgledniamy nowy wezel modelu w widoku
						i++;
					}
				}

				if (change.wasRemoved()) {
					for (int i = change.getFrom(); i <= change.getTo(); ++i) {
						removeFromTree((ForumTreeItem) viewNode.getChildren().get(i)); // usuwamy wezel modelu z widoku
					}
				}
			}
		});

		return viewNode;
	}

	private void expandAll(TreeItem<NodeViewModel> item) {
		item.setExpanded(true);
		item.getChildren().forEach(this::expandAll);
	}

	private void onItemSelected(TreeItem<NodeViewModel> oldItem, TreeItem<NodeViewModel> newItem) {
		detailsController.setModel(newItem != null ? newItem.getValue() : null);
	}

}