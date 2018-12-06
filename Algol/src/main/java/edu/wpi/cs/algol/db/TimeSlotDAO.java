package edu.wpi.cs.algol.db;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import edu.wpi.cs.algol.model.Schedule;
import edu.wpi.cs.algol.db.ScheduleDAO;
import edu.wpi.cs.algol.model.TimeSlot;


//beginDateTime, isOpen, requester, secretCode, 

public class TimeSlotDAO {

	java.sql.Connection conn;

	public TimeSlotDAO() {
		try {
			conn = DatabaseUtil.connect();
		} catch (Exception e) {
			conn = null;
		}

	}

	public TimeSlot getTimeSlot(String scheduleID, LocalDateTime beginDateTime ) throws Exception {

		try {

			TimeSlot timeSlot = null;
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM TimeSlots WHERE scheduleID =? AND beginDateTime =?;");
			ps.setString(1, scheduleID);
			ps.setString(2, beginDateTime.toString()); //info needs to be be parsed
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {
				timeSlot = createTimeSlot(resultSet);
			}

			resultSet.close();
			ps.close();

			return timeSlot;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed in getting timeslot: " + e.getMessage());
		}

	}
	public TimeSlot getTimeSlotFromCode(String scheduleID, String secretCode) throws Exception {

		try {

			TimeSlot timeSlot = null;
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM TimeSlots WHERE scheduleID =? AND secretCode =?;");
			ps.setString(1, scheduleID);
			ps.setString(2, secretCode); //info needs to be be parsed
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {
				timeSlot = createTimeSlot(resultSet);
			}

			resultSet.close();
			ps.close();

			return timeSlot;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed in getting timeslot from secretCode: " + e.getMessage());
		}

	}

	public boolean closeTimeSlot(String scheduleID, String secretCode, String date, String time) throws Exception {

		try {

			// configure input strings
			String[] dateString = date.split("/");
			String[] timeString = time.split(":");

			int month = Integer.parseInt(dateString[0]);
			int dayOfMonth = Integer.parseInt(dateString[1]);
			int year = Integer.parseInt(dateString[2]);

			int hour = Integer.parseInt(timeString[0]);
			int minute = Integer.parseInt(timeString[1]);


			LocalDate inputDate = LocalDate.of(year, month, dayOfMonth);
			LocalTime inputTime = LocalTime.of(hour, minute);
			LocalDateTime dateTime = LocalDateTime.of(inputDate, inputTime);
			ScheduleDAO sDao = new ScheduleDAO();
			Schedule s = sDao.getSchedule(scheduleID);
			if (this.getTimeSlot(scheduleID, dateTime).isOpen() == false) { return true; }

			if (secretCode.equals(s.getSecretCode())) {
				PreparedStatement ps = conn.prepareStatement("UPDATE TimeSlots SET isOpen =? WHERE beginDateTime =? AND scheduleID =?;");
				ps.setString(1, "false");
				ps.setString(2, dateTime.toString());
				ps.setString(3, scheduleID);


				int valsAffected = ps.executeUpdate();
				ps.close();

				return (valsAffected ==1);

			}
			return false;

		} catch (Exception e) {
			throw new Exception("Failed in closing timeslot: " + e.getMessage());
		}
	}

	public boolean openTimeSlot(String scheduleID, String secretCode, String date, String time) throws Exception {

		try {

			// configure input strings
			String[] dateString = date.split("/");
			String[] timeString = time.split(":");

			int month = Integer.parseInt(dateString[0]);
			int dayOfMonth = Integer.parseInt(dateString[1]);
			int year = Integer.parseInt(dateString[2]);

			int hour = Integer.parseInt(timeString[0]);
			int minute = Integer.parseInt(timeString[1]);


			LocalDate inputDate = LocalDate.of(year, month, dayOfMonth);
			LocalTime inputTime = LocalTime.of(hour, minute);
			LocalDateTime dateTime = LocalDateTime.of(inputDate, inputTime);
			ScheduleDAO sDao = new ScheduleDAO();
			Schedule s = sDao.getSchedule(scheduleID);
			if (this.getTimeSlot(scheduleID, dateTime).isOpen() == true) { return true; }

			if (secretCode.equals(s.getSecretCode())) {
				PreparedStatement ps = conn.prepareStatement("UPDATE TimeSlots SET isOpen =? WHERE beginDateTime =? AND scheduleID =?;");
				ps.setString(1, "true");
				ps.setString(2, dateTime.toString());
				ps.setString(3, scheduleID);


				int valsAffected = ps.executeUpdate();
				ps.close();

				return (valsAffected ==1);

			}
			return false;

		} catch (Exception e) {
			throw new Exception("Failed in opening timeslot: " + e.getMessage());
		}
	}

