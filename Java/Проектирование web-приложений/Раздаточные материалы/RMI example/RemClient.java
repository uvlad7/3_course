import java.rmi.*;
import java.net.*;
import java.io.*;

public class RemClient{
 public static void main(String[] args){
  try{
    String host = 
      (args.length > 0) ? args[0] : "localhost";
    Rem remObject = (Rem)Naming.lookup("rmi://"+host+"/Rem");
    System.out.println(remObject.getMessage());
  }
  catch (RemoteException re) {
    System.out.println("RemoteException: "+re);
  }
  catch (NotBoundException nbe) {
    System.out.println("NotBoundException: "+nbe);}
  catch (MalformedURLException mfe) {
    System.out.println("NotBoundException: "+mfe);}

 }
}