import java.awt.event.KeyListener;  
import java.awt.event.KeyEvent; 
import java.awt.event.ActionListener;  
import java.awt.event.ActionEvent; 
import javax.swing.*;
import javax.swing.JOptionPane; 

public class GBSEngine implements KeyListener, ActionListener{

	GenBS parent;  //a reference to GenBS window

	GBSEngine(GenBS gbs){
		parent = gbs;
	}
	public void keyPressed(KeyEvent e){
	}
	public void keyReleased(KeyEvent e){
		Object eventSource = e.getSource(); 
		if (eventSource instanceof JTextField){
			parent.compute();
		}
	}
	public void keyTyped(KeyEvent e){
		Object eventSource = e.getSource(); 
		if (eventSource instanceof JTextField){
			parent.compute();
		}
	}
	public void actionPerformed(ActionEvent e){
		Object eventSource = e.getSource(); 
		if (eventSource == parent.yieldType){
			parent.set_yield_type();
			parent.compute();
		}
		if (eventSource == parent.impliedButton){
			parent.toggle_implied();
			parent.compute();
		}
		if (eventSource == parent.closeButton){
			JOptionPane.showConfirmDialog(null, 
				"The Black Scholes Calculator was written by Prof. Jayanth R. Varma (http://www.iima.ac.in/~jrvarma/)\n" +
		        "and is released under the GNU General Public Licence. \n\n" +
		        "The implied volatility is calculated using the UnivariateRealSolver from the Apache Commons \n" +
		        "Mathematics Library (http://commons.apache.org/math/) which is released under the Apache Licence\n" +
		        "by the Apache Software Foundation.", 
		        "Black Scholes Calculator Exiting...", 
		        JOptionPane.PLAIN_MESSAGE);
			System.exit(0);
		}
		if (eventSource == parent.rateType){
	    	parent.compute();
		}
	}

}
