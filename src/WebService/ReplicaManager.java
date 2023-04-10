/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WebService;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;

/**
 *
 * @author Rohan
 */
public class ReplicaManager {

    public void restartServices0() {
        try {
            AddMovie.time_out = 0;
            AddMovie.ssf = "n";
//            URL urlATW = new URL("http://10.0.0.34:8080/movieATW?wsdl");
//            QName qNameATW = new QName("http://WebService/", "ATWImplService");
//            Service serviceATW = Service.create(urlATW, qNameATW);
//            IATW atw = serviceATW.getPort(IATW.class);
//            
//            
//
//            URL urlVER = new URL("http://10.0.0.34:8081/movieVER?wsdl");
//            QName qNameVER = new QName("http://WebService/", "VERImplService");
//            Service serviceVER = Service.create(urlVER, qNameVER);
//            IVER ver = serviceVER.getPort(IVER.class);
//
//            URL urlOUT = new URL("http://10.0.0.34:8082/movieOUT?wsdl");
//            QName qNameOUT = new QName("http://WebService/", "OUTImplService");
//            Service serviceOUT = Service.create(urlOUT, qNameOUT);
//            IOUT out = serviceOUT.getPort(IOUT.class);
//
//            Sequencer.atw = atw;
//            Sequencer.ver = ver;
//            Sequencer.out = out;
            String restart = "restart";
            DatagramSocket ds = new DatagramSocket();

            InetAddress ip = InetAddress.getByName("10.0.0.34");
            byte buf[] = null;

            buf = restart.getBytes();
            
            DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, 3034);
            ds.send(packet);
            DatagramPacket packet2 = new DatagramPacket(buf, buf.length, ip, 3035);
            ds.send(packet2);
            DatagramPacket packet3 = new DatagramPacket(buf, buf.length, ip, 3036);
            ds.send(packet3);
            ds.close();
            
            System.out.println("Replica 1 Restarted");

        } catch (Exception e) {
            System.err.println("Error while restarting services: " + e.getMessage());
        }

    }

    public void restartServices1() {
        try {
            AddMovie.time_out = 0;
            AddMovie.ssf = "n";
            String restart = "restart";
            DatagramSocket ds = new DatagramSocket();

            InetAddress ip = InetAddress.getByName("10.0.0.170");
            byte buf[] = null;

            buf = restart.getBytes();
            
            DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, 3034);
            ds.send(packet);
            DatagramPacket packet2 = new DatagramPacket(buf, buf.length, ip, 3035);
            ds.send(packet2);
            DatagramPacket packet3 = new DatagramPacket(buf, buf.length, ip, 3036);
            ds.send(packet3);
            ds.close();

            System.out.println("Replica 2 Restarted");

        } catch (Exception e) {
            System.err.println("Error while restarting services: " + e.getMessage());
        }
    }

    public void restartServices2() {
        try {
            AddMovie.time_out = 0;
            AddMovie.ssf = "n";
            String restart = "restart";
            DatagramSocket ds = new DatagramSocket();

            InetAddress ip = InetAddress.getByName("10.0.0.34");
            byte buf[] = null;

            buf = restart.getBytes();
            
            DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, 3034);
            ds.send(packet);
            DatagramPacket packet2 = new DatagramPacket(buf, buf.length, ip, 3035);
            ds.send(packet2);
            DatagramPacket packet3 = new DatagramPacket(buf, buf.length, ip, 3036);
            ds.send(packet3);
            ds.close();

            System.out.println("Replica 3 Restarted");

        } catch (Exception e) {
            System.err.println("Error while restarting services: " + e.getMessage());
        }
    }
}
