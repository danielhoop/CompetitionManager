package ch.danielhoop.sql;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

// http://www.kfu.com/~nsayer/Java/dyn-jdbc.html
/**
 * Class to load a driver during runtime. This way the driver doesn't have to be included into the project. 
 * @author Daniel Hoop
 *
 */
public class DynamicDriverLoader implements Driver {

	public static void registerDriver(String pathToDriverJar, String driverClassName)
			throws SQLException, MalformedURLException, InstantiationException, IllegalAccessException, ClassNotFoundException, FileNotFoundException {
		URL u = new URL("jar:file:" +pathToDriverJar+ "!/");
		if(! new File(pathToDriverJar).exists()) throw new FileNotFoundException("The file does not exist: " + pathToDriverJar);
		@SuppressWarnings("resource") // don't close the ucl, otherwise you will get an error!
		URLClassLoader ucl = new URLClassLoader(new URL[] { u });
		Driver d = (Driver) Class.forName(driverClassName, true, ucl).newInstance();
		DriverManager.registerDriver(new DynamicDriverLoader(d));
	}

	private Driver driver;
	DynamicDriverLoader(Driver d) {
		this.driver = d;
	}
	public boolean acceptsURL(String u) throws SQLException {
		return this.driver.acceptsURL(u);
	}
	public Connection connect(String u, Properties p) throws SQLException {
		return this.driver.connect(u, p);
	}
	public int getMajorVersion() {
		return this.driver.getMajorVersion();
	}
	public int getMinorVersion() {
		return this.driver.getMinorVersion();
	}
	public DriverPropertyInfo[] getPropertyInfo(String u, Properties p) throws SQLException {
		return this.driver.getPropertyInfo(u, p);
	}
	public boolean jdbcCompliant() {
		return this.driver.jdbcCompliant();
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}
}
