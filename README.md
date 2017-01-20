Overview
--------

This software provides JAVA code for option valuation using Black Scholes. It provides a set of JAVA functions for Black Scholes option values, implied volatility and greeks. It also provides a graphic use interface using Swing where the user can provide the asset price, strike, interest rate, volatility and other parameters. The GUI instantly displays the option values and greeks as the input parameters are changed.

The main code is released under the GPL. The implied volatility is calculated using the `UnivariateRealSolver` from the Apache Commons Mathematics Library (http://commons.apache.org/math/) which is released under the Apache Licence by the Apache Software Foundation.

Usage
-----

To run the application locally, it is sufficient to have the two jar files -- the actual code (`GenBS.jar`) and Apache Commons Mathematics Library (`org.apache.commons.math.analysis.solvers.jar`).
`java -jar GenBS.jar`

To run it as a Java Web Start application, the file `GenBS.jnlp` can be used. This will also work locally (`javaws GenBS.jnlp`)

Building
--------

The included `build.xml` allows the code to be built from the command line using `apache ant`. Executing the command `ant` from within the folder containing `build.xml` will do the job. Run `ant clean` after that to delete the intermediate build files.
