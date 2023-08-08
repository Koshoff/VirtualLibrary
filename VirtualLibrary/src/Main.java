import java.time.*;
import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import java.time.Instant;

class Administration {


    // main menu
    public static void mainMenu(){

        System.out.println("Welcome to the main menu");
        System.out.println("Choose your option");
        System.out.println(" (r)egister | (l)ogin | (a)dmin | (e)xit");
        Scanner s = new Scanner(System.in);
        char choice = s.next().charAt(0);

        switch(choice){
            // login
            case 'l':
                User user=logUser();
                if (user!=null){
                    userMenu(user);
                }
                else {
                    mainMenu();
                }

                break;
            // admin login
            case 'a':
                if (logAdmin()){
                    adminMenu();
                }
                else {
                    mainMenu();
                }
                break;
            // register new user
            case 'r':
                regUser();
                mainMenu();
                break;
            // exit
            case 'e': System.exit(0);
                break;
        }
    }

    // Admin menu
    public static void adminMenu(){

        System.out.println("Welcome to the admin panel");
        System.out.println("Choose your option");
        System.out.println(" (r)egister book | (d) elete book | (e)xit");
        Scanner s = new Scanner(System.in);
        char choice = s.next().charAt(0);

        switch(choice){
            // register new book
            case 'r': regBook();
                adminMenu();
                break;
            // delete a book
            case 'd':
                if (deleteBook()){
                    System.out.println("book deleted");
                }
                adminMenu();

                // exit system
            case 'e': System.exit(0);break;
        }
    }

    // User Menu
    public static void userMenu(User user){
        System.out.println("(s)earch book | (m)ake deposit | (e)xit");
        Scanner s = new Scanner(System.in);
        char choice = s.next().charAt(0);
        switch(choice){
            // search book
            case 's':
                System.out.println("Enter title:");
                String book_title=s.nextLine();
                Book book = searchBook(book_title);
                // if book exists - load rent menu
                if (book!=null){
                    rentMenu(user, book);
                }
                userMenu(user);

                break;
            case 'm':
                if (user.makeDeposit()){
                    System.out.println("Deposit made successfuly!");
                    System.out.println("Current amount:"+user.getDeposit());
                }
                userMenu(user);
                break;
            case 'e': System.exit(0);break;
        }

    }

    // RentMenu
    public static void rentMenu(User user, Book book){

        System.out.println("(r)ent | (m)ain menu |(e)xit");
        Scanner s = new Scanner(System.in);
        char choice = s.next().charAt(0);

        switch(choice){
            // rent the book
            case 'r':
                rentBook(user, book);
                userMenu(user);
                break;
            case 'm': userMenu(user);
            case 'e': System.exit(0);break;
        }
    }

    // Methodd to register user
    public static boolean regUser(){
        try {
            Scanner s = new Scanner(System.in);
            System.out.println("Enter nickname:");
            String nickname = s.nextLine();
            System.out.println("Enter password:");
            String password = s.nextLine();
            System.out.println("Enter email");
            String email = s.nextLine();
            if (User.verifyPassword(password)) {
                User user = new User(nickname, password, email);

                BufferedWriter fw = new BufferedWriter(
                        new FileWriter("users/users.txt", true));
                fw.append(user.getUid()+","+user.getNickname()+"\n");
                fw.close();
                FileOutputStream userfile = new FileOutputStream("users/"+String.valueOf(user.getUid())+".ser");
                ObjectOutputStream out = new ObjectOutputStream(userfile);
                out.writeObject(user);
                out.close();
                System.out.println("User registered successfuly!");
                //mainMenu();
                return true;
            }
            else{System.out.println("Password must contain 8 characters, 1 digit, 1 special symbol !");
                return regUser();
            }
        }
        catch(Exception e){
            System.out.println("Something went wrong!");
            return regUser();
            //return false;
        }

    }

