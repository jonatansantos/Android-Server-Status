package ubu.itig.serverstatus.notificador;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Clase correspondiente al WebService Notificador.
 * 
 * @author David Herrero de la Peña.
 * @author Jonatan Santos Barrios.
 * @version 1.0
 * 
 * */
public class Notificador {
	
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
	private String bd;
	
	/**
	 * 
	 * Nombre del usuario de la base de datos.
	 * 
	 */
	private String user;
	
	/**
	 * 
	 * Contraseña de usuario de la base de datos.
	 * 
	 */
	private String password;
	
	/**
	 * 
	 * Servidor donde se encuantra la base de datos.
	 * 
	 */
	private String server;
	
	/**
	 * 
	 * Cadena de conexión para la base de datos.
	 * 
	 */
	private String cadena;
	
	/**
	 * 
	 * Logger
	 * 
	 */
	private Logger logger;
	
	
	/**
	 * Constructor de la clase, en la que se lee el archivo de configuración.
	 * 
	 */
	public Notificador(){
		try{
			if(conexion == null){
		   		//Leer el fichero de propiedades
		   		lecturaFicheroPropiedades();
	   		 
	    		//Crear la conexion con base de datos
		    	conexion = DriverManager.getConnection(cadena,user,password);
		    	
		    	
		    	logger = Logger.getLogger(getClass().getName());
			}
	    } catch (SQLException e) {
	    	e.printStackTrace();	    	
		}
	}	
	
	/**
     * Leer el fichero de propiedades y obtiene los parametros de configuración
     * 
     */
    private void lecturaFicheroPropiedades() {
    	
    	Properties propiedades = new Properties();
		
    	try {
    		//Leemos el fichero
    	    propiedades.load(Notificador.class.getClassLoader().getResourceAsStream("conf.properties"));
    	    
    	    //Obtenemos las propiedades
    	    server = propiedades.getProperty("dataBaseServer");
    	    bd = propiedades.getProperty("dataBaseName");
    	    user = propiedades.getProperty("dataBaseUser");
    	    password = propiedades.getProperty("dataBasePassword");
    	    
    	    cadena="jdbc:mysql://" + server + "/" + bd;
    	    
    	} catch (IOException e) {
    		logger.warning("Error IO al leer el archivo de configuración: " + e);
    	}
    	
	}
	
