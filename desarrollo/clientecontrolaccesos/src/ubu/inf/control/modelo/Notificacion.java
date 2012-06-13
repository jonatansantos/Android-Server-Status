package ubu.inf.control.modelo;

import java.util.Comparator;
import java.util.Date;

/**
 * Clase para guardar todos los datos relativos a una notificación.
 * 
 * @author David Herrero de la Peña
 * @author Jonatan Santos Barrios
 * @version 1.0
 */
public class Notificacion {

	/**
	 * Servidor desde el que se manda la notificación.
	 * 
	 * @uml.property name="serv"
	 * @uml.associationEnd
	 */
	private Servidor serv;
	/**
	 * Texto de la notificación.
	 * 
	 * @uml.property name="texto"
	 */
	private String texto;
	/**
	 * Fecha en la ha ocurrido la notificación.
	 * 
	 * @uml.property name="fecha"
	 */
	private Date fecha;
	/**
	 * Tipo de notificación: 0 ssh , 1 email.
	 * 
	 * @uml.property name="tipo"
	 */
	private int tipo;
	/**
	 * Tipo de urgencia: 0 grave, 1 normal, 0 info.
	 * 
	 * @uml.property name="urgencia"
	 */
	private int urgencia;

	/**
	 * ID de la notificacion.
	 * 
	 * @uml.property name="id"
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
			Servidor serv, int id) {
		super();
		this.texto = texto;
		this.fecha = fecha;
		this.tipo = tipo;
		this.urgencia = urgencia;
		this.serv = serv;
		this.id = id;

	}

	@Override
	public boolean equals(Object o) {
		Notificacion otro = (Notificacion) o;
		return otro.getId() == id && otro.getServ().getId() == serv.getId()
				&& otro.getTipo() == tipo
				&& otro.getServ().getIp().equals(serv.getIp());
	}

	/**
	 * @return
	 * @uml.property name="id"
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 * @uml.property name="id"
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return
	 * @uml.property name="serv"
	 */
	public Servidor getServ() {
		return serv;
	}

	/**
	 * @param serv
	 * @uml.property name="serv"
	 */
	public void setServ(Servidor serv) {
		this.serv = serv;
	}

	/**
	 * @return
	 * @uml.property name="texto"
	 */
	public String getTexto() {
		return texto;
	}

	/**
	 * @param texto
	 * @uml.property name="texto"
	 */
	public void setTexto(String texto) {
		this.texto = texto;
	}

	/**
	 * @return
	 * @uml.property name="fecha"
	 */
	public Date getFecha() {
		return fecha;
	}

	/**
	 * @param fecha
	 * @uml.property name="fecha"
	 */
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	/**
	 * @return
	 * @uml.property name="tipo"
	 */
	public int getTipo() {
		return tipo;
	}

	/**
	 * @param tipo
	 * @uml.property name="tipo"
	 */
	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	/**
	 * @return
	 * @uml.property name="urgencia"
	 */
	public int getUrgencia() {
		return urgencia;
	}

	/**
	 * @param urgencia
	 * @uml.property name="urgencia"
	 */
	public void setUrgencia(int urgencia) {
		this.urgencia = urgencia;
	}

}
