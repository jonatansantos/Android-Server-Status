package ubu.inf.control.modelo;

import java.util.Comparator;
import java.util.Date;

/**
 * Clase para guardar todos los datos relativos a una notificación.
 * 
 * @author David Herrero de la Peña
 * @author Jonatan Santos Barrios
 * @version 1.0
 * 
 * 
 */
public class Notificacion {

	/**
	 * Servidor desde el que se manda la notificación.
	 */
	private Servidor serv;
	/**
	 * Texto de la notificación.
	 */
	private String texto;
	/**
	 * Fecha en la ha ocurrido la notificación.
	 */
	private Date fecha;
	/**
	 * Tipo de notificación: 0 ssh , 1 email.
	 */
	private int tipo;
	/**
	 * Tipo de urgencia: 0 grave, 1 normal, 0 info.
	 */
	private int urgencia;

	/**
	 * ID de la notificacion.
	 */
	private int id;
	/**
	 * Constructor de la clase.
	 * 
	 * @param texto
	 * @param fecha
	 * @param tipo
	 * @param urgencia
	 * @param serv
	 */
	public Notificacion(String texto, Date fecha, int tipo, int urgencia,
			Servidor serv,int id) {
		super();
		this.texto = texto;
		this.fecha = fecha;
		this.tipo = tipo;
		this.urgencia = urgencia;
		this.serv = serv;
		this.id=id;

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Servidor getServ() {
		return serv;
	}

	public void setServ(Servidor serv) {
		this.serv = serv;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	public int getUrgencia() {
		return urgencia;
	}

	public void setUrgencia(int urgencia) {
		this.urgencia = urgencia;
	}

}