	/**
	 * Devuelve el numero de notificaciones nuevas que tiene el dispositivo pasado del tipo de pasado.
	 * 
	 * @param String idDispositivo Identificador único de dispositivo.
	 * @param Integer tipoMensaje Tipo de mensaje por el que se pregunta si hay notificaciones.
	 * @return String con el numero de notificaciones.
	 */
	public String hayNotificaciones(String idDispositivo, Integer tipoMensaje){
		
		try {		
			// Crear los objetos Statement.
			Statement st = conexion.createStatement();
			Statement st1 = conexion.createStatement();
			
			//Comprobar si existe el dispositivo
			String sql = "SELECT * FROM Dispositivos WHERE dispositivo = '" + idDispositivo + "';";
			
			ResultSet rsDispositivo = st.executeQuery(sql);
			
			//Sino existe el dispositivo lo creamos
			if(!rsDispositivo.next()){
				//Crear el dispositivo en la tabla dispositivos
				String sqlCrearDispositivo = "INSERT INTO Dispositivos (dispositivo) VALUES ('" + idDispositivo + "');";
				
				st.executeUpdate(sqlCrearDispositivo);
				
				//Obtener el nuevo id del dispositivo
				sql = "SELECT MAX(idDispositivo) AS Numero FROM Dispositivos;";
				
				ResultSet rsIdDispositivo = st.executeQuery(sql);
				
				rsIdDispositivo.next();
				
				int idNuevoDispositivo = rsIdDispositivo.getInt("Numero"); 
				
				//Obtener las notificaciones
				sql = "SELECT idNotificacion FROM Notificaciones;";
				
				ResultSet rsNotificaciones = st.executeQuery(sql);
				
				//Recorrer las notificaciones
				while(rsNotificaciones.next()){
					//Crear la relacion DispositivoNotificacion
					String sqlCrearDispositivoNotificacion = "INSERT INTO DispositivosNotificaciones (idDispositivo, idNotificacion) " +
							" VALUES (" + idNuevoDispositivo + ", " + rsNotificaciones.getInt("idNotificacion") + ");";
					
					st1.executeUpdate(sqlCrearDispositivoNotificacion);
				}			
			}
			
			//Obtener el numero de notificaciones nuevas para ese dispositivo y ese tipo de mensaje
			sql = "SELECT COUNT(*) AS Numero FROM DispositivosNotificaciones " +
					"INNER JOIN Dispositivos ON DispositivosNotificaciones.idDispositivo = Dispositivos.idDispositivo " +
					"INNER JOIN Notificaciones ON DispositivosNotificaciones.idNotificacion = Notificaciones.idNotificacion " +
					"  WHERE descargada = 0 " +
					"	AND Dispositivos.dispositivo = '" + idDispositivo + "' " +
					"	AND Notificaciones.tipoMensaje = " + tipoMensaje + ";";
			
			//Ejecutar la consulta para saber el numero de notificacicones sin descargar
			ResultSet rsNumeroNotificaciones = st.executeQuery(sql);

			rsNumeroNotificaciones.next();
			
			int numeroNotificaciones = rsNumeroNotificaciones.getInt("Numero");
			
			//Cerrar la conexion
			st.close();
			st1.close();		
			
			return "" + numeroNotificaciones;
			
			
		} catch (SQLException e) {
			logger.warning("Error SQL al comprobar si hay notificaciones nuevas: " + e);
			return "-1";
		}
	}
	
	/**
	 * Devuelve en un array de arrays con la información de todas las notificaciones no descargadas.
	 *
	 * @param String idDispositivo Identificador único de dispositivo.
	 * @param Integer tipoMensaje Tipo de mensaje del que se quieren obtener las notificaciones.
	 * @return Object[][] Contiene toda la información de las notificaciones.
	 */
	public Object[][] obtenerNotificacionesNuevas(String idDispositivo, Integer tipoMensaje){
		
		try {
			//Creamos el array de la informacion de las notificaciones
			String[][]notificaciones = new String[Integer.parseInt(hayNotificaciones(idDispositivo,tipoMensaje))][5];
			int numeroNotificacion = 0;		
			
			// Crear el objeto Statement.
			Statement st = conexion.createStatement();
			Statement st1 = conexion.createStatement();
			
			//Obtener el idDispositivo
			String sql = "SELECT idDispositivo FROM Dispositivos WHERE dispositivo = '" + idDispositivo + "';";
			
			ResultSet rsIdDispositivo = st.executeQuery(sql);
			
			rsIdDispositivo.next();
			
			int dispositivo = rsIdDispositivo.getInt("idDispositivo");
			
			//Consulta para obtener las notificaciones
			sql = "SELECT Notificaciones.* AS Numero FROM DispositivosNotificaciones " +
					"INNER JOIN Dispositivos ON DispositivosNotificaciones.idDispositivo = Dispositivos.idDispositivo " +
					"INNER JOIN Notificaciones ON DispositivosNotificaciones.idNotificacion = Notificaciones.idNotificacion " +
					"  WHERE descargada = 0 " +
					"	AND Dispositivos.dispositivo = '" + idDispositivo + "' " +
					"	AND Notificaciones.tipoMensaje = " + tipoMensaje + ";";		
			
			//Obtener las notificaciones
			ResultSet rsNotificaciones = st.executeQuery(sql);
			
			//Recorrer las notificaciones y añadirlas al array
			while(rsNotificaciones.next()){
				
				int idNotificacion = rsNotificaciones.getInt("idNotificacion");
				
				//Introducir informacion de la notificacion en el array
				notificaciones[numeroNotificacion][0] = "" + idNotificacion;
				notificaciones[numeroNotificacion][1] = "" + rsNotificaciones.getTimestamp("fecha").getTime();
				notificaciones[numeroNotificacion][2] = "" + rsNotificaciones.getInt("tipoMensaje");
				notificaciones[numeroNotificacion][3] = "" + rsNotificaciones.getInt("urgencia");
				notificaciones[numeroNotificacion][4] = rsNotificaciones.getString("mensaje");

				//PActualizar a descargada la notificacion
				sql = "UPDATE DispositivosNotificaciones SET descargada = 1 " +
						" WHERE idDispositivo = " + dispositivo +
						" AND idNotificacion = " + idNotificacion + ";";
				
				st1.executeUpdate(sql);
				
				numeroNotificacion += 1;
			}
			
			//Comprobar si el array es de longitud 1, para convertirlo en un array de longitud 2, con el ultimo registro vacio
			// para evitar problemas en el cliente a la hora de tranferir la información por SOAP.
			if (notificaciones.length == 1){
				//Crear el array auxiliar
				String[][] aux = new String[2][5];
				
				//Recorrer la información de la notificacion y la guarda en el array auxiliar, junto con el registro vacio
				for(int i = 0; i < 5; i++){
					aux[0][i] = notificaciones[0][i];
					//Poner el idDispositivo del registro vacio a -1 para que el cliente lo descarte
					if(i == 0){
						aux [1][i] = "-1" ;
					} else {
						aux[1][i] = "";
					}
				}
				
				//Convertir el array de notificaciones en el auxiliar
				notificaciones = aux;
			}
			
			//Cerrar la conexion
			st1.close();
			st.close();
			return notificaciones;
			
		} catch (SQLException e) {
			logger.warning("Error SQL al obtener las notificaciones nuevas: " + e);
			return null;
		}

	}	
	
