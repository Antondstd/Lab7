import com.google.gson.Gson;

import java.io.*;
import java.net.DatagramPacket;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonSyntaxException;

public class Orders {
    public static String lastPath = new String("");
    public static String lastImportedCollection = new String("");
    public static Date date = new Date();
    public static Type type = new TypeToken<ConcurrentHashMap<String, Human>>(){}.getType();
    static Gson gson = new Gson();
    static byte[] buffer = new byte[3000];
    static String  sout = new String("");

    /****
     * Перебирает коллекцию и показвает информацию о каждом элементе
     * @param collection
     */
    public static void show (ConcurrentHashMap<String, Human> collection, DatagramPacket request){

        collection.entrySet().stream().forEach(o -> sout = sout.concat("Key: " + o.getKey() + " - " + o.getValue().toString() + "\n"));
        /*for(ConcurrentHashMap.Entry<String, Human> item : collection.entrySet()){
            Object h = item.getValue();
            sout = sout.concat("Key: " + item.getKey() + " - " + h.toString() + "\n");
        }*/
        send(sout, request);
    }

    /**
     * Добавляет элемент в коллекцию
     * @param collection
     * @param order Значения элемента в json
     * @return collection Возвращает коллекцию с добавленным элементом
     */
    public static synchronized ConcurrentHashMap<String, Human> insert (ConcurrentHashMap<String, Human> collection, String order, DatagramPacket request){

        String[] splitedLine = order.split("[}]\\s*", 2);
        splitedLine[0] = splitedLine[0].concat("}");
        System.out.println(splitedLine[0] + " and " + splitedLine[1]);
        try {
            splitedLine[0] = gson.fromJson(splitedLine[0],Key.class).toString();
            if (splitedLine[0] == null){
                throw new JsonSyntaxException("");
            }
            Human h = gson.fromJson(splitedLine[1], Human.class);
            h.setDate();
            collection.put(splitedLine[0], h);
            sout = "В коллекцию успешно добавлен новый элемент.";
        }
        catch(JsonSyntaxException mes){
            sout = "Неверный формат данных";
        }

        send(sout, request);
        return collection;
    }

    /********
     * Выходит из программы и сохраняет коллекцию в файл
     * @param collection
     * @param path
     */
    public static void exit(ConcurrentHashMap<String, Human> collection, String path, DatagramPacket request){

        try {
                String JSON = gson.toJson(collection);
                FileWriter writer = new FileWriter(path);
                writer.write(JSON);
                writer.close();
                sout = "Файл успешно сохранен";
            }
            catch(IOException ex){

                sout =ex.getMessage();
            }
        catch (SecurityException mes) {
            sout = mes.getMessage();
        }
        catch(JsonSyntaxException mes){
            sout = "Неверный формат данных";
        }
        send(sout,request);
    }

    /***
     * Выводит информацию о коллекции
     * @param collection
     */
    public static void info(ConcurrentHashMap<String, Human> collection, DatagramPacket request){

        String sout = new String("Коллекция имеет тип ConcurrentHashMap и содержит в себе объекты типа Human \n" +
                "Инициализирована " + date + "\n" +
                "Содержит в себе " + collection.size() + " элементов");
        send(sout, request);
    }


    /***
     *
     * @param path
     * @return перезагруженную коллекцию
     */
    public static synchronized ConcurrentHashMap<String, Human> load(String path) {
        ConcurrentHashMap<String, Human> collection = new ConcurrentHashMap<>();

            try {
                File importedFile = new File(path);
                if (!(importedFile.isFile())) throw new FileNotFoundException("Путь не указывает на файл.");
                if (!(importedFile.exists()))
                    throw new FileNotFoundException("Файла по указанному пути не существует.");
                if (!(importedFile.canRead())) throw new SecurityException("Данный файл не может быть прочитан.");
                Scanner importedFileText = new Scanner(importedFile);
                String importedCollectionText = new String();
                while (importedFileText.hasNextLine()) {
                    importedCollectionText = importedCollectionText.concat(importedFileText.nextLine());
                }
                importedFileText.close();
                collection = gson.fromJson(importedCollectionText, type);
            } catch (FileNotFoundException mes) {
                System.out.println(mes);
            } catch (SecurityException mes) {
                System.out.println(mes);
            }
            catch(JsonSyntaxException mes){
                System.out.println("Неверный формат данных");
            }
            return collection;

    }

