package ubu.itig.serverstatus.notificador;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;

public class Notificador {

	//Datos de la conexión con la base de datos
	private static Connection conexion;
	private String bd="android_server_status";
	private String user="jonatan";
	private String password="prueba";
	private String server="jdbc:mysql://localhost/"+bd;
	
	public Notificador(){
				
		//Crear la conexión
		try{
			conexion = DriverManager.getConnection(server,user,password);
			
			ServerSocket server = new ServerSocket(5150);
			Socket client = server.accept();
			  
			BufferedReader is = new BufferedReader( new InputStreamReader(client.getInputStream()));
			  
			String inputLine;
			  
			while ((inputLine = is.readLine())!= null) {
				String linea;
				
				linea = inputLine.substring(inputLine.indexOf(">") + 1);

				analizarLinea(linea);
			}
			  
			is.close();			  
			client.close();
			server.close();
			
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Analiza la linea para comprobar si es uno de los avisos que se deben notificar.
	 * 
	 * @param linea String con la linea del fichero.
	 */
	private static void analizarLinea(String linea){
		
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
	 * @return int Tipo de mensaje: -1 Mensaje no notificable, 1 Conexión fallida, 2 Intento de robo de claves, 3 Conexión con exito.
	 */
	private static int obtenerUrgenciaSSH(String mensaje){
		int tipo = -1;
		
		//Comprobamos si es uno de los mensajes a notificar
		if(mensaje.indexOf("Failed password") > -1){
			tipo = 1;
		} else if (mensaje.indexOf("POSSIBLE BREAK-IN ATTEMPT!") > -1){
			tipo = 2;
		} else if (mensaje.indexOf("Accepted password") > -1){
			tipo = 3;
		}
		
		return tipo;
	}
	
	/**
	 * Obtiene la urgencia del mensaje de correo.
	 * 
	 * @param mensaje String con el mensaje del aviso.
	 * @return int Tipo de mensaje: -1 Mensaje no notificable, 1 Conexión con el servidor fallida, 2 Correo enviado con exito.
	 */
	private static int obtenerUrgenciaCorreo(String mensaje){
		int tipo = -1;
		
		//Comprobamos si es uno de los mensajes a notificar
		if(mensaje.indexOf("SASL LOGIN authentication failed: authentication failure") > -1){
			tipo = 1;
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
	private static Calendar obtenerFecha(String lineaComprobar) {
		
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
	private static int obtenerMes(String cadenaMes) {
		
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
	 * Devuelve el numero de notificaciones nuevas que tiene el dispositivo pasado del tipo de pasado.
	 * 
	 * @param String idDispositivo Identificador único de dispositivo.
	 * @param int tipoMensaje Tipo de mensaje por el que se pregunta si hay notificaciones.
	 * @return int con el numero de notificaciones.
	 */
	public static int hayNotificaciones(String idDispositivo, int tipoMensaje){
		
		try {
			
			int ultimaNotificacion;
			String columnaABuscar = null;
			
			//Segun el tipo de mensaje se obtiene la columna a buscar en la base de datos
			if(tipoMensaje == 0){
				columnaABuscar = "ultimaNotificacionSSH";						
			} else if (tipoMensaje == 1){
				columnaABuscar = "ultimaNotificacionMail";
			}
			
			// Crear el objeto Statement.
			Statement st = conexion.createStatement();
			
			//Ejecutar la consulta para saber cual fue la ultima notificación de dicho dispositivo
			ResultSet rsUltimaNotificacion = st.executeQuery("SELECT " + columnaABuscar +
					"											FROM Dispositivos " +
					"											WHERE dispositivo = '" + idDispositivo + "';");
			
			//Comprobar si existia el dispositivo
			if(rsUltimaNotificacion.next()){
				ultimaNotificacion = rsUltimaNotificacion.getInt("id_Notificacion");
			} else {
				//Crear el dispositivo en la base de datos
				String añadirDispositivo = "INSERT INTO Dispositivo (dispositivo) VALUES ('" + idDispositivo + "')";
				
				int rs = st.executeUpdate(añadirDispositivo);
				ultimaNotificacion = 0;
			}
			
			//Ejecutar la consulta para saber el numero de notificaciones nuevas
			ResultSet rsNumeroNotificaciones = st.executeQuery("SELECT COUNT(id_Notificacion) AS Numero " +
					"							FROM Notificaciones " +
					"							WHERE id_Notificacion > " + ultimaNotificacion +
					"							AND tipo_Mensaje = " + tipoMensaje + ";");
			
			rsNumeroNotificaciones.next();
			
			int numeroNotificaciones = rsNumeroNotificaciones.getInt("Numero");
			
			st.close();
			
			return numeroNotificaciones;
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return -1;
	}
	
	/**
	 * Devuelve en un array de arrays con la información de todas las notificaciones.
	 * 
	 * @return ArrayList<ArrayList<String>> Contiene toda la información de las notificaciones.
	 */
	public static ArrayList<ArrayList<String>> obtenerNotificaciones(String idDispositivo, int tipoMensaje, int numeroNotificaciones){
		
		
		try {
			int ultimaNotificacion = 0;
			String columnaAModificar = null;
			ArrayList<ArrayList<String>> notificaciones = new ArrayList<ArrayList<String>>();
			ArrayList<String> notificacion;
			
			// Crear el objeto Statement.
			Statement st = conexion.createStatement();
			
			//Obtener las notificaciones
			ResultSet rsNotificaciones = st.executeQuery("SELECT TOP " + numeroNotificaciones + "* FROM Notificaciones WHERE tipo_Mensaje = " + tipoMensaje + " ORDER BY id_Notificacion DESC;");
			
			//Segun el tipo de mensaje se obtiene la columna a modificar en la base de datos
			if(tipoMensaje == 0){
				columnaAModificar = "ultimaNotificacionSSH";						
			} else if (tipoMensaje == 1){
				columnaAModificar = "ultimaNotificacionMail";
			}
			
			//Recorrer las notificaciones y añadirlas al array
			while(rsNotificaciones.next()){
				notificacion = new ArrayList<String>();
				
				if(ultimaNotificacion == 0){
					ultimaNotificacion = rsNotificaciones.getInt("id_Notificacion");
				}
				
				//Introducir informacion de la notificacion en el array
				notificacion.add(rsNotificaciones.getTimestamp("fecha").toString());
				notificacion.add("" +rsNotificaciones.getInt("tipo_Mensaje"));
				notificacion.add("" +rsNotificaciones.getInt("urgencia"));
				notificacion.add(rsNotificaciones.getString("mensaje"));
				
				notificaciones.add(notificacion);
			}
			
			//Actualizar la ultimaNotificacion para el dispositivo
			int rsActualizarUltimaNotificacion = st.executeUpdate("UPDATE Dispositivos" +
					"												SET " + columnaAModificar + " = " + ultimaNotificacion +
					"												WHERE dispositivo = '" + idDispositivo + "';");
			
			return notificaciones;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * Graba en base de datos la información correspondiente de la notificación.
	 * 
	 * @param linea String con la linea del fichero
	 */
	private static void grabarNotificacion(Calendar fecha, int tipoMensaje, int urgencia, String mensaje){
		
		java.sql.Timestamp horaSql = new java.sql.Timestamp(fecha.getTime().getTime());
		
		// Creamos el objeto Statement.
		Statement st;
		try {
			st = conexion.createStatement();
			
			// Creamos la consulta sql y la ejecutamos con un ResultSet.
			String sql = "INSERT INTO Notificaciones(tipo_mensaje, urgencia, fecha,  mensaje) VALUES (" + tipoMensaje +  ", " + urgencia + ", '" + horaSql + "', '" + mensaje + "');";
			int rs = st.executeUpdate(sql);
			
			//Controlamos que se haya registrado en la base de datos
			if(rs == 0){
				
			}
			
			st.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
}