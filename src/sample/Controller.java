package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
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
//import java.security.Timestamp;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Controller implements Initializable {
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    @FXML
    public TableView<TableData> queueTable;
    @FXML
    public TableColumn<TableData, String> arriveData;
    @FXML
    public TableColumn<TableData, Integer> clientID, fileID, fileSize;
    @FXML
    public TableColumn<TableData, Double> priorFile;
    @FXML
    public Label disc1, disc2, disc3, disc4, disc5;

    int CLIENT_AMOUNT = 5;
    int MIN_FILE_SIZE = 1, MAX_FILE_SIZE = 100;
    int MIN_FILE_AMOUNT = 1, MAX_FILE_AMOUNT = 5;//for one client

    Random random = new Random();

    int client_id = 0;

    ArrayList<OneDisc> discs = new ArrayList<>();
    public static boolean[] discs_flag = new boolean[]{true, true, true, true, true};
    private ObservableList<TableData> data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        queueTable.setItems(data);

        arriveData.setCellValueFactory(new PropertyValueFactory<>("arriveData"));
        clientID.setCellValueFactory(new PropertyValueFactory<>("clientID"));
        fileID.setCellValueFactory(new PropertyValueFactory<>("fileID"));
        fileSize.setCellValueFactory(new PropertyValueFactory<>("fileSize"));
        priorFile.setCellValueFactory(new PropertyValueFactory<>("priorFile"));
        queueTable.setPlaceholder(new Label("No rows to display"));

        createDiscs();
        //until 1 seconds add new clients
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> generateClients()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(true);
        timeline.play();

        //manage discs
        Timeline timeline2 = new Timeline(new KeyFrame(Duration.seconds(0.001), ev -> manageDiscs()));
        timeline2.setCycleCount(Timeline.INDEFINITE);
        timeline2.setAutoReverse(true);
        timeline2.play();

    }

    private void manageDiscs() {
        for (int i = 0; i < 5; i++) {//true means disc unused
            if (discs_flag[i] && data.size() != 0) {
                try {
                    TableData chosenFile = get_current_biggest_prior();
                    discs.get(i).file_id = chosenFile.getFileID();
                    discs.get(i).client_id = chosenFile.getClientID();
                    discs.get(i).file_size = chosenFile.getFileSize();
                    discs_flag[i] = false;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void generateClients() {
        if (client_id <= CLIENT_AMOUNT) {
            int files_amount = random.nextInt(MAX_FILE_AMOUNT - MIN_FILE_AMOUNT) + MIN_FILE_AMOUNT;
            long current_time = System.currentTimeMillis();
            for (int i = 0; i < files_amount; i++) {
                int file_size = random.nextInt(MAX_FILE_SIZE - MIN_FILE_SIZE) + MIN_FILE_SIZE;
                data.add(new TableData(i, file_size, 0, formatter.format(current_time), client_id));
                Platform.runLater(() -> queueTable.refresh());
            }
            client_id++;
        }
    }

    private void createDiscs() {
        discs.add(new OneDisc(0, true, disc1));
        discs.add(new OneDisc(1, true, disc2));
        discs.add(new OneDisc(2, true, disc3));
        discs.add(new OneDisc(3, true, disc4));
        discs.add(new OneDisc(4, true, disc5));
        for (int i = 0; i < 5; i++) {//run all discs
            Thread thread = new Thread(discs.get(i), "DISC: " + i);
            thread.start();
        }
    }

    TableData get_current_biggest_prior() throws ParseException {

        long current_time = System.currentTimeMillis();

        long longest_wait = current_time - new Timestamp(formatter.parse(data.get(0).getArriveData()).getTime()).getTime();
        for (TableData t : data)
            if (longest_wait < current_time - new Timestamp(formatter.parse(t.getArriveData()).getTime()).getTime())
                longest_wait = current_time - new Timestamp(formatter.parse(t.getArriveData()).getTime()).getTime();

        int biggest_file = data.get(0).getFileSize();
        for (TableData t : data)
            if (biggest_file < t.getFileSize())
                biggest_file = t.getFileSize();

        double max_prior = 0.0;
        TableData prior_file = data.get(0);

        for (int i = 0; i < data.size(); i++) {
            double temp = count_prior(data.get(i).getFileSize(), current_time - new Timestamp(formatter.parse(data.get(i).getArriveData()).getTime()).getTime(), longest_wait, biggest_file);
            data.get(i).setPriorFile(temp);
            if (temp > max_prior) {
                max_prior = temp;
                prior_file = data.get(i);
            }
        }
        data.remove(prior_file);
        queueTable.refresh();
        Platform.runLater(() -> queueTable.refresh());
        return prior_file;
    }


    double count_prior(int file_size, long wait_time, long MAX_WAITING, int BIGGEST_FILE) {
        double size = (double) (BIGGEST_FILE - file_size) / BIGGEST_FILE;
        double arrive = Math.exp(Math.pow(wait_time / (double) MAX_WAITING, 2));
        double prior = size + arrive;
        return prior;
    }

}

