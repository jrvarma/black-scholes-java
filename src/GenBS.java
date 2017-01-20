import javax.swing.*; 
import java.awt.GridLayout; 
import java.awt.BorderLayout; 

public class GenBS extends JApplet{
	private static final long serialVersionUID = 1L;
	static int iS = 0, iX = 1, it = 2, ir = 3, iVP = 4, iq = 5;
	static String [] labels = {"Asset Price", "Exercise Price", "Time to Maturity", 
		"Interest Rate %", "Volatility %", "Dividend Yield %"};
	double [] bsArgs = {100, 100, 1, 0, 20, 0};
	double q; double rf;
	JLabel [] bsArgNames;
	JLabel outLabel, callValues, putValues;
	JTextField [] bsFields;
	JComboBox<?> rateType, yieldType;
	JPanel topPanel, midPanel, bottomPanel, fullPanel;
	JButton impliedButton, closeButton;
	boolean impliedMode = false;
	BlackScholes bs;
	BSImplied bsi;
	private static final int no_yield = 0; 
	private static final int constant_yield = 1; 
	private static final int forex = 2;
	private static final int futures = 3;
	int yield_type;
	public GenBS(){
		topPanel = new JPanel();
		midPanel = new JPanel();
		bottomPanel = new JPanel();
		fullPanel = new JPanel();
		// Set the layout manager for various panels 
		BorderLayout bl = new BorderLayout(); 
		fullPanel.setLayout(bl);
		GridLayout gl = new GridLayout(3, 4, 5, 5);  
		topPanel.setLayout(gl);
	    gl = new GridLayout(1, 3, 5, 5);
		midPanel.setLayout(gl);
	    gl = new GridLayout(2, 2, 5, 5);
		bottomPanel.setLayout(gl);
		// top panel
	    bsArgNames = new JLabel[6];
	    bsFields = new JTextField[6];
	    for(int i=0; i < 6; i++){
	    	bsArgNames[i] = new JLabel(labels[i]);
	    	bsFields[i] = new JTextField(Double.toString(bsArgs[i]));
	    }
	    //bsArgNames[iq].
	    for(int i=0; i < 3; i++){
	    	topPanel.add(bsArgNames[i]);
	    	topPanel.add(bsFields[i]);
	    	topPanel.add(bsArgNames[i+3]);
	    	topPanel.add(bsFields[i+3]);
	    }
		// mid panel
		outLabel = new JLabel("<html>Price<br/><br/>Delta<br/><br/>Gamma<br/><br/>" +
				"Vega<br/><br/>Rho<br/><br/>Theta</html>");
		callValues = new JLabel();
		callValues.setBorder(BorderFactory.createTitledBorder("Call Option"));
		putValues = new JLabel();
		putValues.setBorder(BorderFactory.createTitledBorder("Put Option"));
		midPanel.add(outLabel); midPanel.add(callValues); midPanel.add(putValues);
		// bottom panel
		impliedButton = new JButton("Switch to Implied");
		impliedButton.setBorder(BorderFactory.createEmptyBorder(0,30,0,30));
		closeButton = new JButton("Exit");
		closeButton.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
		rateType = new JComboBox<Object>(new Object[] {"Continuous Compounding", "Annual Compounding", "Semi Annual Compounding"});
		rateType.setBorder(BorderFactory.createTitledBorder("Compounding of interest/yield"));
		yieldType = new JComboBox<Object>(new Object[] {"Asset with no dividends", "Asset with constant dividend yield", 
				"Foreign Currency", "Futures on any asset"});
		yieldType.setBorder(BorderFactory.createTitledBorder("Type of Underlying"));
		bottomPanel.add(rateType);
		bottomPanel.add(yieldType);
//		JLabel dummy = new JLabel();
//		dummy.setVisible(false);
//		bottomPanel.add(dummy); 
		bottomPanel.add(impliedButton); 
		bottomPanel.add(closeButton);
		topPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		midPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		// main panel
		fullPanel.add("North", topPanel);
		fullPanel.add("Center", midPanel);
		fullPanel.add("South", bottomPanel);
		fullPanel.setBorder(BorderFactory.createTitledBorder("Black Scholes Calculator"));
//	    JFrame frame = new JFrame("Black Scholes Calculator"); 
//	    frame.setContentPane(fullPanel);
//	    frame.setSize(575, 500); 
//	    frame.setVisible(true); 
	    bs = new BlackScholes(bsArgs[iS], bsArgs[iX], bsArgs[ir]/100, bsArgs[iVP]/100, 
	    		bsArgs[it]);
	    bsi = new BSImplied();
	    set_yield_type();
	    compute();
	    GBSEngine gbse = new GBSEngine(this);
	    rateType.addActionListener(gbse);
		yieldType.addActionListener(gbse);
		impliedButton.addActionListener(gbse);
		closeButton.addActionListener(gbse);
		for(int i = 0; i < 6; i++){
			bsFields[i].addKeyListener(gbse);
		}
	}

	void set_yield_type(){
		yield_type = yieldType.getSelectedIndex();
		boolean hasYield = (yield_type  == constant_yield || yield_type == forex); 
	    bsFields[iq].setEnabled(hasYield);
	    bsFields[iq].setVisible(hasYield);
	    bsArgNames[iq].setVisible(hasYield);
		bsArgs[iq] = 0.0;
		bsFields[iq].setText("0.0");
    	switch (yield_type){
		case constant_yield: bsArgNames[iq].setText("Dividend Yield %");
			break;    	
		case forex: bsArgNames[iq].setText("Foreign Interest Rate %"); 
    		break;
		default: bsArgNames[iq].setText("");
	}
	}
	
