package sample;

import java.util.ArrayList;

public class Income_client {
    public ArrayList<Income_file> file_list;
    public int client_id;
    public long entry_data;

    public Income_client(long entry_time, ArrayList<Income_file> file_list, int client_id) {
        this.file_list = file_list;
        this.client_id = client_id;
        this.entry_data=entry_time;
    }


}
