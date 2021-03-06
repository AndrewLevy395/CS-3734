import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';

import { CreateScheduleRequest } from './create-schedule/create-schedule-request';
import { CreateScheduleResponse } from './create-schedule/create-schedule-response';
import { ViewWeeklyScheduleResponse } from './view-weekly-schedule/view-weekly-schedule-response';
import { CreateMeetingRequest, Response, CancelMeetingRequest, ExtendDateRequest } from './view-weekly-schedule/view-weekly-schedule.component';
import { SearchRequest } from './search/search.component';

@Injectable({
  providedIn: 'root'
})
export class ScheduleService {

  constructor(
    private http: HttpClient
  ) { }

  private createScheduleUrl = "https://24f2jgxv5i.execute-api.us-east-2.amazonaws.com/Alpha/createschedule";
  private viewWeeklyScheduleUrl = "https://24f2jgxv5i.execute-api.us-east-2.amazonaws.com/Alpha/viewweeklyschedule";
  private createMeetingUrl = "https://24f2jgxv5i.execute-api.us-east-2.amazonaws.com/Alpha/createmeeting";
  private retrieveDetailsUrl = "https://24f2jgxv5i.execute-api.us-east-2.amazonaws.com/Alpha/retrievedetails";
  private cancelMeetingUrl = "https://24f2jgxv5i.execute-api.us-east-2.amazonaws.com/Alpha/cancelmeeting";
  private viewWeeklyScheduleOrganizerUrl = "https://24f2jgxv5i.execute-api.us-east-2.amazonaws.com/Alpha/viewweeklyscheduleorganizer";
  private deleteScheduleUrl = "https://24f2jgxv5i.execute-api.us-east-2.amazonaws.com/Alpha/deleteschedule";
  private closeTimeSlotUrl = "https://24f2jgxv5i.execute-api.us-east-2.amazonaws.com/Alpha/closetimeslot";
  private openTimeSlotUrl = "https://24f2jgxv5i.execute-api.us-east-2.amazonaws.com/Alpha/opentimeslot";
  private openAllTimeSlotTimeUrl = "https://24f2jgxv5i.execute-api.us-east-2.amazonaws.com/Alpha/openalltimeslotstime";
  private closeAllTimeSlotTimeUrl = "https://24f2jgxv5i.execute-api.us-east-2.amazonaws.com/Alpha/closealltimeslotstime";
  private openAllTimeSlotDateUrl = "https://24f2jgxv5i.execute-api.us-east-2.amazonaws.com/Alpha/openalltimeslotsday";
  private closeAllTimeSlotDateUrl = "https://24f2jgxv5i.execute-api.us-east-2.amazonaws.com/Alpha/closealltimeslotsday";
  private deleteScheduleOldUrl = "https://24f2jgxv5i.execute-api.us-east-2.amazonaws.com/Alpha/deletescheduleold";
  private reportActivityUrl = "https://24f2jgxv5i.execute-api.us-east-2.amazonaws.com/Alpha/reportactivity";
  private extendDateUrl = "https://24f2jgxv5i.execute-api.us-east-2.amazonaws.com/Alpha/extenddate";
  private retrieveScheduleDetailsUrl = "https://24f2jgxv5i.execute-api.us-east-2.amazonaws.com/Alpha/retrievescheduledetails";
  private showAvailableTimesUrl = "https://24f2jgxv5i.execute-api.us-east-2.amazonaws.com/Alpha/showavailabletimes";

  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };

  
  /* POSTs a new schedule to the server */
  createSchedule(csRequest: CreateScheduleRequest): Observable<CreateScheduleResponse> {
    console.log('ScheduleService.createSchedule(): Attempting to send...');
    console.log(csRequest);

    return this.http.post<CreateScheduleResponse>(this.createScheduleUrl, csRequest, this.httpOptions).pipe(
      tap((csResponse: CreateScheduleResponse) => {
        console.log('received csResponse:');
        console.log(csResponse);
      }),
      catchError(this.handleError<CreateScheduleResponse>('createSchedule')));
  }
  
  
  /* POSTS a new meeting to the server */
  createMeeting(cmRequest: CreateMeetingRequest): Observable<CreateScheduleResponse> {
    console.log('ScheduleService.createMeeting(): Attempting to send...');
    console.log(cmRequest);
    
    return this.http.post<Response>(this.createMeetingUrl, cmRequest, this.httpOptions).pipe(
      tap((cmResponse: Response) => {
        console.log('received cmResponse:');
        console.log(cmResponse);
      }),
      catchError(this.handleError<Response>('createMeeting')));
  }
  
  
  /* GETs a meeting from the server */
  retrieveDetails(secretCode: string, scheduleID: string): Observable<Response> {
    console.log('ScheduleService.getSchdeule(): Attempting to send with scheduleID=' + scheduleID + ' and secretCode=' + secretCode);
    
    var parameters = {"scheduleID":scheduleID,"secretCode":secretCode};
    
    return this.http.get<Response>(this.retrieveDetailsUrl, {
      params: parameters,
    }).pipe(
      tap((resp: Response) => {
        console.log(resp);
      }),
      catchError(this.handleError<Response>('retrieveDetails')));
  }
  
  
  /* POSTs a request to cancel a meeting */
  cancelMeeting(req: CancelMeetingRequest): Observable<Response> {
    console.log('ScheduleService.cancelMeeting(): Attempting to send...');
    console.log(req);
    
    return this.http.post<Response>(this.cancelMeetingUrl, req, this.httpOptions).pipe(
      tap((resp: Response) => {
        console.log('received response:');
        console.log(resp);
      }),
      catchError(this.handleError<Response>('cancelMeeting')));
  }

  
  /* GETs a schedule from the server */
  getSchedule(scheduleID: string, date: string | null): Observable<ViewWeeklyScheduleResponse> {
    console.log('ScheduleService.getSchdeule(): Attempting to send with scheduleID=' + scheduleID + ' and date=' + date);

    var parameters = {"scheduleID":scheduleID};
    if (date != null) parameters['date'] = date;

    console.log(parameters);
    
    return this.http.get<ViewWeeklyScheduleResponse>(this.viewWeeklyScheduleUrl, {
      params: parameters,
    }).pipe(
      tap((vwsResponse: ViewWeeklyScheduleResponse) => {
        console.log('received vwsResponse:');
        console.log(vwsResponse);
      }),
      catchError(this.handleError<ViewWeeklyScheduleResponse>('getSchedule')));
  }
  
  
  /* GETs a schedule from the server for the organizer */
  getScheduleOrganizer(scheduleID: string, secretCode: string, date: string | null): Observable<ViewWeeklyScheduleResponse> {
    console.log('ScheduleService.getSchdeuleOrganizer(): Attempting to send with scheduleID=' + scheduleID + ', secretCode=' + secretCode + ' and date=' + date);

    var parameters = {"scheduleID":scheduleID,"secretCode":secretCode};
    if (date != null) parameters['date'] = date;

    console.log(parameters);
    
    return this.http.get<ViewWeeklyScheduleResponse>(this.viewWeeklyScheduleOrganizerUrl, {
      params: parameters,
    }).pipe(
      tap((vwsResponse: ViewWeeklyScheduleResponse) => {
        console.log('received vwsResponse:');
        console.log(vwsResponse);
      }),
      catchError(this.handleError<ViewWeeklyScheduleResponse>('getScheduleOrganizer')));
  }
  
  
  /* POSTs a request to delete a schedule */
  deleteSchedule(secretCode: string, scheduleID: string): Observable<Response> {
    console.log('deleteSchedule: Attempting to send with scheduleID=' + scheduleID + ', secretCode=' + secretCode);
    
    var parameters = {"scheduleID":scheduleID, "secretCode":secretCode};
    console.log(parameters);
    
    return this.http.post<Response>(this.deleteScheduleUrl, parameters, this.httpOptions).pipe(
      tap((resp: Response) => {
        console.log('received response:');
        console.log(resp);
      }),
      catchError(this.handleError<Response>('deleteSchedule')));
  }
  
  
  /* POSTs a request to close a timeslot */
  closeTimeSlot(scheduleID: string, secretCode: string, date: string, time: string): Observable<Response> {
    console.log(`closeTimeSlot: Attempting to send with scheduleID=${scheduleID}, secretCode=${secretCode}, date=${date}, and time=${time}`);
    
    var parameters = {"scheduleID":scheduleID, "secretCode":secretCode, "date":date, "time":time};
    console.log(parameters);
    
    return this.http.post<Response>(this.closeTimeSlotUrl, parameters, this.httpOptions).pipe(
      tap((resp: Response) => {
        console.log('received response:');
        console.log(resp);
      }),
      catchError(this.handleError<Response>('closeTimeSlot')));
  }
  
  /* POSTs a request to open a timeslot */
  openTimeSlot(scheduleID: string, secretCode: string, date: string, time: string): Observable<Response> {
    console.log(`openTimeSlot: Attempting to send with scheduleID=${scheduleID}, secretCode=${secretCode}, date=${date}, and time=${time}`);
    
    var parameters = {"scheduleID":scheduleID, "secretCode":secretCode, "date":date, "time":time};
    console.log(parameters);
    
    return this.http.post<Response>(this.openTimeSlotUrl, parameters, this.httpOptions).pipe(
      tap((resp: Response) => {
        console.log('received response:');
        console.log(resp);
      }),
      catchError(this.handleError<Response>('openTimeSlot')));
  }
  
  openAllTimeSlotTime(time: string, scheduleID: string, secretCode: string) {
    console.log(`closeAllTimeSlotTime: Attempting to send with scheduleID=${scheduleID}, secretCode=${secretCode}, and time=${time}`);
    
    var parameters = {"scheduleID": scheduleID, "secretCode": secretCode, "time": time};
    console.log(parameters);
    
    return this.http.post<Response>(this.openAllTimeSlotTimeUrl, parameters, this.httpOptions).pipe(
      tap((resp: Response) => {
        console.log('received response:');
        console.log(resp);
      }),
      catchError(this.handleError<Response>('openAllTimeSlotTime')));
  }
  
  closeAllTimeSlotTime(time: string, scheduleID: string, secretCode: string) {
    console.log(`closeAllTimeSlotTime: Attempting to send with scheduleID=${scheduleID}, secretCode=${secretCode}, and time=${time}`);
    
    var parameters = {"scheduleID": scheduleID, "secretCode": secretCode, "time": time};
    console.log(parameters);
    
    return this.http.post<Response>(this.closeAllTimeSlotTimeUrl, parameters, this.httpOptions).pipe(
      tap((resp: Response) => {
        console.log('received response:');
        console.log(resp);
      }),
      catchError(this.handleError<Response>('closeAllTimeSlotTime')));
  }
  
  openAllTimeSlotDate(date: string, scheduleID: string, secretCode: string) {
    console.log(`closeAllTimeSlotTime: Attempting to send with scheduleID=${scheduleID}, secretCode=${secretCode}, and date=${date}`);
    
    var parameters = {"scheduleID": scheduleID, "secretCode": secretCode, "date": date};
    console.log(parameters);
    
    return this.http.post<Response>(this.openAllTimeSlotDateUrl, parameters, this.httpOptions).pipe(
      tap((resp: Response) => {
        console.log('received response:');
        console.log(resp);
      }),
      catchError(this.handleError<Response>('openAllTimeSlotDate')));
  }
  
  closeAllTimeSlotDate(date: string, scheduleID: string, secretCode: string) {
    console.log(`closeAllTimeSlotTime: Attempting to send with scheduleID=${scheduleID}, secretCode=${secretCode}, and date=${date}`);
    
    var parameters = {"scheduleID": scheduleID, "secretCode": secretCode, "date": date};
    console.log(parameters);
    
    return this.http.post<Response>(this.closeAllTimeSlotDateUrl, parameters, this.httpOptions).pipe(
      tap((resp: Response) => {
        console.log('received response:');
        console.log(resp);
      }),
      catchError(this.handleError<Response>('closeAllTimeSlotDate')));
  }
  
  deleteScheduleOld(daysOld: number, adminPass: string) {
    console.log(`deleteScheduleOld: attempting to send with daysOld=${daysOld}, adminPass=${adminPass}`);
    
    var parameters = {"adminPass": adminPass, "daysOld": daysOld};
    console.log(parameters);
    
    return this.http.post<Response>(this.deleteScheduleOldUrl, parameters, this.httpOptions).pipe(
      tap((resp: Response) => {
        console.log('received response:');
        console.log(resp);
      }),
      catchError(this.handleError<Response>('deleteScheduleOld')));
  }
  
  reportActivity(pastHour: number, adminPass: string) {
    console.log(`reportActivity: attempting to send with pastHour=${pastHour}, adminPass=${adminPass}`);
    
    var parameters = {"adminPass": adminPass, "pastHour": pastHour};
    console.log(parameters);
    
    return this.http.post<Response>(this.reportActivityUrl, parameters, this.httpOptions).pipe(
      tap((resp: Response) => {
        console.log('received response:');
        console.log(resp);
      }),
      catchError(this.handleError<Response>('reportActivity')));
  }

  extendDate(req: ExtendDateRequest) {
    console.log(`extendDate: attempting to send with ${req}`);
    
    return this.http.post<Response>(this.extendDateUrl, req, this.httpOptions).pipe(
      tap((resp: Response) => {
        console.log('received response:');
        console.log(resp);
      }),
      catchError(this.handleError<Response>('extendDate')));
  }
  
  retrieveScheduleDetails(id: string) {
    console.log(`retrieveScheduleDetails: attempting to send with id=${id}`);
    
    var parameters = {"scheduleID": id};
    
    return this.http.post<Response>(this.retrieveScheduleDetailsUrl, parameters, this.httpOptions).pipe(
      tap((resp: Response) => {
        console.log('received response:');
        console.log(resp);
      }),
      catchError(this.handleError<Response>('retrieveScheduleDetails')));
  }
  
  showAvailableTimes(model: SearchRequest) {
    console.log(`showAvailableTimes: attempting to send with ${model}`);
    
    return this.http.post<Response>(this.showAvailableTimesUrl, model, this.httpOptions).pipe(
      tap((resp: Response) => {
        console.log('received response:');
        console.log(resp);
      }),
      catchError(this.handleError<Response>('showAvailableTimes')));
  }

  /**
   * Handle Http operation that failed.
   * Let the app continue.
   * @param operation - name of the operation that failed
   * @param result - optional value to return as the observable result
   */
  private handleError<T> (operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {

      console.error(error); // log to console instead

      // TODO: better job of transforming error for user consumption
      console.log(`${operation} failed: ${error.message}`);

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }
}
