package ubu.itig.serverstatus.scanlog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.commons.daemon.*;


/**
 * Clase correspondiente al Daemon ScanLog.
 * 
 * @author David Herrero de la Peña.
 * @author Jonatan Santos Barrios.
 * @version 1.0
 * 
 * */
public class ScanLog implements Daemon{

	/**
	 * 
	 * Timer para controlar el tiempo.
	 * 
	 */
    private static Timer timer = null;

	/**
	 * 
	 * Conexión con la base de datos.
	 * 
	 */
	private static Connection conexion;
	
	/**
	 * 
	 * Nombre de la base de datos.
	 * 
	 */
	private String bd="android_server_status";
	
	/**
	 * 
	 * Nombre del usuario de la base de datos.
	 * 
	 */
	private String user="jonatan";
	
	/**
	 * 
	 * Contraseña de usuario de la base de datos.
	 * 
	 */
	private String password="prueba";
	
	/**
	 * 
	 * Servidor donde se encuantra la base de datos.
	 * 
	 */
	private String server="jdbc:mysql://localhost/"+bd;
	
	/**
	 * 
	 * Lector de buffer.
	 * 
	 */
	private static BufferedReader is;
	
	/**
	 * 
	 * Servidor de socket.
	 * 
	 */
	private ServerSocket serverSocket;
	
	/**
	 * 
	 * Socket de escucha.
	 * 
	 */
	private Socket client;
    
	/**
	 * Crea una tarea repetitiva, .
	 * 
	 */
    private static void asignarTarea() {
        //Obtener el tiempo
    	timer = new Timer();
    	//Configurar la tarea repetitiva
        timer.schedule(new Analisis(conexion, is), 0, 1000);
    }

    
    /**
     * Tareas a realizar al inicializar el demonio.
     * 
     * @param dc DaemonContext Contexto en el que se ejcutara el demonio.
     */
    @Override
    public void init(DaemonContext dc)throws Exception {
    	 try{
     		//Crear la conexion con base de datos
 	    	conexion = DriverManager.getConnection(server,user,password);
 	    	
 	    	//Crear el servidor socket y el socket de escucha
 	    	serverSocket = new ServerSocket(5150);
 	    	client = serverSocket.accept();
 	    	
 	    	//Crear el lector de buffer
 	    	is = new BufferedReader( new InputStreamReader(client.getInputStream()));
 	    	
 	    } catch (Exception e){
 	    	System.out.println(e.getMessage());
 	    }
    }
    
    /**
     * Tareas a realizar al arrancar el demonio.
     * 
     */
	@Override
    public void start() throws Exception {
        //Crear la tarea repetitiva
        asignarTarea();
    }

    /**
     * Tareas a realizar al parar el demonio.
     * 
     */
    @Override
    public void stop() throws Exception {
        //Parar el reloj, y con ello la tarea repetitiva
    	if (timer != null) {
        	timer.cancel();
        }
    	//Cerrar buffer, sockets y conexion.
    	is.close();
        client.close();
        serverSocket.close();
        conexion.close();
    }

    /**
     * Tareas a realizar al destruir el demonio.
     * 
     */
    @Override
    public void destroy() {
    	
    }
    
 }

/**
 * Clase correspondiente a la tarea repetitiva de Analizar el buffer.
 * 
 * @author David Herrero de la Peña.
 * @author Jonatan Santos Barrios.
 * @version 1.0
 *
 */
class Analisis extends TimerTask {
	
	/**
	 * 
	 * Conexion con la base de datos.
	 * 
	 */
	private static Connection conexion;
	
	/**
	 * 
	 * Lector del buffer.
	 * 
	 */
	private BufferedReader is;

	/**
	 * Constructor de la tarea repetitiva.
	 * 
	 * @param conexion Conexion con la base de datos.
	 * @param is Lector de buffer de entrada.
	 */
	public Analisis(Connection conexion, BufferedReader is) {
		Analisis.conexion = conexion;
		this.is = is;
	}