	/**
	 * Devuelve en un array de arrays con la información de todas las notificaciones descargadas.
	 *
	 * @param String idDispositivo Identificador único de dispositivo.
	 * @param Integer tipoMensaje Tipo de mensaje del que se quieren obtener las notificaciones.
	 * @return Object[][] Contiene toda la información de las notificaciones.
	 */
	public Object[][] obtenerNotificacionesAntiguas(String idDispositivo, Integer tipoMensaje){
		
		try {
			// Crear el objeto Statement.
			Statement st = conexion.createStatement();
			
			//Obtener el numero de notificaciones ya descargadas
			String sql = "SELECT COUNT(*) AS Numero FROM DispositivosNotificaciones " +
					"INNER JOIN Dispositivos ON DispositivosNotificaciones.idDispositivo = Dispositivos.idDispositivo " +
					"INNER JOIN Notificaciones ON DispositivosNotificaciones.idNotificacion = Notificaciones.idNotificacion " +
					"  WHERE descargada = 1 " +
					"	AND Dispositivos.dispositivo = '" + idDispositivo + "' " +
					"	AND Notificaciones.tipoMensaje = " + tipoMensaje + ";";
			
			ResultSet rsNumeroNotificaciones = st.executeQuery(sql);

			rsNumeroNotificaciones.next();
			
			int numeroNotificaciones = rsNumeroNotificaciones.getInt("Numero");
			
			//Crear el array con la longitud igual al numero de notificaciones obtenidas en la consulta anterior
			String[][]notificaciones = new String[numeroNotificaciones][5];
			int numeroNotificacion = 0;		
			
			//Obtener el idDispositivo
			sql = "SELECT idDispositivo FROM Dispositivos WHERE dispositivo = '" + idDispositivo + "';";
			
			ResultSet rsIdDispositivo = st.executeQuery(sql);
			
			rsIdDispositivo.next();
			
			//Consulta para obtener las notificaciones
			sql = "SELECT Notificaciones.* AS Numero FROM DispositivosNotificaciones " +
					"INNER JOIN Dispositivos ON DispositivosNotificaciones.idDispositivo = Dispositivos.idDispositivo " +
					"INNER JOIN Notificaciones ON DispositivosNotificaciones.idNotificacion = Notificaciones.idNotificacion " +
					"  WHERE descargada = 1 " +
					"	AND Dispositivos.dispositivo = '" + idDispositivo + "' " +
					"	AND Notificaciones.tipoMensaje = " + tipoMensaje + ";";		
			
			
			//Obtener las notificaciones
			ResultSet rsNotificaciones = st.executeQuery(sql);
			
			//Recorrer las notificaciones y añadirlas al array
			while(rsNotificaciones.next()){
				
				int idNotificacion = rsNotificaciones.getInt("idNotificacion");
				
				//Introducir informacion de la notificacion en el array
				notificaciones[numeroNotificacion][0] = "" + idNotificacion;
				notificaciones[numeroNotificacion][1] = "" + rsNotificaciones.getTimestamp("fecha").getTime();
				notificaciones[numeroNotificacion][2] = "" + rsNotificaciones.getInt("tipoMensaje");
				notificaciones[numeroNotificacion][3] = "" + rsNotificaciones.getInt("urgencia");
				notificaciones[numeroNotificacion][4] = rsNotificaciones.getString("mensaje");
				
				numeroNotificacion += 1;
			}
			
			//Comprobar si el array es de longitud 1, para convertirlo en un array de longitud 2, con el ultimo registro vacio
			// para evitar problemas en el cliente a la hora de tranferir la información por SOAP.
			if (notificaciones.length == 1){
				//Crear el array auxiliar
				String[][] aux = new String[2][5];
				
				//Recorrer la información de la notificacion y la guarda en el array auxiliar, junto con el registro vacio
				for(int i = 0; i < 5; i++){
					aux[0][i] = notificaciones[0][i];
					//Poner el idDispositivo del registro vacio a -1 para que el cliente lo descarte
					if(i == 0){
						aux [1][i] = "-1" ;
					} else {
						aux[1][i] = "";
					}
				}
				
				//Convertir el array de notificaciones en el auxiliar
				notificaciones = aux;
			}
			
			//Cerrar la conexion
			st.close();
			return notificaciones;
			
		} catch (SQLException e) {
			logger.warning("Error SQL al obtener las notificaciones antiguas: " + e);

			return null;
		}

	}	
	
