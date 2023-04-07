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

@WebService(endpointInterface="WebService.IATW")

public class ATWImpl implements IATW{
    
    @Override
    public String addUser(String UserID) {
        PublishATW.users.add(UserID);
        return "y";
    }

    @Override
    public String checkUserExists(String UserID) {
//        if (PublishATW.users.contains(UserID)) {
        if (!UserID.isEmpty()) {
            return "y";
        } else {
            return "n";
        }
    }

    @Override
    public String addMovieSlots(String movieId, String movieName, double BookingCapacity) {
        HashMap<String, Double> checkExist = PublishATW.allmovieDetails.get(movieName);
        double currentCapacity = 0;
        double newCapacity = 0;
        String request = "Movie ID " + movieId + " Movie Name " + movieName + " Booking Capacity " + BookingCapacity;
        if (checkExist.containsKey(movieId)) {
            currentCapacity = checkExist.get(movieId);
            newCapacity = currentCapacity + BookingCapacity;
            PublishATW.allmovieDetails.get(movieName).put(movieId, newCapacity);
            LogWritterGeneral("Not in request", "addMovieSlots", request, "Failed", "exists");
            return "exists";
        }
        if (movieName.equals("Avengers")) {
            PublishATW.moviedetailAvengers.put(movieId, BookingCapacity);

        } else if (movieName.equals("Avatar")) {
            PublishATW.moviedetailAvatar.put(movieId, BookingCapacity);
        } else if (movieName.equals("Titanic")) {
            PublishATW.moviedetailtTitanic.put(movieId, BookingCapacity);
        }

        System.out.println("All Movie Det: " + PublishATW.allmovieDetails);
        LogWritterGeneral("Not in request", "addMovieSlots", request, "Success", "y");

        return "y";
    }

    @Override
    public String removeMovieSlots(String movieId, String movieName) {
        String request = "Movie ID " + movieId + " Movie Name " + movieName;

        if (PublishATW.allmovieDetails.get(movieName).size() == 0) {
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
        HashMap<String, Double> checkExist = PublishATW.allmovieDetails.get(movieName);
        if (!checkExist.containsKey(movieId)) {
            LogWritterGeneral("Not in request", "removeMovieSlots", request, "Failed", "n");

            return "n";
        } else {
            PublishATW.allmovieDetails.get(movieName).remove(movieId);
            LogWritterGeneral("Not in request", "removeMovieSlots", request, "Success", "y");
            System.out.println("All Movie Det After Remove Funct: " + PublishATW.allmovieDetails);
            return "y";

        }
    }

    @Override
    public String listMovieShowsAvailability(String movieName) {
        String request = "Movie Name " + movieName;
        try {       //For VERDUN
            DatagramSocket ds = new DatagramSocket();

            InetAddress ip = InetAddress.getLocalHost();
            byte buf[] = null;

            String inp = "listATW" + movieName;

            buf = inp.getBytes();

            DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, 3035);  //PACKET FOR VERDUN

            LogWritterGeneral("Not in request", "listMovieShowsAvailability-UDP Request", inp, "Success", "request sent");

            ds.send(DpSend);

            HashMap<String, Double> data = new HashMap<String, Double>();

            DatagramSocket ds1 = new DatagramSocket(3001); // receiving socket 

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
            PublishATW.movieDetailsToDisplay.putAll(data);
            receive = new byte[65535];
            ds1.close();

        } catch (Exception e) {
            System.out.println(e);
        }

        try {       //For OUTREMONT
            DatagramSocket ds = new DatagramSocket();

            InetAddress ip = InetAddress.getLocalHost();
            byte buf[] = null;

            String inp = "listATW" + movieName;

            buf = inp.getBytes();

            DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, 3036);  //PACKET FOR OUTREMONT
            LogWritterGeneral("Not in request", "listMovieShowsAvailability-UDP Request", inp, "Success", "request sent");

            ds.send(DpSend);

            HashMap<String, Double> data = new HashMap<String, Double>();

            DatagramSocket ds1 = new DatagramSocket(3001); // receiving socket 

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
            PublishATW.movieDetailsToDisplay.putAll(data);
            receive = new byte[65535];
            ds1.close();

        } catch (Exception e) {
            System.out.println(e);
        }
        HashMap<String, Double> movieList = PublishATW.allmovieDetails.get(movieName);
        HashMap<String, Double> finalReturnMap = new HashMap<String, Double>();
        finalReturnMap.putAll(movieList);
        finalReturnMap.putAll(PublishATW.movieDetailsToDisplay);

        PublishATW.movieDetailsToDisplay.clear();
        if (finalReturnMap.containsKey("0")) {
            finalReturnMap.remove("0");
        }

        LogWritterGeneral("Not in request", "listMovieShowsAvailability", request, "Success", finalReturnMap.toString());