    /*****
     *
     * @param collection
     * @return Возвращает коллекцию, импортированную из файла
     */
    public static synchronized ConcurrentHashMap<String, Human> importCollection(ConcurrentHashMap<String, Human> collection, String importedCollectionText, DatagramPacket request) {
        try {
            if (!importedCollectionText.isEmpty()) {
                collection = gson.fromJson(importedCollectionText, type);
                sout = "Коллекция успешно импортирована";
                lastImportedCollection = importedCollectionText;
            }
            else{
                sout = "Не удалось добавить в коллекцию";
            }
        }
        catch(JsonSyntaxException mes){
            sout = "Неверный формат данных";
        }
        send(sout, request);
        return collection;
    }

    public static ConcurrentHashMap<String, Human> importCollection(ConcurrentHashMap<String, Human> collection, String path) {

        try {
            File importedFile = new File(path);
            if (!(importedFile.canRead())) throw new SecurityException("Данный файл не может быть прочитан.");
            if (!(importedFile.exists())) throw new FileNotFoundException("Файла по указанному пути не существует.");
            if (!(importedFile.canRead())) throw new SecurityException("Данный файл не может быть прочитан.");
            Scanner importedFileText = new Scanner(System.in);
            importedFileText = new Scanner(importedFile);
            String importedCollectionText = new String();
            while (importedFileText.hasNextLine()) {
                importedCollectionText = importedCollectionText.concat(importedFileText.nextLine());
            }
            importedFileText.close();
            if (!importedCollectionText.isEmpty()) {
                collection = gson.fromJson(importedCollectionText, type);
                System.out.println("Коллекция успешно импортирована");
                lastPath = path;
            }
            else {
                System.out.println("Не удалось добавить в коллекцию");
            }
        } catch (FileNotFoundException mes) {
            System.out.println(mes);
        } catch (SecurityException mes) {
            System.out.println(mes);
        }
        catch(JsonSyntaxException mes){
            System.out.println("Неверный формат данных");
        }
        return collection;
    }

    /****
     * Удаляет элемент из коллекциюю по ключу
     * @param collection
     * @param value
     * @return Коллекцию, без удаленного элемента
     */
    public static synchronized ConcurrentHashMap<String, Human> removeFromCollection(ConcurrentHashMap<String, Human> collection, String value, DatagramPacket request){
        try {
            final String valueFinal = gson.fromJson(value, Key.class).toString();
            if (collection.containsKey(valueFinal)) {
                collection.remove(valueFinal);
                sout = "Элемент успешно удален";
            } else {
                sout = "Нет элемента с таким ключом";
            }
        }
        catch(JsonSyntaxException mes){
            sout = "Неверный формат данных";
        }
        send(sout, request);
        return collection;
    }

    /*****
     * Удаляет все элементы, у которых ключ превышает параметр value
     * @param collection
     * @param value
     * @return collection
     */
    public static synchronized ConcurrentHashMap<String, Human> removeGreaterCollection(ConcurrentHashMap<String, Human> collection, String value, DatagramPacket request){
        try {
            final String valueFinal = gson.fromJson(value, Key.class).toString();
            collection.entrySet().removeIf(o -> valueFinal.compareTo(o.getKey()) < 0);
            sout = "Элементы успешно удалены";
        }
        catch(JsonSyntaxException mes){
            sout = "Неверный формат данных";
        }
        send(sout, request);
        return collection;
    }

    public static void send(String mes, DatagramPacket request){
        buffer = mes.getBytes();
        DatagramPacket response = new DatagramPacket(buffer, buffer.length, request.getAddress(), request.getPort());
        try{
            Server.socket.send(response);
        }
        catch (IOException o){
            System.out.println(o);
        }
        sout = "";

    }

}
