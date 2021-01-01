package sample;

public class OneDisc implements Runnable {

    int disk_number ;
    boolean unused;
    int file_size;

    public OneDisc(int disk_number, boolean unused) {
        this.disk_number = disk_number;
        this.unused = unused;
    }

    @Override
    public void run() {
        unused =false;
        //System.out.println("DISC: "+disk_number+";\tfile_size: "+file_size);
        try {
            Thread.sleep((long)100*file_size);//10mb idzie jedną sekundę
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        unused=true;
    }
}
