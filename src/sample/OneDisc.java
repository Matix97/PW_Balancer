package sample;

import javafx.application.Platform;
import javafx.scene.control.Label;

import java.util.Random;

public class OneDisc implements Runnable {

    int disk_number;
    int file_size;
    int file_id=0, client_id=0;
    Label disc;

    public OneDisc(int disk_number, boolean unused, Label disc1) {
        this.disk_number = disk_number;
        this.disc = disc1;
    }

    @Override
    public void run() {
        while(true){
            try {
                if(!Controller.discs_flag[disk_number]){
                    Platform.runLater(() -> disc.setText("C_id: "+client_id+" F_id: "+file_id));
                    Thread.sleep((long) 100 * file_size);//10mb goes 1 second
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> disc.setText("Available" ));
            Controller.discs_flag[disk_number] = true;
            file_size = 0;
        }

    }
}
