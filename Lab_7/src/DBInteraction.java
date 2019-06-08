import java.sql.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public class DBInteraction {
    public static String createUserBd = new String("Create TABLE IF NOT EXISTS users(id SERIAL PRIMARY KEY,login TEXT NOT NULL UNIQUE, password TEXT NOT NULL)");
    public static String createObjectsBd = new String("Create TABLE IF NOT EXISTS objects(id SERIAL PRIMARY KEY," +
            "login TEXT NOT NULL," +
            "key TEXT NOT NULL," +
            "name TEXT NOT NULL," +
            "gender TEXT NOT NULL," +
            "boots TEXT NOT NULL," +
            "flyingType BOOLEAN NOT NULL," +
            "state TEXT NOT NULL," +
            "standsOn TEXT NOT NULL," +
            "x int4," +
            "y int4," +
            "appearedDate int4)");
//    static final String DB_URL = "jdbc:postgresql://localhost:5432/lab7";
//    static final String USER = "postgres";
//    static final String PASS = "1234567a";
    private static  Connection con;
    private static  Statement stmt;
    private static  DatabaseMetaData dbm;
    static Gson gson = new Gson();
//    static final String DB_URL = "jdbc:postgresql://uriy.yuran.us/anton";
//    static final String USER = "anton";
//    static final String PASS = "aCpBSZpf";
    static final String DB_URL = "jdbc:postgresql://pg/studs";
    static final String USER = "*****";
    static final String PASS = "*****";

    public DBInteraction() {

        System.out.println("Testing connection to PostgreSQL JDBC");

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver is not found. Include it in your library path ");
            e.printStackTrace();
            return;
        }

        System.out.println("PostgreSQL JDBC Driver successfully connected");
        con = null;

        try {
            con = DriverManager
                    .getConnection(DB_URL, USER, PASS);

        } catch (SQLException e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
            return;
        }

        if (con != null) {
            System.out.println("You successfully connected to database now");
        } else {
            System.out.println("Failed to make connection to database");
        }
        stmt = null;
        try {
            dbm = con.getMetaData();
            stmt = con.createStatement();
            stmt.execute(createUserBd);
            stmt.execute(createObjectsBd);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean registration(String email, String password){
        boolean succsess = false;
        try {
            PreparedStatement statement = con.prepareStatement("INSERT INTO users (login, password) VALUES (?, ?)");
            statement.setString(1, email);
            statement.setString(2, password);
            statement.execute();
            succsess = true;
            //stmt.executeUpdate("insert into USERS values('" + email + "','" + password + "')");
        }
        catch (SQLException e){
            System.out.println("Не удалось добавить пользователя");
        }
        return succsess;
    }
    public boolean auth(String email, String password){
        boolean succsess = false;
        password = PasswordGenerator.getCryptedPassword(password);
        try {
            PreparedStatement statement = con.prepareStatement("SELECT id FROM users WHERE login = ? and password = ?");
            statement.setString(1, email);
            statement.setString(2, password);
            ResultSet result = statement.executeQuery();
            succsess = result.next();
        }
        catch (SQLException e){
            System.out.println("Не удалось извлечь информацию");
        }
        return succsess;
    }

    public boolean insert(String email, String password, String stHuman){
        boolean succsess = false;
        String[] splitHuman = stHuman.split("[}]\\s*", 2);
        splitHuman[0]= splitHuman[0].concat("}");
        try {
            splitHuman[0] = gson.fromJson(splitHuman[0],Key.class).toString();
            if (splitHuman[0] == null){
                throw new JsonSyntaxException("");
            }
            Human human = gson.fromJson(splitHuman[1], Human.class);
            human.setDate();

            PreparedStatement statement = con.prepareStatement("INSERT INTO objects (login, key, name, gender, boots, flyingType, state, standsOn, x, y, appearedDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, email);
            statement.setString(2, splitHuman[0]);
            statement.setString(3, human.getName());
            statement.setString(4, human.getGender().toString());
            statement.setString(5, human.getShoes());
            statement.setBoolean(6, human.getFlyingType());
            statement.setString(7, human.getState());
            statement.setString(8, human.getStandOn());
            statement.setInt(9, human.getX());
            statement.setInt(10, human.getY());
            statement.setInt(11, (int)human.getAppeared());
            statement.execute();

            succsess = true;
        }
        catch (SQLException e){
            System.out.println("Не удалось извлечь информацию");
        }
        catch(JsonSyntaxException mes){
            System.out.println("Неверный формат данных");
        }
        catch (NoGenderException e){
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return succsess;
    }
    public boolean insert(String email ,String key, Human human){
        boolean succsess = false;
        try {
            human.setDate();

            PreparedStatement statement = con.prepareStatement("INSERT INTO objects (login, key, name, gender, boots, flyingType, state, standsOn, x, y, appearedDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, email);
            statement.setString(2, key);
            statement.setString(3, human.getName());
            statement.setString(4, human.getGender().toString());
            statement.setString(5, human.getShoes());
            statement.setBoolean(6, human.getFlyingType());
            statement.setString(7, human.getState());
            statement.setString(8, human.getStandOn());
            statement.setInt(9, human.getX());
            statement.setInt(10, human.getY());
            statement.setInt(11, (int)human.getAppeared());
            statement.execute();

            succsess = true;
        }
        catch (SQLException e){
            System.out.println("Не удалось извлечь информацию");
        }
        catch(JsonSyntaxException mes){
            System.out.println("Неверный формат данных");
        }
        catch (NoGenderException e){
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return succsess;
    }

    public String show() {
        String showString = "";
        String key = "";

        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM objects;");
            ResultSet result = statement.executeQuery();

            Human human = new Human();
            while (result.next()) {
                key = result.getString("key");
                human.setName(result.getString("name"));
                human.setX(result.getInt("x"));
                human.setY(result.getInt("y"));
                human.setGender(result.getString("gender"));
                human.setState(result.getString("state"));
                human.setFlyingType(result.getBoolean("flyingtype"));
                human.setStandOn(result.getString("standson"));
                human.appeared = OffsetDateTime.ofInstant(Instant.ofEpochSecond(result.getInt("appeareddate")), ZoneId.systemDefault());

                showString += "Ключ: " + key + " ---- " + human.toString() + "\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return showString;
    }

    public int info() {
        int count = 0;
        try {
            PreparedStatement statement = con.prepareStatement("SELECT COUNT(*) FROM objects;");
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                count = result.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }
    public boolean remove(String email, String password, String stHuman){
        boolean succsess = false;
        String[] splitHuman = stHuman.split("[}]\\s*", 2);
        splitHuman[0]= splitHuman[0].concat("}");
        try {
            splitHuman[0] = gson.fromJson(splitHuman[0],Key.class).toString();
            if (splitHuman[0] == null){
                throw new JsonSyntaxException("");
            }
            Human human = gson.fromJson(splitHuman[1], Human.class);
            human.setDate();
            System.out.println("gG");
            PreparedStatement statement = con.prepareStatement("DELETE FROM objects WHERE (login, key, name, gender, boots, flyingType, state, standsOn, x, y) = (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            statement.setString(1, email);
            statement.setString(2, splitHuman[0]);
            statement.setString(3, human.getName());
            statement.setString(4, human.getGender().toString());
            statement.setString(5, human.getShoes());
            statement.setBoolean(6, human.getFlyingType());
            statement.setString(7, human.getState());
            statement.setString(8, human.getStandOn());
            statement.setInt(9, human.getX());
            statement.setInt(10, human.getY());
            statement.execute();

            succsess = true;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        catch(JsonSyntaxException mes){
            System.out.println("Неверный формат данных");
        }
        catch (NoGenderException e){
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return succsess;
    }
    public boolean removeGreater(String email, String password, String stHuman){
        boolean succsess = false;
        String[] splitHuman = stHuman.split("[}]\\s*", 2);
        splitHuman[0]= splitHuman[0].concat("}");
        try {
            splitHuman[0] = gson.fromJson(splitHuman[0],Key.class).toString();
            if (splitHuman[0] == null){
                throw new JsonSyntaxException("");
            }
            String key = splitHuman[0];
            Human human = gson.fromJson(splitHuman[1], Human.class);

            PreparedStatement statement = con.prepareStatement("SELECT * FROM objects WHERE login = ?");
            statement.setString(1, email);

            ResultSet result = statement.executeQuery();

            statement = con.prepareStatement("DELETE FROM objects WHERE id = ?");

            while (result.next()) {
                System.out.println(result.getString("key"));

                if(key.compareTo(result.getString("key")) < 0) {
                    statement.setInt(1, result.getInt("id"));
                    statement.execute();
                    succsess = true;
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }catch(JsonSyntaxException mes){
            System.out.println("Неверный формат данных");
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return succsess;
    }
}
