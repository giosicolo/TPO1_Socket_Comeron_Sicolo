package ServidorCentral;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ServidorCentral {
    
    private static String IP_SH;
    private static String IP_SPC;
    private static int PUERTO_SC;
    private static int PUERTO_SH;
    private static int PUERTO_SPC;
    private static ConcurrentHashMap<String, String> historialHoroscopo;
    private static ConcurrentHashMap<String, String> historialPronosticoClima;
    
    public static void main(String[] args) {
        ServerSocket socketServerCentral; 
        try {
            // Se leen desde archivo los datos del puerto SC, ip Servidor Horoscopo y Servidor Pronostico Clima, asi como tambien sus puertos
            final String CONFIG_FILE_PATH = "config_server.txt";
            BufferedReader configReader = new BufferedReader(new FileReader(CONFIG_FILE_PATH));    
            PUERTO_SC = Integer.parseInt(configReader.readLine());
            PUERTO_SH = Integer.parseInt(configReader.readLine());
            PUERTO_SPC = Integer.parseInt(configReader.readLine());
            IP_SH = configReader.readLine();
            IP_SPC = configReader.readLine();
            configReader.close();
            
            // Con el puerto propio, se instancia el socket del SC
            System.out.println("ServidorCentral> Iniciando ServidorCentral...");
            socketServerCentral = new ServerSocket(PUERTO_SC);

            // Se crean las caches para almacenar consultas que vayan realizandose
            historialHoroscopo = new ConcurrentHashMap<String, String>();
            historialPronosticoClima = new ConcurrentHashMap<String, String>();
            int idSesion = 1;
            System.out.println("ServidorCentral> ServidorCentral iniciado.");
            
            // SC esta en permanente escucha de solicitudes. Ante un arribo, lanza un thread que llevara a cabo la tarea
            while (true) {
                Socket socketCliente;
                System.out.println("ServidorCentral> Esperando solicitudes de sesion...");
                socketCliente = socketServerCentral.accept(); // Se bloquea hasta nuevo arribo
                System.out.println("ServidorCentral> Nueva solicitud de sesion recibida.");
                // Lanza el thread, con los datos necesarios para que el mismo lleve adelante el trabajo
                new ServidorCentralHilo(socketCliente, IP_SH, IP_SPC, PUERTO_SH, PUERTO_SPC, historialHoroscopo, historialPronosticoClima, idSesion).start();
                idSesion++;
            }
        } catch (IOException ex) {
                System.err.println("ServidorCentral> Error: " +ex.getMessage());
            }
    }
}