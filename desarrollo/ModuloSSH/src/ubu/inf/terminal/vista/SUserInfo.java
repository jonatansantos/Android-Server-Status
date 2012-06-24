package ubu.inf.terminal.vista;
 
import com.jcraft.jsch.UserInfo;
/**
 * Clase auxsiliar para el funcionamiento de Jsch.
 * @author   David Herreo de la Peña
 * @author   Jonatan Santos Barrios
 */
public class SUserInfo implements UserInfo {
 
    /**
	 * @uml.property  name="password"
	 */
    private String password;
    private String passPhrase;
 
    public SUserInfo (String password, String passPhrase) {
        this.password = password;
        this.passPhrase = passPhrase;
    }
 
    public String getPassphrase() {
        return passPhrase;
    }
 
    /**
	 * @return
	 * @uml.property  name="password"
	 */
    public String getPassword() {
        return password;
    }
 
    public boolean promptPassphrase(String arg0) {
        return true;
    }
 
    public boolean promptPassword(String arg0) {
        return false;
    }
 
    public boolean promptYesNo(String arg0) {
        return true;
    }
 
    public void showMessage(String arg0) {
        System.out.println("SUserInfo.showMessage()");
    }
}