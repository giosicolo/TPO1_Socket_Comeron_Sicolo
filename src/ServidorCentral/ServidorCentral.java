package Sockets;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServidorCentral {
    private static final String IP = "localhost";
    private static final int PUERTO_SC = 5000;
    private static final int PUERTO_SH = 6000;
    private static final int PUERTO_SPC = 7000;
    private static ConcurrentHashMap<String, String> historialHoroscopo;
    private static ConcurrentHashMap<String, String> historialPronosticoClima;
    
    public static void main(String[] args) {
        ServerSocket socketServer;
        try {
            socketServer = new ServerSocket(PUERTO_SC);

            historialHoroscopo = new ConcurrentHashMap<String, String>();
            historialPronosticoClima = new ConcurrentHashMap<String, String>();
            int idSesion = 0;
            
            while (true) {
                Socket socketCliente;
                socketCliente = socketServer.accept();
                ((ServidorCentralHilo) new ServidorCentralHilo(socketCliente, IP, PUERTO_SH, PUERTO_SPC, historialHoroscopo, historialPronosticoClima, idSesion++)).start();
            }
        } catch (IOException ex) {
                System.err.println("Error: " +ex.getMessage());
            }
    }
}