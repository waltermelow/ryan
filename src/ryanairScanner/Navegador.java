package ryanairScanner;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;

public class Navegador extends org.eclipse.swt.browser.Browser {
	private boolean ocupado= false;
	private Vuelo vueloCapturando= null;
	
    public Navegador(Composite parent, int style) {
		super(parent, style);
	}

	public boolean isOcupado() {
		return ocupado;
	}
	public void setOcupado(boolean ocupado) {
		this.ocupado = ocupado;
	}
	public Vuelo getVueloCapturando() {
		return vueloCapturando;
	}
	public void setVueloCapturando(Vuelo vueloCapturando) {
		this.vueloCapturando = vueloCapturando;
	}
	public boolean isVueloCapturando() {
		return vueloCapturando != null;
	}
	protected void checkSubclass() {
	}
	
}
