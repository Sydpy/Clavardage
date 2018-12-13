package org.etudinsa.clavardage.ui;

import org.etudinsa.clavardage.sessions.Message;
import org.etudinsa.clavardage.sessions.Session;
import org.etudinsa.clavardage.sessions.SessionManager;
import org.etudinsa.clavardage.users.User;
import org.etudinsa.clavardage.users.UserManager;

import java.io.IOException;
import java.util.Observable;
import java.util.Scanner;

public class CLI extends UI implements Runnable {

    enum CLIMode { HOME, CHAT }

    static class Command {

        final String command;
        final String arg;

        Command(String command, String arg) {
            this.command = command;
            this.arg = arg;
        }

        public static Command fromString(String str) {
            String[] splitted = str.split(" ", 2);
            if (splitted.length == 2)
                return new Command(splitted[0], splitted[1]);

            return new Command(splitted[0], "");
        }
    }

    private CLIMode mode = CLIMode.HOME;
    private String distantUser = null;

    private CLI() {
        UserManager.getInstance().addObserver(this);
        SessionManager.getInstance().addObserver(this);
    }

    @Override
    public void update(Observable observable, Object o) {

        if (observable instanceof UserManager) {
            UserManager um = (UserManager) observable;
            //TODO

        } else if (observable instanceof SessionManager) {

            if (mode == CLIMode.CHAT) {

                SessionManager sm = (SessionManager) observable;
                Session s = (Session) o;

                Message lastMessage = s.getMessages().get(s.getMessages().size() - 1);

                if (lastMessage.getSender().equals(UserManager.getInstance().getMyUser())) {
                    System.out.println("SENT : " + lastMessage.getContent());
                } else if (lastMessage.getSender().pseudo.equals(distantUser)) {
                    System.out.println("RECV : " + lastMessage.getContent());
                }
            }
        }
    }

    private void printPrompt() {
        switch (mode) {

            case HOME:
                System.out.print("> ");
                break;
            case CHAT:
                System.out.println(distantUser + " > ");
                break;
        }
    }

    private void printHomeHelp() {
        System.out.println("help : print this help");
        System.out.println("listusers : list users currently on the network");
        System.out.println("listchats : list opened chats ");
        System.out.println("chat <pseudo> : select the chat with 'pseudo' as the current focused chat");
        System.out.println("exit : exit the application");
    }

    private void listusers() {
        System.out.printf("%20s|%15s\n","Pseudo", "IP");
        System.out.println("--------------------|---------------------");
        User[] userDB = UserManager.getInstance().getUserDB();
        for (User user : userDB) {
            System.out.printf("%20s|%15s\n",user.pseudo, user.ip);
        }
        System.out.println();
    }

    private void chat(String pseudo) {
        mode = CLIMode.CHAT;
        distantUser = pseudo;
    }

    private void executeHomeCommand(Command cmd) {

        if (cmd.command.equals("help")) {
            printHomeHelp();
        } else if(cmd.command.equals("listusers")) {
            listusers();
        } else if(cmd.command.equals("listchats")) {
            //TODO
            System.out.println("TODO");
        } else if(cmd.command.equals("chat")) {
            chat(cmd.arg);
        } else {
            System.out.println("Unknown command, type 'help' to get some... help.");
        }
    }

    private void printChatHelp() {
        System.out.println("help : print this help");
        System.out.println("send <message> : send the message to the current session");
        System.out.println("back : return to HOME mode");
        System.out.println("exit : exit the application");
    }

    private void send(String message) {
        try {
            SessionManager.getInstance().sendMessage(message, distantUser);
        } catch (Exception e) {
            System.err.println("Failed to send message");
            e.printStackTrace();
        }
    }

    private void back() {
        mode = CLIMode.HOME;
        distantUser = null;
    }

    private void executeChatCommand(Command cmd) {

        if (cmd.command.equals("help")) {
            printChatHelp();
        } else if (cmd.command.equals("send")) {
            send(cmd.arg);
        } else if (cmd.command.equals("back")) {
            back();
        } else {
            System.out.println("Unknown command, type 'help' to get some... help.");
        }
    }

    private void executeCommand(Command cmd) {

        switch (mode) {
            case HOME:
                executeHomeCommand(cmd);
                break;
            case CHAT:
                executeChatCommand(cmd);
                break;
        }
    }

    private void createUser() {

        Scanner sc = new Scanner(System.in);
        System.out.println("Choose a pseudo : ");
        try {
            UserManager.getInstance().createMyUser(sc.nextLine());
        } catch (IOException e) {
            e.printStackTrace();
            createUser();
        }
    }

    @Override
    public void run() {

        Scanner sc = new Scanner(System.in);

        System.out.println("Choose a pseudo : ");
        try {
            UserManager.getInstance().createMyUser(sc.nextLine());
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("Starting ClavardageCLI.");
        printHomeHelp();
        System.out.println();


        printPrompt();
        Command cmd = Command.fromString(sc.nextLine());
        while (!cmd.command.equals("exit")) {

            executeCommand(cmd);

            printPrompt();
            cmd = Command.fromString(sc.nextLine());
        }

        System.out.println("Exiting ClavardageCLI.");
    }

    public static void main(String[] args) {

        UserManager.getInstance().start();
        SessionManager.getInstance().start();

        new CLI().run();

        SessionManager.getInstance().stop();
        UserManager.getInstance().stop();
    }

}
