package sample;

import javafx.beans.property.*;

import java.util.ArrayList;

public class TableData {
    private IntegerProperty clientID;
    private StringProperty arriveData;
    private IntegerProperty clientFileAmount;
    private IntegerProperty clientFilesSize;
    private DoubleProperty clientPrior;


    public ArrayList<Integer> client_files;

    public Integer ilosc;

    public TableData(int clientID, String arriveData, int clientFileAmount, int clientFilesSize, double clientPrior, ArrayList<Integer> client_files) {
        this.clientFileAmount = new SimpleIntegerProperty(clientFileAmount);
        this.clientFilesSize = new SimpleIntegerProperty(clientFilesSize);
        this.clientPrior = new SimpleDoubleProperty(clientPrior);
        this.arriveData = new SimpleStringProperty(arriveData);
        this.clientID = new SimpleIntegerProperty(clientID);
        this.client_files = client_files;
        this.ilosc = clientFileAmount;
    }

    public int getClientFilesCurrentSize() {
        return client_files.stream().mapToInt(a -> a).sum();
    }

    public int getOneClientFile() {
        int i = 0;
        try {
            i = client_files.remove(0);
        } catch (IndexOutOfBoundsException e) {
        }
        return i;
    }

    public int getClientFileAmount() {
        return clientFileAmount.get();
    }

    public IntegerProperty clientFileAmountProperty() {
        return clientFileAmount;
    }

    public void setClientFileAmount(int clientFileAmount) {
        this.clientFileAmount.set(clientFileAmount);
    }

    public int getClientFilesSize() {
        return clientFilesSize.get();
    }

    public IntegerProperty clientFilesSizeProperty() {
        return clientFilesSize;
    }

    public void setClientFilesSize(int clientFilesSize) {
        this.clientFilesSize.set(clientFilesSize);
    }

    public double getClientPrior() {
        return clientPrior.get();
    }

    public DoubleProperty clientPriorProperty() {
        return clientPrior;
    }

    public void setClientPrior(double clientPrior) {
        this.clientPrior.set(clientPrior);
    }

    public String getArriveData() {
        return arriveData.get();
    }

    public StringProperty arriveDataProperty() {
        return arriveData;
    }

    public void setArriveData(String arriveData) {
        this.arriveData.set(arriveData);
    }

    public int getClientID() {
        return clientID.get();
    }

    public IntegerProperty clientIDProperty() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID.set(clientID);
    }
}
