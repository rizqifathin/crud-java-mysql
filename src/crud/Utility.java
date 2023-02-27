package crud;

import java.util.Scanner;

public class Utility {

    public static void clearScreen(){
        try {
            if (System.getProperty("os.name").contains("Windows")){
                new ProcessBuilder("cmd","/c","cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033\143");
            }
        } catch (Exception ex){
            System.err.println("tidak bisa clear screen");
        }
    }

    public static boolean getYesorNo(String message){
        Scanner terminalInput = new Scanner(System.in);
        System.out.print("\n"+message+" (y/n)? ");
        String pilihanUser = terminalInput.next();

        while(!pilihanUser.equalsIgnoreCase("y") && !pilihanUser.equalsIgnoreCase("n")) {
            System.err.println("Pilihan anda bukan y atau n");
            System.out.print("\n"+message+" (y/n)? ");
            pilihanUser = terminalInput.next();
        }
        return pilihanUser.equalsIgnoreCase("y");
    }

    public static void showLoginMenu() {
        Utility.clearScreen();
        System.out.println("\n========= Selamat Datang =========");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("0. Keluar\n");
    }

    public static void showMainMenu() {
        Utility.clearScreen();
        System.out.println("\n========= MENU UTAMA =========");
        System.out.println("1. Tamplikan Seluruh Buku");
        System.out.println("2. Tambah Buku");
        System.out.println("3. Pinjam Buku");
        System.out.println("4. Mengembalikan Buku");
        System.out.println("5. Tampilkan Riwayat Peminjaman");
        System.out.println("0. Logout\n");
    }
}
