package org.etudinsa.clavardage.ui;

import org.etudinsa.clavardage.sessions.Message;
import org.etudinsa.clavardage.sessions.Session;
import org.etudinsa.clavardage.sessions.SessionManager;
import org.etudinsa.clavardage.users.User;
import org.etudinsa.clavardage.users.UserManager;
import org.etudinsa.clavardage.users.UserMessage;

import java.net.SocketException;
import java.security.*;
import java.util.Date;
import java.util.Observable;
import java.util.Scanner;

public class CLI extends UI implements Runnable {

    enum CLIMode { HOME, CHAT }

    private static class Command {

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

    private KeyPairGenerator keyGenerator;

    private Scanner scanner = new Scanner(System.in);

    private CLI() throws NoSuchAlgorithmException, NoSuchProviderException {
        userManager.addObserver(this);
        sessionManager.addObserver(this);

        keyGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom rng = SecureRandom.getInstance("SHA1PRNG", "SUN");
        rng.setSeed(new Date().getTime());
        keyGenerator.initialize(1024, rng);
    }

    @Override
    public void update(Observable observable, Object o) {

        if (observable instanceof UserManager) {
            //TODO

            UserMessage um = (UserMessage) o;

            System.out.println();
            System.out.print("UserManager : " + um.type );

            if (um.type == UserMessage.Type.NEWUSER) System.out.print(" " + um.content[0]);

            System.out.println();
            printPrompt();

        } else if (observable instanceof SessionManager) {

            Session s = (Session) o;
            Message lastMessage = s.getMessages().get(s.getMessages().size() - 1);
            User sender = lastMessage.getSender();

            System.out.println();

            if (sender.pseudo.equals(distantUser)) {
                System.out.println(lastMessage.getContent().getDate() + " RECV : " + lastMessage.getContent().getContent());
                printPrompt();
            } else if (sender.pseudo.equals(userManager.getMyUser().pseudo)) {
                System.out.println(lastMessage.getContent().getDate() + " SENT : " + lastMessage.getContent().getContent());
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
        System.out.printf("%20s|%20s|%20s\n","Pseudo", "IP", "Public Key");
        System.out.println("--------------------|---------------------|--------------------");
        User[] userDB = UserManager.getInstance().getUserDB();
        for (User user : userDB) {
            System.out.printf("%20s|%20s|%20s\n", user.pseudo, user.ip, user.publicKey.getAlgorithm());
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
                        System.out.println(message.getContent().getDate() + " SENT : " + message.getContent().getContent());
                    } else {
                        System.out.println(message.getContent().getDate() + " RECV : " + message.getContent().getContent());
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
        System.out.println("help :PrivateKey print this help");
        System.out.println("send <message> : send the message to the current session");
        System.out.println("back : return to HOME mode");
        System.out.println("exit : exit the application");
    }

    private void send(String message) {
        try {
            sessionManager.sendMessage(message, distantUser);
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

        while (!userManager.isConnected()) {
            System.out.print("Choose a pseudo : ");
            try {
                userManager.joinNetwork(scanner.nextLine(), keyGenerator.generateKeyPair());
            } catch (Exception e) {
                System.err.println(e.toString());
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

        connect();

        System.out.println("Starting ClavardageCLI.");
        printHomeHelp();
        System.out.println();

        printPrompt();
        Command cmd = Command.fromString(scanner.nextLine());
        while (!cmd.command.equals("exit")) {

            executeCommand(cmd);

            printPrompt();
            cmd = Command.fromString(scanner.nextLine());
        }

        System.out.println("Exiting ClavardageCLI.");

        disconnect();
    }

    public static void main(String[] args) throws SocketException, NoSuchProviderException, NoSuchAlgorithmException {

        new CLI().run();
    }

}
