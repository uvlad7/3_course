import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

public class RemImpl extends UnicastRemoteObject implements Rem
{
 public RemImpl() throws RemoteException {}

 public String getMessage() throws RemoteException{
  return "Here is a remote message.";
 }

}