package com.bus.ibm.project.service;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bus.ibm.project.model.Booking;
import com.bus.ibm.project.model.Bus;
import com.bus.ibm.project.model.Route;
import com.bus.ibm.project.model.TravellerDetails;
import com.bus.ibm.project.model.User;
import com.bus.ibm.project.repository.BookingRepository;
import com.bus.ibm.project.repository.BusRepository;
import com.bus.ibm.project.repository.RouteRepository;
import com.bus.ibm.project.repository.TravellerRepository;
import com.bus.ibm.project.repository.UserRepository;

@Service
public class BookingService {
     
	@Autowired
	RouteRepository   routeRepo;
	@Autowired
	BusRepository     busRepo;
	@Autowired
	BookingRepository bookingRepo;
	@Autowired
	UserRepository    userRepo;
	@Autowired
	TravellerRepository travellerRepo;
	
	   
    public String bookSeats(String userId,Booking booking,String busId) {
    	//just to demonstrate
    	User user=new User();
    	user.setUserId(userId);
    	userRepo.save(user);
    	Optional<Bus> bus=busRepo.findById(busId);
    	String routeId=routeRepo.getRouteId(booking.getPickUpPoint(),booking.getDroppingPoint());
    	Optional<Route> route=routeRepo.findById(routeId);
    	double totalFare=bus.get().getBusFarePerKm()*booking.getBookedSeats()*route.get().getDistance();
        
    	
    	
       List<Booking> bookings=bookingRepo.findAll();
       if(bookings.isEmpty()) {
    	  booking.setBookingId("RB00000001");
    	  booking.setUser(user);
    	  booking.setBus(new Bus(busId,"",0,0,0.0,"","","","","",""));
    	  booking.setTotalFare(totalFare);
    	  bookingRepo.save(booking);	
    	  return "RB00000001";
       }else {
    	int maxId=0;
    	int lastZeroIndex=0;
    	String  newBookingId;
    	for(Booking newBooking:bookings) {
    		int bookingId=Integer.parseInt(newBooking.getBookingId().substring(2));
    		if((bookingId)>maxId) {
    			maxId=bookingId;
    		 lastZeroIndex=newBooking.getBookingId().lastIndexOf('0');
    		}
    	}
    	   int newMaxId=maxId+1;
    	   if(lastZeroIndex==2) {
    		  newBookingId="RB"+""+newMaxId+"";
    		  } else {  
    	        int formatingValue=lastZeroIndex-1;
    	        newBookingId ="RB"+String.format("%0"+lastZeroIndex+"d", newMaxId);	    
              }
    	   
    	   booking.setBookingId(newBookingId);
    	   booking.setUser(user);
    	   booking.setBus(new Bus(busId,"",0,0,0.0,"","","","","",""));
    	   booking.setTotalFare(totalFare);
    	   bookingRepo.save(booking);
    	   return newBookingId;   
    	   //this bookingId need to be binded in frontEnd
       }
       
       
        
	}
   public  String  addTravellerDetails(String bookingId,Iterable<TravellerDetails> travellers){
    	  for(TravellerDetails traveller:travellers) {
    		   traveller.setBooking(new Booking(bookingId,null, null, "","",0.0,0));
    	  }
    	  travellerRepo.saveAll(travellers);
    	  return bookingId;
    	  
    }
    public List<Booking> getBookingsOfUser(String userId){
    	return bookingRepo.findByUserUserId(userId);
    	 
     }
    //
    
     /*bookingConfirmation(Iterable<TravellerDetails> traveller,String bookingId){
    	 Properties properties=new Properties();
    	 properties.put("mail.smtp.host", "smtp.gmail.com");
		 properties.put("mail.smtp.auth", "true");
		 properties.put("mail.smtp.starttls.enable", "true");
		 properties.put("mail.smtp.auth", "true");
		 properties.put("mail.smtp.port", "587");
		 Session session=Session.getDefaultInstance(properties,
				         new javax.mail.Authenticator(){
				         protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                         return 	new  javax.mail.PasswordAuthentication("rakshithsourav@gmail.com","rakshith@24");
				         }
        	});
		 
		 MimeMessage message=new MimeMessage(session);
		 message.setRecipient(Message.RecipientType.TO, new InternetAddress("madhumadhan2468@gmail.com"));
		 message.setSubject("Booking Confirmation ");
		 
     }*/
     
     

}