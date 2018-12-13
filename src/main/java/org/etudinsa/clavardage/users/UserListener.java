package org.etudinsa.clavardage.users;

import java.awt.datatransfer.StringSelection;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.Arrays;
import java.util.Observable;

class UserListener extends Observable implements Runnable {

    static class ReceivedBroadcastMessage {

        final BroadcastMessage broadcastMessage;
        final InetAddress address;

        ReceivedBroadcastMessage(BroadcastMessage broadcastMessage, InetAddress address) {
            this.broadcastMessage = broadcastMessage;
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

                BroadcastMessage message = BroadcastMessage.fromString(new String(data));
                
                notifyObservers(new ReceivedBroadcastMessage(message, packet.getAddress()));
                
            } catch (SocketTimeoutException ignored) {
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
				e.printStackTrace();
			}
        }
    }

    void stop() {
        socket.close();
    }
}
