import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

public class ServidorHoroscopoHilo extends Thread {
    
    private Socket socketCliente;
    private int idSesion;
    private DataInputStream entrada;
    private DataOutputStream salida;
    
    public ServidorHoroscopoHilo(Socket sc, int unIdSesion) {
        this.socketCliente = sc;
        this.idSesion = unIdSesion;
        try {
            this.entrada = new DataInputStream(socketCliente.getInputStream());
            this.salida = new DataOutputStream(socketCliente.getOutputStream());
        } catch (IOException ex) {
            System.err.println("ServidorHoroscopo> Error: "+ex.getMessage());
        }
    }
    
    @Override
    public void run() {
        try {
            entrada.readUTF();
            System.out.println("ServidorHoroscopo> Atendiendo consulta de Cliente "+idSesion);
            String respuestaHoroscopo = prediccionHoroscopo();
            salida.writeUTF(respuestaHoroscopo);
            System.out.println("ServidorHoroscopo> Respuesta enviada a Cliente "+idSesion);
            socketCliente.close();
            System.out.println("ServidorHoroscopo> Finalizando conexion con Cliente "+idSesion);
        } catch (IOException ex) {
            System.err.println("ServidorHoroscopo> Error: "+ex.getMessage());
        }
    }
    
    private String prediccionHoroscopo() {
        String[] predicciones = {
            "Vas a superar esto. No durará para siempre. Lo que vives en este momento es temporal. El dolor que sientes, en unos meses se convertirá solo en un recuerdo",
            "Es verdad, todo apesta en este momento, pero te sorprenderás las grandes cosas que te están esperando. Todo lo que has soñado está a tu alcance. Continúa avanzando hacia esa vida de ensueño que deseas.",
            "La mejor venganza es aprender a amarte a ti misma. No te gastes por demostrarlo a los demás. Toma la vida lo mejor posible ¡no importa si te lo reconocen o no los demás!",
            "Mereces ser feliz, nunca permitas que te digan lo contrario. Tu corazón está lleno de bondad y eso es muy difícil de encontrar. Somos afortunados quienes podemos compartir una tarde contigo, lo mejor sería que tú nos mantengas cerca el resto de tu vida",
            "Verás que todo tu esfuerzo será recompensado. Estás trabajando muy duro para alcanzar tus metas; aprende a ser paciente. Llegará lo que tanto anhelas",
            "Perder una batalla, no significa perder la guerra. No permitas que ningún fracaso te aleje de tus sueños, Un mal día no significa que toda tu vida apesta. Si te rompen el corazón, no es sinónimo de renunciar al amor.",
            "Eres hermosa tanto por dentro como por fuera; mereces una relación sana llena de amor. Si alguien te hace dudar de ello, no vale la pena que desperdicies ni un segundo de tu tiempo en él.",
            "Has evolucionado, ya no eres la misma persona que eras ayer. Lograste florecer, así que deja de preocuparte por tus errores del pasado y enfócate en construir tu futuro",
            "Aprende a amarte, deja de mirarte de esa forma poco amorosa. Ni eres una carga para los demás, ni estás llena de tantos defectos como lo piensas. Quiérete mucho",
            "Deja la frustración a un lado, ¡mira todo lo que has avanzado rumbo a tus metas! Siéntete orgullosa de tus logros y deja de criticarte tanto. Lo estás haciendo perfecto.",
            "Tu fuerza interior es superior a lo que te imaginas. Mírate desde otra perspectiva para que puedas apreciar tus logros ¡has realizado todo lo que una vez soñaste!",
            "Sal a buscar tus sueños, persigue tu felicidad, yo estoy aquí para apoyarte en todo momento, no tienes porqué sentirte sola, porque voy sosteniendo tu mano, acompañándote en todo el camino"
            };
        String resultado;
        ArrayList<String> listaPredicionesHoroscopo = new ArrayList<String>();
        
        Collections.addAll(listaPredicionesHoroscopo, predicciones);
        Collections.shuffle(listaPredicionesHoroscopo);
        resultado = listaPredicionesHoroscopo.get(0);
        
        return resultado;
    }
}
