import com.google.gson.JsonSyntaxException;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import com.google.gson.Gson;
import java.util.Scanner;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


public class Client {
    public static void main(String[] args){
        String email = new String();
        String password = new String();
        try {
            InetAddress address = InetAddress.getByName("localhost");
            DatagramSocket socket = new DatagramSocket();

            byte[] buffer = new byte[3000];
            byte[] bufferResponce = new byte[3000];
            int port = 6879;
            Scanner scanner = new Scanner(System.in);
            String line = new String();
            do{
                line = scanner.nextLine();
                /*if (line.split(" ")[0].equals("import")){
                    try {
                        String path = new String(line.split(" ")[1]);
                        File importedFile = new File(path);
                        if (!(importedFile.canRead())) throw new SecurityException("Данный файл не может быть прочитан.");
                        if (!(importedFile.exists())) throw new FileNotFoundException("Файла по указанному пути не существует.");
                        if (!(importedFile.canRead())) throw new SecurityException("Данный файл не может быть прочитан.");
                        //date = new Date(importedFile.lastModified());
                        Scanner importedFileText = new Scanner(System.in);
                        importedFileText = new Scanner(importedFile);
                        String importedCollectionText = new String();
                        while (importedFileText.hasNextLine()) {
                            importedCollectionText = importedCollectionText.concat(importedFileText.nextLine());
                        }
                        importedFileText.close();
                        line = "import " + email + " " + password + " " + importedCollectionText;
                    } catch (FileNotFoundException mes) {
                        System.out.println(mes);
                    } catch (SecurityException mes) {
                        System.out.println(mes);
                    }
                    catch(JsonSyntaxException mes){
                        System.out.println("Неверный формат данных");
                    }
                }
                if (line.split(" ")[0].equals("auth")){
                    email = line.split(" ")[1];
                    password = line.split(" ")[2];
                }*/
                switch (line.split(" ")[0]){
                    case ("import"):{
                        try {
                            String path = new String(line.split(" ")[1]);
                            System.out.println(path);
                            File importedFile = new File(path);
                            if (!(importedFile.canRead())) throw new SecurityException("Данный файл не может быть прочитан.");
                            if (!(importedFile.exists())) throw new FileNotFoundException("Файла по указанному пути не существует.");
                            if (!(importedFile.canRead())) throw new SecurityException("Данный файл не может быть прочитан.");
                            //date = new Date(importedFile.lastModified());
                            Scanner importedFileText = new Scanner(importedFile);
                            String importedCollectionText = new String();
                            while (importedFileText.hasNextLine()) {
                                importedCollectionText = importedCollectionText.concat(importedFileText.nextLine());
                            }
                            importedFileText.close();
                            line = "import " + email + " " + password + " " + importedCollectionText;
                        } catch (FileNotFoundException mes) {
                            System.out.println(mes);
                        } catch (SecurityException mes) {
                            System.out.println(mes);
                        }
                        catch(JsonSyntaxException mes){
                            System.out.println("Неверный формат данных");
                        }
                    }
                    break;
                    case ("auth"):
                        try {
                            email = line.split(" ")[1];
                            password = line.split(" ")[2];
                        }
                        catch (Exception e){
                            System.out.println("Введено недостаточно данных");
                        }
                        break;
                    case("insert"):
                    case("remove_greater_key"):
                    case ("remove"):{
                        try {
                            line = line.split(" ")[0] + " " + email + " " + password + " " + line.split(" ", 2)[1];
                        }
                        catch (Exception e){
                            System.out.println("Введено недостаточно данных");
                        }
                    }
                    break;
                    case("info"):
                    case("show"):{
                        line = line.split(" ")[0] + " " + email + " " + password;
                    }
                }

                buffer = line.getBytes();
                DatagramPacket sending = new DatagramPacket(buffer,buffer.length,address,port);
                DatagramPacket response = new DatagramPacket(bufferResponce, bufferResponce.length);
                socket.send(sending);
                socket.setSoTimeout(3000);

                    try {
                        socket.receive(response);
                        String quote = new String(bufferResponce, 0, response.getLength());
                        System.out.println(quote);
                    }
                    catch (SocketTimeoutException e) {
                        // timeout exception.
                        System.out.println("Ответ от сервера не получен");
                    }


            }while(!line.equalsIgnoreCase("exit"));
        }catch (Exception o){

        }
    }
}
