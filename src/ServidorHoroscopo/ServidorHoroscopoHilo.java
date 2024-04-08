package ServidorHoroscopo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class ServidorHoroscopoHilo extends Thread {
    
    private Socket socketCliente;
    private int idSesion;
    private DataInputStream entrada;
    private DataOutputStream salida;
    
    public ServidorHoroscopoHilo(Socket sc, int unIdSesion) {
        this.socketCliente = sc;
        this.idSesion = unIdSesion;

        // Buffers que leen y escriben desde/en el socket creado por el Cliente
        try {
            this.entrada = new DataInputStream(socketCliente.getInputStream());
            this.salida = new DataOutputStream(socketCliente.getOutputStream());
        } catch (IOException ex) {
            System.err.println("ServidorHoroscopo> Error: "+ex.getMessage());
        }
    }
    
    // Comportamiento que realiza todo thread de tipo Servidor Horoscopo
    @Override
    public void run() {
        try {
            String signo = entrada.readUTF(); // Permanece bloqueado hasta poder leer el signo desde el buffer
            System.out.println("ServidorHoroscopo> Atendiendo consulta de Cliente "+idSesion);
            String respuestaHoroscopo = prediccionHoroscopo(signo); // Genera la prediccion del horoscopo para ese signo
            salida.writeUTF(respuestaHoroscopo); // Envia la respuesta al SC
            System.out.println("ServidorHoroscopo> Respuesta enviada a Cliente "+idSesion);
            socketCliente.close(); // Por ultimo, cierra el socket y el hilo termina su ciclo de vida
            System.out.println("ServidorHoroscopo> Finalizando conexion con Cliente "+idSesion);
        } catch (IOException ex) {
            System.err.println("ServidorHoroscopo> Error: "+ex.getMessage());
        }
    }
    
    // Metodo que, a partir frases de horoscopo, retorna una prediccion aleatoria para un signo 
    private String prediccionHoroscopo(String signo) {
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
            "Sal a buscar tus sueños, persigue tu felicidad, yo estoy aquí para apoyarte en todo momento, no tienes porqué sentirte sola, porque voy sosteniendo tu mano, acompañándote en todo el camino",
            "Hoy es un buen día para expresar gratitud y apreciar las pequeñas cosas de la vida.",
            "Toma un momento para meditar y encontrar paz interior en medio del ajetreo diario.",
            "Practica la empatía y el cuidado al interactuar con los demás, eso creará conexiones genuinas.",
            "Haz una pausa para reconocer tus logros y celebrar tus éxitos, por pequeños que sean.",
            "Aprovecha el día para aprender algo nuevo y expandir tus conocimientos y habilidades.",
            "Sal de tu zona de confort y desafía tus límites hoy, te sorprenderás de lo que eres capaz.",
            "Dedica tiempo a cuidar tu cuerpo y mente con ejercicios de relajación y autocuidado.",
            "Comparte tu luz con el mundo siendo amable y generoso, eso hará que tu día sea más brillante.",
            "Concéntrate en el presente y practica la atención plena, eso te ayudará a encontrar calma y claridad.",
            "Haz una lista de tus metas y prioridades para el día y trabaja hacia su logro con determinación.",
            "Establece límites saludables y aprende a decir 'no' cuando sea necesario para cuidar tu bienestar.",
            "Busca la belleza en tu entorno y encuentra inspiración en las pequeñas cosas que te rodean.",
            "Conéctate con la naturaleza y disfruta de un momento al aire libre, eso revitalizará tu espíritu.",
            "Haz un acto de bondad sin esperar nada a cambio, eso traerá alegría tanto a ti como a los demás.",
            "Organiza tu espacio y elimina el desorden para crear un ambiente tranquilo y armonioso.",
            "Practica la gratitud escribiendo en un diario tres cosas por las que estás agradecido hoy.",
            "Escucha a tu intuición y sigue tu corazón al tomar decisiones importantes en tu vida.",
            "Comparte tu tiempo y talento con los demás, eso fortalecerá tus conexiones y relaciones.",
            "Planifica un momento para relajarte y recargar energías, permitiéndote desconectar del estrés diario.",
            "Crea una rutina de autocuidado que incluya actividades que te nutran física, mental y emocionalmente.",
            "Hoy, la luna en tu signo te invita a conectarte con tus emociones y confiar en tu intuición.",
            "La alineación planetaria te anima a buscar la armonía en tus relaciones más cercanas hoy.",
            "Mercurio retrógrado sugiere precaución en la comunicación; piensa antes de hablar o enviar mensajes.",
            "Venus en trígono con Marte promueve la pasión y el romance; es un buen momento para expresar amor.",
            "El sol en conjunción con Júpiter te brinda optimismo y confianza en tus capacidades para alcanzar tus metas.",
            "Saturno en tu signo te insta a asumir responsabilidades y comprometerte con tus objetivos a largo plazo.",
            "La energía de Marte te impulsa a tomar acción y perseguir tus ambiciones con determinación y valentía.",
            "Neptuno en oposición a tu signo te aconseja mantener los pies en la tierra y evitar la ilusión o el autoengaño.",
            "Plutón retrógrado te invita a explorar tus aspectos más profundos y a liberarte de patrones limitantes.",
            "La cuadratura entre Urano y tu signo indica la necesidad de flexibilidad y adaptabilidad ante los cambios repentinos en tu entorno.",
        };
        String resultado;
        ArrayList<String> listaPredicionesHoroscopo = new ArrayList<String>();
        Random random = new Random();
        int ventana = random.nextInt(signo.length());

        Collections.addAll(listaPredicionesHoroscopo, predicciones);
        Collections.shuffle(listaPredicionesHoroscopo);
        
        resultado = listaPredicionesHoroscopo.get(0 + ventana);
        
        return resultado;
    }
}