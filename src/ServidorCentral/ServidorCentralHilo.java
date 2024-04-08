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
    private static String IP_SH;
    private static String IP_SPC;
    private static int PUERTO_SH;
    private static int PUERTO_SPC;
    private ConcurrentHashMap<String, String> historialHoroscopo;
    private ConcurrentHashMap<String, String> historialPronosticoClima;
    private int idSesion;
    private DataInputStream entrada;
    private DataOutputStream salida;
    
    public ServidorCentralHilo(Socket sc, String direccionIP_SH, String direccionIP_SPC, int puertoSH, int puertoSPC, ConcurrentHashMap<String, String> historialH, ConcurrentHashMap<String, String> historialPC, int unIdSesion) {
            this.socketCliente = sc;
            this.IP_SH = direccionIP_SH;
            this.IP_SPC = direccionIP_SPC;
            this.PUERTO_SH = puertoSH;
            this.PUERTO_SPC = puertoSPC;
            this.historialHoroscopo = historialH;
            this.historialPronosticoClima = historialPC;
            this.idSesion = unIdSesion;

        // Buffers que leen y escriben desde/en el socket creado por el Cliente    
        try {
            this.entrada = new DataInputStream(socketCliente.getInputStream());
            this.salida = new DataOutputStream(socketCliente.getOutputStream());
        } catch (IOException ex) {
            System.err.println("ServidorCentral> Error: "+ex.getMessage());
        }
    }
    
    // Comportamiento que realiza todo thread de tipo Servidor Central
    @Override
    public void run() {
        try {
            String consultaCliente = "";
            String consultaHoroscopo;
            String consultaPronosticoClima;
            String respuestaHoroscopo;
            String respuestaPronosticoClima;
            String respuestaConsulta;

            // El hilo permanece con vida hasta que el cliente decide irse u ocurre una excepcion
            while (!(consultaCliente.equalsIgnoreCase("exit"))) {
                consultaCliente = entrada.readUTF(); // Permanece bloqueado hasta poder leer la consulta desde el buffer
                System.out.println("ServidorCentral> Atendiendo consulta de Cliente "+idSesion);
                
                // Se verifica que la consulta ingresada mantenga el formato establecido: signo fecha (dd/mm/aaaa)
                if (consultaCliente.isBlank() || consultaCliente.isEmpty()) 
                    respuestaConsulta = "ServidorCentral> Error: Formato de consulta no valido.";
                else { 
                    if (consultaCliente.matches("[a-zA-Z]+ \\d{2}/\\d{2}/\\d{4}")) {
                        // Si cumple con el patron (esqueleto de la consulta), se separan los datos de la misma
                        consultaHoroscopo = consultaCliente.split(" ")[0].toLowerCase();
                        consultaPronosticoClima = consultaCliente.split(" ")[1];
                        
                        Socket socketClienteSH = null;
                        DataInputStream entradaSH = null;
                        DataOutputStream salidaSH;
                        // Var. para enviar las dos consultas sin tener que bloquearse y esperar por respuesta  
                        boolean peticionSH = false; // Indica si la peticion al SH fue realizada    
                        
                        // Se valida que el signo ingresado sea correcto y se busca en la cache de horoscopo
                        if (validarConsultaHoroscopo(consultaHoroscopo)) {
                            respuestaHoroscopo = historialHoroscopo.get(consultaHoroscopo);
                            // Si no esta en la cache, se traslada la consulta al servidor de horoscopo
                            if (respuestaHoroscopo == null) {
                                // Creamos el socket con su direccion ip y puerto asociado
                                socketClienteSH = new Socket(IP_SH, PUERTO_SH);
                                
                                entradaSH = new DataInputStream(socketClienteSH.getInputStream());
                                salidaSH = new DataOutputStream(socketClienteSH.getOutputStream());
                                
                                // Se envia la consulta
                                salidaSH.writeUTF(consultaHoroscopo);
                                peticionSH = true;
                            } 
                        }
                        else 
                            respuestaHoroscopo = "Signo no valido";
                        
                        Socket socketClienteSPC = null;
                        DataInputStream entradaSPC = null;
                        DataOutputStream salidaSPC;
                        // Var. para enviar las dos consultas sin tener que bloquearse y esperar por respuesta
                        boolean peticionSPC = false; // Indica si la peticion al SPC fue realizada    
                        
                        // Se valida que la fecha ingresada sea correcta y se busca en la cache de pronostico clima
                        if (validarConsultaPronosticoClima(consultaPronosticoClima)) {
                            respuestaPronosticoClima = historialPronosticoClima.get(consultaPronosticoClima);
                            // Si no esta en la cache, se traslada la consulta al servidor de pronostico clima
                            if (respuestaPronosticoClima == null) {
                                // Creamos el socket con su direccion ip y puerto asociado
                                socketClienteSPC = new Socket(IP_SPC, PUERTO_SPC);
                                
                                entradaSPC = new DataInputStream(socketClienteSPC.getInputStream());
                                salidaSPC = new DataOutputStream(socketClienteSPC.getOutputStream());

                                // Se envia la consulta
                                salidaSPC.writeUTF(consultaPronosticoClima);
                                peticionSPC = true;
                            }
                        }
                        else 
                            respuestaPronosticoClima = "Fecha no valida";
                        
                        // Se espera por resultados de los servidores y agrega nueva entrada a la cache correspondiente (si alguna consulta fue realizada)
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
        // Finalmente, se cierra el socket    
        socketCliente.close();
        System.out.println("ServidorCentral> Finalizando conexion con Cliente "+idSesion);
        } catch (IOException ex) {
            System.err.println("ServidorCentral> Error: "+ex.getMessage());
        }
    }
    
    // Metodo que comprueba si el signo ingresado por el usuario es valido
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
    
    // Metodo que comprueba si la fecha ingresada por el usuario es valida (rango de valores de fechas, meses)
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