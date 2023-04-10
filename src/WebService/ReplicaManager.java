/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WebService;

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
            URL urlATW = new URL("http://10.0.0.34:8080/movieATW?wsdl");
            QName qNameATW = new QName("http://WebService/", "ATWImplService");
            Service serviceATW = Service.create(urlATW, qNameATW);
            IATW atw = serviceATW.getPort(IATW.class);

            URL urlVER = new URL("http://10.0.0.34:8081/movieVER?wsdl");
            QName qNameVER = new QName("http://WebService/", "VERImplService");
            Service serviceVER = Service.create(urlVER, qNameVER);
            IVER ver = serviceVER.getPort(IVER.class);

            URL urlOUT = new URL("http://10.0.0.34:8082/movieOUT?wsdl");
            QName qNameOUT = new QName("http://WebService/", "OUTImplService");
            Service serviceOUT = Service.create(urlOUT, qNameOUT);
            IOUT out = serviceOUT.getPort(IOUT.class);

            Sequencer.atw = atw;
            Sequencer.ver = ver;
            Sequencer.out = out;
            
            System.out.println("Replica 1 Restarted");

        } catch (Exception e) {
            System.err.println("Error while restarting services: " + e.getMessage());
        }

    }

    public void restartServices1() {
        try {
            URL urlATW2 = new URL("http://10.0.0.170:8083/movieATW?wsdl");
            QName qNameATW2 = new QName("http://WebService/", "ATWImplService");
            Service serviceATW2 = Service.create(urlATW2, qNameATW2);
            IATW atw2 = serviceATW2.getPort(IATW.class);

            URL urlVER2 = new URL("http://10.0.0.170:8084/movieVER?wsdl");
            QName qNameVER2 = new QName("http://WebService/", "VERImplService");
            Service serviceVER2 = Service.create(urlVER2, qNameVER2);
            IVER ver2 = serviceVER2.getPort(IVER.class);

            URL urlOUT2 = new URL("http://10.0.0.170:8085/movieOUT?wsdl");
            QName qNameOUT2 = new QName("http://WebService/", "OUTImplService");
            Service serviceOUT2 = Service.create(urlOUT2, qNameOUT2);
            IOUT out2 = serviceOUT2.getPort(IOUT.class);

            Sequencer.atw2 = atw2;
            Sequencer.ver2 = ver2;
            Sequencer.out2 = out2;
            
            System.out.println("Replica 2 Restarted");

        } catch (Exception e) {
            System.err.println("Error while restarting services: " + e.getMessage());
        }
    }

    public void restartServices2() {
        try {

            URL urlATW3 = new URL("http://10.0.0.34:8080/movieATW?wsdl");
            QName qNameATW3 = new QName("http://WebService/", "ATWImplService");
            Service serviceATW3 = Service.create(urlATW3, qNameATW3);
            IATW atw3 = serviceATW3.getPort(IATW.class);

            URL urlVER3 = new URL("http://10.0.0.34:8081/movieVER?wsdl");
            QName qNameVER3 = new QName("http://WebService/", "VERImplService");
            Service serviceVER3 = Service.create(urlVER3, qNameVER3);
            IVER ver3 = serviceVER3.getPort(IVER.class);

            URL urlOUT3 = new URL("http://10.0.0.34:8082/movieOUT?wsdl");
            QName qNameOUT3 = new QName("http://WebService/", "OUTImplService");
            Service serviceOUT3 = Service.create(urlOUT3, qNameOUT3);
            IOUT out3 = serviceOUT3.getPort(IOUT.class);

            Sequencer.atw3 = atw3;

            Sequencer.ver3 = ver3;

            Sequencer.out3 = out3;
            
            System.out.println("Replica 3 Restarted");

        } catch (Exception e) {
            System.err.println("Error while restarting services: " + e.getMessage());
        }
    }
}
