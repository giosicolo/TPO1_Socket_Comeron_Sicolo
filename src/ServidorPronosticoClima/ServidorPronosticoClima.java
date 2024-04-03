import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorPronosticoClima {
    
    private static final int PUERTO_SPC = 7000;
    
    public static void main(String[] args) {
    ServerSocket socketServerPronosticoClima;
        try {
            System.out.println("ServidorPronosticoClima> Iniciando ServidorPronosticoClima...");
            socketServerPronosticoClima = new ServerSocket(PUERTO_SPC);
            int idSesion = 1;
            System.out.println("ServidorPronosticoClima> ServidorPronosticoClima iniciado.");
            
            while(true) {
                Socket socketCliente;
                System.out.println("ServidorPronosticoClima> Esperando solicitudes de sesion...");
                socketCliente = socketServerPronosticoClima.accept();
                System.out.println("ServidorPronosticoClima> Nueva solicitud de sesion recibida.");
                new ServidorPronosticoClimaHilo(socketCliente, idSesion).start();
                idSesion++;
            }
        } catch (IOException ex) {
            System.err.println("ServidorPronosticoClima> Error: " +ex.getMessage());
        }    
    }
}