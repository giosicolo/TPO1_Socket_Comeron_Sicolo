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
        try {
            this.entrada = new DataInputStream(socketCliente.getInputStream());
            this.salida = new DataOutputStream(socketCliente.getOutputStream());
        } catch (IOException ex) {
            System.err.println("ServidorPronosticoClima> Error: "+ex.getMessage());
        }
    }
    
    @Override
    public void run() {
        try {
            String diaConsulta = entrada.readUTF();
            System.out.println("ServidorPronosticoClima> Atendiendo consulta de Cliente "+idSesion);
            String resultadoPronosticoClima = prediccionPronosticoClima(diaConsulta);
            salida.writeUTF(resultadoPronosticoClima);
            System.out.println("ServidorPronosticoClima> Respuesta enviada a Cliente "+idSesion);
            socketCliente.close();
            System.out.println("ServidorPronosticoClima> Finalizando conexion con Cliente "+idSesion);
        } catch (IOException ex) {
            System.err.println("ServidorPronosticoClima> Error: "+ex.getMessage());
        }
    }
    
    private String prediccionPronosticoClima(String diaConsulta) {
        String [] predicciones =  {"Soleado", "Lluvioso", "Nublado", "Ventoso", "Tormentoso", "Neblina", "Templado", "Nevado"};
        String resultado, predSelecc;
        ArrayList<String> listaPredicionesPronosticoClima = new ArrayList<String>();
        
        Collections.addAll(listaPredicionesPronosticoClima, predicciones);
        Collections.shuffle(listaPredicionesPronosticoClima);

        predSelecc= listaPredicionesPronosticoClima.get(0);

        resultado = predSelecc + " - Con una temperatura de " + obtenerTemperaturaConsulta(diaConsulta) + "° Grados";
        
        return resultado;
    }

    private int obtenerTemperaturaConsulta(String fecha){
        String[] dmy = fecha.split("/");
        int mes= Integer.parseInt(dmy[1]);
        Random rd = new Random();
        int temp=0 ;

        if (mes>=1 && mes<4){
            temp= (15 + rd.nextInt(30));
        }else {
            if ((mes>=4 && mes<7) || (mes>=9 && mes<13))  {
                temp= (5 + rd.nextInt(25));   
            }else{
                temp= (1 +rd.nextInt(19));
            }
        }
    return temp;
    }


}