    // Method to log in user
    public static User logUser(){

        try {

            File user_list = new File("users/users.txt");
            Scanner s1 = new Scanner(user_list);
            Scanner s2 = new Scanner(System.in);
            System.out.println("Enter username:");
            String nickname = s2.nextLine();
            System.out.println("Enter password:");
            String password = s2.nextLine();

            Pattern pattern1 = Pattern.compile(nickname);
            boolean isthere=false;
            String row="";
            while (s1.hasNext()){
                row = s1.nextLine();
                Matcher matcher = pattern1.matcher(row);

                if (matcher.find()){
                    isthere=true;
                    break;
                }
            }
            if (isthere){
                String[] splitted = row.split(",",2);
                String uid = splitted[0];
                FileInputStream fileIn = new FileInputStream("users/"+uid+".ser");
                ObjectInputStream in = new ObjectInputStream(fileIn);
                User user = (User) in.readObject();
                System.out.println("Here we have a user"+user.getUid());

                in.close();
                fileIn.close();
                boolean success = user.checkPass(password);
                if (success){
                    System.out.println("Login successful!");
                    //userMenu(user);
                    return user;
                }
                else {
                    System.out.println("Wrong password!");
                    return null;
                }
            }
            else{
                System.out.println("No such user!");
                return null;


            }
        }
        catch (Exception e){
            System.out.println("Something went wrong!");
            return null;
        }
    }


    // Method to login administrator
    public static boolean logAdmin(){

        try {

            File user_list = new File("admins.txt");
            Scanner s1 = new Scanner(user_list);
            Scanner s2 = new Scanner(System.in);
            System.out.println("Enter username:");
            String nickname = s2.nextLine();
            System.out.println("Enter password:");
            String password = s2.nextLine();

            Pattern pattern1 = Pattern.compile(nickname);
            boolean isthere=false;
            String row="";
            while (s1.hasNext()){
                row = s1.nextLine();
                Matcher matcher = pattern1.matcher(row);

                if (matcher.find()){
                    isthere=true;
                    break;
                }
            }
            if (isthere){
                String[] splitted = row.split(",");
                String readpass = splitted[1];

                if (password.equals(readpass)){
                    System.out.println("Login successful!");
                    return true;

                }
                else {
                    System.out.println("Wrong password!");
                    return false;

                }
            }
            else{
                System.out.println("No such user!");
                return false;

            }
        }
        catch (Exception e){
            System.out.println("Something went wrong!");
            return false;
        }

    }

