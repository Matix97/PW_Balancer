package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

public class Controller implements Initializable {
    @FXML
    public TableView<TableData> queueTable;
    private ObservableList<TableData> data= FXCollections.observableArrayList();
    @FXML
    public TableColumn<TableData,String> arriveData;
    @FXML
    public TableColumn<TableData,Integer> clientID, fileID, fileSize;
    @FXML
    public  TableColumn<TableData,Double> priorFile;

    @FXML
    public Label disc1, disc2, disc3, disc4, disc5;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AtomicInteger i = new AtomicInteger();
        while(i.getAndIncrement() <50)
            data.add(new TableData(1,1,1,"20-23-2321",2));

        queueTable.setItems(data);

        arriveData.setCellValueFactory(new PropertyValueFactory<>("arriveData"));
        clientID.setCellValueFactory(new PropertyValueFactory<>("clientID"));
        fileID.setCellValueFactory(new PropertyValueFactory<>("fileID"));
        fileSize.setCellValueFactory(new PropertyValueFactory<>("fileSize"));
        priorFile.setCellValueFactory(new PropertyValueFactory<>("priorFile"));
        queueTable.setPlaceholder(new Label("No rows to display"));

        disc1.setText("asdsad");
        Timeline timeline  = new Timeline(new KeyFrame(Duration.seconds(5), ev -> {
            disc1.setText("TEST");

                  }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(true);

        timeline.play();
    }
}
