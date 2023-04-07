/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WebService;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 *
 * @author Rohan
 */
@WebService
@SOAPBinding(style=SOAPBinding.Style.RPC)
public interface IATW {
	
	public String addUser(String UserID);
        public String checkUserExists(String UserID);
        public String addMovieSlots(String movieId, String movieName, double BookingCapacity);
        public String removeMovieSlots(String movieId, String movieName);
        public String listMovieShowsAvailability(String movieName);
        public String searchSlotForBooking(String movieId, String movieName);
        public String bookMovieTickets(String customerID, String movieID, String movieName, double numberOfTickets);
        public String getBookingSchedule(String customerID);
        public String cancelMovieTickets(String customerID, String movieId, String movieName, double numberOfTickets);
        public String exchangeTickets(String customerID, String old_movieID, String new_movieID, String old_movieName, String new_movieName, double numberOfTickets);

}
