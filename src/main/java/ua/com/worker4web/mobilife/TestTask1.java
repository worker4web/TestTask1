package ua.com.worker4web.mobilife;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by worker4web on 11/30/2017.
 */
public class TestTask1 {
    private static Worker worker;
    private static Map<Long, Providers> providersMap;

    public static void main(String[] args) {
        if(args.length>0) {
            worker = Worker.getInstance();
            worker.start();

            providersMap = new HashMap<>();
            for (String arg : args) {
                Providers provider = new Providers(arg);
                providersMap.put(provider.getId(), provider);
            }
        }
        else
            System.out.println("Need at least one file!");
    }

    public static void createSHA(InputStream inputStream, long threadId) {
        worker.addInputStream(inputStream, threadId);
    }

    public static void setSHA(byte[] sha256, long threadId) {
        Providers provider = providersMap.get(threadId);
            if(provider != null)
                provider.saveSHA(sha256);
    }

    public static void closeThread(long id) {
        providersMap.remove(id);
        if(providersMap.size()==0) {
            providersMap = null;
            Worker.getInstance().stoped=true;
        }
    }

}