    //Method to register new book
    public static void regBook(){
        String isbn, title, author,  content;
        Scanner s = new Scanner(System.in);
        System.out.println("Enter ISBN:");
        isbn = s.nextLine();
        System.out.println("Enter Title:");
        title = s.nextLine();
        System.out.println("Enter author:");
        author = s.nextLine();
        System.out.println("Enter content:");
        content = s.nextLine();
        Book book = new Book(isbn, title, author,  content);
        //File catalogue = new File("books/catalogue.txt");
        try{
            BufferedWriter catalogue = new BufferedWriter(
                    new FileWriter("books/catalogue.txt", true));
            //FileWriter catalogue = new FileWriter();
            catalogue.append(book.getIsbn()+","+book.getTitle()+"\n");
            FileOutputStream fileOut =
                    new FileOutputStream("books/"+book.getIsbn()+".ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(book);
            out.close();
            fileOut.close();
            catalogue.close();
            System.out.printf("New book is saved");
            //return true;
        } catch (IOException i) {
            System.out.printf("Can't save book!");
            // return false;
        }

    }

    public static boolean deleteBook(){

        try {
            File catalogue = new File("books/catalogue.txt");
            File tempFile = new File("books/TempFile.txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
            Scanner s1 = new Scanner(catalogue);
            Scanner s2 = new Scanner(System.in);
            System.out.println("Enter ISBN:");
            String isbn=s2.nextLine();
            Pattern pattern1 = Pattern.compile(isbn);
            boolean isthere=false;
            String row ="";
            while (s1.hasNext()){
                row = s1.nextLine();
                Matcher matcher = pattern1.matcher(row);
                if (matcher.find()){
                    isthere=true;

                    continue;
                }
                writer.write(row + "\n");
            }
            writer.close();
            tempFile.renameTo(catalogue);

            if (isthere){
                //Scanner s = new Scanner(System.in);
                //String isbn = s.nextLine();
                File myObj = new File("books/"+isbn+".ser");
                try {
                    myObj.delete();
                    return true;
                }
                catch (Exception e){
                    System.out.println("Something went wrong!");
                    return false;
                }}

            else{
                System.out.println("Book is not in catalogue!");
                return false;}


        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }


    }


    // Method to search wether a book is in catalogue
    public static Book searchBook(String title){

        File catalogue = new File("books/catalogue.txt");
        try {
            Scanner s1 = new Scanner(catalogue);
            Scanner s2 = new Scanner(System.in);
            Pattern pattern1 = Pattern.compile(s2.nextLine());
            boolean isthere=false;
            String row ="";
            while (s1.hasNext()){
                row = s1.nextLine();
                Matcher matcher = pattern1.matcher(row);
                if (matcher.find()){
                    isthere=true;
                    break;
                }
            }
            if (isthere){
                String[] split = row.split(",");
                String isbn = split[0];
                FileInputStream fileIn = new FileInputStream("books/"+isbn+".ser");
                ObjectInputStream in = new ObjectInputStream(fileIn);
                Book book = (Book) in.readObject();
                in.close();
                fileIn.close();
                System.out.println("Book is in catalogue");
                return book;
            }
            else{
                System.out.println("Book is not in catalogue");
                return null;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }


    // Method to read book info
    public static Book getBook(String isbn){
        try{
            FileInputStream fileIn = new FileInputStream("books/"+isbn+".ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            Book book = (Book) in.readObject();
            return book;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    // Method to rent a book
    public static void rentBook(User user, Book book){
        double userMoney = user.getDeposit();
        if (userMoney>=2){
            userMoney-=2;
            user.setDeposit(userMoney);
            System.out.println(book.getContent());
        }
        else{
            System.out.println("Not enough money in deposit");
        }
    }
}

// We make User class Serializable because we are going to save obj in file
class User implements java.io.Serializable {

    private String uid;
    private String nickname;
    private String password;
    private LocalDateTime regDate;
    private String email;
    private double deposit;


    public User (String nick, String pass, String email){

        this.uid =String.valueOf(Instant.now().getEpochSecond());
        this.nickname = nick;
        this.password=pass;
        this.email=email;
        this.regDate = LocalDateTime.now();
        this.deposit=0;
    }

    public void setDeposit(double deposit) {
        this.deposit = deposit;
    }

    public String getUid() {
        return uid;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPassword() {
        return password;
    }

    public LocalDateTime getRegDate() {
        return regDate;
    }

    public String getEmail() {
        return email;
    }

    public double getDeposit() {
        return deposit;
    }

    // Method to validate password
    public static boolean verifyPassword(String password){

        Pattern pattern1 = Pattern.compile("[\\d+]");
        Pattern pattern2 = Pattern.compile("[a-z]||[A-Z]");
        Pattern pattern3 = Pattern.compile("[^\\w+]");

        Matcher matcher = pattern1.matcher(password);
        boolean first = matcher.find();
        System.out.println(first);
        matcher = pattern2.matcher(password);
        boolean second = matcher.find();
        System.out.println(second);
        matcher = pattern3.matcher(password);
        boolean third = matcher.find();
        System.out.println(third);

        if (first&&second&&third&&password.length()>=8){
            return true;
        }
        else{
            return false;

        }
    }

    // Method to check read pass with entered pass
    public boolean checkPass(String pass){
        if (this.password.equals(pass)){
            return true;
        }
        else {
            return false;
        }
    }

    // Method to make a deposit
    public boolean makeDeposit(){
        Scanner s = new Scanner(System.in);
        try {
            System.out.println("Enter amount");
            double amount = s.nextDouble();
            this.deposit+=amount;
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
}


class Book implements java.io.Serializable {

    private String isbn;
    private String title;
    private String author;
    private String content;

    public Book(String isbn, String title, String author, String content){

        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.content = content;

    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}


public class Main {
    public static void main(String[] args) {
        Administration.mainMenu();
    }
}