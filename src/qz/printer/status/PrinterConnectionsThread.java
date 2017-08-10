package qz.printer.status;

import com.sun.jna.platform.win32.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrinterConnectionsThread extends Thread {

    private static final Logger log = LoggerFactory.getLogger(PrinterConnectionsThread.class);

    private boolean running = true;
    private Winspool.PRINTER_INFO_1[] currentPrinterList;

    public PrinterConnectionsThread() {
        super("Printer Connection Monitor");
    }

    @Override
    public void run() {
        currentPrinterList = WinspoolUtil.getPrinterInfo1();
        while (running) {
            try {sleep(1000);} catch (Exception ignore) {}
            Winspool.PRINTER_INFO_1[] newPrinterList = WinspoolUtil.getPrinterInfo1();

            if (newPrinterList.length != currentPrinterList.length){
                PrinterStatusMonitor.relaunchThreads();
            } else if (!arrayEquiv(currentPrinterList, newPrinterList)) {
                PrinterStatusMonitor.relaunchThreads();
            }
            currentPrinterList = newPrinterList;
        }
    }

    private boolean arrayEquiv (Winspool.PRINTER_INFO_1[] a, Winspool.PRINTER_INFO_1[] b) {
        for (int i = 0; i < a.length; i++) {
            if (!a[i].pName.equals(b[i].pName)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void interrupt() {
        running = false;
        //Todo Remove this debugging log
        log.warn("Closing thread Printer Connection Monitor");
        super.interrupt();
    }
}