	void toggle_implied(){
		impliedMode = ! impliedMode;
    	bsArgs[iVP] = 0.0;
    	bsFields[iVP].setText("0.0");
    	bsArgNames[iVP].setText(impliedMode ? "Option Price" : "Volatility %");
    	impliedButton.setText(impliedMode ? "Switch to Price" : "Switch to Implied");
		outLabel.setText("<html>"  
				+ (impliedMode ? "Implied %" : "Price") 
				+ "<br/><br/>Delta<br/><br/>Gamma<br/><br/>" 
				+ "Vega<br/><br/>Rho<br/><br/>Theta</html>");
	}
	void compute(){
		try{
			for(int i = 0; i < 6; i++){
				if(bsFields[i].getText().length() > 0){
					bsArgs[i] = Double.parseDouble(bsFields[i].getText());
				}else{
					bsArgs[i] = 0.0;
				}
			}
		}catch(NumberFormatException e){
			callValues.setText("  Non Numeric Data");
			putValues.setText("  Non Numeric Data");
			return;
		}
		if(bsArgs[iS] <= 0.00 || bsArgs[iX] < 0.0 || bsArgs[ir] < -100.0 || bsArgs[iVP] < 0 ||
				bsArgs[it] < 0.0 ||  bsArgs[iq] < -100){
			callValues.setText("   Not Valid Data");
			putValues.setText("    Not Valid Data");
			return;	
		}
		rf = effective_rate(bsArgs[ir]/100);
    	switch (yield_type){
			case forex: 
			case constant_yield: q = bsArgs[iq]/100; break;    	
			case futures: q = bsArgs[ir]/100; break;    	
			case no_yield: 
			default: q = 0.0; 
    	}
		q = effective_rate(q);
		if(impliedMode){
			// call values
			bsi.reset(bsArgs[iS], bsArgs[iX], rf, bsArgs[iVP], bsArgs[it], q, false);
			if(! "Success".equalsIgnoreCase(bsi.Status)){
				callValues.setText("     " + bsi.Status);
			}else{
				display(bsi.Value, false);
			}
			// put values
			bsi.reset(bsArgs[iS], bsArgs[iX], rf, bsArgs[iVP], bsArgs[it], q, true);
			if(! "Success".equalsIgnoreCase(bsi.Status)){
				putValues.setText("     " + bsi.Status);
			}else{
				display(bsi.Value, true);
			}
		}else{
			display(bsArgs[iVP]/100, false); // call values
			display(bsArgs[iVP]/100, true);  // put values
			
		}
	}
	
	void display(double sigma, boolean isPut){
		bs.reset(bsArgs[iS], bsArgs[iX], rf, sigma, bsArgs[it], q);
		String s = "<html><pre>"  
    		// + (isPut ? "     Put   " : "     Call  ") + "<br/><br/>"   
 	    	+ myFormat(impliedMode ? sigma*100 : (isPut ? bs.BSPut() : bs.BSCall()), true) + "<br/><br/>"   
 	    	+ myFormat(isPut ? bs.BSPutDelta() : bs.BSCallDelta(), true)  + "<br/><br/>"  
    		+ myFormat(bs.BSGamma(),  true) + "<br/><br/>"  
    		+ myFormat(bs.BSVega(), true)  + "<br/><br/>"  
 	    	+ ((yield_type == futures)? 
 	    			myFormat(isPut ? bs.BSFuturesPutRho() : bs.BSFuturesCallRho(), true) 
 	    			: myFormat(isPut ? bs.BSPutRho() : bs.BSCallRho(), true)) + "<br/><br/>"   
	    	+ myFormat(isPut ? bs.BSPutTheta() : bs.BSCallTheta(), true);   
	    (isPut ? putValues : callValues).setText(s);
	}
	
	double effective_rate(double r){
		final int continuous = 0; 
		final int annual = 1; 
		final int semi_annual = 2;
		switch (rateType.getSelectedIndex()){
			case continuous: return r;
			case annual: return Math.log(1+r);
			case semi_annual: return 2*Math.log(1+r/2);
			default: return r;
		}
	}

	String myFormat(double x, boolean ForceZeroInf){
		final double Small = 0.0001, Large = 1000, vSmall = 1E-30, vLarge = 1E+30;
		double AbsX = Math.abs(x);
		//If ForceZeroInf is True then
		//very small numbers are displayed as zero
		if (ForceZeroInf && AbsX <= vSmall){
			return "       0";
		}
		//	If ForceZeroInf is True then
		// very large numbers are displayed as +/- infinity
		if (ForceZeroInf && AbsX >= vLarge){
			return (x < 0) ? "     +Infinity" : "    -Infinity";
		}
		// use exponential format unless numbers are within range where fixed decimal format is possible
		if (AbsX > Small && AbsX < Large - Small / 2) {
			return String.format("%13.4f", x);
		}else{
			return String.format("%13.2g", x);
		}
	
	}
	
	public static void main(String[] args){
		GenBS G = new GenBS();
	    JFrame frame = new JFrame("Black Scholes Calculator"); 
	    frame.setContentPane(G.fullPanel);
	    frame.setSize(575, 500); 
	    frame.setVisible(true); 

	}

	public void init(){
		getContentPane().add(fullPanel);
	}

	
}