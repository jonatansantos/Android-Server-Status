package ubu.inf.control.modelo;

import android.graphics.Color;
/**Clase para guardar todos los datos relativos a un servidor.
 * 
 * @author David Herrero de la Peña
 * @author Jonatan Santos Barrios
 * @version 1.0
 * 
 *
 */
public class Servidor {
	/**
	 * IP del servidor.
	 */
	private String ip;
	/**
	 * Descripción del servidor.
	 */
	private String descripcion;
	/**
	 * Indica si se inicia al encender el teléfono.
	 */
	private boolean inicio;
	/**
	 * ID del servidor.
	 */
	private int id;
	/**
	 * Color para identificar al servidor.
	 */
	private int color;
	/**
	 * Constructor de la clase Servidor.
	 * @param ip
	 * @param descripcion
	 * @param inicio
	 * @param id
	 * @param color
	 */
	public Servidor(String ip, String descripcion, boolean inicio, int id,
			int color) {
		super();
		this.ip = ip;
		this.descripcion = descripcion;
		this.inicio = inicio;
		this.id = id;
		this.color = color;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public boolean isInicio() {
		return inicio;
	}
	public void setInicio(boolean inicio) {
		this.inicio = inicio;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
	@Override
	/**
	 * Función que dice si dos servidores son iguales comparandolos por su ID.
	 */
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		Servidor oaux = (Servidor) o;
		return oaux.getId()==id;
	}
	
	

}
