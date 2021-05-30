import java.rmi.*;
import java.net.*;

public class RemServer{
 public static void main(String[] args){
  try{
   RemImpl localObject = new RemImpl();
   Naming.rebind("rmi:///Rem", localObject);
  }
  catch (RemoteException re) {
    System.out.println("RemoteException: "+re);
  }
  catch (MalformedURLException mfe) {
    System.out.println("NotBoundException: "+mfe);}

 }
}