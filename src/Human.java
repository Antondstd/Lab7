import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Date;

public class Human extends aCreature implements Comparable<Human>{
    enum BootsTypeOn {
        None,
        Sneakers,
        Sandals;
    }
    enum StandsOn {
        Parket,
        Asphalt,
        Concrete;
    }
    enum State {
        Angry,
        OutOfBreath,
        Relaxed

    }
    private String name;
    public OffsetDateTime appeared = OffsetDateTime.now();
    private int weight;
    private int x;
    private int y;
    private boolean flyingType;
    private Gender gender;
    private BootsTypeOn boots;
    private StandsOn standOn;
    private State state = State.Relaxed;
    Human (){

    }

    Human (String name, Gender gender, boolean flyingType, BootsTypeOn boots, StandsOn floor, int x, int y){
        this.name  = name;
        this.gender = gender;
        this.flyingType = flyingType;
        this.boots = boots;
        standOn = floor;
        /*try {
            this.birth = dateFormat.parse(birth);
        }catch (ParseException e) {
            e.printStackTrace();
        }*/
        this.x  = x;
        this.y = y;
        System.out.println("Создан персонаж с именем " + name + " и полом " + gender);
    }
    Human (String name, Gender gender, boolean flyingType, BootsTypeOn boots, StandsOn floor){
        this.name  = name;
        this.gender = gender;
        this.flyingType = flyingType;
        this.boots = boots;
        standOn = floor;
        System.out.println("Создан персонаж с именем " + name + " и полом " + gender);
    }
    Human (String name, Gender gender, boolean flyingType, BootsTypeOn boots){
        this.name  = name;
        this.gender = gender;
        this.flyingType = flyingType;
        this.boots = boots;
        System.out.println("Создан персонаж с именем " + name + " и полом " + gender);
    }
    Human (String name, Gender gender, boolean flyingType){
        this.name  = name;
        this.gender = gender;
        this.flyingType = flyingType;
        System.out.println("Создан персонаж с именем " + name + " и полом " + gender);
    }
    Human (String name, boolean flyingType){
        this.name  = name;
        this.gender = null;
        this.flyingType = flyingType;
        System.out.println("Создан персонаж с именем " + name + " и полом " + gender);
    }
    private int countNotGiveUp = 0;

    public int getCountNotGiveUp() {
        return countNotGiveUp;
    }

    public void setCountNotGiveUp(int countNotGiveUp) {
        this.countNotGiveUp = countNotGiveUp;
    }
    public String getShoes(){
        if (this.boots == BootsTypeOn.None)
            System.out.println(name + " стоит босиком на " + standOn);
        return this.boots.toString();
    }
    public String getState(){
        return this.state.toString();
    }
    public String getStandOn(){
        return this.standOn.toString();
    }
    public Gender getGender() throws NoGenderException{
        if (gender == null){
            throw new NoGenderException();
        }
        else {
            return gender;
        }
    }
    public String getName(){
        return name;
    }
    public boolean getFlyingType(){
        return flyingType;
    }

    public void setState(String state) {
        State result;
        switch (state){
            case("Angry"): result = State.Angry;
            break;
            case("OutOfBreath"): result = State.OutOfBreath;
                break;
            case("Relaxed"): result = State.Relaxed;
                break;
                default: result = State.Relaxed;
        }
        this.state = result;
        //System.out.println("Состояние персонажа " + name + " изменен на " + state);
    }
    public void setFlyingType(boolean type){
        this.flyingType = type;
    }

    @Override
    public String toString(){
        String info = "Имя: " + this.name + " Пол: " + this.gender + " Возможность летать: " + this.flyingType + " Ботинки: " + this.boots + " Локация: " + this.standOn + " Дата появления: " + this.appeared;
        return info;
    }

    public void setDate(){
        this.appeared = OffsetDateTime.now();
    }
    public long getAppeared() {
        return (long)appeared.toEpochSecond();
    }

    public int getX(){
        return this.x;
    }
    public int getY(){
        return this.y;
    }
    public void setGender(String gender){
        Gender result;
        switch (gender){
            case("Male"): result = Gender.Male;
            break;
            case("Female"): result = Gender.Female;
            break;
            default: result = Gender.Male;
        }
        this.gender = result;
    }

    public void setStandOn(String standOn) {
        StandsOn set = StandsOn.Asphalt;
        switch (standOn){
            case("Asphalt"): set = StandsOn.Asphalt;
            break;
            case("Concrete"): set = StandsOn.Concrete;
            break;
            case("Parket"): set = StandsOn.Parket;
            break;
        }
        this.standOn = set;
    }

    public void setBoots(String boots1){
        BootsTypeOn boots;
        switch (boots1){
            case("None"): boots = BootsTypeOn.None;
            break;
            case ("Sneakers"): boots = BootsTypeOn.Sneakers;
            break;
            case ("Sandal"): boots = BootsTypeOn.Sandals;
            break;
            default: boots = BootsTypeOn.None;
        }
        this.boots = boots;
    }
    public void setX(int x){
        this.x = x;
    }
    public void setY(int y){
        this.y = y;
    }
    public void setName(String name){
        this.name = name;
    }

    public int compareTo(Human h){

        return name.compareTo(h.getName());
    }
}