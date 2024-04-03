import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

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
            entrada.readUTF();
            System.out.println("ServidorPronosticoClima> Atendiendo consulta de Cliente "+idSesion);
            String resultadoPronosticoClima = prediccionPronosticoClima();
            salida.writeUTF(resultadoPronosticoClima);
            System.out.println("ServidorPronosticoClima> Respuesta enviada a Cliente "+idSesion);
            socketCliente.close();
            System.out.println("ServidorPronosticoClima> Finalizando conexion con Cliente "+idSesion);
        } catch (IOException ex) {
            System.err.println("ServidorPronosticoClima> Error: "+ex.getMessage());
        }
    }
    
    private String prediccionPronosticoClima() {
        String [] predicciones = {"Despejado", "Nublado", "Precipitaciones", "Viento"};
        String resultado;
        ArrayList<String> listaPredicionesPronosticoClima = new ArrayList<String>();
        
        Collections.addAll(listaPredicionesPronosticoClima, predicciones);
        Collections.shuffle(listaPredicionesPronosticoClima);
        resultado = listaPredicionesPronosticoClima.get(0);
        
        return resultado;
    }
}
