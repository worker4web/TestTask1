package ua.com.worker4web.mobilife;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Created by worker4web on 11/30/2017.
 */
public class Worker extends Thread {

    private static final Worker instance = new Worker();

    public static boolean stoped = false;

    private ConcurrentMap<Long, InputStream> inputStreamArray;

    protected Worker(){
        inputStreamArray = new ConcurrentHashMap<>();
    }

    public static Worker getInstance() {return instance;}

    public void run() {
        try {
            System.out.println("worker run");

            while (!stoped) {
                if (!inputStreamArray.isEmpty()) {
                    Iterator<Long> iterator = inputStreamArray.keySet().iterator();
                    InputStream inputStream;
                    Long key;
                    if(iterator.hasNext()) {
                        key = iterator.next();
                        inputStream = inputStreamArray.remove(key);
                    }
                    else
                        break;

                    try {
                    String result = new BufferedReader(new InputStreamReader(inputStream))
                            .lines().collect(Collectors.joining("\n"));
                    inputStream.close();

                        MessageDigest md = MessageDigest.getInstance("SHA-256");
                        byte[] hash = md.digest(result.getBytes(StandardCharsets.UTF_8));
                        TestTask1.setSHA(hash, key);
                    }catch (NoSuchAlgorithmException ex) {
                        TestTask1.setSHA(null, this.getId());
                    }
                    catch (IOException ex) {
                        System.out.println("Error read file");
                        TestTask1.setSHA(null, this.getId());
                    }
                } else
                    sleep(100);
            }
            System.out.println("worker finish");
        } catch (InterruptedException ie) {
            System.out.println(ie.toString());
        }
    }

    synchronized public void addInputStream(InputStream inputStream, long threadId) {
        inputStreamArray.put(threadId, inputStream);
    }


}
