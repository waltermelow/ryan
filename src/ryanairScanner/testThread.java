package ryanairScanner;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.TabFolder;

public class testThread {
//	
//	private static Browser ie = new Browser(ryanairinspector2gui.., SWT.BORDER);
//	
//	public testThread() {
//		lanzaAlert();
//	}
//	
//	public static void lanzaAlert() {
//		ie.execute("alert('dentro de testThread')");
//	}
	
	private static Browser ie;
	
	public Browser crearBrowser(TabFolder tabFolder) {
		Browser miBrowser = new Browser(tabFolder, SWT.BORDER);
//		itemT3.setControl(miBrowser);
//		browser.setBounds(0, 0, 787, 691);
//		tabFolder.pack();		
//		itemT3.setText("cambiado.......");
//		tabFolder.pack();		
//		miBrowser.setUrl("http://www.tupadre.com");
		miBrowser.execute("alert('en crearBr....')");
		return miBrowser;
	}
	
	
}
