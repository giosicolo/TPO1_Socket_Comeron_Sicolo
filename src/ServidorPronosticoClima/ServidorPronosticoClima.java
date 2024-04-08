package ServidorPronosticoClima;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorPronosticoClima {
    
    private static int PUERTO_SPC;
    
    public static void main(String[] args) {
    ServerSocket socketServerPronosticoClima;
        try {
            // Se leen desde archivo los datos del puerto SP
            final String CONFIG_FILE_PATH = "config_serverClima.txt";
            BufferedReader configReader = new BufferedReader(new FileReader(CONFIG_FILE_PATH));
            PUERTO_SPC = Integer.parseInt(configReader.readLine());
            configReader.close();
            
            // Con el puerto propio, se instancia el socket del SP
            System.out.println("ServidorPronosticoClima> Iniciando ServidorPronosticoClima...");
            socketServerPronosticoClima = new ServerSocket(PUERTO_SPC);
            int idSesion = 1;
            System.out.println("ServidorPronosticoClima> ServidorPronosticoClima iniciado.");
            
            // SP esta en permanente escucha de solicitudes. Ante un arribo, lanza un thread que llevara a cabo la tarea
            while (true) {
                Socket socketCliente;
                System.out.println("ServidorPronosticoClima> Esperando solicitudes de sesion...");
                socketCliente = socketServerPronosticoClima.accept(); // Se bloquea hasta nuevo arribo
                System.out.println("ServidorPronosticoClima> Nueva solicitud de sesion recibida.");
                // Lanza el thread, con los datos necesarios para que el mismo lleve adelante el trabajo
                new ServidorPronosticoClimaHilo(socketCliente, idSesion).start();
                idSesion++;
            }
        } catch (IOException ex) {
            System.err.println("ServidorPronosticoClima> Error: " +ex.getMessage());
        }    
    }
}