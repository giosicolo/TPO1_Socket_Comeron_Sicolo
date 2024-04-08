package ServidorPronosticoClima;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class ServidorPronosticoClimaHilo extends Thread{
    
    private Socket socketCliente;
    private int idSesion;
    private DataInputStream entrada;
    private DataOutputStream salida;
    
    public ServidorPronosticoClimaHilo(Socket sc, int unIdSesion) {
        this.socketCliente = sc;
        this.idSesion = unIdSesion;

        // Buffers que leen y escriben desde/en el socket creado por el Cliente
        try {
            this.entrada = new DataInputStream(socketCliente.getInputStream());
            this.salida = new DataOutputStream(socketCliente.getOutputStream());
        } catch (IOException ex) {
            System.err.println("ServidorPronosticoClima> Error: "+ex.getMessage());
        }
    }
    
    // Comportamiento que realiza todo thread de tipo Servidor Pronostico Clima
    @Override
    public void run() {
        try {
            String diaConsulta = entrada.readUTF(); // Permanece bloqueado hasta poder leer la fecha desde el buffer
            System.out.println("ServidorPronosticoClima> Atendiendo consulta de Cliente "+idSesion);
            String resultadoPronosticoClima = prediccionPronosticoClima(diaConsulta); // Genera la prediccion del clima para esa fecha
            salida.writeUTF(resultadoPronosticoClima); // Envia la respuesta al SC
            System.out.println("ServidorPronosticoClima> Respuesta enviada a Cliente "+idSesion);
            socketCliente.close(); // Por ultimo, cierra el socket y el hilo termina su ciclo de vida
            System.out.println("ServidorPronosticoClima> Finalizando conexion con Cliente "+idSesion);
        } catch (IOException ex) {
            System.err.println("ServidorPronosticoClima> Error: "+ex.getMessage());
        }
    }
    
    // Metodo que, dada una fecha, genera una prediccion aleatoria de como sera el clima y su temperatura.
    private String prediccionPronosticoClima(String diaConsulta) {
        String [] predicciones =  {"Soleado", "Lluvioso", "Nublado", "Ventoso", "Tormentoso", "Neblina", "Templado", "Nevado"};
        String resultado, predSelecc;
        ArrayList<String> listaPredicionesPronosticoClima = new ArrayList<String>();
        
        Collections.addAll(listaPredicionesPronosticoClima, predicciones);
        Collections.shuffle(listaPredicionesPronosticoClima);

        predSelecc = listaPredicionesPronosticoClima.get(0);

        resultado = predSelecc + " - Con una temperatura de " + obtenerTemperaturaConsulta(diaConsulta) + "° Grados";
        
        return resultado;
    }

    // Metodo que, dependiendo de la estacion del año (mes), devuelve en forma aleatoria una temperatura para dicha fecha
    private int obtenerTemperaturaConsulta(String fecha) {
        String[] dmy = fecha.split("/");
        int mes = Integer.parseInt(dmy[1]);
        Random rd = new Random();
        int temp = 0;

        if (mes >= 1 && mes < 4) {
            temp = (15 + rd.nextInt(30));
        } else {
            if ((mes >= 4 && mes < 7) || (mes >= 9 && mes < 13)) {
                temp = (5 + rd.nextInt(25));   
            } else {
                temp = (1 + rd.nextInt(19));
            }
        }
        return temp;
    }
}