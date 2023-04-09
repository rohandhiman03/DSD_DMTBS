/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WebService;

import static WebService.PublishVER.data;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

/**
 *
 * @author Rohan
 */
public class Sequencer {

    public static int sequenceNumber = 0;

    public static void main(String[] args) throws SocketException, IOException, ExecutionException {
        DatagramSocket ds = new DatagramSocket(5001);
        byte[] receive = new byte[65535];

        URL urlATW = new URL("http://192.168.56.1:8080/movieATW?wsdl");
        QName qNameATW = new QName("http://WebService/", "ATWImplService");
        Service serviceATW = Service.create(urlATW, qNameATW);
        IATW atw = serviceATW.getPort(IATW.class);

        URL urlATW2 = new URL("http://192.168.56.1:8080/movieATW?wsdl");
        QName qNameATW2 = new QName("http://WebService/", "ATWImplService");
        Service serviceATW2 = Service.create(urlATW, qNameATW);
        IATW atw2 = serviceATW2.getPort(IATW.class);

        URL urlATW3 = new URL("http://192.168.56.1:8080/movieATW?wsdl");
        QName qNameATW3 = new QName("http://WebService/", "ATWImplService");
        Service serviceATW3 = Service.create(urlATW, qNameATW);
        IATW atw3 = serviceATW3.getPort(IATW.class);

        URL urlVER = new URL("http://192.168.56.1:8081/movieVER?wsdl");
        QName qNameVER = new QName("http://WebService/", "VERImplService");
        Service serviceVER = Service.create(urlVER, qNameVER);
        IVER ver = serviceVER.getPort(IVER.class);

        URL urlVER2 = new URL("http://192.168.56.1:8081/movieVER?wsdl");
        QName qNameVER2 = new QName("http://WebService/", "VERImplService");
        Service serviceVER2 = Service.create(urlVER, qNameVER);
        IVER ver2 = serviceVER2.getPort(IVER.class);

        URL urlVER3 = new URL("http://192.168.56.1:8081/movieVER?wsdl");
        QName qNameVER3 = new QName("http://WebService/", "VERImplService");
        Service serviceVER3 = Service.create(urlVER, qNameVER);
        IVER ver3 = serviceVER3.getPort(IVER.class);

        URL urlOUT = new URL("http://192.168.56.1:8082/movieOUT?wsdl");
        QName qNameOUT = new QName("http://WebService/", "OUTImplService");
        Service serviceOUT = Service.create(urlOUT, qNameOUT);
        IOUT out = serviceOUT.getPort(IOUT.class);

        URL urlOUT2 = new URL("http://192.168.56.1:8082/movieOUT?wsdl");
        QName qNameOUT2 = new QName("http://WebService/", "OUTImplService");
        Service serviceOUT2 = Service.create(urlOUT, qNameOUT);
        IOUT out2 = serviceOUT2.getPort(IOUT.class);

        URL urlOUT3 = new URL("http://192.168.56.1:8082/movieOUT?wsdl");
        QName qNameOUT3 = new QName("http://WebService/", "OUTImplService");
        Service serviceOUT3 = Service.create(urlOUT, qNameOUT);
        IOUT out3 = serviceOUT3.getPort(IOUT.class);

        DatagramPacket DpReceive = null;

        

        while (true) {

            DpReceive = new DatagramPacket(receive, receive.length);
            System.out.println("Sequencer Waiting!!");
            ds.receive(DpReceive);
            sequenceNumber++;
            System.out.println(sequenceNumber);
            System.out.println("Client:-" + data(receive));
            String rec = data(receive).toString();
            String[] function = rec.split(",");
            ExecutorService executor = Executors.newFixedThreadPool(3);
            if (function[0].equals("addMovie")) {
                String userID = function[1];
                String movieID = function[2];
                String movieName = function[3];
                int BookingCapacity = Integer.parseInt(function[4]);
                Future<String> result1 = null;
                Future<String> result2 = null;
                Future<String> result3 = null;

                String bookingSuccess = "";
                if (userID.substring(0, 3).equals("ATW")) {
                    result1 = executor.submit(() -> atw.addMovieSlots(movieID, movieName, BookingCapacity));
                    result2 = executor.submit(() -> atw2.addMovieSlots(movieID, movieName, BookingCapacity));
                    result3 = executor.submit(() -> atw3.addMovieSlots(movieID, movieName, BookingCapacity));

                } else if (userID.substring(0, 3).equals("VER")) {
                    result1 = executor.submit(() -> ver.addMovieSlots(movieID, movieName, BookingCapacity));
                    result2 = executor.submit(() -> ver2.addMovieSlots(movieID, movieName, BookingCapacity));
                    result3 = executor.submit(() -> ver3.addMovieSlots(movieID, movieName, BookingCapacity));
                } else if (userID.substring(0, 3).equals("OUT")) {
                    result1 = executor.submit(() -> out.addMovieSlots(movieID, movieName, BookingCapacity));
                    result2 = executor.submit(() -> out2.addMovieSlots(movieID, movieName, BookingCapacity));
                    result3 = executor.submit(() -> out3.addMovieSlots(movieID, movieName, BookingCapacity));
                }

                try {
                    String resultString1 = result1.get();
                    String resultString2 = result2.get();
                    String resultString3 = result3.get();

                    bookingSuccess = resultString1 + "," + resultString2 + "," + resultString3;
                    System.out.println("bookingSuccess: " + bookingSuccess);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                DatagramSocket dsSend = new DatagramSocket();
                InetAddress ip = InetAddress.getByName("192.168.56.1");
                byte buf[] = null;

                buf = bookingSuccess.getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, 5002);
                dsSend.send(packet);
                dsSend.close();

                executor.shutdown();
            } else if (function[0].equals("removeMovie")) {
                String userID = function[1];
                String movieID = function[2];
                String movieName = function[3];
                String removeSuccess = "";
                Future<String> result1 = null;
                Future<String> result2 = null;
                Future<String> result3 = null;
                if (userID.substring(0, 3).equals("ATW")) {
                    result1 = executor.submit(() -> atw.removeMovieSlots(movieID, movieName));
                    result2 = executor.submit(() -> atw2.removeMovieSlots(movieID, movieName));
                    result3 = executor.submit(() -> atw3.removeMovieSlots(movieID, movieName));
                } else if (userID.substring(0, 3).equals("VER")) {
                    result1 = executor.submit(() -> ver.removeMovieSlots(movieID, movieName));
                    result2 = executor.submit(() -> ver2.removeMovieSlots(movieID, movieName));
                    result3 = executor.submit(() -> ver3.removeMovieSlots(movieID, movieName));
                } else if (userID.substring(0, 3).equals("OUT")) {
                    result1 = executor.submit(() -> out.removeMovieSlots(movieID, movieName));
                    result2 = executor.submit(() -> out2.removeMovieSlots(movieID, movieName));
                    result3 = executor.submit(() -> out3.removeMovieSlots(movieID, movieName));
                }

                try {
                    String resultString1 = result1.get();
                    String resultString2 = result2.get();
                    String resultString3 = result3.get();

                    removeSuccess = resultString1 + "," + resultString2 + "," + resultString3;
                    System.out.println("removeSuccess: " + removeSuccess);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                DatagramSocket dsSend = new DatagramSocket();
                InetAddress ip = InetAddress.getByName("192.168.56.1");
                byte buf[] = null;

                buf = removeSuccess.getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, 5002);
                dsSend.send(packet);
                dsSend.close();

                executor.shutdown();
            } else if (function[0].equals("listMovie")) {

                String showList = "";
                String userID = function[1];
                String movieName = function[2];

                if (userID.substring(0, 3).equals("ATW")) {
                    showList = atw.listMovieShowsAvailability(movieName);
                } else if (userID.substring(0, 3).equals("VER")) {
                    showList = ver.listMovieShowsAvailability(movieName);
                } else if (userID.substring(0, 3).equals("OUT")) {
                    showList = out.listMovieShowsAvailability(movieName);
                }

                DatagramSocket dsSend = new DatagramSocket();
                InetAddress ip = InetAddress.getByName("192.168.56.1");
                byte buf[] = null;

                buf = showList.getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, 5002);
                dsSend.send(packet);
                dsSend.close();
            } else if (function[0].equals("getMovieSchedule")) {

                String showList = "";
                String userID = function[1];
                if (userID.substring(0, 3).equals("ATW")) {
                    showList = atw.getBookingSchedule(userID);
                } else if (userID.substring(0, 3).equals("VER")) {
                    showList = ver.getBookingSchedule(userID);
                } else if (userID.substring(0, 3).equals("OUT")) {
                    showList = out.getBookingSchedule(userID);
                }

                DatagramSocket dsSend = new DatagramSocket();
                InetAddress ip = InetAddress.getByName("192.168.56.1");
                byte buf[] = null;

                buf = showList.getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, 5002);
                dsSend.send(packet);
                dsSend.close();
            } else if (function[0].equals("cancelMovie")) {

                String userID = function[1];
                String movieID = function[2];
                String movieName = function[3];
                int nooftktstocancel = Integer.parseInt(function[4]);

                String cancelConfirmation = "";

                if (movieID.substring(0, 3).equals("ATW")) {
                    cancelConfirmation = atw.cancelMovieTickets(userID, movieID, movieName, nooftktstocancel);
                } else if (movieID.substring(0, 3).equals("VER")) {
                    cancelConfirmation = ver.cancelMovieTickets(userID, movieID, movieName, nooftktstocancel);
                } else if (movieID.substring(0, 3).equals("OUT")) {
                    cancelConfirmation = out.cancelMovieTickets(userID, movieID, movieName, nooftktstocancel);
                }

                DatagramSocket dsSend = new DatagramSocket();
                InetAddress ip = InetAddress.getByName("192.168.56.1");
                byte buf[] = null;

                buf = cancelConfirmation.getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, 5002);
                dsSend.send(packet);
                dsSend.close();
            } else if (function[0].equals("bookingExchange")) {

                String userID = function[1];
                String movieID = function[2];
                String movieName = function[3];;
                String newMovieID = function[4];;
                String new_movieName = function[5];;
                Double noofexch = Double.parseDouble(function[6]);

                String bookingExhange = "";

                if (movieID.substring(0, 3).equals("ATW")) {
                    bookingExhange = atw.exchangeTickets(userID, movieID, newMovieID, movieName, new_movieName, noofexch);
                } else if (movieID.substring(0, 3).equals("VER")) {
                    bookingExhange = ver.exchangeTickets(userID, movieID, newMovieID, movieName, new_movieName, noofexch);
                } else if (movieID.substring(0, 3).equals("OUT")) {
                    bookingExhange = out.exchangeTickets(userID, movieID, newMovieID, movieName, new_movieName, noofexch);
                }

                if (bookingExhange.equals("booked")) {
                    System.out.println(noofexch + " Tickets exchanged for Movie " + new_movieName);
                } else if (bookingExhange.equals("capntavl")) {
                    System.out.println("Entered No of tickets required could not be exchanged for the Movie ");
                } else if (bookingExhange.equals("otherbkgcntexcd")) {
                    System.out.println("Max limit to book movie in another theatre exhausted for this week.");
                } else if (bookingExhange.equals("bookatw")) {
                    bookingExhange = atw.bookMovieTickets(userID, newMovieID, new_movieName, noofexch);
                    if (bookingExhange.equals("booked")) {
                        System.out.println(noofexch + " Tickets exchanged for Movie " + new_movieName);
                    } else if (bookingExhange.equals("capntavl")) {
                        System.out.println("Entered No of tickets required could not be exchanged for the Movie  ");
                    } else if (bookingExhange.equals("otherbkgcntexcd")) {
                        System.out.println("Max limit to book movie in another theatre exhausted for this week.");
                    }
                } else if (bookingExhange.equals("bookver")) {
                    bookingExhange = ver.bookMovieTickets(userID, newMovieID, new_movieName, noofexch);
                    if (bookingExhange.equals("booked")) {
                        System.out.println(noofexch + " Tickets exchanged for Movie " + new_movieName);
                    } else if (bookingExhange.equals("capntavl")) {
                        System.out.println("Entered No of tickets required could not be exchanged for the Movie  ");
                    } else if (bookingExhange.equals("otherbkgcntexcd")) {
                        System.out.println("Max limit to book movie in another theatre exhausted for this week.");
                    }
                } else if (bookingExhange.equals("bookout")) {
                    bookingExhange = out.bookMovieTickets(userID, newMovieID, new_movieName, noofexch);
                    if (bookingExhange.equals("booked")) {
                        System.out.println(noofexch + " Tickets exchanged for Movie " + new_movieName);
                    } else if (bookingExhange.equals("capntavl")) {
                        System.out.println("Entered No of tickets required could not be exchanged for the Movie  ");
                    } else if (bookingExhange.equals("otherbkgcntexcd")) {
                        System.out.println("Max limit to book movie in another theatre exhausted for this week.");
                    }
                } else if (bookingExhange.equals("n")) {
                    System.out.println("Some error occured during booking. Booking could not be completed.");
                } else if (bookingExhange.equals("doesnotcontain")) {
                    System.out.println("No bookings for this customer id.");
                } else if (bookingExhange.equals("olddoesntexist")) {
                    System.out.println("No bookings for this MOVIE id by the customer.");
                }

                DatagramSocket dsSend = new DatagramSocket();
                InetAddress ip = InetAddress.getByName("192.168.56.1");
                byte buf[] = null;

                buf = bookingExhange.getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, 5002);
                dsSend.send(packet);
                dsSend.close();

            } else if (function[0].equals("bookMovie")) {

                String userID = function[1];
                String movieID = function[2];
                String movieName = function[3];
                int BookingCapacity = Integer.parseInt(function[4]);
                String movieFound = "";

                String bookingConfirmation = "";
                if (userID.substring(0, 3).equals("ATW")) {
                    movieFound = atw.searchSlotForBooking(movieID, movieName);
                } else if (userID.substring(0, 3).equals("VER")) {
                    movieFound = ver.searchSlotForBooking(movieID, movieName);
                } else if (userID.substring(0, 3).equals("OUT")) {
                    movieFound = out.searchSlotForBooking(movieID, movieName);
                }

                if (movieFound.equals("y")) {
                    if (userID.substring(0, 3).equals("ATW")) {
                        bookingConfirmation = atw.bookMovieTickets(userID, movieID, movieName, BookingCapacity);
                    } else if (userID.substring(0, 3).equals("VER")) {
                        bookingConfirmation = ver.bookMovieTickets(userID, movieID, movieName, BookingCapacity);
                    } else if (userID.substring(0, 3).equals("OUT")) {
                        bookingConfirmation = out.bookMovieTickets(userID, movieID, movieName, BookingCapacity);
                    }
                } else {
                    bookingConfirmation = "notavl";
                }

                DatagramSocket dsSend = new DatagramSocket();
                InetAddress ip = InetAddress.getByName("192.168.56.1");
                byte buf[] = null;

                buf = bookingConfirmation.getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, 5002);
                dsSend.send(packet);
                dsSend.close();
            }

            receive = new byte[65535];

        }
    }

}