	/**
	 * 
	 * Correr la tarea repetitiva
	 * 
	 */
	@Override
	public void run() {

		try {
			//Linea de entrada
			String inputLine;
			
			//Comprobar si hay lineas que leer
			while (is.ready() && (inputLine = is.readLine())!= "") {
				String linea;
				
				//Obtener la linea
				linea = inputLine.substring(inputLine.indexOf(">") + 1);

				//Analizar la linea
				analizarLinea(linea);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Analiza la linea para comprobar si es uno de los avisos que se deben notificar.
	 * 
	 * @param linea String con la linea del fichero.
	 */
	private void analizarLinea(String linea){

		String mensaje;
		Calendar fecha;
		int urgencia;

		//Obtenemos la fecha de la linea
		fecha = obtenerFecha(linea);

		//Obtener la parte del mensaje de la linea
		mensaje = linea.substring(16);
		mensaje = mensaje.substring(linea.indexOf(":") + 2);

		//Comprobar si es un mensaje de la conexión ssh o de email
		if(linea.indexOf("sshd") > -1){
			//Analizar mensaje ssh
			urgencia = obtenerUrgenciaSSH(mensaje);
			if (urgencia != -1){
				grabarNotificacion(fecha, 0, urgencia, mensaje);
			}

		} else if (linea.indexOf("postfix") > -1) {
			//Analizar mensaje correo
			urgencia = obtenerUrgenciaCorreo(mensaje);
			if (urgencia != -1){
				grabarNotificacion(fecha, 1, urgencia, mensaje);
			}
		}		
	}

	/**
	 * Obtiene la urgencia del mensaje SSH.
	 * 
	 * @param mensaje String con el mensaje del aviso.
	 * @return int Tipo de mensaje: -1 Mensaje no notificable, 0 Intento de robo de claves, 1 Conexión fallida, 2 Conexión con exito.
	 */
	private int obtenerUrgenciaSSH(String mensaje){
		int tipo = -1;

		//Comprobamos si es uno de los mensajes a notificar
		if(mensaje.indexOf("Failed password") > -1){
			tipo = 1;
		} else if (mensaje.indexOf("POSSIBLE BREAK-IN ATTEMPT!") > -1){
			tipo = 0;
		} else if (mensaje.indexOf("Accepted password") > -1){
			tipo = 2;
		}

		return tipo;
	}

	/**
	 * Obtiene la urgencia del mensaje de correo.
	 * 
	 * @param mensaje String con el mensaje del aviso.
	 * @return int Tipo de mensaje: -1 Mensaje no notificable, 0 Conexión con el servidor fallida, 2 Correo enviado con exito.
	 */
	private int obtenerUrgenciaCorreo(String mensaje){
		int tipo = -1;

		//Comprobamos si es uno de los mensajes a notificar
		if(mensaje.indexOf("SASL LOGIN authentication failed: authentication failure") > -1){
			tipo = 0;
		} else if (mensaje.indexOf("status=sent") > -1){
			tipo = 2;
		}

		return tipo;
	}

	/**
	 * Obtiene la fecha y hora de la cadena de texto de la linea del fichero pasada.
	 * 
	 * @param lineaComprobar String con la linea del fichero.
	 * @return Calendar con la fecha y hora que se produjo la linea.
	 */
	private Calendar obtenerFecha(String lineaComprobar) {

		Calendar calendario = Calendar.getInstance();

		//Obtener los datos de la cadena
		String cadenaMes = lineaComprobar.substring(0,3);
		String cadenaDia = lineaComprobar.substring(4,6);
		String cadenaHora = lineaComprobar.substring(7,9);
		String cadenaMinuto = lineaComprobar.substring(10,12);
		String cadenaSegundo = lineaComprobar.substring(13,15);

		//Pasar los datos a integer
		int año = calendario.get(Calendar.YEAR);
		int mes = obtenerMes(cadenaMes);
		int dia;
		//Comprobar si es un dia con un digito o dos
		if(Character.isDigit(cadenaDia.charAt(0))){
			dia = Integer.parseInt(cadenaDia);
		}else{
			String cadenaDiaAux = cadenaDia.substring(1,2);
			dia = Integer.parseInt(cadenaDiaAux);
		}
		int hora = Integer.parseInt(cadenaHora);
		int minuto = Integer.parseInt(cadenaMinuto);
		int segundo = Integer.parseInt(cadenaSegundo);

		//Establecer fecha y hora de la linea
		calendario.set(año, mes, dia, hora, minuto, segundo);

		return calendario;
	}

	/**
	 * Transforma la abreviatura de un mes pasado en el integer correspondiente a dicho mes.
	 * 
	 * @param cadenaMes String Abreviatura del mes.
	 * @return int Con el numero de mes correspondiente a la cadena pasada.
	 */
	private int obtenerMes(String cadenaMes) {

		int mes = -1;

		//Comprobar de que mes se trata
		if(cadenaMes == "Jan"){
			mes = 0;
		} else if (cadenaMes == "Feb"){
			mes = 1;
		} else if (cadenaMes == "Mar"){
			mes = 2;
		} else if (cadenaMes == "Apr"){
			mes = 3;
		} else if (cadenaMes == "May"){
			mes = 4;
		} else if (cadenaMes == "Jun"){
			mes = 5;
		} else if (cadenaMes == "Jul"){
			mes = 6;
		} else if (cadenaMes == "Aug"){
			mes = 7;
		} else if (cadenaMes == "Sep"){
			mes = 8;
		} else if (cadenaMes == "Oct"){
			mes = 9;
		} else if (cadenaMes == "Nov"){
			mes = 10;
		} else if (cadenaMes == "Dec"){
			mes = 11;
		}

		return mes;
	}

	/**
	 * Graba en base de datos la información correspondiente de la notificación.
	 * 
	 * @param fecha Calendar Fecha y hora de la notificacion.
	 * @param tipoMensaje int Tipo de mensaje de la notificacion.
	 * @param urgencia int Urgencia de la notificacion.
	 * @param mensaje String Mensaje de la notificacion.
	 */
	private static void grabarNotificacion(Calendar fecha, int tipoMensaje, int urgencia, String mensaje){
		//Pasar la fecha a fecha SQL
		java.sql.Timestamp horaSql = new java.sql.Timestamp(fecha.getTime().getTime());

		try {
			//Crear objetos Statement
			Statement st = conexion.createStatement();
			Statement st1 = conexion.createStatement();
			
			//Crear la notificacion
			String sql = "INSERT INTO Notificaciones (fecha, tipoMensaje, urgencia, mensaje) " +
					"VALUES ('" + horaSql + "', " + tipoMensaje +", " + urgencia + ", '" + mensaje + "');";

			st.executeUpdate(sql);

			//Obtener el id de la notificacion creada anteriormente
			sql = "SELECT MAX(idNotificacion) AS Notificacion FROM Notificaciones;";

			ResultSet rsIdNotificacion = st.executeQuery(sql);

			rsIdNotificacion.next();

			int idNotificacion = rsIdNotificacion.getInt("Notificacion");

			//Obtener los todos los dispositivos
			sql = "SELECT idDispositivo FROM Dispositivos;";

			ResultSet rsDispositivos = st.executeQuery(sql);

			//Recorrer los dispositivos
			while(rsDispositivos.next()){
				//Crear la relacion DispositivoNotificacion
				sql = "INSERT INTO DispositivosNotificaciones (idDispositivo, idNotificacion) " +
						" VALUES (" + rsDispositivos.getInt("idDispositivo") + ", " + idNotificacion + ");";

				st1.executeUpdate(sql);
			}
			//Cerrar los Statement
			st1.close();
			st.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
