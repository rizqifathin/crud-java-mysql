package tutorial;

import crud.DatabaseUtil;
import crud.Utility;
import crud.Operation;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/belajar_java-mysql?allowMultiQueries=true";
    static final String USER = "root";
    static final String PASS = "password";

    public static void main(String[] args) throws SQLException {
        DatabaseUtil.checkConnection(JDBC_DRIVER, DB_URL, USER, PASS);
        Scanner terminalInput = new Scanner(System.in);
        boolean isLoginOk;
        boolean isContinue = true;

        while (!DatabaseUtil.conn.isClosed()){
            while (isContinue){
                Utility.showLoginMenu();
                System.out.print("PILIHAN> ");
                String inputLogin = terminalInput.next();
                switch (inputLogin) {
                    case "1" -> {
                        isLoginOk = Operation.login();
                        while (isLoginOk) {
                            Utility.showMainMenu();
                            System.out.print("PILIHAN> ");
                            String inputMain = terminalInput.next();
                            switch (inputMain) {
                                case "1" -> Operation.showBookList();
                                case "2" -> Operation.insertBook();
                                case "3" -> Operation.borrowBook();
                                case "4" -> Operation.returnBook();
                                case "5" -> Operation.showBorrowHistory();
                                case "0" -> isLoginOk = false;
                                default -> System.err.println("\nInput anda tidak ditemukan\nSilahkan pilih [0-5]");
                            }
                        }
                    }
                    case "2" -> Operation.register();
                    case "0" -> {
                        DatabaseUtil.closeConnection();
                        isContinue = false;
                    }
                    default -> System.err.println("\nInput anda tidak ditemukan\nSilahkan pilih [0-2]");
                }
            }
        }

    }
}