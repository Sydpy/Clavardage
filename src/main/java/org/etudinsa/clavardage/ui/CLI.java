package org.etudinsa.clavardage.ui;

import org.etudinsa.clavardage.sessions.Message;
import org.etudinsa.clavardage.sessions.Session;
import org.etudinsa.clavardage.sessions.SessionManager;
import org.etudinsa.clavardage.users.User;
import org.etudinsa.clavardage.users.UserManager;
import org.etudinsa.clavardage.users.UserMessage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
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

    private UserManager userManager = UserManager.getInstance();
    private SessionManager sessionManager = SessionManager.getInstance();

    private CLI() {
        userManager.addObserver(this);
        sessionManager.addObserver(this);
    }

    @Override
    public void update(Observable observable, Object o) {

        if (observable instanceof UserManager) {
            //TODO

            UserMessage um = (UserMessage) o;

            System.out.println();
            System.out.println("UserManager : " + um.toString());

            if (um.type == UserMessage.Type.USERDB_REQUEST && userManager.isUserDBAuthority())
                System.out.println("UserManager : Answering");

            printPrompt();

        } else if (observable instanceof SessionManager) {

            Session s = (Session) o;
            Message lastMessage = s.getMessages().get(s.getMessages().size() - 1);
            User sender = lastMessage.getSender();

            System.out.println();

            if (sender.pseudo.equals(distantUser)) {
                System.out.println("RECV : " + lastMessage.getContent());
                printPrompt();
            } else if (sender.pseudo.equals(userManager.getMyUser().pseudo)) {
                System.out.println("SENT : " + lastMessage.getContent());
            } else {
                System.out.println("SessionManager : New message from " + sender.pseudo);
                printPrompt();
            }
        }
    }

    private void printPrompt() {
        switch (mode) {

            case HOME:
                System.out.print("> ");
                break;
            case CHAT:
                System.out.print(distantUser + " > ");
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

        try {
            Session session = sessionManager.getSessionByDistantUserPseudo(distantUser);

            if (session == null) {
                System.out.println("No session yet, start chating");
            } else {

                for (Message message : session.getMessages()) {
                    User sender = message.getSender();
                    if (sender.equals(userManager.getMyUser())) {
                        System.out.println("SENT : " + message.getContent());
                    } else {
                        System.out.println("RECV : " + message.getContent());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void connect() {

        Scanner sc = new Scanner(System.in);

        while (!userManager.isConnected()) {
            System.out.print("Choose a pseudo : ");
            try {
                userManager.joinNetwork(sc.nextLine());
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }

        sessionManager.start();
    }

    private void disconnect() {
        try {
            userManager.leaveNetwork();
        } catch (Exception e) {
            e.printStackTrace();
        }
        sessionManager.stop();
    }

    @Override
    public void run() {

        Scanner sc = new Scanner(System.in);

        connect();

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

        disconnect();
    }

    public static void main(String[] args) throws SocketException {

        new CLI().run();
    }

}
