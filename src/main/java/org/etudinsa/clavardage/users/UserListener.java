package org.etudinsa.clavardage.users;

import java.net.*;
import java.util.Arrays;
import java.util.Observable;

class UserListener implements Runnable {

    static class ReceivedUserMessage {

        final UserMessage userMessage;
        final InetAddress address;

        ReceivedUserMessage(UserMessage userMessage, InetAddress address) {
            this.userMessage = userMessage;
            this.address = address;
        }
    }

    static final int LISTENING_PORT = 2512;

    private final DatagramSocket socket;

    UserListener() throws SocketException {
        this.socket = new DatagramSocket(LISTENING_PORT);
        this.socket.setSoTimeout(2000);
    }

    @Override
    public void run() {

        byte[] buffer = new byte[1024];

        while (true) {

            if (socket.isClosed()) return;

            Arrays.fill(buffer, (byte) 0);

            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                byte[] data = packet.getData();

                UserMessage message = UserMessage.fromString(new String(data));

                UserManager.getInstance().receivedMessageFrom(message, packet.getAddress());
                
            } catch (SocketTimeoutException ignored) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void stop() {
        socket.close();
    }
}
