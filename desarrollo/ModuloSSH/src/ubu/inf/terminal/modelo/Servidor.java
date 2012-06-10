package ubu.inf.terminal.modelo;
/**
 * Clase donde se guarda la información de los servidores favoritos.
 * @author   David Herreo de la Peña
 * @author   Jonatan Santos Barrios
 */
public class Servidor {
	/**
	 * @uml.property  name="id"
	 */
	private int id;
	/**
	 * @uml.property  name="ip"
	 */
	private String ip;
	/**
	 * @uml.property  name="usuario"
	 */
	private String usuario;
	/**
	 * @uml.property  name="contraseña"
	 */
	private String contraseña;
	/**
	 * @uml.property  name="puerto"
	 */
	private String puerto;
	/**
	 * @uml.property  name="descripcion"
	 */
	private String descripcion;
	
	
	/**
	 * Constructor de la clase Servidor.
	 * @param id
	 * @param ip
	 * @param usuario
	 * @param contraseña
	 * @param puerto
	 * @param descripcion
	 */
	public Servidor(int id, String ip, String usuario, String contraseña,
			String puerto, String descripcion) {
		super();
		this.id = id;
		this.ip = ip;
		this.usuario = usuario;
		this.contraseña = contraseña;
		this.puerto = puerto;
		this.descripcion = descripcion;
	}
	
	/**
	 * @return
	 * @uml.property  name="id"
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param  id
	 * @uml.property  name="id"
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return
	 * @uml.property  name="ip"
	 */
	public String getIp() {
		return ip;
	}
	/**
	 * @param  ip
	 * @uml.property  name="ip"
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}
	/**
	 * @return
	 * @uml.property  name="usuario"
	 */
	public String getUsuario() {
		return usuario;
	}
	/**
	 * @param  usuario
	 * @uml.property  name="usuario"
	 */
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	/**
	 * @return
	 * @uml.property  name="contraseña"
	 */
	public String getContraseña() {
		return contraseña;
	}
	/**
	 * @param  contraseña
	 * @uml.property  name="contraseña"
	 */
	public void setContraseña(String contraseña) {
		this.contraseña = contraseña;
	}
	/**
	 * @return
	 * @uml.property  name="puerto"
	 */
	public String getPuerto() {
		return puerto;
	}
	/**
	 * @param  puerto
	 * @uml.property  name="puerto"
	 */
	public void setPuerto(String puerto) {
		this.puerto = puerto;
	}
	/**
	 * @return
	 * @uml.property  name="descripcion"
	 */
	public String getDescripcion() {
		return descripcion;
	}
	/**
	 * @param  descripcion
	 * @uml.property  name="descripcion"
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	

}
