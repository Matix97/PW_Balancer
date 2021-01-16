package sample;

public class UploadedFile {
    int clientID;
    int fileSize;

    public UploadedFile(int clientID, int fileSize) {
        this.clientID = clientID;
        this.fileSize = fileSize;
    }

    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }
}
