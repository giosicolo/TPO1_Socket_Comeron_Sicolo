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
            // Se leen desde archivo los datos del puerto SH
            final String CONFIG_FILE_PATH = "config_serverHoroscopo.txt";
            BufferedReader configReader = new BufferedReader(new FileReader(CONFIG_FILE_PATH));
            PUERTO_SH = Integer.parseInt(configReader.readLine());
            configReader.close();
            
            // Con el puerto propio, se instancia el socket del SH
            System.out.println("ServidorHoroscopo> Iniciando ServidorHoroscopo...");
            socketServerHoroscopo = new ServerSocket(PUERTO_SH);
            int idSesion = 1;
            System.out.println("ServidorHoroscopo> ServidorHoroscopo iniciado.");
            
            // SH esta en permanente escucha de solicitudes. Ante un arribo, lanza un thread que llevara a cabo la tarea
            while (true) {
                Socket socketCliente;
                System.out.println("ServidorHoroscopo> Esperando solicitudes de sesion...");
                socketCliente = socketServerHoroscopo.accept(); // Se bloquea hasta nuevo arribo
                System.out.println("ServidorHoroscopo> Nueva solicitud de sesion recibida.");
                // Lanza el thread, con los datos necesarios para que el mismo lleve adelante el trabajo
                new ServidorHoroscopoHilo(socketCliente, idSesion).start();
                idSesion++;
            }
        } catch (IOException ex) {
            System.err.println("ServidorHoroscopo> Error: " +ex.getMessage());
        }
    }
}