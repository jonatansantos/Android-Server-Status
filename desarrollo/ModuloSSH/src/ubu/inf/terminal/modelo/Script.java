package ubu.inf.terminal.modelo;

import java.util.ArrayList;

/**
 * Script , es un conjunto de comandos.
 * @author   David Herreo de la Peña
 * @author   Jonatan Santos Barrios
 */
public class Script {

	/**
	 * @uml.property  name="comandos"
	 */
	private ArrayList<String> comandos;
	/**
	 * @uml.property  name="nombre"
	 */
	private String nombre;
	/**
	 * @uml.property  name="cantidad"
	 */
	private int cantidad;
	/**
	 * @uml.property  name="idScript"
	 */
	private int idScript;
	/**
	 * Constructor de la clase Script.
	 * @param nombre
	 * @param cantidad
	 * @param idScript
	 */
	public Script(String nombre, int cantidad,
			int idScript) {
		super();
		this.comandos = new ArrayList<String>();
		this.nombre = nombre;
		this.cantidad = cantidad;
		this.idScript = idScript;
	}
	/**
	 * @return
	 * @uml.property  name="comandos"
	 */
	public ArrayList<String> getComandos() {
		return comandos;
	}
	public void addComando(String comando) {
		comandos.add(comando);
	}
	/**
	 * @return
	 * @uml.property  name="nombre"
	 */
	public String getNombre() {
		return nombre;
	}
	/**
	 * @param  nombre
	 * @uml.property  name="nombre"
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	/**
	 * @return
	 * @uml.property  name="cantidad"
	 */
	public int getCantidad() {
		return cantidad;
	}
	/**
	 * @param  cantidad
	 * @uml.property  name="cantidad"
	 */
	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
	/**
	 * @return
	 * @uml.property  name="idScript"
	 */
	public int getIdScript() {
		return idScript;
	}
	/**
	 * @param  idScript
	 * @uml.property  name="idScript"
	 */
	public void setIdScript(int idScript) {
		this.idScript = idScript;
	}
	
	
}
