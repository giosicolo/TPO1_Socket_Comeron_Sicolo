package ServidorCentral;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
            this.idSesion = unIdSesion;
            
        try {
            this.entrada = new DataInputStream(socketCliente.getInputStream());
            this.salida = new DataOutputStream(socketCliente.getOutputStream());
        } catch (IOException ex) {
            System.err.println("ServidorCentral> Error: "+ex.getMessage());
        }
    }
    
    @Override
    public void run() {
        try {
            String consultaCliente = "";
            String consultaHoroscopo;
            String consultaPronosticoClima;
            String respuestaHoroscopo;
            String respuestaPronosticoClima;
            String respuestaConsulta;
        
            while(!(consultaCliente.equalsIgnoreCase("exit"))) {
                consultaCliente = entrada.readUTF();
                System.out.println("ServidorCentral> Atendiendo consulta de Cliente "+idSesion);
                
                if (consultaCliente.isBlank() || consultaCliente.isEmpty()) 
                    respuestaConsulta = "ServidorCentral> Error: Formato de consulta no valido.";
                else { 
                    if (consultaCliente.matches("[a-zA-Z]+ \\d{2}/\\d{2}/\\d{4}")) {
                        consultaHoroscopo = consultaCliente.split(" ")[0].toLowerCase();
                        consultaPronosticoClima = consultaCliente.split(" ")[1];
                        
                        Socket socketClienteSH = null;
                        DataInputStream entradaSH = null;
                        DataOutputStream salidaSH;
                        boolean peticionSH = false;
                        
                        if (validarConsultaHoroscopo(consultaHoroscopo)) {
                            respuestaHoroscopo = historialHoroscopo.get(consultaHoroscopo);
                            if (respuestaHoroscopo == null) {
                                socketClienteSH = new Socket(IP, PUERTO_SH);
                                
                                entradaSH = new DataInputStream(socketClienteSH.getInputStream());
                                salidaSH = new DataOutputStream(socketClienteSH.getOutputStream());
                                
                                salidaSH.writeUTF(consultaHoroscopo);
                                peticionSH = true;
                            } 
                        }
                        else 
                            respuestaHoroscopo = "Signo no valido";
                        
                        Socket socketClienteSPC = null;
                        DataInputStream entradaSPC = null;
                        DataOutputStream salidaSPC;
                        boolean peticionSPC = false;
                        
                        if (validarConsultaPronosticoClima(consultaPronosticoClima)) {
                            respuestaPronosticoClima = historialPronosticoClima.get(consultaPronosticoClima);
                            if (respuestaPronosticoClima == null) {
                                socketClienteSPC = new Socket(IP, PUERTO_SPC);
                                
                                entradaSPC = new DataInputStream(socketClienteSPC.getInputStream());
                                salidaSPC = new DataOutputStream(socketClienteSPC.getOutputStream());
                                
                                salidaSPC.writeUTF(consultaPronosticoClima);
                                peticionSPC = true;
                            }
                        }
                        else 
                            respuestaPronosticoClima = "Fecha no valida";
                        
                        if (peticionSH) {
                            respuestaHoroscopo = entradaSH.readUTF();
                            historialHoroscopo.put(consultaHoroscopo, respuestaHoroscopo);
                            socketClienteSH.close();
                        }
                        
                        if (peticionSPC) {
                            respuestaPronosticoClima = entradaSPC.readUTF();
                            historialPronosticoClima.put(consultaPronosticoClima, respuestaPronosticoClima);
                            socketClienteSPC.close();
                        }
                        
                        respuestaConsulta = "Horoscopo: "+respuestaHoroscopo+"\n"
                                          + "Pronostico Clima: "+respuestaPronosticoClima;
                    }
                    else {
                        if (consultaCliente.equalsIgnoreCase("exit")) 
                            respuestaConsulta = "ServidorCentral> Adios Cliente "+idSesion;
                        else
                            respuestaConsulta = "ServidorCentral> Error: Formato de consulta no valido.";
                    }
                }
                salida.writeUTF(respuestaConsulta);
                System.out.println("ServidorCentral> Respuesta enviada a Cliente "+idSesion);
            }
        socketCliente.close();
         System.out.println("ServidorCentral> Finalizando conexion con Cliente "+idSesion);
        } catch (IOException ex) {
            System.err.println("ServidorCentral> Error: "+ex.getMessage());
        }
    }
    
    private boolean validarConsultaHoroscopo(String signoHoroscopo) {
        String[] signos = {"aries", "tauro", "geminis", "cancer", "leo", "virgo", "libra", "escorpio", 
                           "sagitario", "capricornio", "acuario", "piscis"};
        boolean esSignoValido = false;
        
        int i = 0;
        while ((i < signos.length) && (!esSignoValido)) {
            if (signos[i].equals(signoHoroscopo)) 
                esSignoValido = true;
            i++;
        }
        return esSignoValido;
    }
    
    private boolean validarConsultaPronosticoClima(String fechaPronosticoClima) {
        boolean esFechaValida = true;
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        formatoFecha.setLenient(false);
        
        try {
            formatoFecha.parse(fechaPronosticoClima);
        } catch (ParseException e) {
            esFechaValida = false;
        }
        return esFechaValida;
    }
}