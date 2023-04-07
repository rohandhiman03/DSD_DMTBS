/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WebService;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import javax.jws.WebService;

/**
 *
 * @author Rohan
 */
@WebService(endpointInterface="WebService.IVER")
public class VERImpl implements IVER{
    
    @Override
    public String addUser(String UserID) {
        PublishVER.users.add(UserID);
        return "y";
    }

    @Override
    public String checkUserExists(String UserID) {
        if (!UserID.isEmpty()) {
            return "y";
        } else {
            return "n";
        }
    }

    @Override
    public String addMovieSlots(String movieId, String movieName, double BookingCapacity) {
        HashMap<String, Double> checkExist = PublishVER.allmovieDetails.get(movieName);
        double currentCapacity = 0;
        double newCapacity = 0;
        String request = "Movie ID " + movieId + " Movie Name " + movieName + " Booking Capacity " + BookingCapacity;
        if (checkExist.containsKey(movieId)) {
            currentCapacity = checkExist.get(movieId);
            newCapacity = currentCapacity + BookingCapacity;
            PublishVER.allmovieDetails.get(movieName).put(movieId, newCapacity);
            LogWritterGeneral("Not in request", "addMovieSlots", request, "Failed", "exists");
            return "exists";
        }
        if (movieName.equals("Avengers")) {
            PublishVER.moviedetailAvengers.put(movieId, BookingCapacity);

        } else if (movieName.equals("Avatar")) {
            PublishVER.moviedetailAvatar.put(movieId, BookingCapacity);
        } else if (movieName.equals("Titanic")) {
            PublishVER.moviedetailtTitanic.put(movieId, BookingCapacity);
        }

        System.out.println("All Movie Det: " + PublishVER.allmovieDetails);
        LogWritterGeneral("Not in request", "addMovieSlots", request, "Success", "y");

        return "y";
    }

    @Override
    public String removeMovieSlots(String movieId, String movieName) {
        String request = "Movie ID " + movieId + " Movie Name " + movieName;

        if (PublishVER.allmovieDetails.get(movieName).size() == 0) {
            LogWritterGeneral("Not in request", "removeMovieSlots", request, "Failed", "n");

            return "n";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = "20" + movieId.substring(8, 10) + "-" + movieId.substring(6, 8) + "-" + movieId.substring(4, 6);
        Date inputDate = null;
        try {
            inputDate = dateFormat.parse(date);
        } catch (ParseException ex) {
            LogWritterGeneral("Not in request", "removeMovieSlots", request, "Failed", "Invalid Date");

            System.out.println("Invalid Date " + date + " " + ex);
        }
        Date currentDate = new Date();
        String formattedDate = dateFormat.format(currentDate);
        try {
            currentDate = dateFormat.parse(formattedDate);
        } catch (ParseException ex) {
            System.out.println(ex);
        }
        if (!inputDate.equals(currentDate)) {
            if (inputDate.before(currentDate)) {
                LogWritterGeneral("Not in request", "removeMovieSlots", request, "Failed", "olddate");

                return "olddate";
            }
        }
        HashMap<String, Double> checkExist = PublishVER.allmovieDetails.get(movieName);
        if (!checkExist.containsKey(movieId)) {
            LogWritterGeneral("Not in request", "removeMovieSlots", request, "Failed", "n");

            return "n";
        } else {
            PublishVER.allmovieDetails.get(movieName).remove(movieId);
            LogWritterGeneral("Not in request", "removeMovieSlots", request, "Success", "y");
            System.out.println("All Movie Det After Remove Funct: " + PublishVER.allmovieDetails);
            return "y";

        }
    }

    @Override
    public String listMovieShowsAvailability(String movieName) {
        String request = "Movie Name " + movieName;

        try {       //For ATWATER
            DatagramSocket ds = new DatagramSocket();

            InetAddress ip = InetAddress.getLocalHost();
            byte buf[] = null;

            String inp = "listVER" + movieName;

            buf = inp.getBytes();

            DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, 3034);  //PACKET FOR ATWATER

            LogWritterGeneral("Not in request", "listMovieShowsAvailability-UDP Request", inp, "Success", "request sent");

            ds.send(DpSend);

            HashMap<String, Double> data = new HashMap<String, Double>();

            DatagramSocket ds1 = new DatagramSocket(3002); // receiving socket 

            byte[] receive = new byte[65535];

            DatagramPacket DpReceive = null;
            while (data.isEmpty()) {
                DpReceive = new DatagramPacket(receive, receive.length);

                ds1.receive(DpReceive);

                ByteArrayInputStream bais = new ByteArrayInputStream(DpReceive.getData());
                ObjectInputStream ois = new ObjectInputStream(bais);
                data = (HashMap<String, Double>) ois.readObject();

            }
            LogWritterGeneral("Not in request", "listMovieShowsAvailability-UDP Response", data.toString(), "Success", "response received");

            System.out.println("Received data: " + data);
            PublishVER.movieDetailsToDisplay.putAll(data);
            receive = new byte[65535];
            ds1.close();

        } catch (Exception e) {
            System.out.println(e);
        }

        try {       //For OUTREMONT
            DatagramSocket ds = new DatagramSocket();

            InetAddress ip = InetAddress.getLocalHost();
            byte buf[] = null;

            String inp = "listVER" + movieName;

            buf = inp.getBytes();

            DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, 3036);  //PACKET FOR OUTREMONT

            LogWritterGeneral("Not in request", "listMovieShowsAvailability-UDP Request", inp, "Success", "request sent");

            ds.send(DpSend);

            HashMap<String, Double> data = new HashMap<String, Double>();

            DatagramSocket ds1 = new DatagramSocket(3002); // receiving socket 

            byte[] receive = new byte[65535];

            DatagramPacket DpReceive = null;
            while (data.isEmpty()) {
                DpReceive = new DatagramPacket(receive, receive.length);

                ds1.receive(DpReceive);

                ByteArrayInputStream bais = new ByteArrayInputStream(DpReceive.getData());
                ObjectInputStream ois = new ObjectInputStream(bais);
                data = (HashMap<String, Double>) ois.readObject();

            }
            LogWritterGeneral("Not in request", "listMovieShowsAvailability-UDP Response", data.toString(), "Success", "response received");

            System.out.println("Received data: " + data);
            PublishVER.movieDetailsToDisplay.putAll(data);
            receive = new byte[65535];
            ds1.close();

        } catch (Exception e) {
            System.out.println(e);
        }
        HashMap<String, Double> movieList = PublishVER.allmovieDetails.get(movieName);
        HashMap<String, Double> finalReturnMap = new HashMap<String, Double>();
        finalReturnMap.putAll(movieList);
        finalReturnMap.putAll(PublishVER.movieDetailsToDisplay);

