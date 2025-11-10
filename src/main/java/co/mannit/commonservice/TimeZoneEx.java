package co.mannit.commonservice;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeZoneEx {

	public static void main(String[] args) {
		
		String ss = "f%s_field";
		System.out.println(String.format(ss, 1));
		
		String str = "40.0|10.0|5.0";
		String[] astr = str.split("\\|");
		StringBuilder ordered = new StringBuilder();
		for(int i=astr.length-1; i >= 0; i--) {
			ordered.append(astr[i]).append("|");
		}
//		if(ordered.length()>0) {
			ordered.delete(ordered.length()-1, ordered.length());
//		}
		
		System.out.println("=========>"+ordered.toString());
		
		 TimeZone defaultTimeZone = TimeZone.getDefault();
	        System.out.println("Default Time Zone: " + defaultTimeZone.getDisplayName()+" ID :"+defaultTimeZone.getID());

	        // Get a list of available time zone IDs
	        String[] availableTimeZoneIds = TimeZone.getAvailableIDs();
	        System.out.println("Available Time Zone IDs:");
	        for (String timeZoneId : availableTimeZoneIds) {
	            System.out.println(timeZoneId);
	        }
		
	        timeDiffernce("Asia/Calcutta");
	        dd();
	}
	
	static public void timeDiffernce(String zoneId) {
		String timezoneId = "America/New_York";

        // Get the current time in UTC
        ZonedDateTime utcDateTime = ZonedDateTime.now(ZoneId.of("UTC"));

        // Get the current time in the specified timezone
        ZonedDateTime localDateTime = ZonedDateTime.now(ZoneId.of(zoneId));

        // Calculate the time difference
        Duration duration = Duration.between(localDateTime, utcDateTime);

        // Print the time difference
        System.out.println("Time difference between UTC and " + timezoneId + ": " + duration.toMinutes());
        
	}
	
	static private void dd() {
		   String timezoneId = "UTC";

	        // Get the ZoneOffset for the specified timezone
	        ZoneOffset zoneOffset = ZoneId.of(timezoneId).getRules().getOffset(java.time.Instant.now());

	        // Format the ZoneOffset as "+HH:mm" (e.g., "+05:30")
	        System.out.println(zoneOffset.getTotalSeconds());
	        System.out.println(zoneOffset.getTotalSeconds()/60);
	        
	        
	        String offsetString = zoneOffset.toString();

	        // Use regular expression to extract the hours and minutes
	        Pattern pattern = Pattern.compile("([+-])(\\d{2}):(\\d{2})");
	        Matcher matcher = pattern.matcher(offsetString);

	        if (matcher.matches()) {
	            // Extract the sign, hours, and minutes
	            String sign = matcher.group(1);
	            String hours = matcher.group(2);
	            String minutes = matcher.group(3);

	            // Print the extracted offset
	            System.out.println("Offset from " + timezoneId + ": " + sign + hours + ":" + minutes);
	        } else {
	            System.out.println("Unable to extract offset for " + timezoneId);
	        }
	        
	        
	        String str = "232312:N|dsdsd:Y|1211:N";
	        
	        String[] pos_id_Array = str.split("\\|");
	        StringBuilder sb = new StringBuilder();
	        for(String pos_id_dtc : pos_id_Array) {
	        	String[] newArray = pos_id_dtc.split(":");
	        	if(!sb.isEmpty()) {
	        		sb.append(":");
	        	}
	        	sb.append(newArray[1]);
	        }
	        
	        System.out.println("dddd : "+sb.toString());
	}
}
