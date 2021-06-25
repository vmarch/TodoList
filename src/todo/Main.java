package todo;

import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import todo.tools.ConstantManager;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setAlwaysOnTop(true);
        BorderPane root = FXMLLoader.load(getClass().getResource("ui/fxml/todo.fxml"));
        primaryStage.setTitle(ConstantManager.APP_NAME);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
