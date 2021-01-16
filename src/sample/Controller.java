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
                            UploadedFile chosenFile = get_current_biggest_prior();
                            if(chosenFile!=null){
                                current_client_id[i] = chosenFile.getClientID();
                                current_file_size[i] = chosenFile.getFileSize();
                                discs_available[i] = false;
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {
                    Thread.sleep(30);
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
                ArrayList<Integer> files = new ArrayList<>();
                for (int i = 0; i < files_amount; i++) {
                    int curr_file = random.nextInt(MAX_FILE_SIZE - MIN_FILE_SIZE) + MIN_FILE_SIZE;
                    files_size += curr_file;
                    files.add(curr_file);
                }
                data.add(new TableData(client_id, formatter.format(current_time), files_amount, files_size, 0.0, files));
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

    UploadedFile get_current_biggest_prior() throws ParseException {

        long current_time = System.currentTimeMillis();

        long longest_wait = current_time - new Timestamp(formatter.parse(data.get(0).getArriveData()).getTime()).getTime();
        int biggest_file = data.get(0).getClientFilesCurrentSize();
        for (TableData t : data){
            if (longest_wait < current_time - new Timestamp(formatter.parse(t.getArriveData()).getTime()).getTime())
                longest_wait = current_time - new Timestamp(formatter.parse(t.getArriveData()).getTime()).getTime();
            if (biggest_file < t.getClientFilesCurrentSize())
                biggest_file = t.getClientFilesCurrentSize();
        }
        if(biggest_file==0){
            return null;
        }
        double max_prior = 0.0;
        TableData prior_file = data.get(0);

        for (TableData datum : data) {
            double temp = count_prior(datum.getClientFilesCurrentSize(),
                    current_time - new Timestamp(formatter.parse(datum.getArriveData()).getTime()).getTime(),
                    longest_wait, biggest_file,datum.getClientFileAmount());
            datum.setClientPrior(temp);
            if (temp > max_prior) {
                max_prior = temp;
                prior_file = datum;
            }
        }
//        for(TableData datum : data)
//            if (datum == prior_file)
//                datum.ilosc-=1;
       // data.remove(prior_file);

        int current_file = prior_file.getOneClientFile();
        prior_file.ilosc-=1;
        prior_file.setClientFileAmount(prior_file.ilosc);
//        if(prior_file.ilosc==0)//todo
//            data.remove(prior_file);
        //prior_file.setClientFileAmount(prior_file.getClientFileAmount()-1);
      //  data.add(prior_file);

        return new UploadedFile(prior_file.getClientID(),current_file);
    }


    double count_prior(int file_size, long wait_time, long MAX_WAITING, int BIGGEST_FILE,int how_many) {
        if(how_many==0)
            return 0;
        double size = (double) (BIGGEST_FILE - file_size) / BIGGEST_FILE;
        double arrive = Math.exp(Math.pow(wait_time / (double) MAX_WAITING, 2));
        return  size + arrive;//
    }

}

