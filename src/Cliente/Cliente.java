import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Cliente {
    
    private static final String IP = "localhost";
    private static final int PUERTO_SC = 5000;
    private static Socket socketCliente;
    private static DataInputStream entrada;
    private static DataOutputStream salida;
    private static BufferedReader consola;
    
    public static void main(String[] args) {
        try {
            socketCliente = new Socket(IP, PUERTO_SC);
            entrada = new DataInputStream(socketCliente.getInputStream());
            salida = new DataOutputStream(socketCliente.getOutputStream());
            consola = new BufferedReader(new InputStreamReader(System.in));
            
            String consulta = "";
            String respuesta;
            
            while(!consulta.equalsIgnoreCase("exit")) {
                System.out.println("Cliente> La consulta debe seguir el siguiente formato: signoHoroscopo fechaPronosticoClima");
                System.out.println("Cliente> Ejemplo: tauro 02/04/2024");
                System.out.print("Cliente> Ingrese su consulta o exit: ");
                consulta = consola.readLine();
                
                salida.writeUTF(consulta);
                
                System.out.println("Cliente> Esperando por el resultado... ");
                respuesta = entrada.readUTF();
                System.out.println("Cliente> Respuesta "+respuesta);
                System.out.println("");
            } 
            socketCliente.close();
            consola.close();
            System.out.println("Cliente> Finalizando la conexion.");
        } catch (IOException ex) {
            System.err.println("Cliente> Error: " +ex.getMessage());
        }
    }
}