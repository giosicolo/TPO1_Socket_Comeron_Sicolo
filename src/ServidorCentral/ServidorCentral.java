package ServidorCentral;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ServidorCentral {
    
    private static  String IP;
    private static  int PUERTO_SC ;
    private static  int PUERTO_SH ;
    private static  int PUERTO_SPC ;
    private static ConcurrentHashMap<String, String> historialHoroscopo;
    private static ConcurrentHashMap<String, String> historialPronosticoClima;
    
    public static void main(String[] args) {
        ServerSocket socketServerCentral; 
        try {
            String configFilePath = "config_server.txt";
            BufferedReader configReader = new BufferedReader(new FileReader(configFilePath));    
            PUERTO_SC = Integer.parseInt(configReader.readLine());
            PUERTO_SPC = Integer.parseInt(configReader.readLine());
            PUERTO_SH = Integer.parseInt(configReader.readLine());
            IP = configReader.readLine();
            configReader.close();
            
            System.out.println("ServidorCentral> Iniciando ServidorCentral...");
            socketServerCentral = new ServerSocket(PUERTO_SC);

            historialHoroscopo = new ConcurrentHashMap<String, String>();
            historialPronosticoClima = new ConcurrentHashMap<String, String>();
            int idSesion = 1;
            System.out.println("ServidorCentral> ServidorCentral iniciado.");
            
            while (true) {
                Socket socketCliente;
                System.out.println("ServidorCentral> Esperando solicitudes de sesion...");
                socketCliente = socketServerCentral.accept();
                System.out.println("ServidorCentral> Nueva solicitud de sesion recibida.");
                new ServidorCentralHilo(socketCliente, IP, PUERTO_SH, PUERTO_SPC, historialHoroscopo, historialPronosticoClima, idSesion).start();
                idSesion++;
            }
        } catch (IOException ex) {
                System.err.println("ServidorCentral> Error: " +ex.getMessage());
            }
    }
}