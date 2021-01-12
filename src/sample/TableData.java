package sample;

import javafx.beans.property.*;

public class TableData {
    private IntegerProperty fileID;
    private IntegerProperty fileSize;
    private DoubleProperty priorFile;
    private StringProperty arriveData;
    private IntegerProperty clientID;

    public TableData(int fileID, int fileSize, double priorFile, String arriveData, int clientID) {
        this.fileID = new SimpleIntegerProperty(fileID);
        this.fileSize = new SimpleIntegerProperty(fileSize);
        this.priorFile = new SimpleDoubleProperty(priorFile);
        this.arriveData = new SimpleStringProperty(arriveData);
        this.clientID = new SimpleIntegerProperty(clientID);
    }
    public int getFileID() {
        return fileID.get();
    }

    public IntegerProperty fileIDProperty() {
        return fileID;
    }

    public void setFileID(int fileID) {
        this.fileID.set(fileID);
    }

    public int getFileSize() {
        return fileSize.get();
    }

    public IntegerProperty fileSizeProperty() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize.set(fileSize);
    }

    public double getPriorFile() {
        return priorFile.get();
    }

    public DoubleProperty priorFileProperty() {
        return priorFile;
    }

    public void setPriorFile(double priorFile) {
        this.priorFile.set(priorFile);
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
