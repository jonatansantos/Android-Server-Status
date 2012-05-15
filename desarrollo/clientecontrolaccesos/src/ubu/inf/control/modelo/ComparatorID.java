package ubu.inf.control.modelo;

import java.util.Comparator;
/**Clase que comprar entre dos Notificaciones por el ID.
 * 
 * @author David Herrero de la Peña
 * @author Jonatan Santos Barrios
 * @version 1.0
 * 
 * @see Comparator
 * @see Notificacion
 *
 */
public  class ComparatorID implements Comparator<Notificacion> {

	@Override
	public int compare(Notificacion lhs, Notificacion rhs) {
		
		return lhs.getServ().getId()-rhs.getServ().getId();
	}

}
