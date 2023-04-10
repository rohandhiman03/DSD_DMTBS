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
import java.util.concurrent.CompletableFuture;
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
    public static IATW atw;
    public static IATW atw2;
    public static IATW atw3;
    public static IVER ver;
    public static IVER ver2;
    public static IVER ver3;
    public static IOUT out;
    public static IOUT out2;
    public static IOUT out3;

    public static void main(String[] args) throws SocketException, IOException, ExecutionException, InterruptedException {
        DatagramSocket ds = new DatagramSocket(5005);
        byte[] receive = new byte[65535];

        URL urlATW = new URL("http://10.0.0.34:8080/movieATW?wsdl");
        QName qNameATW = new QName("http://WebService/", "ATWImplService");
        Service serviceATW = Service.create(urlATW, qNameATW);
        atw = serviceATW.getPort(IATW.class);
        
        URL urlATW2 = new URL("http://10.0.0.170:8083/movieATW?wsdl");
        QName qNameATW2 = new QName("http://WebService/", "ATWImplService");
        Service serviceATW2 = Service.create(urlATW2, qNameATW2);
        atw2 = serviceATW2.getPort(IATW.class);

        URL urlATW3 = new URL("http://10.0.0.34:8080/movieATW?wsdl");
        QName qNameATW3 = new QName("http://WebService/", "ATWImplService");
        Service serviceATW3 = Service.create(urlATW3, qNameATW3);
        atw3 = serviceATW3.getPort(IATW.class);

        URL urlVER = new URL("http://10.0.0.34:8081/movieVER?wsdl");
        QName qNameVER = new QName("http://WebService/", "VERImplService");
        Service serviceVER = Service.create(urlVER, qNameVER);
        ver = serviceVER.getPort(IVER.class);

        URL urlVER2 = new URL("http://10.0.0.170:8084/movieVER?wsdl");
        QName qNameVER2 = new QName("http://WebService/", "VERImplService");
        Service serviceVER2 = Service.create(urlVER2, qNameVER2);
        ver2 = serviceVER2.getPort(IVER.class);

        URL urlVER3 = new URL("http://10.0.0.34:8081/movieVER?wsdl");
        QName qNameVER3 = new QName("http://WebService/", "VERImplService");
        Service serviceVER3 = Service.create(urlVER3, qNameVER3);
        ver3 = serviceVER3.getPort(IVER.class);

        URL urlOUT = new URL("http://10.0.0.34:8082/movieOUT?wsdl");
        QName qNameOUT = new QName("http://WebService/", "OUTImplService");
        Service serviceOUT = Service.create(urlOUT, qNameOUT);
        out = serviceOUT.getPort(IOUT.class);

        URL urlOUT2 = new URL("http://10.0.0.170:8085/movieOUT?wsdl");
        QName qNameOUT2 = new QName("http://WebService/", "OUTImplService");
        Service serviceOUT2 = Service.create(urlOUT2, qNameOUT2);
        out2 = serviceOUT2.getPort(IOUT.class);

        URL urlOUT3 = new URL("http://10.0.0.34:8082/movieOUT?wsdl");
        QName qNameOUT3 = new QName("http://WebService/", "OUTImplService");
        Service serviceOUT3 = Service.create(urlOUT3, qNameOUT3);
        out3 = serviceOUT3.getPort(IOUT.class);

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
                int time_out = Integer.parseInt(function[5]);
                System.out.println("TimeOut" + time_out);
                String ssf = function[6];
                Future<String> result1 = CompletableFuture.completedFuture(" ");
                Future<String> result2 = CompletableFuture.completedFuture(" ");
                Future<String> result3 = CompletableFuture.completedFuture(" ");

                String bookingSuccess = "";
                try{
                if (userID.substring(0, 3).equals("ATW")) {
                    result1 = executor.submit(() -> atw.addMovieSlots(movieID, movieName, BookingCapacity));
                    Thread.sleep(time_out);
                    result2 = executor.submit(() -> atw2.addMovieSlots(movieID, movieName, BookingCapacity));
                    if(ssf.equals("y")){executor.shutdown();}
                    result3 = executor.submit(() -> atw3.addMovieSlots(movieID, movieName, BookingCapacity));

                } else if (userID.substring(0, 3).equals("VER")) {
                    result1 = executor.submit(() -> ver.addMovieSlots(movieID, movieName, BookingCapacity));
                    result2 = executor.submit(() -> ver2.addMovieSlots(movieID, movieName, BookingCapacity));
                    result3 = executor.submit(() -> ver3.addMovieSlots(movieID, movieName, BookingCapacity));
                } else if (userID.substring(0, 3).equals("OUT")) {
                    result1 = executor.submit(() -> out.addMovieSlots(movieID, movieName, BookingCapacity));
                    result2 = executor.submit(() -> out2.addMovieSlots(movieID, movieName, BookingCapacity));
                    result3 = executor.submit(() -> out3.addMovieSlots(movieID, movieName, BookingCapacity));
                }}
                catch(Exception e){System.out.println(e);}

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
                InetAddress ip = InetAddress.getByName("10.0.0.34");
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
                InetAddress ip = InetAddress.getByName("10.0.0.34");
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
                Future<String> result1 = null;
                Future<String> result2 = null;
                Future<String> result3 = null;

                if (userID.substring(0, 3).equals("ATW")) {
                    result1 = executor.submit(() -> atw.listMovieShowsAvailability(movieName));
                    result2 = executor.submit(() -> atw2.listMovieShowsAvailability(movieName));
                    result3 = executor.submit(() -> atw3.listMovieShowsAvailability(movieName));
                } else if (userID.substring(0, 3).equals("VER")) {
                    result1 = executor.submit(() -> ver.listMovieShowsAvailability(movieName));
                    result2 = executor.submit(() -> ver2.listMovieShowsAvailability(movieName));
                    result3 = executor.submit(() -> ver3.listMovieShowsAvailability(movieName));
                } else if (userID.substring(0, 3).equals("OUT")) {
                    result1 = executor.submit(() -> out.listMovieShowsAvailability(movieName));
                    result2 = executor.submit(() -> out2.listMovieShowsAvailability(movieName));
                    result3 = executor.submit(() -> out3.listMovieShowsAvailability(movieName));
                }

                try {
                    String resultString1 = result1.get();
                    String resultString2 = result2.get();
                    String resultString3 = result3.get();

                    showList = resultString1 + "," + resultString2 + "," + resultString3;
                    System.out.println("showList: " + showList);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                DatagramSocket dsSend = new DatagramSocket();
                InetAddress ip = InetAddress.getByName("10.0.0.34");
                byte buf[] = null;

                buf = showList.getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, 5002);
                dsSend.send(packet);
                dsSend.close();
                executor.shutdown();

            } else if (function[0].equals("getMovieSchedule")) {

                String showList = "";
                Future<String> result1 = null;
                Future<String> result2 = null;
                Future<String> result3 = null;
                String userID = function[1];
                if (userID.substring(0, 3).equals("ATW")) {
                    result1 = executor.submit(() -> atw.getBookingSchedule(userID));
                    result2 = executor.submit(() -> atw2.getBookingSchedule(userID));
                    result3 = executor.submit(() -> atw3.getBookingSchedule(userID));
                } else if (userID.substring(0, 3).equals("VER")) {
                    result1 = executor.submit(() -> ver.getBookingSchedule(userID));
                    result2 = executor.submit(() -> ver2.getBookingSchedule(userID));
                    result3 = executor.submit(() -> ver3.getBookingSchedule(userID));
                } else if (userID.substring(0, 3).equals("OUT")) {
                    result1 = executor.submit(() -> out.getBookingSchedule(userID));
                    result2 = executor.submit(() -> out2.getBookingSchedule(userID));
                    result3 = executor.submit(() -> out3.getBookingSchedule(userID));
                }

                try {
                    String resultString1 = result1.get();
                    String resultString2 = result2.get();
                    String resultString3 = result3.get();

                    showList = resultString1 + "," + resultString2 + "," + resultString3;
                    System.out.println("showList: " + showList);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                DatagramSocket dsSend = new DatagramSocket();
                InetAddress ip = InetAddress.getByName("10.0.0.34");
                byte buf[] = null;

                buf = showList.getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, 5002);
                dsSend.send(packet);
                dsSend.close();
                executor.shutdown();
            } else if (function[0].equals("cancelMovie")) {

                String userID = function[1];
                String movieID = function[2];
                String movieName = function[3];
                int nooftktstocancel = Integer.parseInt(function[4]);
                Future<String> result1 = null;
                Future<String> result2 = null;
                Future<String> result3 = null;
                String cancelConfirmation = "";

                if (movieID.substring(0, 3).equals("ATW")) {

                    result1 = executor.submit(() -> atw.cancelMovieTickets(userID, movieID, movieName, nooftktstocancel));
                    result2 = executor.submit(() -> atw2.cancelMovieTickets(userID, movieID, movieName, nooftktstocancel));
                    result3 = executor.submit(() -> atw3.cancelMovieTickets(userID, movieID, movieName, nooftktstocancel));

                } else if (movieID.substring(0, 3).equals("VER")) {
                    result1 = executor.submit(() -> ver.cancelMovieTickets(userID, movieID, movieName, nooftktstocancel));
                    result2 = executor.submit(() -> ver2.cancelMovieTickets(userID, movieID, movieName, nooftktstocancel));
                    result3 = executor.submit(() -> ver3.cancelMovieTickets(userID, movieID, movieName, nooftktstocancel));
                } else if (movieID.substring(0, 3).equals("OUT")) {
                    result1 = executor.submit(() -> out.cancelMovieTickets(userID, movieID, movieName, nooftktstocancel));
                    result2 = executor.submit(() -> out2.cancelMovieTickets(userID, movieID, movieName, nooftktstocancel));
                    result3 = executor.submit(() -> out3.cancelMovieTickets(userID, movieID, movieName, nooftktstocancel));
                }

                try {
                    String resultString1 = result1.get();
                    String resultString2 = result2.get();
                    String resultString3 = result3.get();

                    cancelConfirmation = resultString1 + "," + resultString2 + "," + resultString3;
                    System.out.println("cancelConfirmation: " + cancelConfirmation);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                DatagramSocket dsSend = new DatagramSocket();
                InetAddress ip = InetAddress.getByName("10.0.0.34");
                byte buf[] = null;

                buf = cancelConfirmation.getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, 5002);
                dsSend.send(packet);
                dsSend.close();
                executor.shutdown();
            } else if (function[0].equals("bookingExchange")) {

                String userID = function[1];
                String movieID = function[2];
                String movieName = function[3];
                String newMovieID = function[4];
                String new_movieName = function[5];
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
                InetAddress ip = InetAddress.getByName("10.0.0.34");
                byte buf[] = null;

                buf = bookingExhange.getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, 5002);
                dsSend.send(packet);
                dsSend.close();
                executor.shutdown();
            } else if (function[0].equals("bookMovie")) {

                String userID = function[1];
                String movieID = function[2];
                String movieName = function[3];
                int BookingCapacity = Integer.parseInt(function[4]);
                Future<String> result1 = null;
                Future<String> result2 = null;
                Future<String> result3 = null;
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
                        result1 = executor.submit(() -> atw.bookMovieTickets(userID, movieID, movieName, BookingCapacity));
                        result2 = executor.submit(() -> atw2.bookMovieTickets(userID, movieID, movieName, BookingCapacity));
                        result3 = executor.submit(() -> atw3.bookMovieTickets(userID, movieID, movieName, BookingCapacity));
                    } else if (userID.substring(0, 3).equals("VER")) {
                        result1 = executor.submit(() -> ver.bookMovieTickets(userID, movieID, movieName, BookingCapacity));
                        result2 = executor.submit(() -> ver2.bookMovieTickets(userID, movieID, movieName, BookingCapacity));
                        result3 = executor.submit(() -> ver3.bookMovieTickets(userID, movieID, movieName, BookingCapacity));
                    } else if (userID.substring(0, 3).equals("OUT")) {
                        result1 = executor.submit(() -> out.bookMovieTickets(userID, movieID, movieName, BookingCapacity));
                        result2 = executor.submit(() -> out2.bookMovieTickets(userID, movieID, movieName, BookingCapacity));
                        result3 = executor.submit(() -> out3.bookMovieTickets(userID, movieID, movieName, BookingCapacity));
                    }
                    try {
                        String resultString1 = result1.get();
                        String resultString2 = result2.get();
                        String resultString3 = result3.get();

                        bookingConfirmation = resultString1 + "," + resultString2 + "," + resultString3;
                        System.out.println("bookingSuccess: " + bookingConfirmation);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    bookingConfirmation = "notavl,notavl,notavl";
                }

                DatagramSocket dsSend = new DatagramSocket();
                InetAddress ip = InetAddress.getByName("10.0.0.34");
                byte buf[] = null;

                buf = bookingConfirmation.getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, 5002);
                dsSend.send(packet);
                dsSend.close();
                executor.shutdown();
            }

            receive = new byte[65535];

        }
    }

}
