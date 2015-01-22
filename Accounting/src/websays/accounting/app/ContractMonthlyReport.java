/*
 *    SaaSMetrics4J : https://github.com/hugozaragoza/SaaSMetrics4J
 *
 *    (c) 2014, Hugo Zaragoza, Websays.
 */
package websays.accounting.app;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import websays.accounting.BillingReportPrinter;
import websays.accounting.CalendarWebsays;
import websays.accounting.Contract;
import websays.accounting.Contracts;
import websays.accounting.PrinterASCII;
import websays.accounting.Reporting;

public class ContractMonthlyReport extends BasicCommandLineApp {
  
  final int M = Calendar.MONTH;
  final int Y = Calendar.YEAR;
  BillingReportPrinter printer = new PrinterASCII();
  private CalendarWebsays calendar = new CalendarWebsays(Locale.getDefault(), TimeZone.getDefault());
  
  public static void main(String[] args) throws Exception {
    init(args);
    if (contractID == null) {
      System.err.println("ERROR You much define --contract contractID");
      return;
    }
    
    ContractMonthlyReport cmr = new ContractMonthlyReport();
    cmr.report(contractID);
  }
  
  void report(int contractID) throws Exception {
    
    initContracts();
    Contract c = contracts.getContract(contractID);
    if (c == null) {
      System.err.println("ERROR: NO CONTRACT FOUND WITH contractID=" + contractID);
      return;
    }
    
    ContractMonthlyReport cmr = new ContractMonthlyReport();
    cmr.report(c);
    
  }
  
  private void report(Contract c) throws ParseException, SQLException {
    Calendar cS = calendar.getCalendar(c.startContract);
    Calendar cE = (Calendar) cS.clone();
    if (c.endContract != null) {
      cE = calendar.getCalendar(c.endContract);
    }
    int margin = 1;
    report(c, cS.get(Y), cS.get(M) - margin, cE.get(Y), cE.get(M) + margin); // to dangerous 0 based
    if (c.endContract == null) {
      System.out.println("... and continues forever ...");
    }
  }
  
  public void report(Contract c, int yearStart, int monthStart, int yearEnd, int monthEnd) throws ParseException, SQLException {
    Calendar cal = calendar.getCalendar(yearStart, monthStart, 1);
    int m, y;
    Contracts contracts = new Contracts();
    contracts.add(c);
    Reporting r = new Reporting(contracts, printer);
    r.showInvoicesHeadlineWhenNone = false;
    
    do {
      m = cal.get(M) + 1;
      y = cal.get(Y);
      r.displayBilling(y, m);
      cal.add(M, 1);
    } while (y != yearEnd || m < monthEnd + 2);
    
  }
  
}