	/**
	 * Borrar de la base de datos la relacion entre dispositivo y notificacion.
	 * 
	 * @param idDispositivo Identificador unico de dispositivo.
	 * @param idNotificacion Identificador unico de notificacion.
	 * @return String con 1 si todo ha salido bien, 0 si se ha producido algun error con la base de datos.
	 */
	public String borrarNotificacion(String idDispositivo, Integer idNotificacion){
		try {
			// Crear el objeto Statement.
			Statement st = conexion.createStatement();
			
			//Obtener el idDispositivo
			String sql = "SELECT idDispositivo FROM Dispositivos WHERE dispositivo = '" + idDispositivo + "';";
					
			ResultSet rsIdDispositivo = st.executeQuery(sql);
						
			rsIdDispositivo.next();
						
			int dispositivo = rsIdDispositivo.getInt("idDispositivo");
						
			//Consulta para borrar la relacion
			sql = "DELETE FROM DispositivosNotificaciones " +
					" WHERE idDispositivo = " + dispositivo + " " +
					" AND idNotificacion = " + idNotificacion + ";";
			
			st.executeUpdate(sql);
			
			//Cerrar la conexion
			st.close();
			
			//Devolver 1, que todo ha ido bien
			return "" + 1;
			
		} catch (SQLException e){
			logger.warning("Error SQL al borrar notificación: " + e);
			return "" + 0;
		}
			
	}
}