        PublishVER.movieDetailsToDisplay.clear();
        if (finalReturnMap.containsKey("0")) {
            finalReturnMap.remove("0");
        }

        LogWritterGeneral("Not in request", "listMovieShowsAvailability", request, "Success", finalReturnMap.toString());

        return finalReturnMap.toString();
    }

    @Override
    public String searchSlotForBooking(String movieId, String movieName) {
        String request = "Movie ID " + movieId + " Movie Name " + movieName;
        if (PublishVER.allmovieDetails.get(movieName).size() == 0) {
            LogWritterGeneral("Not in request", "searchSlotForBooking", request, "Success", "n");
            return "n";
        }

        HashMap<String, Double> checkExist = PublishVER.allmovieDetails.get(movieName);
        if (checkExist.containsKey(movieId)) {
            LogWritterGeneral("Not in request", "searchSlotForBooking", request, "Success", "y");
            return "y";
        } else {
            LogWritterGeneral("Not in request", "searchSlotForBooking", request, "Success", "n");
            return "n";
        }
    }

    @Override
    public String bookMovieTickets(String customerID, String movieID, String movieName, double numberOfTickets) {
        String request = "Customer ID " + customerID + " Movie ID " + movieID + " Movie Name " + movieName + " Number Of Tickets " + numberOfTickets;
        HashMap<String, Double> checkExist = PublishVER.allmovieDetails.get(movieName);
        HashMap<String, Double> customerMovieMap = new HashMap<String, Double>();
        double currentCapacityMovie = 0;
        double currentCapacityBooked = 0;
        double newCapacityMovie = 0;
        double countOtherBkg = 0;
        double existCount = 0;
        if (PublishVER.allbookingDetails.containsKey(customerID)) {
            customerMovieMap = PublishVER.allbookingDetails.get(customerID);
            if (customerMovieMap.containsKey(movieID)) {
                currentCapacityBooked = customerMovieMap.get(movieID);
                existCount = 1;
            }
        }

        if (PublishVER.customerOtherBkgCount.containsKey(customerID)) {
            countOtherBkg = PublishVER.customerOtherBkgCount.get(customerID);
        } else {
            PublishVER.customerOtherBkgCount.put(customerID, countOtherBkg);
        }
        currentCapacityMovie = checkExist.get(movieID);

        if (currentCapacityMovie >= numberOfTickets) {
            if (!customerID.substring(0, 3).equals(movieID.substring(0, 3))) {
                if (countOtherBkg == 3) {
                    LogWritterGeneral(customerID, "bookMovieTickets", request, "Failed", "otherbkgcntexcd");

                    return "otherbkgcntexcd";
                } else {
                    countOtherBkg++;
                    try {       //For ATWATER
                        DatagramSocket ds = new DatagramSocket();

                        InetAddress ip = InetAddress.getLocalHost();
                        byte buf[] = null;

                        String inp = "up" + customerID + countOtherBkg;

                        buf = inp.getBytes();

                        DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, 3034);  //PACKET FOR ATWATER

                        ds.send(DpSend);

                    } catch (Exception e) {
                        System.out.println(e);
                    }

                    try {       //For OUTREMONT
                        DatagramSocket ds = new DatagramSocket();

                        InetAddress ip = InetAddress.getLocalHost();
                        byte buf[] = null;

                        String inp = "up" + customerID + countOtherBkg;

                        buf = inp.getBytes();

                        DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, 3036);  //PACKET FOR OUTREMONT

                        ds.send(DpSend);

                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    PublishVER.customerOtherBkgCount.put(customerID, countOtherBkg);
                }

            }
            newCapacityMovie = currentCapacityMovie - numberOfTickets;
            PublishVER.allmovieDetails.get(movieName).put(movieID, newCapacityMovie);
//            cutomerDetails = new HashMap<String, Integer>();
            PublishVER.allbookingDetails.put(customerID, PublishVER.cutomerDetails);
            if (existCount == 0) {
                PublishVER.allbookingDetails.get(customerID).put(movieID, numberOfTickets);
            } else {
                currentCapacityBooked = currentCapacityBooked + numberOfTickets;
                PublishVER.allbookingDetails.get(customerID).put(movieID, currentCapacityBooked);
            }
            LogWritterGeneral(customerID, "bookMovieTickets", request, "Success", "booked");
            System.out.println("allbookingDetails: " + PublishVER.allbookingDetails.toString());
            System.out.println("customerOtherBkgCount: " + PublishVER.customerOtherBkgCount.toString());
            return "booked";

        } else {
            LogWritterGeneral(customerID, "bookMovieTickets", request, "Failed", "capntavl");

            return "capntavl";
        }
    }

    @Override
    public String getBookingSchedule(String customerID) {
        String request = "Customer ID " + customerID;

        try {       //For ATWATER
            DatagramSocket ds = new DatagramSocket();

            InetAddress ip = InetAddress.getLocalHost();
            byte buf[] = null;

            String inp = "bkngVER" + customerID;

            buf = inp.getBytes();

            DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, 3034);  //PACKET FOR ATWATER

            LogWritterGeneral(customerID, "getBookingSchedule-UDP Request", inp, "Success", "request sent");

            ds.send(DpSend);

            HashMap<String, Double> data = new HashMap<String, Double>();

            DatagramSocket ds1 = new DatagramSocket(3005); // receiving socket 

            byte[] receive = new byte[65535];

            DatagramPacket DpReceive = null;
            while (data.isEmpty()) {
                DpReceive = new DatagramPacket(receive, receive.length);

                ds1.receive(DpReceive);

                ByteArrayInputStream bais = new ByteArrayInputStream(DpReceive.getData());
                ObjectInputStream ois = new ObjectInputStream(bais);
                data = (HashMap<String, Double>) ois.readObject();

            }
            LogWritterGeneral(customerID, "getBookingSchedule-UDP Response", data.toString(), "Success", "response received");

            System.out.println("Received data: " + data);
            PublishVER.customerDetailsToDisplay.putAll(data);
            receive = new byte[65535];
            ds1.close();

        } catch (Exception e) {
            System.out.println(e);
        }

        try {       //For OUTREMONT
            DatagramSocket ds = new DatagramSocket();

            InetAddress ip = InetAddress.getLocalHost();
            byte buf[] = null;

            String inp = "bkngVER" + customerID;

            buf = inp.getBytes();

            DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, 3036);  //PACKET FOR OUTREMONT
            LogWritterGeneral(customerID, "getBookingSchedule-UDP Request", inp, "Success", "request sent");

            ds.send(DpSend);

            HashMap<String, Double> data = new HashMap<String, Double>();

            DatagramSocket ds1 = new DatagramSocket(3005); // receiving socket 

            byte[] receive = new byte[65535];

            DatagramPacket DpReceive = null;
            while (data.isEmpty()) {
                DpReceive = new DatagramPacket(receive, receive.length);

                ds1.receive(DpReceive);

                ByteArrayInputStream bais = new ByteArrayInputStream(DpReceive.getData());
                ObjectInputStream ois = new ObjectInputStream(bais);
                data = (HashMap<String, Double>) ois.readObject();

            }
            LogWritterGeneral(customerID, "getBookingSchedule-UDP Response", data.toString(), "Success", "response received");

            System.out.println("Received data: " + data);
            PublishVER.customerDetailsToDisplay.putAll(data);
            receive = new byte[65535];
            ds1.close();

        } catch (Exception e) {
            System.out.println(e);
        }

        HashMap<String, Double> finalReturnMap = new HashMap<String, Double>();
        HashMap<String, Double> movieList = new HashMap<String, Double>();

        if (!PublishVER.allbookingDetails.isEmpty()) {
            movieList = PublishVER.allbookingDetails.get(customerID);
        }

        finalReturnMap.putAll(PublishVER.customerDetailsToDisplay);
        finalReturnMap.putAll(movieList);

        if (finalReturnMap.containsKey("0")) {
            finalReturnMap.remove("0");
        }

        PublishVER.customerDetailsToDisplay.clear();
        LogWritterGeneral(customerID, "getBookingSchedule", request, "Success", finalReturnMap.toString());

        return finalReturnMap.toString();
    }

    @Override
    public String cancelMovieTickets(String customerID, String movieId, String movieName, double numberOfTickets) {
        String request = "Customer ID " + customerID + " Movie Id " + movieId + " Movie Name " + movieName + " Number Of Tickets " + numberOfTickets;
        HashMap<String, Double> movieList = new HashMap<String, Double>();
        HashMap<String, Double> movieListOnServer = PublishVER.allmovieDetails.get(movieName);

        if (!PublishVER.allbookingDetails.isEmpty()) {
            movieList = PublishVER.allbookingDetails.get(customerID);
        } else {
            LogWritterGeneral(customerID, "cancelMovieTickets", request, "Failed", "nobooking");

            return "nobooking";
        }

        double bkgcapacityonserver = movieListOnServer.get(movieId);
        double noofticketsbooked = movieList.get(movieId);

        if (noofticketsbooked > numberOfTickets) {
            noofticketsbooked = noofticketsbooked - numberOfTickets;
            PublishVER.allbookingDetails.get(customerID).put(movieId, noofticketsbooked);
            bkgcapacityonserver = bkgcapacityonserver + numberOfTickets;
            PublishVER.allmovieDetails.get(movieName).put(movieId, bkgcapacityonserver);
            LogWritterGeneral(customerID, "cancelMovieTickets", request, "Success", "cancelled");

            return "cancelled";
        } else if (noofticketsbooked == numberOfTickets) {
            PublishVER.allbookingDetails.get(customerID).remove(movieId);
            bkgcapacityonserver = bkgcapacityonserver + numberOfTickets;
            PublishVER.allmovieDetails.get(movieName).put(movieId, bkgcapacityonserver);
            LogWritterGeneral(customerID, "cancelMovieTickets", request, "Success", "allcancelled");

            return "allcancelled";
        } else if (noofticketsbooked < numberOfTickets) {
            LogWritterGeneral(customerID, "cancelMovieTickets", request, "Failed", "cncltktexceed");

            return "cncltktexceed";
        } else {
            LogWritterGeneral(customerID, "cancelMovieTickets", request, "Failed", "cncltktexceed");

            return "nobooking";
        }
    }

    private void LogWritterGeneral(String userID, String requestType, String request, String status, String serverResponse) {
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String directory = "C:\\Users\\Rohan\\Documents\\NetBeansProjects\\CORBA_ServerVERLogs" + date;
        try {

            Path path = Paths.get(directory);

            Files.createDirectories(path);

        } catch (IOException e) {
            System.err.println("Failed to create directory!" + e.getMessage());
        }
        String fileName = "C:\\Users\\Rohan\\Documents\\NetBeansProjects\\CORBA_ServerVERLogs" + date + "\\" + "ServerATW" + ".txt";

//        String fileName = "C:\\Users\\Rohan\\Documents\\NetBeansProjects\\ServerATW_Logs_" + date + "\\ServerATW.txt";
        try {
            String datecurr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
            FileWriter myWriter = new FileWriter(fileName, true);
            myWriter.write("\r\n---------------------" + datecurr + "-----------------------\r\n");
            myWriter.write("User : " + userID + "\r\n");
            myWriter.write("Request Type : " + requestType + "\r\n");
            myWriter.write("Request : " + request + "\r\n");
            myWriter.write("Request Status : " + status + "\r\n");
            myWriter.write("Server Response : " + serverResponse + "\r\n");
            myWriter.write("---------------------------------------------------------------\r\n");
            myWriter.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public String exchangeTickets(String customerID, String old_movieID, String new_movieID, String old_movieName, String new_movieName, double numberOfTickets) {
        HashMap<String, Double> movieList = new HashMap<String, Double>();
        if (PublishVER.allbookingDetails.containsKey(customerID)) {
            movieList = PublishVER.allbookingDetails.get(customerID);

            if (movieList.containsKey(old_movieID)) {
                if (numberOfTickets <= movieList.get(old_movieID)) {
                    if (new_movieID.substring(0, 3).equals("VER")) {
                        //local check
                        String cancelSuccess = cancelMovieTickets(customerID, old_movieID, old_movieName, numberOfTickets);
                        String bookSuccess = bookMovieTickets(customerID, new_movieID, new_movieName, numberOfTickets);
                        return bookSuccess;
                    } else if (new_movieID.substring(0, 3).equals("ATW")) {
                        try {       //For ATWATER
                            DatagramSocket ds = new DatagramSocket();

                            InetAddress ip = InetAddress.getLocalHost();
                            byte buf[] = null;

                            String inp = "chkavlVER" + new_movieID + new_movieName;

                            buf = inp.getBytes();

                            DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, 3034);  //PACKET FOR ATWATER

//                        LogWritterGeneral(customerID, "getBookingSchedule-UDP Request", inp, "Success", "request sent");
                            ds.send(DpSend);

//                        HashMap<String, Double> data = new HashMap<String, Double>();
                            String data = "";
                            DatagramSocket ds1 = new DatagramSocket(3005); // receiving socket 

                            byte[] receive = new byte[65535];

                            DatagramPacket DpReceive = null;
                            while (data.isEmpty()) {
                                DpReceive = new DatagramPacket(receive, receive.length);

                                ds1.receive(DpReceive);

                                data = data(receive).toString();

                            }
//                        LogWritterGeneral(customerID, "getBookingSchedule-UDP Response", data.toString(), "Success", "response received");

                            System.out.println("Received data: " + data);

                            receive = new byte[65535];
                            ds1.close();

                            if (data.equals("y")) {
                                //book in VER
                                String cancelsuccess = cancelMovieTickets(customerID, old_movieID, old_movieName, numberOfTickets);
                                System.out.println("Cancel Success " + cancelsuccess);
                                return "bookatw";
                            } else {
                                return "n";
                            }

                        } catch (Exception e) {
                            System.out.println(e);
                        }

                    } else if (new_movieID.substring(0, 3).equals("OUT")) {
                        try {       //For OUTREMONT
                            DatagramSocket ds = new DatagramSocket();

                            InetAddress ip = InetAddress.getLocalHost();
                            byte buf[] = null;

                            String inp = "chkavlVER" + new_movieID + new_movieName;

                            buf = inp.getBytes();

                            DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, 3036);  //PACKET FOR OUTREMONT

//                        LogWritterGeneral(customerID, "getBookingSchedule-UDP Request", inp, "Success", "request sent");
                            ds.send(DpSend);

//                        HashMap<String, Double> data = new HashMap<String, Double>();
                            String data = "";
                            DatagramSocket ds1 = new DatagramSocket(3005); // receiving socket 

                            byte[] receive = new byte[65535];

                            DatagramPacket DpReceive = null;
                            while (data.isEmpty()) {
                                DpReceive = new DatagramPacket(receive, receive.length);

                                ds1.receive(DpReceive);

                                data = data(receive).toString();

                            }
//                        LogWritterGeneral(customerID, "getBookingSchedule-UDP Response", data.toString(), "Success", "response received");

                            System.out.println("Received data: " + data);

                            receive = new byte[65535];
                            ds1.close();

                            if (data.equals("y")) {
                                //book in VER
                                String cancelsuccess = cancelMovieTickets(customerID, old_movieID, old_movieName, numberOfTickets);
                                System.out.println("Cancel Success " + cancelsuccess);
                                return "bookout";
                            } else {
                                return "n";
                            }

                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                }
                 else{
                    return "capntavl";
                }
            } else {
                return "olddoesntexist";
            }
        } else {
            return "doesnotcontain";
        }
        return "y";
    }

    public static StringBuilder data(byte[] a) {
        if (a == null) {
            return null;
        }
        StringBuilder ret = new StringBuilder();
        int i = 0;
        while (a[i] != 0) {
            ret.append((char) a[i]);
            i++;
        }
        return ret;
    }
}
