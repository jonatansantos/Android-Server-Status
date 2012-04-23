package ubu.itig;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Clase Analisis
 * 
 * @author Jonatan Santos Barrios.
 * @author David Herrero de la Peña.
 * @version 1.0
 * 
 * */
public class Analisis {

	/**
	 * Array de Notificaciones 
	 */
	private ArrayList<Notificacion> notificaciones = new ArrayList<Notificacion>();
	
	/**
	 * Timer, para realizar la tarea periodica de analizar el fichero.
	 */
	Timer timer;
	
	/**
	 * TimerTask, tarea periodica.
	 */
	TimerTask analizar = new TimerTask(){
		public void run(){
			try {
				//Abrir el fichero
				BufferedReader archivo = new BufferedReader(new FileReader("/var/log/auth.log"));
				String linea;
				ArrayList<String> lineas = new ArrayList<String>();
				notificaciones = new ArrayList<Notificacion>();
				//Obtener todas las lineas del fichero
				while((linea = archivo.readLine()) != null){
					lineas.add(linea);
				}
				//Cerrar el fichero
				archivo.close();
				
				//Recorrer el fichero en sentido inverso, las ultimas lineas las primeras en analizar
				// comprobando que se hayan realizado recientemente
				while(!lineas.isEmpty() && comprobarHora(lineas.get(lineas.size()-1)) ){
					//Obtener linea
					linea = lineas.remove(lineas.size()-1);
					
					//Comprobar si es un mensaje de la conexión ssh
					if(linea.indexOf("sshd") > -1){
						//Añadimos la notificacion al array de notificaciones
						notificaciones.add(crearNotificacion(linea));
					}
				}
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}; 
	
	/**
	 * Constructor del Analisis.
	 * 
	 * */
	public Analisis(){
		//Crear el timer
		timer = new Timer();
		//Indicar en milisegundos cuando empezar y la frecuencia con la que realizar la tarea
		timer.schedule(analizar, 0, 60000);
	}
	
	/**
	 * Comprueba se la linea pasado se ha producido recientemente y debe se notificada.
	 * 
	 * @param lineaComprobar String con la linea del fichero.
	 * @return true si la linea se ha producido en el tiempo establecido, false si no.
	 */
	private static boolean comprobarHora(String lineaComprobar){
		
		//Obtener la hora actual
		Calendar calendario = Calendar.getInstance();
		
		//Obtener la hora de la linea a comprobar
		Calendar horaLinea =  obtenerHora(lineaComprobar);
		
		//Comparar las horas		
		long diferencia = calendario.getTimeInMillis() - horaLinea.getTimeInMillis();
		//Comprobar si se ha producido en las ultimos x milisegundos
		if(diferencia <= 60000){
			return true;
		}
		
		return false;
	}

	/**
	 * Obtiene la fecha y hora de la cadena de texto de la linea del fichero pasada.
	 * 
	 * @param lineaComprobar String con la linea del fichero.
	 * @return Calendar con la fecha y hora que se produjo la linea.
	 */
	private static Calendar obtenerHora(String lineaComprobar) {
		
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
		switch(cadenaMes){
			case "Jan":
				mes = 0;
				break;
			case "Feb":
				mes = 1;
				break;
			case "Mar":
				mes = 2;
				break;
			case "Apr":
				mes = 3;
				break;
			case "May":
				mes = 4;
				break;
			case "Jun":
				mes = 5;
				break;
			case "Jul":
				mes = 6;
				break;
			case "Aug":
				mes = 7;
				break;
			case "Sep":
				mes = 8;
				break;
			case "Oct":
				mes = 9;
				break;
			case "Nov":
				mes = 10;
				break;
			case "Dec":
				mes = 11;
				break;
		}
		
		return mes;
	}
	
	/**
	 * Crea una notificación a partir de la linea del fichero.
	 * 
	 * @param linea String con la linea del fichero.
	 * @return Notificacion.
	 */
	private static Notificacion crearNotificacion(String linea){
		
		String mensaje;
		
		//Obtener la parte del mensaje de la linea
		mensaje = linea.substring(16);
		mensaje = mensaje.substring(linea.indexOf(":") + 2);
		
		return new Notificacion(obtenerHora(linea), obtenerDatosEquipo(), mensaje);
		
	}

	/**
	 * Devuelve los datos  de nombre de equipo y direccion IP.
	 * 
	 * @return String con el nombre del equipo y la direccion IP.
	 */
	private static String obtenerDatosEquipo() {
		
		String equipo = null;
		try {
			InetAddress direccion = InetAddress.getLocalHost();
			equipo = direccion.getHostName() + ":" + direccion.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		return equipo;
	}
	
	
	/**
	 * Devuelve si hay notificaciones nuevas o no.
	 * 
	 * @return true si hay nuevas notificaciones y false si no las hay.
	 */
	public boolean hayNotificaciones(){
		return !notificaciones.isEmpty();
	}
	
	public ArrayList<Notificacion> obtenerNotificaciones(){
		
		return notificaciones;
	}
}
