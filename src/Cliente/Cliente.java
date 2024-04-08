package Cliente;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Cliente {
    
    private static String IP_SC;
    private static int PUERTO_SC;
    private static Socket socketCliente;
    private static DataInputStream entrada;
    private static DataOutputStream salida;
    private static BufferedReader consola;
    
    public static void main(String[] args) {
        try {
            // Se lee desde archivo los datos de ip y puerto del SC
            final String CONFIG_FILE_PATH = "config_cliente.txt";
            BufferedReader configReader = new BufferedReader(new FileReader(CONFIG_FILE_PATH));
            PUERTO_SC = Integer.parseInt(configReader.readLine());
            IP_SC = configReader.readLine();
            configReader.close();

            // Se crea el socket para comunicarse con el SC
            socketCliente = new Socket(IP_SC, PUERTO_SC);
            entrada = new DataInputStream(socketCliente.getInputStream());
            salida = new DataOutputStream(socketCliente.getOutputStream());
            consola = new BufferedReader(new InputStreamReader(System.in));
            
            String consulta = "";
            String respuesta;
            
            // Se ingresa por consola la consulta, respetando el formato indicado
            while (!consulta.equalsIgnoreCase("exit")) {
                System.out.println("Cliente> La consulta debe seguir el siguiente formato: signoHoroscopo fechaPronosticoClima");
                System.out.println("Cliente> Ejemplo: tauro 02/04/2024");
                System.out.print("Cliente> Ingrese su consulta o exit: ");
                consulta = consola.readLine();
                
                // Envio de la consulta ingresada con destino al SC 
                salida.writeUTF(consulta);
                
                // Se aguarda por el resultado (en espera)
                System.out.println("Cliente> Esperando por el resultado... ");
                respuesta = entrada.readUTF();
                System.out.println("Cliente> Respuesta "+respuesta);
                System.out.println("");
            } 
            //Finalmente, si el usuario ingresa exit, se cierra el socket y el buffer de leer por consola
            socketCliente.close();
            consola.close();
            System.out.println("Cliente> Finalizando la conexion.");
        } catch (IOException ex) {
            System.err.println("Cliente> Error: " +ex.getMessage());
        }
    }
}