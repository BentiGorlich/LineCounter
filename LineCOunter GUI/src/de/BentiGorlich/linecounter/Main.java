package de.BentiGorlich.linecounter;

import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Main extends Application{
	
	Parent p;
	Stage s;
	Scene sc;
	
	File path_f;
	
	@FXML
	ProgressBar progress;
	@FXML
	TextField datatypes;
	@FXML
	Label path_label;
	@FXML
	Label max_lines_nr;
	@FXML
	Label max_lines_doc;
	@FXML
	Label max_char_nr;
	@FXML
	Label max_char_doc;
	@FXML
	Label total_characters;
	@FXML
	Label total_lines;
	@FXML
	Label total_docs;
	@FXML
	Label total_bytes;
	@FXML
	Label max_bytes;
	@FXML
	Label max_bytes_doc;
	@FXML
	GridPane result;
	
	@Override
	public void start(Stage s) throws Exception {
		FXMLLoader loader = new FXMLLoader(Paths.get("res","layouts", "main.fxml").toUri().toURL());
		loader.setController(this);
		p = loader.load();
		sc = new Scene(p);
		this.s = s;
		s.setScene(sc);
		s.setTitle("Linecounter");
		
		s.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> val, Number arg1, Number arg2) {
				progress.setPrefWidth(val.getValue().doubleValue());
			}
		});
		progress.setVisible(false);
		result.setVisible(false);
		s.show();
	}
	
	public static void main(String... args) {
		launch(args);
	}
	
	@FXML
	private void files(ActionEvent e) {
		DirectoryChooser pic_file = new DirectoryChooser();
		pic_file.setTitle("Select the Path");
		File path = pic_file.showDialog(s);
		if(path != null) {
			this.path_f = path;
			this.path_label.setText(path.toString());
		}
	}
	
	@FXML
	private void search(ActionEvent e) {
		if(path_f != null) {
			String[] type = this.datatypes.getText().replaceAll(" ", "").replaceAll(";;",";").split(";");
			if(type.length == 1) {
				if(type[0].equals("")) {
					type = new String[0];
				}
			}
			result.setVisible(false);
			Search search = new Search(path_f, type);
			search.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent e){
					System.out.println("succeeded");
					try {
						Count c = search.get();
						total_lines.setText(String.valueOf(c.total_lines));
						total_lines.setVisible(true);
						total_characters.setText(String.valueOf(c.total_chars));
						total_characters.setVisible(true);
						total_docs.setText(String.valueOf(c.total_docs));
						total_docs.setVisible(true);
						total_bytes.setText(c.getBytes());
						total_bytes.setVisible(true);
						max_char_nr.setText(String.valueOf(c.max_chars));
						max_char_nr.setVisible(true);
						max_lines_nr.setText(String.valueOf(c.max_lines));
						max_lines_nr.setVisible(true);
						max_bytes.setText(c.getMaxBytes());
						max_bytes.setVisible(true);
						max_bytes_doc.setText(c.max_bytes_doc);
						max_bytes_doc.setTooltip(new Tooltip(c.max_bytes_doc));
						max_bytes_doc.setVisible(true);
						max_char_doc.setText(c.max_chars_doc);
						max_char_doc.setTooltip(new Tooltip(c.max_chars_doc));
						max_char_doc.setVisible(true);
						max_lines_doc.setText(c.max_lines_doc);
						max_lines_doc.setTooltip(new Tooltip(c.max_lines_doc));
						max_lines_doc.setVisible(true);
						progress.progressProperty().unbind();
						progress.setProgress(1.0);
						result.setVisible(true);
					} catch (InterruptedException | ExecutionException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
			});
			progress.setVisible(true);
			progress.progressProperty().unbind();
			progress.setProgress(-1);
			search.setOnFailed(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent val) {
					System.out.println("failed");
					Alert a = new Alert(AlertType.ERROR, "There was an error!!!\n" + val.getSource().getException().getMessage(), ButtonType.CLOSE);
					val.getSource().getException().printStackTrace();
					a.initOwner(s);
					a.initModality(Modality.APPLICATION_MODAL);
					a.show();
					if(a.getResult() == ButtonType.CLOSE) {
						a.close();
						s.close();
					}
				}
			});
			progress.progressProperty().bind(search.progressProperty());
			Thread t = new Thread(search);
			t.start();
		}
	}
}
