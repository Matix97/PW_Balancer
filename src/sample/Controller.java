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
import java.text.SimpleDateFormat;
import java.util.*;

public class Controller implements Initializable {
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    @FXML
    public TableView<TableData> queueTable;
    //data to table
    private ObservableList<TableData> data = FXCollections.observableArrayList();
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

    List<Income_client> all_awaiting_clients = Collections.synchronizedList(new ArrayList<>());
    int client_id = 0;

    ArrayList<OneDisc> discs = new ArrayList<>();

    public static boolean[] discs_flag = new boolean[]{true, true, true, true, true};


    private int findByClientIdAndFileIdInTableData(int clientID, int fileID) {//searching in fxml data
        for (TableData t : data) {
            if (t.getClientID() == clientID && t.getFileID() == fileID)
                return data.indexOf(t);
        }
        return -1;
    }

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
            if (discs_flag[i] && all_awaiting_clients.size() != 0) {
                Income_file_wrapper chosenFile = get_current_biggest_prior();
                if (chosenFile == null)
                    break;
                //set file size(information to estimated how long disc should save file)

                discs.get(i).file_id = chosenFile.file_id;
                discs.get(i).client_id = chosenFile.client_id;
                discs.get(i).file_size = chosenFile.income_file.file_size;
                discs_flag[i] = false;
                //disc1.setText("UN_Available");

            }
            //else
              //  disc1.setText("Available");
        }
        queueTable.refresh();

    }

    private void generateClients() {
        if (client_id <= CLIENT_AMOUNT) {
            //rand file amount for client
            int files_amount = random.nextInt(MAX_FILE_AMOUNT - MIN_FILE_AMOUNT) + MIN_FILE_AMOUNT;
            ArrayList<Income_file> files = new ArrayList<>(files_amount);
            //get client arrive time
            long current_time = System.currentTimeMillis();
            for (int i = 0; i < files_amount; i++) {
                int file_size = random.nextInt(MAX_FILE_SIZE - MIN_FILE_SIZE) + MIN_FILE_SIZE;
                files.add(new Income_file(file_size));
                //add also file to FX table, at start prior 0(will be count when some disc will be free)
                data.add(new TableData(i, file_size, 0, formatter.format(current_time), client_id));
            }
            //add client to all clients
            all_awaiting_clients.add(new Income_client(current_time, files, client_id));
            client_id++;
//            //start disc one by one, after fifth client all are running
//            if(client_id<5){
//                Thread thread = new Thread(discs.get(client_id),"DISC: " + client_id);
//                thread.start();
//            }
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

    Income_file_wrapper get_current_biggest_prior() {
       
        long current_time = System.currentTimeMillis();

        long longest_wait = all_awaiting_clients.get(0).entry_data;
        for (Income_client a : all_awaiting_clients)
            if (longest_wait < a.entry_data)
                longest_wait = a.entry_data;

        int biggest_file = all_awaiting_clients.get(0).file_list.get(0).file_size;
        for (Income_client a : all_awaiting_clients)
            for (Income_file f : a.file_list)
                if (biggest_file < f.file_size)
                    biggest_file = f.file_size;
        double max_prior = 0.0;
        Income_file prior_file = all_awaiting_clients.get(0).file_list.get(0);
        int klient_id = 0;
        int file_id = 0;
        int real_id = all_awaiting_clients.get(0).client_id;

        for (int i = 0; i < all_awaiting_clients.size(); i++) {//Income_client a : all_awaiting_clients
            for (int j = 0; j < all_awaiting_clients.get(i).file_list.size(); j++) {//Income_file f : a.file_list

                double temp = count_prior(all_awaiting_clients.get(i).file_list.get(j).file_size, current_time - all_awaiting_clients.get(i).entry_data, longest_wait, biggest_file);
                int idx = findByClientIdAndFileIdInTableData(i, j);
                if (idx != -1)
                    data.get(idx).setPriorFile(temp);
                queueTable.refresh();
                if (temp > max_prior) {
                    max_prior = temp;
                    prior_file = all_awaiting_clients.get(i).file_list.get(j);
                    klient_id = i;
                    file_id = j;
                    real_id = all_awaiting_clients.get(i).client_id;
                }
            }
        }
        queueTable.refresh();

        if (all_awaiting_clients.size() > 0)
            if (all_awaiting_clients.get(klient_id).file_list.size() == 1) {
                int idx = findByClientIdAndFileIdInTableData(klient_id, file_id);
                all_awaiting_clients.remove(klient_id);
                if (idx != -1)
                    data.remove(idx);
            } else {
                int idx = data.indexOf(new TableData(file_id, prior_file.file_size, max_prior, formatter.format(all_awaiting_clients.get(klient_id).entry_data), klient_id));
                all_awaiting_clients.get(klient_id).file_list.remove(file_id);
                if (idx != -1)
                    data.remove(idx);
            }
        else return null;

        return new Income_file_wrapper(real_id, file_id, prior_file, max_prior);
    }


    double count_prior(int file_size, long wait_time, long MAX_WAITING, int BIGGEST_FILE) {

        double size =  (double)(BIGGEST_FILE - file_size) / BIGGEST_FILE;
        double arrive = Math.exp(Math.pow(wait_time / (double)MAX_WAITING,2));
        double prior = size + arrive;
        return prior;
    }

}

class Income_file_wrapper {
    int client_id;
    int file_id;
    Income_file income_file;
    double prior_value;

    public Income_file_wrapper(int client_id, int file_id, Income_file income_file, double prior_value) {
        this.client_id = client_id;
        this.file_id = file_id;
        this.income_file = income_file;
        this.prior_value = prior_value;
    }
}