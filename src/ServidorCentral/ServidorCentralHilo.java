package Sockets;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServidorCentralHilo extends Thread {
    
    private Socket socketCliente;
    private static String IP;
    private static int PUERTO_SH;
    private static int PUERTO_SPC;
    private ConcurrentHashMap<String, String> historialHoroscopo;
    private ConcurrentHashMap<String, String> historialPronosticoClima;
    private int idSesion;
    private DataInputStream entrada;
    private DataOutputStream salida;
    
    public ServidorCentralHilo(Socket sc, String direccionIP, int puertoSH, int puertoSPC, ConcurrentHashMap<String, String> historialH, ConcurrentHashMap<String, String> historialPC, int unIdSesion) {
            this.socketCliente = sc;
            this.IP = direccionIP;
            this.PUERTO_SH = puertoSH;
            this.PUERTO_SPC = puertoSPC;
            this.historialHoroscopo = historialH;
            this.historialPronosticoClima = historialPC;
            this.idSesion = idSesion;
            
        try {
            this.entrada = new DataInputStream(socketCliente.getInputStream());
            this.salida = new DataOutputStream(socketCliente.getOutputStream());
        } catch (IOException ex) {
            System.err.println("Error: "+ex.getMessage());
        }
    }
    
    @Override
    public void run() {
        String consultaCliente = "";
        String consultaHoroscopo;
        String consultaPronosticoClima;
        String respuestaHoroscopo;
        String respuestaPronosticoClima;
        String respuestaConsulta = "";
        
        while(!(consultaCliente.equalsIgnoreCase("exit"))) {
            try {
                consultaCliente = entrada.readUTF();
                
                if (consultaCliente.isBlank() || consultaCliente.isEmpty()) 
                    respuestaConsulta = "Error: Formato de consulta no valido.";
                else { 
                    if (consultaCliente.matches("[a-zA-Z]+ \\d{2}/\\d{2}/\\d{4}")) {
                        consultaHoroscopo = consultaCliente.split(" ")[0].toLowerCase();
                        consultaPronosticoClima = consultaCliente.split(" ")[1];
                        
                        Socket socketClienteSH = null;
                        DataInputStream entradaSH = null;
                        DataOutputStream salidaSH;
                        boolean flagSH = false;
                        
                        if (validarConsultaHoroscopo(consultaHoroscopo)) {
                            respuestaHoroscopo = historialHoroscopo.get(consultaHoroscopo);
                            if (respuestaHoroscopo == null) {
                                socketClienteSH = new Socket(IP, PUERTO_SH);
                                
                                entradaSH = new DataInputStream(socketClienteSH.getInputStream());
                                salidaSH = new DataOutputStream(socketClienteSH.getOutputStream());
                                
                                salidaSH.writeUTF(consultaHoroscopo);
                                flagSH = true;
                            } 
                        }
                        else 
                            respuestaHoroscopo = "Signo no valido";
                        
                        Socket socketClienteSPC = null;
                        DataInputStream entradaSPC = null;
                        DataOutputStream salidaSPC;
                        boolean flagSPC = false;
                        
                        if (validarConsultaPronosticoClima(consultaPronosticoClima)) {
                            respuestaPronosticoClima = historialPronosticoClima.get(consultaPronosticoClima);
                            if (respuestaPronosticoClima == null) {
                                socketClienteSPC = new Socket(IP, PUERTO_SPC);
                                
                                entradaSPC = new DataInputStream(socketClienteSPC.getInputStream());
                                salidaSPC = new DataOutputStream(socketClienteSPC.getOutputStream());
                                
                                salidaSPC.writeUTF(consultaPronosticoClima);
                                flagSPC = true;
                            }
                        }
                        else 
                            respuestaPronosticoClima = "Fecha no valida";
                        
                        if (flagSH) {
                            respuestaHoroscopo = entradaSH.readUTF();
                            historialHoroscopo.put(consultaHoroscopo, respuestaHoroscopo);
                            socketClienteSH.close();
                        }
                        
                        if (flagSPC) {
                            respuestaPronosticoClima = entradaSPC.readUTF();
                            historialPronosticoClima.put(consultaPronosticoClima, respuestaPronosticoClima);
                            socketClienteSPC.close();
                        }
                        
                        respuestaConsulta = "Predicciones: [Horoscopo: "+respuestaHoroscopo+"]\n [Pronostico Clima: "+respuestaPronosticoClima+"]";
                    }
                    else {
                        if (consultaCliente.equalsIgnoreCase("exit"))
                            respuestaConsulta = "Adios Cliente "+idSesion;
                        else
                            respuestaConsulta = "Error: Formato de consulta no valido.";
                    }
                }
                salida.writeUTF(respuestaConsulta);
            } catch (IOException ex) {
                System.err.println("Error: "+ex.getMessage());
            }
        }
    }
    
    private boolean validarConsultaHoroscopo(String signoHoroscopo) {
        boolean esSignoValido = false;
        
        return esSignoValido;
    }
    
    private boolean validarConsultaPronosticoClima(String fechaPronosticoClima) {
        boolean esFechaValida = false;
        
        return esFechaValida;
    }
}