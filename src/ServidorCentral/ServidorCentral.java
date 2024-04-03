import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ServidorCentral {
    
    private static final String IP = "localhost";
    private static final int PUERTO_SC = 5000;
    private static final int PUERTO_SH = 6000;
    private static final int PUERTO_SPC = 7000;
    private static ConcurrentHashMap<String, String> historialHoroscopo;
    private static ConcurrentHashMap<String, String> historialPronosticoClima;
    
    public static void main(String[] args) {
        ServerSocket socketServerCentral;
        try {
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