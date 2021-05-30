import java.rmi.*;
public interface Rem extends Remote{
 public String getMessage() throws RemoteException;
}