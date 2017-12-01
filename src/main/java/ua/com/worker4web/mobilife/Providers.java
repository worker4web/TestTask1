package ua.com.worker4web.mobilife;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Created by worker4web on 11/30/2017.
 */
public class Providers extends Thread {

    private String filePath;

    Providers(String filePath) {
        this.filePath = filePath;
        start();
    }

    Providers() {
        throw new RuntimeException("Need file name");
    }

    public void run() {
        System.out.println(String.format("run thread=%s, with filename=%s", this.getName(), filePath));
        try {
            InputStream inputStream = new FileInputStream(filePath);
            TestTask1.createSHA(inputStream, this.getId());
            synchronized (this) {
                wait();
            }
        } catch (IOException ex) {
            System.out.println(ex.toString());
            TestTask1.closeThread(this.getId());
        }
        catch (InterruptedException ie) {
            System.out.println(ie.toString());
            TestTask1.closeThread(this.getId());
        }
    }

    public void saveSHA(byte[] code) {
        try {
            if(code!=null) {
                Files.write(Paths.get(filePath), code, StandardOpenOption.APPEND);
                System.out.println("Finish append file::" + filePath);
            }
            else
                System.out.println("Error get SHA");
        }catch (IOException e) {
            System.out.println("Error append SHA to file:"+ filePath);
        }
        TestTask1.closeThread(this.getId());
        synchronized (this) {
            notify();
        }
    }
}
