//package sample;
//
//import java.util.*;
//
//public class MainBrain {
//
//    int CLIENT_AMOUNT = 5;
//    //minimalny, maksymalny rozmiar pliku
//    int min_size = 1, max_size = 100;//10000;
//    //ilosc plikow jednego klienta
//    int min_file = 1, max_file = 5;
//    Random random = new Random();
//    //ArrayList<Income_client> all_awaiting_clients = new ArrayList<>();
//    List<Income_client> all_awaiting_clients = Collections.synchronizedList(new ArrayList<>());
//    int client_id = 0;
//    ArrayList<OneDisc> discs = new ArrayList<>();
//
//    public MainBrain() {
//        start();
//    }
//
//    void start() {
//        createDiscs();
//        //   synchronized(all_awaiting_clients){
//        //   System.out.println("pupa -1");
//        generateClients();
//
//        // System.out.println("pupa -2");
//        try {
//            manageDiscs();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        //   System.out.println("pupa -3");
//        // }
//        System.out.println("THE END OF THE FUCKING WORLD");
//    }
//
//    private void manageDiscs() throws InterruptedException {
//
//        while (true) {
//            //Thread.sleep(500);
//            for (OneDisc d : discs) {
//                // System.out.println("PUPA n: "+d.unused+all_awaiting_clients.size());
//                if (d.unused && all_awaiting_clients.size()!=0) {
//                    //     System.out.println("Pupa 1");
//                    Income_file_wrapper i = get_current_bigest_prior();
//                    // System.out.println(i.toString());
//                    //    if(i!=null){
//                    d.file_size = i.income_file.file_size;
//                    String thrName=  "Klient: "+i.client_id + "\tFile ID: "+i.file_id + "\tFile SIZE: "+i.income_file.file_size;
//                    Thread thread = new Thread(d,thrName);
//                    System.out.println("******************************************");
//                    System.out.println(thrName+ "\tDISC: "+d.disk_number);
//                    System.out.println("\n");
//                    printAllClientsAndFiles();
//                    System.out.println("\n");
//                    thread.start();
//
//                }
//            }
//            if(client_id ==(CLIENT_AMOUNT) &&all_awaiting_clients.size()==0)
//            {
//                break;
//            }
//        }
//    }
//    private void printAllClientsAndFiles(){
//        for (int i =0;i<all_awaiting_clients.size();i++){//Income_client a : all_awaiting_clients
//            for (int j=0;j<all_awaiting_clients.get(i).file_list.size();j++){//Income_file f : a.file_list
//                System.out.println("Klient ID: "+all_awaiting_clients.get(i).client_id+" File id:"+j+"\tSIZE: "+all_awaiting_clients.get(i).file_list.get(j).file_size);
//            }
//        }
//    }
//
//    private void createDiscs() {
//        for (int i = 0; i < 5; i++)
//            discs.add(new OneDisc(i, true, disc1));
//    }
//
//    private void generateClients() {
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                //klient może mieć maks 10 plików
////                int files_amount = random.nextInt(max_file - min_file) + min_file;
////                ArrayList<Income_file> files = new ArrayList<>();
////                for (int i = 0; i < files_amount; i++)
////                    files.add(new Income_file(random.nextInt(max_size - min_size) + min_size));//(max_size - min_size) + min_size
////
////                all_awaiting_clients.add(new Income_client(System.currentTimeMillis(), files, client_id));
////                System.out.println("Pupa add"+client_id+" FILES: "+files_amount);
////                client_id++;
//                if (client_id == CLIENT_AMOUNT)
//                    timer.cancel();
//            }
//        }, 0, 1000);//co 1 seuknd nowe kklient
//    }
//
//
//    /**
//     * Priorytety liczymy dla klinetów a nie plików
//     **/
//    Income_file_wrapper get_current_bigest_prior() {
//        long current_time = System.currentTimeMillis();
//
//        long longest_wait = all_awaiting_clients.get(0).entry_data;
//        for (Income_client a : all_awaiting_clients)
//            if (longest_wait < a.entry_data)
//                longest_wait = a.entry_data;
//
//        int biggest_file = all_awaiting_clients.get(0).file_list.get(0).file_size;
//        for (Income_client a : all_awaiting_clients)
//            for (Income_file f : a.file_list)
//                if (biggest_file < f.file_size)
//                    biggest_file = f.file_size;
//        double max_prior =0.0;
//        Income_file prior_file = all_awaiting_clients.get(0).file_list.get(0);
//        int klient_id=0;
//        int file_id =0;
//        int real_id = all_awaiting_clients.get(0).client_id;
//
//        for (int i =0;i<all_awaiting_clients.size();i++){//Income_client a : all_awaiting_clients
//            for (int j=0;j<all_awaiting_clients.get(i).file_list.size();j++){//Income_file f : a.file_list
//                double temp = count_prior(all_awaiting_clients.get(i).file_list.get(j).file_size, current_time - all_awaiting_clients.get(i).entry_data, longest_wait, biggest_file);
//                if (temp>max_prior){
//                    max_prior=temp;
//                    prior_file = all_awaiting_clients.get(i).file_list.get(j);
//                    klient_id = i;
//                    file_id = j;
//                    real_id =  all_awaiting_clients.get(i).client_id;
//                }
//            }
//        }
//
//        if(all_awaiting_clients.get(klient_id).file_list.size()==1)
//            all_awaiting_clients.remove(klient_id);
//        else
//            all_awaiting_clients.get(klient_id).file_list.remove(file_id);
//        return new Income_file_wrapper(real_id,file_id,prior_file,max_prior);
//
//
//
//    }
//
//
//    //file_size in MB
//    double count_prior(int file_size, long wait_time, long MAX_WAITING, int BIGGEST_FILE) {
//
//        //przedział 0-1
//        double size = (BIGGEST_FILE-file_size) / BIGGEST_FILE;
//        //przedział 0-1
//        double arrive = wait_time / MAX_WAITING;
//
//        double prior = size + arrive;
//        return prior;
//    }
//}
////class Income_file_wrapper {
////    int client_id;
////    int file_id;
////    Income_file income_file;
////
////    public Income_file_wrapper(int klient_id, int file_id, Income_file income_file) {
////        this.client_id = klient_id;
////        this.file_id = file_id;
////        this.income_file = income_file;
////    }
////
////    @Override
////    public String toString() {
////        return "Income_file_wrapper{" +
////                "klient_id=" + client_id +
////                ", file_id=" + file_id +
////                ", income_file=" + income_file +
////                '}';
////    }
////}
