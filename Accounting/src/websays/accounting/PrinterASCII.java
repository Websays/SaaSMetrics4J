/*
 *    SaaSMetrics4J : https://github.com/hugozaragoza/SaaSMetrics4J
 *
 *    (c) 2014, Hugo Zaragoza, Websays.
 */
package websays.accounting;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class PrinterASCII {
  
  private static final Logger logger = Logger.getLogger(PrinterASCII.class);
  
  static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
  
  static final String line = "---------------------------------------------\n";
  static final String line1 = "=========================================================\n";
  static final String line2 = "---------------------------------------------------------\n";
  
  public String printBill(Bill b, boolean sumary) {
    String s = String.format("INVOICE FOR CLIENT: %-30s\t%10s€\n", b.clientName, //
        NumberFormat.getIntegerInstance().format(b.sumFee));
    if (!sumary) {
      for (int i = 0; i < b.items.size(); i++) {
        BilledItem bi = b.items.get(i);
        int monthNumber = bi.period.monthNumber(bi.period.billDate);
        s += String.format("   %-20s\t(B%dM%d %s-%s %s€)", bi.contract_name, bi.period.period, monthNumber,
            sdf.format(bi.period.periodStart), sdf.format(bi.period.periodEnd), NumberFormat.getIntegerInstance().format(bi.fee));
        if (bi.notes != null && bi.notes.size() > 0) {
          s += "\t" + StringUtils.join(bi.notes, " | ");
        }
        s += "\n";
      }
    }
    return s;
  }
  
  public String printBills(ArrayList<Bill> bills, boolean summary) {
    if (bills.size() == 0) {
      return "\n";
    }
    StringBuilder sb = new StringBuilder();
    
    // sb.append(line);
    // sb.append(line);
    ArrayList<Bill> noBills = new ArrayList<Bill>();
    Date billDate = null;
    
    for (Bill b : bills) {
      if (b.sumFee == 0) {
        noBills.add(b);
      } else {
        sb.append(printBill(b, summary));
        sb.append("\n");
        
        if (billDate == null) {
          billDate = b.date;
        } else {
          if (!billDate.equals(b.date)) {
            logger.warn("not all bills have same date! " + "\n\t" + sdf.format(b.date) + "<>" + sdf.format(billDate) + "\n\t"
                + b.clientName);
          }
        }
      }
    }
    
    sb.append("\n" + line2 + "(Active contracts with no bills this month:)\n\n");
    
    for (Bill b : noBills) {
      sb.append(printBill(b, summary));
      sb.append("\n");
    }
    
    // sb.append(line);
    return sb.toString();
  }
  
  public static void printTitle(String string, boolean connectToDB) throws IOException {
    String msg = "\n\n" + line1;
    if (!connectToDB) {
      msg += "WARNING! NOT CONNECTED TO DB!!!\n";
    }
    msg += string + "\n" + line1 + "\n";
    System.out.print(msg);
  }
  
  public static void printSubtitle(String string) throws IOException {
    String msg = "\n\n" + line2;
    msg += string + "\n" + line2 + "\n";
    System.out.print(msg);
  }
  
}
