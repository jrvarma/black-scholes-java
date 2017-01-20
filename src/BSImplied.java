import org.apache.commons.math.analysis.*;
import org.apache.commons.math.analysis.solvers.*;

class BS_vega_of_sigma implements UnivariateRealFunction{
  	private BlackScholes bs;
  	private double s, X, t;

  	public double value(double x) {
		bs.reset(s, X, 0.0, x, t, 0.0);
        return bs.BSVega();
	}
    BS_vega_of_sigma(double s0, double X0, double t0){
    	s= s0; X = X0; t = t0; 
		bs = new BlackScholes();
    }
	
}
class BS_f_of_sigma implements DifferentiableUnivariateRealFunction{

  	private BlackScholes bs;
  	private BS_vega_of_sigma vega;
  	private double s, X, t, price;

    // evaluation function
	public double value(double x) {
		bs.reset(s, X, 0.0, x, t, 0.0);
        return bs.BSCall() - price;
	}

    BS_f_of_sigma(double s0, double X0, double t0, double price0){
    	s= s0; X = X0; t = t0; price = price0;
		bs = new BlackScholes();
		vega = new BS_vega_of_sigma(s, X, t);
    }
    public UnivariateRealFunction derivative(){
    	return vega;
    }

}

class BSImplied{
	// Black Scholes Implied Volatility from Call or Put Price
	// Computes implied volatility from call price (if PutOpt is false)
	// or from put price (if PutOpt is true)
	// Value contains the estimated implied volatility
	// Status contains the status of the estimate:
	// "undefined" if the implied is undefined
	// "success" if the iterations converges
	// "error" if iterations do not converge
	private double s, X, r, price, t;
	private double div_yield = 0;
	private boolean PutOpt;
	private double intrinsic;
	double Value;
	String Status;

	BSImplied(){
		Status = "No Data";
	}

	BSImplied(double s0, double X0, double r0, double price0, double t0, boolean isPut){
		this(s0, X0, r0, price0, t0, 0.0, isPut);
	}

	BSImplied(double s0, double X0, double r0, double price0, double t0, double div_yield0, boolean isPut){
		reset(s0, X0, r0, price0, t0, div_yield0, isPut);
	}

	void reset(double s0, double X0, double r0, double price0, double t0, double div_yield0, boolean isPut){
		r =r0; t = t0; s = s0; div_yield = div_yield0; PutOpt = isPut;
		// discount the exercise price to eliminate r
  		X = X0 * Math.exp(-r * t);
		// discount the stock price to eliminate dividend yield
  		s = s0 * Math.exp(-div_yield * t);
  		// use put call parity to convert put option into call option
		price = price0 + ((PutOpt) ? (s - X) : 0);
		findImplied();
		
	}

	@SuppressWarnings("deprecation")
	void findImplied(){
		intrinsic = s - X;
		// price must be at least intrinsic value (Max(SminusX,0)) and cannot exceed s
		if (price < intrinsic || price < 0 || price > s) {
			Value = 0;
			Status = "undefined";
			return;
		}
		if (price == intrinsic || price == 0) {
		// if price equals intrinsic value, volatility is zero
			Value = 0.0;
			Status = "success";
			return;
		}
		if (X == 0) { // and price <> s is implicit here
    	// if x is 0, option is same as stock
			Value = 0;
			Status = "undefined";
			return;
		}
        // Create instace of class holding function to be minimised
        BS_f_of_sigma  funct = new BS_f_of_sigma (s, X, t, price);

		UnivariateRealSolverFactory factory = UnivariateRealSolverFactory.newInstance();
		UnivariateRealSolver solver = factory.newDefaultSolver();
		try{
			Value= solver.solve(funct, 0.0D, 100.0D);
			Status = "success";			
		}catch(Exception e){
			Status = "error";
		}
		return;
	}

}
