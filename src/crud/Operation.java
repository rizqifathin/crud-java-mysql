package crud;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class Operation {
    private static String username;

    public static boolean login(){
        Utility.clearScreen();
        System.out.println("\n[Login]\n");

        boolean loginOk = false;
        Scanner terminalInput = new Scanner(System.in);
        System.out.print("Masukkan username: ");
        String usernameInput = terminalInput.next();
        System.out.print("Masukkan password: ");
        String passInput = terminalInput.next();

        String sql = "SELECT pass FROM accounts WHERE username = '%s'";
        sql = String.format(sql,usernameInput);
        try {
            DatabaseUtil.rs = DatabaseUtil.stmt.executeQuery(sql);
            if(DatabaseUtil.rs.next()){
                Operation.username = usernameInput;
                String passDB = DatabaseUtil.rs.getString("pass");
                loginOk = Objects.equals(passDB, passInput);
                if (!loginOk) System.err.println("Password salah");
            }else{
                System.err.println("Username belum terdaftar");
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return loginOk;
    }

    public static void register(){
        try {
            DatabaseUtil.conn.setAutoCommit(false);

            Utility.clearScreen();
            System.out.println("\n[Register]\n");

            Scanner terminalInput = new Scanner(System.in);
            System.out.print("Masukkan username: ");
            String usernameInput = terminalInput.next();
            System.out.print("Masukkan pass: ");
            String passInput = terminalInput.next();
            String sql = "INSERT INTO accounts(username, pass) VALUES('%s','%s')";
            sql = String.format(sql,usernameInput,passInput);
            boolean isUpdate = Utility.getYesorNo("Apakah anda ingin register");
            if(isUpdate){
                DatabaseUtil.stmt.execute(sql);
            }
            DatabaseUtil.conn.commit();
        }catch (SQLException ex){
            try{
                if(DatabaseUtil.conn != null)
                    DatabaseUtil.conn.rollback();
            }catch(SQLException e){
                System.out.println(e.getMessage());
            }
        }
    }

    public static void showBookList(){
        try {
            Utility.clearScreen();
            System.out.println("\n[Daftar Buku]\n");
            System.out.println("\n|   ID   |\tTahun |\tPenulis                |\tJudul Buku             |\tQty");
            System.out.println("----------------------------------------------------------------------------------------");

            String sql = "SELECT * FROM books";
            DatabaseUtil.rs = DatabaseUtil.stmt.executeQuery(sql);

            while(DatabaseUtil.rs.next()){
                System.out.printf("|  %5s ", DatabaseUtil.rs.getString("id"));
                System.out.printf("|\t%4d  ", DatabaseUtil.rs.getInt("year_"));
                System.out.printf("|\t%-20s   ", DatabaseUtil.rs.getString("author"));
                System.out.printf("|\t%-20s   ", DatabaseUtil.rs.getString("title"));
                System.out.printf("|\t%d   ", DatabaseUtil.rs.getInt("quantity"));
                System.out.print("\n");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void insertBook(){
        try {
            DatabaseUtil.conn.setAutoCommit(false);

            Utility.clearScreen();
            boolean isUpdate;
            System.out.println("[Tambah Buku]\n");

            Scanner terminalInput = new Scanner(System.in);
            System.out.print("ID buku: ");
            String idBook = terminalInput.next();
            System.out.print("Tahun Terbit: ");
            int yearRelease = terminalInput.nextInt();
            System.out.print("Judul buku: ");
            terminalInput.nextLine();
            String title = terminalInput.nextLine();
            System.out.print("Penulis: ");
            String author = terminalInput.nextLine();
            System.out.print("Deskripsi buku: ");
            String description = terminalInput.nextLine();
            System.out.print("Jumlah buku: ");
            int quantity = terminalInput.nextInt();


            String sql = "SELECT id,quantity FROM books WHERE id = '%s'";
            sql = String.format(sql,idBook);
            DatabaseUtil.rs = DatabaseUtil.stmt.executeQuery(sql);
            if(DatabaseUtil.rs.next()){
                isUpdate = Utility.getYesorNo("Id buku sudah ada, keterangan buku yang sebelumnya akan dirubah\ndan jumlah buku akan ditambah dengan sebelumnya. Lanjut");
                quantity += DatabaseUtil.rs.getInt("quantity");
                sql = "UPDATE books SET tahun = %d, author = '%s', title = '%s', description_ = '%s', quantity = %d WHERE id = '%s'";
                sql = String.format(sql,yearRelease,author,title,description,quantity,idBook);
            }else{
                isUpdate = Utility.getYesorNo("Apakah anda ingin menambah buku");
                sql = "INSERT INTO books (id,year_,title,author,description_,quantity) VALUES('%s','%d','%s','%s','%s','%d')";
                sql = String.format(sql,idBook, yearRelease, title, author, description, quantity);
            }
            if(isUpdate){
                DatabaseUtil.stmt.execute(sql);
            }
            DatabaseUtil.conn.commit();
        }catch (SQLException ex){
            try{
                if(DatabaseUtil.conn != null)
                    DatabaseUtil.conn.rollback();
            }catch(SQLException e){
                System.out.println(e.getMessage());
            }
        }
    }

    public static void borrowBook(){
        try{
            DatabaseUtil.conn.setAutoCommit(false);

            Utility.clearScreen();
            System.out.println("[Pinjam Buku]\n");
            Scanner terminalInput = new Scanner(System.in);
            System.out.print("ID buku: ");
            String idBook = terminalInput.next();
            String sql = "SELECT b.quantity as quantity, h.borrowing_status as status FROM books as b " +
                    "LEFT JOIN " +
                    "(SELECT id_book,borrowing_status FROM borrowing_history " +
                    "WHERE id=(SELECT max(id) FROM borrowing_history WHERE username_account = '%s')) as h " +
                    "ON (b.id = h.id_book) " +
                    "WHERE b.id = '%s'";
            sql = String.format(sql, Operation.username, idBook);
            DatabaseUtil.rs = DatabaseUtil.stmt.executeQuery(sql);

            if (DatabaseUtil.rs.next()){
                int tempQuantity = DatabaseUtil.rs.getInt("quantity");
                String tempStatus = DatabaseUtil.rs.getString("status");
                if (tempQuantity>0){
                    if (Objects.equals(tempStatus, "Borrowing")) System.out.print("Anda sedang meminjam buku ini");
                    else {
                        tempQuantity--;
                        sql = "INSERT INTO borrowing_history(id_book,username_account,borrowing_status) VALUES('%s','%s','%s');" +
                                "UPDATE books SET quantity = %d WHERE id = '%s';";
                        sql = String.format(sql,idBook,Operation.username,"borrowing",tempQuantity,idBook);
                        boolean isUpdate = Utility.getYesorNo("Apakah anda ingin meminjam");
                        if (isUpdate) DatabaseUtil.stmt.execute(sql);
                    }
                }
                else System.out.println("Stok buku habis");
            }
            else System.out.println("Id buku tidak tersedia");
            DatabaseUtil.conn.commit();
        }catch (SQLException ex){
            try{
                if(DatabaseUtil.conn != null)
                    DatabaseUtil.conn.rollback();
            }catch(SQLException e){
                System.out.println(e.getMessage());
            }
        }
    }

    public static void returnBook(){
        try{
            DatabaseUtil.conn.setAutoCommit(false);
            Utility.clearScreen();
            System.out.println("[Mengembalikan Buku]\n");
            Scanner terminalInput = new Scanner(System.in);
            System.out.print("ID buku: ");
            String idBook = terminalInput.next();
            String sql = "SELECT id, borrowing_status FROM borrowing_history " +
                    "WHERE id = (SELECT max(id) FROM borrowing_history WHERE id_book = '%s')";
            sql = String.format(sql, idBook);
            DatabaseUtil.rs = DatabaseUtil.stmt.executeQuery(sql);
            if (DatabaseUtil.rs.next()){
                String tempStatus = DatabaseUtil.rs.getString("borrowing_status");
                String id = DatabaseUtil.rs.getString("id");
                if (!Objects.equals(tempStatus, "Returned")){
                    sql = "UPDATE borrowing_history " +
                            "SET time_end = NOW(), borrowing_status = '%s' " +
                            "WHERE id = '%s';" +
                            "UPDATE books SET quantity = quantity+1 WHERE id = '%s'";
                    sql = String.format(sql, "Returned", id, idBook);
                    boolean isUpdate = Utility.getYesorNo("Apakah anda ingin mengembalikan buku");
                    if (isUpdate) DatabaseUtil.stmt.execute(sql);
                }
                else System.out.println("Buku telah dikembalikan");
            }
            else System.out.println("Id buku tidak dipinjam");
            DatabaseUtil.conn.commit();
        }catch (SQLException ex) {
            try {
                if (DatabaseUtil.conn != null)
                    DatabaseUtil.conn.rollback();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void showBorrowHistory(){
        try{
            Utility.clearScreen();
            System.out.println("\n[Daftar Riwayat Peminjaman Buku]");
            System.out.println("\n| ID |\tID Buku |\tJudul Buku             |\tTanggal Pinjam       |\tTanggal Kembali      |\tStatus");
            System.out.println("----------------------------------------------------------------------------------------------------------");
            String sql = "SELECT * FROM borrowing_history as h JOIN books as b ON (h.id_book = b.id)";
            DatabaseUtil.rs = DatabaseUtil.stmt.executeQuery(sql);

            while(DatabaseUtil.rs.next()){
                System.out.printf("|%2s  ", DatabaseUtil.rs.getString("h.id"));
                System.out.printf("|\t%5s   ", DatabaseUtil.rs.getString("b.id"));
                System.out.printf("|\t%-20s   ", DatabaseUtil.rs.getString("b.title"));
                System.out.printf("|\t%-20s  ", DatabaseUtil.rs.getString("h.time_start"));
                System.out.printf("|\t%-20s  ", DatabaseUtil.rs.getString("h.time_end"));
                System.out.printf("|\t%-10s   ", DatabaseUtil.rs.getString("h.borrowing_status"));
                System.out.print("\n");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