	public boolean updateTimeSlot(TimeSlot timeSlot) throws Exception {

		try { // include a
			String query = "UPDATE TimeSlots SET requester=?, isOpen=?, secretCode =? WHERE beginDateTime=? AND scheduleID =?;";
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, timeSlot.getRequester());
			ps.setString(2, timeSlot.isOpen() ? "true" : "false");
			ps.setString(3, timeSlot.getSecretCode()); 
			ps.setString(4, timeSlot.getBeginDateTime().toString()); 
			ps.setString(5, timeSlot.getScheduleId()); 		// Schedule ID	
			int valsAffected = ps.executeUpdate();
			ps.close();

			return (valsAffected ==1);
		} catch (Exception e) {
			throw new Exception("Failed to update report: " + e.getMessage());
		}

	}


	public boolean addTimeSlot(TimeSlot timeSlot) throws Exception {

		try {
			PreparedStatement ps = conn.prepareStatement("INSERT INTO TimeSlots (scheduleID, beginDateTime, isOpen) values(?,?,?);");

			ps.setString(1, timeSlot.getScheduleId());
			ps.setString(2,timeSlot.getBeginDateTime().toString());
			ps.setString(3, (timeSlot.isOpen() ? "true" : "false"));
			ps.execute();

			return true;
		} catch (Exception e) {
			throw new Exception("Fail to add new timeslot: " + e.getMessage());
		}

	}

	public boolean addMeeting(TimeSlot timeSlot) throws Exception {

		try {
			PreparedStatement ps = conn.prepareStatement("UPDATE TimeSlots SET requester=?, isOpen=?, secretCode =? WHERE beginDateTime=? AND scheduleID =?;");
			ps.setString(1, timeSlot.getRequester());
			ps.setString(2, timeSlot.isOpen() ? "true" : "false"); 
			ps.setString(3, timeSlot.getSecretCode());
			ps.setString(4,timeSlot.getBeginDateTime().toString());
			ps.setString(5,timeSlot.getScheduleId());
			int numAffected = ps.executeUpdate();

			return (numAffected == 1);

		} catch (Exception e) {
			throw new Exception("Fail to add new meeting in timeslot: " + e.getMessage());
		}

	}

	public boolean deleteAllTimeSlots(String scheduleID) throws Exception {
		try {
			PreparedStatement ps = conn.prepareStatement("DELETE from TimeSlots WHERE scheduleID =?;");

			ps.setString(1, scheduleID);
			int numAffected = ps.executeUpdate();

			return (numAffected == 1);
		} catch (Exception e) {
			throw new Exception ("Fail to delete all time slots: " + e.getMessage());
		}
	}

	public boolean cancelMeeting(String scheduleID, LocalDateTime beginDateTime ) throws Exception {

		try {
			PreparedStatement ps = conn.prepareStatement("UPDATE TimeSlots SET requester=?, isOpen=?, secretCode =? WHERE beginDateTime=? AND scheduleID =?;");

			ps.setString(1, null);
			ps.setString(2, "true");
			ps.setString(3, null);
			ps.setString(4, beginDateTime.toString());
			ps.setString(5, scheduleID);
			int numAffected = ps.executeUpdate();

			return (numAffected == 1);
		} catch (Exception e) {
			throw new Exception ("Fail to cancel meeting: "+ e.getMessage());
		}



	}

	// something for getting multiple timeslots for various reasons
	public ArrayList<TimeSlot> getAllTimeSlots(String id) throws Exception{

		ArrayList<TimeSlot> allTimeSlots = new ArrayList<TimeSlot>();

		try {

			// selects all timeslots with the given scheduleID
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM TimeSlots WHERE scheduleID = ?");
			ps.setString(1, id);
			ResultSet resultSet = ps.executeQuery();

			while(resultSet.next()) {
				TimeSlot ts = createTimeSlot(resultSet);
				allTimeSlots.add(ts);
			}

			resultSet.close();
			ps.close();

			return allTimeSlots;


		} catch (Exception e) {
			throw new Exception("Failed in getting all timeslots: " + e.getMessage());
		}


	}

	public ArrayList<TimeSlot> getWeeklyTimeSlots(String scheduleID, String dateStart) throws Exception {

		ArrayList<TimeSlot> weekts = new ArrayList<TimeSlot>();
		//allts = daoT.getAllTimeSlots(id);
		ScheduleDAO daoS = new ScheduleDAO();
		TimeSlotDAO daoT = new TimeSlotDAO();

		Schedule s = daoS.getSchedule(scheduleID);
		TimeSlot startts;
		if (!dateStart.isEmpty()) {			// a valid date has been included
			String[] date = dateStart.split("-");
			//String[] time = dateTime.split(":");

			int month = Integer.parseInt(date[0]);	// parsing might not work correctly
			int day = Integer.parseInt(date[1]);
			int year = Integer.parseInt(date[2]);
			LocalDateTime ldt;

			ldt = LocalDateTime.of(LocalDate.of(year, month, day), s.getStartTime());
			// check which date schedule begins on
			startts = daoT.getTimeSlot(scheduleID, ldt);

			// starts on a monday
			if(startts.getBeginDateTime().getDayOfWeek().equals(DayOfWeek.MONDAY)) {

				for (int i = 0; i < 5; i ++) { // if day starts on monday

					for(LocalTime time = (s.getStartTime().getMinute()% s.getDuration() == 0) ? s.getStartTime() : s.getStartTime().plusMinutes(s.getDuration() - s.getStartTime().getMinute()%s.getDuration()); time.isBefore(s.getEndTime()); time = time.plusMinutes(s.getDuration())) {
						if (LocalDate.of(year, month, day).plusDays(i).isBefore(s.getEndDate())) {
							weekts.add(daoT.getTimeSlot(scheduleID, LocalDateTime.of(LocalDate.of(year, month, day).plusDays(i), time)));
						}
					}

				}

			}
			// does not start on a monday
			else {
				int newDay = day;
				while (!startts.getBeginDateTime().getDayOfWeek().equals(DayOfWeek.MONDAY)) {
					startts = daoT.getTimeSlot(scheduleID, LocalDateTime.of(LocalDate.of(year, month, newDay).plusDays(-1), s.getStartTime()));
					if (startts == null) {
						startts = daoT.getTimeSlot(scheduleID, LocalDateTime.of(LocalDate.of(year, month, newDay).plusDays(1), s.getStartTime()));
						break;
					}

				}
				year = startts.getBeginDateTime().getYear();
				month = startts.getBeginDateTime().getMonthValue();
				day = startts.getBeginDateTime().getDayOfMonth();
				for (int i = 0; i < 5; i ++) {
					if (LocalDate.of(year, month, day).plusDays(i).isBefore(s.getEndDate().plusDays(1))) {
						for(LocalTime time = (s.getStartTime().getMinute()% s.getDuration() == 0) ? s.getStartTime() : s.getStartTime().plusMinutes(s.getDuration() - s.getStartTime().getMinute()%s.getDuration()); time.isBefore(s.getEndTime()); time = time.plusMinutes(s.getDuration())) {
							weekts.add(daoT.getTimeSlot(scheduleID, LocalDateTime.of(LocalDate.of(year, month, day).plusDays(i), time)));
						}
					}
				}
			}


			return weekts;


		}
		// no date has been indicated to show (shows default
		else {

			startts = daoT.getTimeSlot(scheduleID, LocalDateTime.of(s.getStartDate(),s.getStartTime()));

			int day = startts.getBeginDateTime().getDayOfMonth();
			int month = startts.getBeginDateTime().getMonthValue();
			int year = startts.getBeginDateTime().getYear();

			for (int i = 0;LocalDate.of(year, month, day).plusDays(i).isBefore(s.getEndDate().plusDays(1)); i ++) {
				if ( !startts.getBeginDateTime().getDayOfWeek().equals(DayOfWeek.FRIDAY)) {
					for(LocalTime time = (s.getStartTime().getMinute()% s.getDuration() == 0) ? s.getStartTime() : s.getStartTime().plusMinutes(s.getDuration() - s.getStartTime().getMinute()%s.getDuration()); time.isBefore(s.getEndTime()); time = time.plusMinutes(s.getDuration())) {

						startts = daoT.getTimeSlot(scheduleID, LocalDateTime.of(LocalDate.of(year, month, day).plusDays(i), time));
						weekts.add(startts);

					}
				}
			}


			return weekts;

		}


	}

	/*
	 * Open All slots on given day date as mm/dd/yyyy or mm-dd-yyyy
	 */

	public boolean openTimeSlotsOnDay(String scheduleID, String secretCode, String date) throws Exception {
		try {

			String[] dateString = date.split("/");


			int month = Integer.parseInt(dateString[0]);
			int dayOfMonth = Integer.parseInt(dateString[1]);
			int year = Integer.parseInt(dateString[2]);

			LocalDate inputDate = LocalDate.of(year, month, dayOfMonth);

			// acquiring time
			ScheduleDAO sDao = new ScheduleDAO();
			Schedule s = sDao.getSchedule(scheduleID);
			LocalTime startTime = s.getStartTime();


			if (secretCode.equals(s.getSecretCode())) {

				boolean status = false;
				LocalTime t = startTime;
				while (t.isBefore(s.getEndTime())) {
					if (this.getTimeSlot(scheduleID, LocalDateTime.of(inputDate, t)).getSecretCode() != null) {
						status = this.openTimeSlot(scheduleID, secretCode, date, t.toString());
					}
					t=t.plusMinutes(s.getDuration());
					t=t.plusMinutes(0);
				}
				return status;

			}
			throw new Exception();

		} catch (Exception e) {
			throw new Exception("Failed in opening timeslot: " + e.getMessage());
		}

	}

	/*
	 * Open All slots on given time
	 */

	public boolean openTimeSlotsAtTime(String scheduleID, String secretCode, String time) throws Exception {
		try {

			// configure input strings


			String[] inputTime = time.split(":");
			int hour = Integer.parseInt(inputTime[0]);
			int minute = Integer.parseInt(inputTime[1]);

			ScheduleDAO sDao = new ScheduleDAO();
			Schedule s = sDao.getSchedule(scheduleID);
			LocalDate startDate = s.getStartDate();
			if (secretCode.equals(s.getSecretCode())) {
				boolean status = true;
				LocalDate d = startDate;
				while (d.isBefore(s.getEndDate().plusDays(1)) && status == true) {
					String[] dateS = d.toString().split("-");
					String date ="";
					date = dateS[1]+ "/" + dateS[2]+ "/" + dateS[0];
					if (this.getTimeSlot(scheduleID, LocalDateTime.of(d,LocalTime.of(hour, minute))).getSecretCode() != null) {
						status = this.openTimeSlot(scheduleID, secretCode, date, time);
					}
					d=d.plusDays(1);
					d=d.plusDays(0);
				}
				return status;
			}
			else
				return false;
			//if (this.getTimeSlot(scheduleID, dateTime).isOpen() == false) { return true; }

			/*if (secretCode.equals(s.getSecretCode())) {
				PreparedStatement ps = conn.prepareStatement("UPDATE TimeSlots SET isOpen =? WHERE beginDateTime =? AND scheduleID =?;");
				ps.setString(1, "false");
				ps.setString(2, dateTime.toString());
				ps.setString(3, scheduleID);


				int valsAffected = ps.executeUpdate();
				ps.close();

				return (valsAffected ==1);

			}
			return false; */

		} catch (Exception e) {
			throw new Exception("Failed in opening timeslot: " + e.getMessage());
		}
	}

	/*
	 * Close All slots on given day
	 */

	public boolean closeTimeSlotsOnDay(String scheduleID, String secretCode, String date) throws Exception {
		try {

			// configure input strings
			String[] dateString = date.split("/");


			int month = Integer.parseInt(dateString[0]);
			int dayOfMonth = Integer.parseInt(dateString[1]);
			int year = Integer.parseInt(dateString[2]);

			// acquiring time
			ScheduleDAO sDao = new ScheduleDAO();
			Schedule s = sDao.getSchedule(scheduleID);
			LocalTime startTime = s.getStartTime();


			LocalDate inputDate = LocalDate.of(year, month, dayOfMonth);
			//			LocalDate nextDate = inputDate.plusDays(1);
			//LocalTime inputTime = LocalTime.of(startTime.getHour(), startTime.getMinute());

			//			LocalDateTime nextDateTime = LocalDateTime.of(nextDate, inputTime);
			// if (this.getTimeSlot(scheduleID, dateTime).isOpen() == true) { return true; }

			if (secretCode.equals(s.getSecretCode())) {
				/*int valsAffected = 0;
				PreparedStatement ps = null;
				LocalTime t = startTime;
				while (t.isBefore(s.getEndTime().plusMinutes(s.getDuration()))) {
					LocalDateTime dateTime = LocalDateTime.of(inputDate, t);
					ps = conn.prepareStatement("UPDATE TimeSlots SET isOpen =? WHERE beginDateTime =? AND scheduleID =?;");
					ps.setString(1, "false");
					ps.setString(2, dateTime.toString());
					ps.setString(3, scheduleID);
					t.plusMinutes(s.getDuration());
					valsAffected = ps.executeUpdate();


				}
				ps.close();*/
				boolean status = false;
				LocalTime t = startTime;
				while (t.isBefore(s.getEndTime())) {
					if (this.getTimeSlot(scheduleID, LocalDateTime.of(inputDate, t)).getSecretCode() != null) {
						status = this.closeTimeSlot(scheduleID, secretCode, date, t.toString());
					}
					t=t.plusMinutes(s.getDuration());
					t=t.plusMinutes(0);
				}
				return status;

			}
			return false;

		} catch (Exception e) {
			throw new Exception("Failed in closing timeslot: " + e.getMessage());
		}

	}


	/*
	 * Close All slots on given time
	 */
	public boolean closeTimeSlotsAtTime(String scheduleID, String secretCode, String time) throws Exception {
		try {
			
			String[] inputTime = time.split(":");
			int hour = Integer.parseInt(inputTime[0]);
			int minute = Integer.parseInt(inputTime[1]);

			ScheduleDAO sDao = new ScheduleDAO();
			Schedule s = sDao.getSchedule(scheduleID);
			LocalDate startDate = s.getStartDate();
			if (secretCode.equals(s.getSecretCode())) {
				boolean status = false;
				LocalDate d = startDate;
				while (d.isBefore(s.getEndDate().plusDays(1))) {
					String[] dateS = d.toString().split("-");
					String date ="";
					date = dateS[1]+ "/" + dateS[2]+ "/" + dateS[0];
					if (this.getTimeSlot(scheduleID, LocalDateTime.of(d,LocalTime.of(hour, minute))).getSecretCode() != null) {
						status = this.closeTimeSlot(scheduleID, secretCode, date, time);
						}
					d=d.plusDays(1);
					d=d.plusDays(0);
				}
				return status;
			}else 
				return false;
		}  catch (Exception e) {
			throw new Exception("Failed in closing timeslot: " + e.getMessage());
		}
	}

	public TimeSlot createTimeSlot(ResultSet resultSet) throws Exception {

		String beginDateTime = resultSet.getString("beginDateTime");
		String scheduleID = resultSet.getString("scheduleID");
		String secretCode = resultSet.getString("secretCode");
		String requester = resultSet.getString("requester");
		String isOpen = resultSet.getString("isOpen");
		return new TimeSlot(secretCode, beginDateTime, isOpen, requester, scheduleID);
	}


}