package edu.wpi.cs.algol.lambda.cancelmeeting;

public class CancelMeetingResponse {
	public String message;
	public int httpCode;
	
	public CancelMeetingResponse (String m, int code) {
		this.message = m;
		this.httpCode = code;
	}
	
	// 200 means success
	public CancelMeetingResponse (String m) {
		this.message = m;
		this.httpCode = 202;
	}
	
	public String toString() {
		return "message: " + message;
	}
}
