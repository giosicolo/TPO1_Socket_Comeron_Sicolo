package Sockets;

import java.io.*;
import java.net.*;

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
                System.out.println("Ingrese accion a realizar: ");
                consulta = consola.readLine();
                
                salida.writeUTF(consulta);
                respuesta = entrada.readUTF();
                System.out.println("Respuesta del Servidor: "+respuesta);
            } 
            socketCliente.close();
            consola.close();
        } catch (IOException ex) {
            System.err.println("Error: " +ex.getMessage());
        }
    }
}