        return finalReturnMap.toString();
    }

    @Override
    public String searchSlotForBooking(String movieId, String movieName) {
        String request = "Movie ID " + movieId + " Movie Name " + movieName;
        if (PublishATW.allmovieDetails.get(movieName).size() == 0) {
            LogWritterGeneral("Not in request", "searchSlotForBooking", request, "Success", "n");
            return "n";
        }

        HashMap<String, Double> checkExist = PublishATW.allmovieDetails.get(movieName);
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
        HashMap<String, Double> checkExist = PublishATW.allmovieDetails.get(movieName);
        HashMap<String, Double> customerMovieMap = new HashMap<String, Double>();
        double currentCapacityMovie = 0;
        double currentCapacityBooked = 0;
        double newCapacityMovie = 0;
        double countOtherBkg = 0;
        double existCount = 0;
        if (PublishATW.allbookingDetails.containsKey(customerID)) {
            customerMovieMap = PublishATW.allbookingDetails.get(customerID);
            if (customerMovieMap.containsKey(movieID)) {
                currentCapacityBooked = customerMovieMap.get(movieID);
                existCount = 1;
            }
        }

        if (PublishATW.customerOtherBkgCount.containsKey(customerID)) {
            countOtherBkg = PublishATW.customerOtherBkgCount.get(customerID);
        } else {
            PublishATW.customerOtherBkgCount.put(customerID, countOtherBkg);
        }
        currentCapacityMovie = checkExist.get(movieID);

        if (currentCapacityMovie >= numberOfTickets) {
            if (!customerID.substring(0, 3).equals(movieID.substring(0, 3))) {
                if (countOtherBkg == 3) {
                    LogWritterGeneral(customerID, "bookMovieTickets", request, "Failed", "otherbkgcntexcd");

                    return "otherbkgcntexcd";
                } else {
                    countOtherBkg++;
                    try {       //For VERDUN
                        DatagramSocket ds = new DatagramSocket();

                        InetAddress ip = InetAddress.getLocalHost();
                        byte buf[] = null;

                        String inp = "up" + customerID + countOtherBkg;

                        buf = inp.getBytes();

                        DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, 3035);  //PACKET FOR VERDUN

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

                    PublishATW.customerOtherBkgCount.put(customerID, countOtherBkg);
                }

            }
            newCapacityMovie = currentCapacityMovie - numberOfTickets;
            PublishATW.allmovieDetails.get(movieName).put(movieID, newCapacityMovie);
//            PublishATW.cutomerDetails = new HashMap<String, Double>();
            PublishATW.allbookingDetails.put(customerID, PublishATW.cutomerDetails);
            if (existCount == 0) {
                PublishATW.allbookingDetails.get(customerID).put(movieID, numberOfTickets);
            } else {
                currentCapacityBooked = currentCapacityBooked + numberOfTickets;
                PublishATW.allbookingDetails.get(customerID).put(movieID, currentCapacityBooked);
            }
            LogWritterGeneral(customerID, "bookMovieTickets", request, "Success", "booked");
            System.out.println("allbookingDetails: " + PublishATW.allbookingDetails.toString());
            System.out.println("customerOtherBkgCount: " + PublishATW.customerOtherBkgCount.toString());
            return "booked";

        } else {
            LogWritterGeneral(customerID, "bookMovieTickets", request, "Failed", "capntavl");

            return "capntavl";
        }
    }

    @Override
    public String getBookingSchedule(String customerID) {
        String request = "Customer ID " + customerID;
        try {       //For VERDUN
            DatagramSocket ds = new DatagramSocket();

            InetAddress ip = InetAddress.getLocalHost();
            byte buf[] = null;

            String inp = "bkngATW" + customerID;

            buf = inp.getBytes();

            DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, 3035);  //PACKET FOR VERDUN

            LogWritterGeneral(customerID, "getBookingSchedule-UDP Request", inp, "Success", "request sent");

            ds.send(DpSend);

            HashMap<String, Double> data = new HashMap<String, Double>();

            DatagramSocket ds1 = new DatagramSocket(3004); // receiving socket 

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
            PublishATW.customerDetailsToDisplay.putAll(data);
            receive = new byte[65535];
            ds1.close();

        } catch (Exception e) {
            System.out.println(e);
        }

        try {       //For OUTREMONT
            DatagramSocket ds = new DatagramSocket();

            InetAddress ip = InetAddress.getLocalHost();
            byte buf[] = null;

            String inp = "bkngATW" + customerID;

            buf = inp.getBytes();

            DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, 3036);  //PACKET FOR OUTREMONT
            LogWritterGeneral(customerID, "getBookingSchedule-UDP Request", inp, "Success", "request sent");

            ds.send(DpSend);

            HashMap<String, Double> data = new HashMap<String, Double>();

            DatagramSocket ds1 = new DatagramSocket(3004); // receiving socket 

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
            PublishATW.customerDetailsToDisplay.putAll(data);
            receive = new byte[65535];
            ds1.close();

        } catch (Exception e) {
            System.out.println(e);
        }

        HashMap<String, Double> finalReturnMap = new HashMap<String, Double>();
        HashMap<String, Double> movieList = new HashMap<String, Double>();

        if (!PublishATW.allbookingDetails.isEmpty()) {
            movieList = PublishATW.allbookingDetails.get(customerID);
        }

        finalReturnMap.putAll(PublishATW.customerDetailsToDisplay);
        finalReturnMap.putAll(movieList);

        if (finalReturnMap.containsKey("0")) {
            finalReturnMap.remove("0");
        }

        PublishATW.customerDetailsToDisplay.clear();

        LogWritterGeneral(customerID, "getBookingSchedule", request, "Success", finalReturnMap.toString());

        return finalReturnMap.toString();
    }

    @Override
    public String cancelMovieTickets(String customerID, String movieId, String movieName, double numberOfTickets) {
        String request = "Customer ID " + customerID + " Movie Id " + movieId + " Movie Name " + movieName + " Number Of Tickets " + numberOfTickets;
        HashMap<String, Double> movieList = new HashMap<String, Double>();
        HashMap<String, Double> movieListOnServer = PublishATW.allmovieDetails.get(movieName);

        if (!PublishATW.allbookingDetails.isEmpty()) {
            movieList = PublishATW.allbookingDetails.get(customerID);
        } else {
            LogWritterGeneral(customerID, "cancelMovieTickets", request, "Failed", "nobooking");

            return "nobooking";
        }

        double bkgcapacityonserver = movieListOnServer.get(movieId);
        double noofticketsbooked = movieList.get(movieId);

        if (noofticketsbooked > numberOfTickets) {
            noofticketsbooked = noofticketsbooked - numberOfTickets;
            PublishATW.allbookingDetails.get(customerID).put(movieId, noofticketsbooked);
            bkgcapacityonserver = bkgcapacityonserver + numberOfTickets;
            PublishATW.allmovieDetails.get(movieName).put(movieId, bkgcapacityonserver);
            LogWritterGeneral(customerID, "cancelMovieTickets", request, "Success", "cancelled");

            return "cancelled";
        } else if (noofticketsbooked == numberOfTickets) {
            PublishATW.allbookingDetails.get(customerID).remove(movieId);
            bkgcapacityonserver = bkgcapacityonserver + numberOfTickets;
            PublishATW.allmovieDetails.get(movieName).put(movieId, bkgcapacityonserver);
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
        String directory = "C:\\Users\\Rohan\\Documents\\NetBeansProjects\\CORBA_ServerATWLogs" + date;
        try {

            Path path = Paths.get(directory);

            Files.createDirectories(path);

        } catch (IOException e) {
            System.err.println("Failed to create directory!" + e.getMessage());
        }
        String fileName = "C:\\Users\\Rohan\\Documents\\NetBeansProjects\\CORBA_ServerATWLogs" + date + "\\" + "ServerATW" + ".txt";

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
        if (PublishATW.allbookingDetails.containsKey(customerID)) {
            movieList = PublishATW.allbookingDetails.get(customerID);

            if (movieList.containsKey(old_movieID)) {
                if (numberOfTickets <= movieList.get(old_movieID)) {
                    if (new_movieID.substring(0, 3).equals("ATW")) {
                        //local check
                        String cancelSuccess = cancelMovieTickets(customerID, old_movieID, old_movieName, numberOfTickets);
                        System.out.println("cancel success " + cancelSuccess);
                        String bookSuccess = bookMovieTickets(customerID, new_movieID, new_movieName, numberOfTickets);
                        System.out.println("booksuccess " + bookSuccess);
                        return bookSuccess;
                    } else if (new_movieID.substring(0, 3).equals("VER")) {
                        try {       //For VERDUN
                            DatagramSocket ds = new DatagramSocket();

                            InetAddress ip = InetAddress.getLocalHost();
                            byte buf[] = null;

                            String inp = "chkavlATW" + new_movieID + new_movieName;

                            buf = inp.getBytes();

                            DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, 3035);  //PACKET FOR VERDUN

//                        LogWritterGeneral(customerID, "getBookingSchedule-UDP Request", inp, "Success", "request sent");
                            ds.send(DpSend);

//                        HashMap<String, Double> data = new HashMap<String, Double>();
                            String data = "";
                            DatagramSocket ds1 = new DatagramSocket(3004); // receiving socket 

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
                                return "bookver";
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

                            String inp = "chkavlATW" + new_movieID + new_movieName;

                            buf = inp.getBytes();

                            DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, 3036);  //PACKET FOR OUTREMONT

//                        LogWritterGeneral(customerID, "getBookingSchedule-UDP Request", inp, "Success", "request sent");
                            ds.send(DpSend);

//                        HashMap<String, Double> data = new HashMap<String, Double>();
                            String data = "";
                            DatagramSocket ds1 = new DatagramSocket(3004); // receiving socket 

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
                    return "nooftktexceed";
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
