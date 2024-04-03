package ServidorHoroscopo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorHoroscopo {
    
    private static int PUERTO_SH;
    
    public static void main(String[] args) {
        ServerSocket socketServerHoroscopo;
        try {
             String configFilePath = "config_serverHoroscopo.txt";
             BufferedReader configReader = new BufferedReader(new FileReader(configFilePath));
             PUERTO_SH = Integer.parseInt(configReader.readLine());
             configReader.close();
            
            System.out.println("ServidorHoroscopo> Iniciando ServidorHoroscopo...");
            socketServerHoroscopo = new ServerSocket(PUERTO_SH);
            int idSesion = 1;
            System.out.println("ServidorHoroscopo> ServidorHoroscopo iniciado.");
            
            while(true) {
                Socket socketCliente;
                System.out.println("ServidorHoroscopo> Esperando solicitudes de sesion...");
                socketCliente = socketServerHoroscopo.accept();
                System.out.println("ServidorHoroscopo> Nueva solicitud de sesion recibida.");
                new ServidorHoroscopoHilo(socketCliente, idSesion).start();
                idSesion++;
            }
        } catch (IOException ex) {
            System.err.println("ServidorHoroscopo> Error: " +ex.getMessage());
        }
    }
}
