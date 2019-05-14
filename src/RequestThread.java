import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.sql.SQLOutput;
import java.util.concurrent.ConcurrentHashMap;

public class RequestThread extends Thread {
    DatagramPacket request;
    String[] splitedLine;
    String result = new String();
    byte[] buffer = new byte[3000];
    public  RequestThread(String[] splitedLine, DatagramPacket request) {
        this.splitedLine = splitedLine;
        this.request = request;

    }
    public void run(){

        result = splitedLine[0];
        switch (result) {
            case ("insert"): {
//                try {
////                    //System.out.println(splitedLine[1]);
////                    Server.collection = Orders.insert(Server.collection, splitedLine[1], request);
////
////                } catch (Exception e) {
////                    String sout = new String("Не было введено достаточно данных");
////                    Orders.send(sout, request);
////
////                }
                try {
                    String[] secondSplit = splitedLine[1].split(" ", 3);
                    boolean suc = Server.db.auth(secondSplit[0],secondSplit[1]);
                    if (suc){
                        suc = Server.db.insert(secondSplit[0],secondSplit[1], secondSplit[2]);
                    }
                    if (suc){
                        Orders.send("Добавлен новый объект в базу данных", request);
                    }
                    else{
                        Orders.send("Не удалось добавить объект в базу данных", request);
                    }
                } catch (Exception e) {
                    String sout = new String("Не было введено достаточно данных");
                    Orders.send(sout, request);

                }
            }
            break;
            case ("save"):
            case ("exit"): {
                if (!(Server.collection.isEmpty())) {
                    File defaultFile = new File(Orders.lastPath);
                    if (Orders.lastPath.length() != 0 & defaultFile.canWrite()) {
                        Orders.exit(Server.collection, Orders.lastPath,request);
                    } else {
                        String Path = new File("").getAbsolutePath();
                        String newPath = Path.concat("/" + (int) (Math.random() * 10000) + ".json");
                        File checkFile = new File(newPath);
                        while (checkFile.isFile() & checkFile.canRead()) {
                            newPath = Path.concat("/" + (int) (Math.random() * 10000) + ".json");
                            checkFile = new File(newPath);
                        }
                        String sout = new String(newPath);
                        Orders.lastPath = sout;
                        Orders.send(sout, request);

                        Orders.exit(Server.collection, newPath, request);
                    }
                }
            }
            break;
            case ("info"): {
//                if (!(Server.collection.isEmpty())) Orders.info(Server.collection, request);
//                else{
//                    String sout = new String("Коллекция пуста");
//                    Orders.send(sout, request);
//                }
                try {
                    String[] secondSplit = splitedLine[1].split(" ", 3);
                    String show = new String("");
                    boolean suc = Server.db.auth(secondSplit[0],secondSplit[1]);
                    if (suc){
                        Orders.send("База данных содержит в себе " + Server.db.info() + " объектов", request);
                    }
                    else{
                        Orders.send("Не верный логин или пароль", request);
                    }
                } catch (Exception e) {
                    String sout = new String("Не было введено достаточно данных");
                    Orders.send(sout, request);

                }
            }
            break;
            case ("remove_greater_key"): {
//                if (!(Server.collection.isEmpty())) Orders.removeGreaterCollection(Server.collection, splitedLine[1], request);
//                else{
//                    String sout = new String("Коллекция пуста");
//                    Orders.send(sout, request);
//                }
                try {
                    String[] secondSplit = splitedLine[1].split(" ", 3);
                    boolean suc = Server.db.auth(secondSplit[0],secondSplit[1]);
                    if (suc){
                        suc = Server.db.removeGreater(secondSplit[0],secondSplit[1], secondSplit[2]);
                    }
                    if (suc){
                        Orders.send("Объекты удалены", request);
                    }
                    else{
                        Orders.send("Нет подходящих объектов", request);
                    }
                } catch (Exception e) {
                    String sout = new String("Не было введено достаточно данных");
                    Orders.send(sout, request);

                }
            }
            break;
            case ("show"): {
//                if (!(Server.collection.isEmpty())) Orders.show(Server.collection, request);
//                else{
//                    String sout = new String("Коллекция пуста");
//                    Orders.send(sout, request);
//                }
                try {
                    String[] secondSplit = splitedLine[1].split(" ", 3);
                    String show = new String("");
                    boolean suc = Server.db.auth(secondSplit[0],secondSplit[1]);
                    if (suc){
                        show = Server.db.show();
                    }
                    if (!show.isEmpty()){
                        Orders.send(show, request);
                    }
                    else{
                        Orders.send("Не удалось извлечь данные", request);
                    }
                } catch (Exception e) {
                    String sout = new String("Не было введено достаточно данных");
                    Orders.send(sout, request);

                }

            }
            break;
            case ("load"): {
                if (Orders.lastPath.length() != 0) {
                    Server.collection = Orders.load(Orders.lastPath);
                }
                if (Orders.lastImportedCollection.length() != 0)
                {

                }
                    else {
                    String sout = new String("Нельзя перезагрузить коллекцию, так как изначально не был указан путь.");
                    Orders.send(sout, request);
                }
                break;
            }
            case ("import"): {
                //Server.collection = Orders.importCollection(Server.collection, splitedLine[1], request);
                try {
                    String[] secondSplit = splitedLine[1].split(" ", 3);
                    boolean suc = Server.db.auth(secondSplit[0],secondSplit[1]);
                    ConcurrentHashMap<String, Human> collection;
                    Type type = new TypeToken<ConcurrentHashMap<String, Human>>(){}.getType();
                    Gson gson = new Gson();
                    if (suc){
                        System.out.println(secondSplit[2]);
                        collection = gson.fromJson(secondSplit[2], type);
                        for(ConcurrentHashMap.Entry<String, Human> item : collection.entrySet()){
                            Object h = item.getValue();
                            Human human = (Human) h;
                            Server.db.insert(secondSplit[0],item.getKey(),human);
                        }
                        Orders.send("Объекты были добавлены в базу данных", request);
                    }
                    else{
                        Orders.send("Неверный логин или пароль", request);
                    }
                }
                catch(JsonSyntaxException mes){
                    Orders.send("Неверный формат данных", request);
                    //mes.printStackTrace();
                    System.out.println(mes.getMessage());
                }catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                    String sout = new String("Не было введено достаточно данных");
                    Orders.send(sout, request);
                }
            }
            break;

            case ("remove"): {
//                try {
//
//                    if (!(Server.collection.isEmpty()))
//                        Orders.removeFromCollection(Server.collection, splitedLine[1], request);
//                    else {
//                        String sout = new String("Коллекция пуста");
//                        Orders.send(sout, request);
//                    }
//                }
//                catch (ArrayIndexOutOfBoundsException e){
//                    String sout = new String("Не указан элемент");
//                    Orders.send(sout, request);
//                }

                try {
                    String[] secondSplit = splitedLine[1].split(" ", 3);
                    boolean suc = Server.db.auth(secondSplit[0],secondSplit[1]);
                    if (suc){
                        suc = Server.db.remove(secondSplit[0],secondSplit[1], secondSplit[2]);
                    }
                    if (suc){
                        Orders.send("Удален объект из базы данных", request);
                    }
                    else{
                        Orders.send("Не удалось удалить объект из базы данных", request);
                    }
                } catch (Exception e) {
                    String sout = new String("Не было введено достаточно данных");
                    Orders.send(sout, request);

                }

            }
            break;

            case ("reg"):{
                try {
                    String generatedPassword = PasswordGenerator.generate(10);
                    String hex = PasswordGenerator.getCryptedPassword(generatedPassword);
                    boolean suc = Server.db.registration(splitedLine[1],hex);
                    if (suc) {
                        Mail.send(splitedLine[1], generatedPassword);
                        Orders.send("На указанную почту отправлен пароль", request);
                    }
                    else {
                        Orders.send("Не удалось добавить пользователя", request);
                    }

                }
                catch (Exception e){
                    System.out.println("Не было введено достаточно данных");
                }
            }
            break;
            case ("auth"):{
                splitedLine  = splitedLine[1].split(" ");
                boolean suc = Server.db.auth(splitedLine[0],splitedLine[1]);
                if (suc) Orders.send("Успешная авторизация", request);
                else Orders.send("Не удалось авторизоваться", request);
            }
            break;
            default: String sout = new String("Такой команды не существует");
                Orders.send(sout, request);

        }
    }
}
