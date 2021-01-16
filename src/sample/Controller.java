package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static javafx.application.Platform.*;

public class Controller implements Initializable {
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    @FXML
    public TableView<TableData> queueTable;
    @FXML
    public TableColumn<TableData, String> arriveData;
    @FXML
    public TableColumn<TableData, Integer> clientID, clientFileAmount, clientFilesSize;
    @FXML
    public TableColumn<TableData, Double> clientPrior;
    @FXML
    public Label disc0, disc1, disc2, disc3, disc4;

    public static boolean[] discs_available = new boolean[]{true, true, true, true, true};
    public static int[] current_file_size = new int[]{0, 0, 0, 0, 0};
    public static int[] current_client_id = new int[]{0, 0, 0, 0, 0};

    int CLIENT_AMOUNT = 25;
    int MIN_FILE_SIZE = 1, MAX_FILE_SIZE = 100;
    int MIN_FILE_AMOUNT = 1, MAX_FILE_AMOUNT = 5;//for one client

    Random random = new Random();
    int client_id = 0;

    private final ObservableList<TableData> data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        queueTable.setItems(data);

        arriveData.setCellValueFactory(new PropertyValueFactory<>("arriveData"));
        clientID.setCellValueFactory(new PropertyValueFactory<>("clientID"));
        clientFileAmount.setCellValueFactory(new PropertyValueFactory<>("clientFileAmount"));
        clientFilesSize.setCellValueFactory(new PropertyValueFactory<>("clientFilesSize"));
        clientPrior.setCellValueFactory(new PropertyValueFactory<>("clientPrior"));
        queueTable.setPlaceholder(new Label("Brak klientów"));

        createDiscs();
        generateClients();
        manageDiscs();
    }

    public void manageDiscs() {
        Thread manageThread = new Thread(() -> {
            while (true) {
                for (int i = 0; i < 5; i++) {//true means disc unused
                    if (discs_available[i] && data.size() != 0) {
                        try {
                            TableData chosenFile = get_current_biggest_prior();
                            current_client_id[i] = chosenFile.getClientID();
                            current_file_size[i] = chosenFile.getClientFilesSize();
                            discs_available[i] = false;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        manageThread.start();
    }

    private void generateClients() {

        Thread generateClients = new Thread(() -> {
            while (client_id < CLIENT_AMOUNT) {
                int files_amount = random.nextInt(MAX_FILE_AMOUNT - MIN_FILE_AMOUNT) + MIN_FILE_AMOUNT;
                long current_time = System.currentTimeMillis();
                int files_size = 0;
                for (int i = 0; i < files_amount; i++) {
                    files_size += random.nextInt(MAX_FILE_SIZE - MIN_FILE_SIZE) + MIN_FILE_SIZE;
                }
                data.add(new TableData(client_id, formatter.format(current_time), files_amount, files_size, 0.0));
                //queueTable.refresh();
                client_id++;
                //one second wait until next client arrive
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        generateClients.start();

    }

    private void createDiscs() {
        for (int i = 0; i < 5; i++) {//run all discs
            int finalI = i;
            Thread taskThread = new Thread(() -> {

                while (true) {
                    if (finalI == 0) {
                        manageDisc(finalI, disc0);
                    }
                    if (finalI == 1) {
                        manageDisc(finalI, disc1);
                    }
                    if (finalI == 2) {
                        manageDisc(finalI, disc2);
                    }
                    if (finalI == 3) {
                        manageDisc(finalI, disc3);
                    }
                    if (finalI == 4) {
                        manageDisc(finalI, disc4);
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            taskThread.start();
        }
    }

    private void manageDisc(int disk_number, Label disc5) {
        try {
            if (!discs_available[disk_number]) {
                runLater(() -> disc5.setText("Klient: " + current_client_id[disk_number]));
                Thread.sleep((long) 100 * current_file_size[disk_number]);//10mb goes 1 second
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        runLater(() -> disc5.setText("Available"));
        discs_available[disk_number] = true;
        current_file_size[disk_number] = 0;
    }

    TableData get_current_biggest_prior() throws ParseException {

        long current_time = System.currentTimeMillis();

        long longest_wait = current_time - new Timestamp(formatter.parse(data.get(0).getArriveData()).getTime()).getTime();
        for (TableData t : data)
            if (longest_wait < current_time - new Timestamp(formatter.parse(t.getArriveData()).getTime()).getTime())
                longest_wait = current_time - new Timestamp(formatter.parse(t.getArriveData()).getTime()).getTime();

        int biggest_file = data.get(0).getClientFilesSize();
        for (TableData t : data)
            if (biggest_file < t.getClientFilesSize())
                biggest_file = t.getClientFilesSize();

        double max_prior = 0.0;
        TableData prior_file = data.get(0);

        for (TableData datum : data) {
            double temp = count_prior(datum.getClientFilesSize(), current_time - new Timestamp(formatter.parse(datum.getArriveData()).getTime()).getTime(), longest_wait, biggest_file);
            datum.setClientPrior(temp);
            if (temp > max_prior) {
                max_prior = temp;
                prior_file = datum;
            }
        }
        data.remove(prior_file);
        queueTable.refresh();
        runLater(() -> queueTable.refresh());
        return prior_file;
    }


    double count_prior(int file_size, long wait_time, long MAX_WAITING, int BIGGEST_FILE) {
        double size = (double) (BIGGEST_FILE - file_size) / BIGGEST_FILE;
        double arrive = Math.exp(Math.pow(wait_time / (double) MAX_WAITING, 2));
        return size + arrive;
    }

}

