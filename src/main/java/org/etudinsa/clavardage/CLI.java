package org.etudinsa.clavardage;

import org.etudinsa.clavardage.sessions.*;
import org.etudinsa.clavardage.users.User;
import org.etudinsa.clavardage.users.UserManager;
import org.etudinsa.clavardage.users.UserObserver;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

public class CLI implements UserObserver, SessionObserver, Runnable {


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
                return new Command(splitted[0].trim(), splitted[1].trim());

            return new Command(splitted[0].trim(), "");
        }
    }

    private CLIMode mode = CLIMode.HOME;
    private User distantUser = null;

    private KeyPairGenerator keyGenerator;

    private Scanner scanner = new Scanner(System.in);

    private CLI() throws NoSuchAlgorithmException, NoSuchProviderException {

        this.userManager.registerUserObserver(this);
        this.sessionManager.registerSessionObserver(this);

        keyGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom rng = SecureRandom.getInstance("SHA1PRNG", "SUN");
        rng.setSeed(new Date().getTime());
        keyGenerator.initialize(1024, rng);
    }

    @Override
    public void messageSent(Message message) {
        StringBuilder sb = new StringBuilder("\033[1A\r\033[32m");
        sb.append(message.getContent().getDate());
        sb.append(" : ");
        sb.append(message.getContent().getContent());

        sb.append("\033[37m");
        System.out.println(sb.toString());
    }

    @Override
    public void messageReceived(Message message) {
        StringBuilder sb = new StringBuilder("\r\033[34m");
        if (distantUser != null && distantUser.ip.equals(message.getDistantIP())) {
            sb.append(message.getContent().getDate());
            sb.append(" : ");
            sb.append(message.getContent().getContent());
        } else {
            sb.append("\033[5m");
            sb.append("New message from ");
            sb.append(userManager.getUserByIp(message.getDistantIP()));
            sb.append("\033[25m");
        }

        sb.append("\033[37m");
        System.out.println(sb.toString());
        printPrompt();
    }

    @Override
    public void newUser(User newUser) {
        StringBuilder sb = new StringBuilder("\r\033[33mNew user ");
        sb.append(newUser.pseudo);

        sb.append("\033[37m");
        System.out.println(sb.toString());
        printPrompt();
    }

    @Override
    public void userLeaving(User userLeaving) {
        StringBuilder sb = new StringBuilder("\r\033[90m");
        sb.append(userLeaving.pseudo);
        sb.append(" is leaving");

        sb.append("\033[37m");
        System.out.println(sb.toString());
        printPrompt();
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
        User[] userDB = userManager.getUserDB();
        for (User user : userDB) {
            System.out.printf("%20s|%20s|%20s\n", user.pseudo, user.ip, user.publicKey.getAlgorithm());
        }
        System.out.println();
    }

    private void chat(String pseudo) {

        try {
            Session session = sessionManager.getSessionByDistantUserPseudo(pseudo);

                for (Message message : session.getMessages()) {

                    StringBuilder sb = new StringBuilder();
                    if (message.isSent()) {
                        sb.append("\033[32m");
                    } else {
                        sb.append("\033[34m");
                    }

                    sb.append(message.getContent().getDate().toString());
                    sb.append(" : ");
                    sb.append(message.getContent().getContent());
                    sb.append("\033[37m");

                    System.out.println(sb.toString());
                }

            mode = CLIMode.CHAT;
            distantUser = userManager.getUserByPseudo(pseudo);

        } catch (Exception e) {
            System.out.println(e.getMessage());
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
            sessionManager.sendMessage(message, distantUser.pseudo);
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

        sessionManager.startListening();
    }

    private void disconnect() {
        try {
            userManager.leaveNetwork();
        } catch (Exception e) {
            e.printStackTrace();
        }

        sessionManager.stopListening();
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

    private static UserManager userManager;
    private static SessionManager sessionManager;

    public static void main(String[] args) throws NoSuchProviderException, NoSuchAlgorithmException, UnknownHostException {

        ManagerFactory managerFactory;

        if (Arrays.asList(args).contains("--mock")) {
            managerFactory = new ManagerFactory(true);
        } else {
//            managerFactory = new ManagerFactory(false);
        	managerFactory = new ManagerFactory(InetAddress.getLocalHost());
        }

        userManager = managerFactory.getUserManager();
        sessionManager = managerFactory.getSessionManager();

        new CLI().run();
    }

	@Override
	public void updatedUserList() {		
	}
}
