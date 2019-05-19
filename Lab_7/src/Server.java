import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Thread.sleep;

public class Server {
    public static final int PORT = 6879;
    public static byte[] buffer = new byte[3000];
    public static ConcurrentHashMap<String, Human> collection = new ConcurrentHashMap<>();
    public static DatagramSocket socket;
    public static DBInteraction db;

    public static void main(String[] args) {
        String result = new String();
        String[] splitedLine;
        Gson gson = new Gson();


        if (args.length != 0) {
            collection = Orders.importCollection(collection, args[0]);
        } else {
            System.out.println("Путь до файла не был указан");
        }
        try {
            String quote = new String();
            socket = new DatagramSocket(PORT);
            db = new DBInteraction();

            while(true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);
                quote = new String(buffer, 0, request.getLength());

                splitedLine = quote.split(" ", 2);

                result = splitedLine[0];
                System.out.println(result);
                Thread RequestThread = new RequestThread(splitedLine, request);
                RequestThread.start();
            }

            }catch(Exception o){
                System.out.println(o);
            }
        }

}
