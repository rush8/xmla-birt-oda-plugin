/*
 *************************************************************************
 * Copyright (c) 2012 Pulak Bose
 *  
 *************************************************************************
 */

package org.eclipse.birt.report.data.oda.olapxmla.impl;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDataSetMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.olap4j.OlapConnection;
import org.olap4j.OlapWrapper;

import com.ibm.icu.util.ULocale;

/**
 * Implementation class of IConnection for an ODA runtime driver.
 */
public class Connection implements IConnection {

	private boolean m_isOpen = false;

	/*
	 * @see
	 * org.eclipse.datatools.connectivity.oda.IConnection#open(java.util.Properties
	 * )
	 */

	private OlapConnection olapConnection = null;

	public void open(Properties connProperties) throws OdaException {

		if (olapConnection == null) {
			// Load the driver
			try {
				Class.forName("org.olap4j.driver.xmla.XmlaOlap4jDriver");

				// Connect
				java.sql.Connection connection = DriverManager
						.getConnection(

						// This is the SQL Server service end point.
								"jdbc:xmla:Server=" + connProperties.getProperty("xmlaServiceEndPoint")

								// Tells the XMLA driver to use a SOAP request
								// cache layer.
								// We will use an in-memory static cache.
										+ ";Cache=org.olap4j.driver.xmla.cache.XmlaOlap4jNamedMemoryCache"

										// Sets the cache name to use. This
										// allows
										// cross-connection cache sharing. Don't
										// give the driver
										// a cache name and it disables sharing.
										+ ";Cache.Name=MyNiftyConnection"

										// Some cache performance tweaks.
										// Look at the javadoc for details.
										+ ";Cache.Mode=LFU;Cache.Timeout=600;Cache.Size=100",

								// XMLA is over HTTP, so BASIC authentication is
								// used.
								null, null);

				olapConnection = ((OlapWrapper) connection)
						.unwrap(OlapConnection.class);
				
				olapConnection.getOlapDatabase();
							
			} catch (ClassNotFoundException e) {
				throw new OdaException("Could not establish connection to the XMLA datasource");
			} catch (Exception e) {
				throw new OdaException("Could not establish connection to the XMLA datasource");
			}
		}
		m_isOpen = true;
	}

	/*
	 * @see
	 * org.eclipse.datatools.connectivity.oda.IConnection#setAppContext(java
	 * .lang.Object)
	 */
	public void setAppContext(Object context) throws OdaException {
		// do nothing; assumes no support for pass-through context
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#close()
	 */
	public void close() throws OdaException {
		try {
			olapConnection.close();
		} catch (SQLException e) {
			throw new OdaException(e.getMessage());
		}
		m_isOpen = false;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#isOpen()
	 */
	public boolean isOpen() throws OdaException {
		// TODO Auto-generated method stub
		return m_isOpen;
	}

	/*
	 * @see
	 * org.eclipse.datatools.connectivity.oda.IConnection#getMetaData(java.lang
	 * .String)
	 */
	public IDataSetMetaData getMetaData(String dataSetType) throws OdaException {
		// assumes that this driver supports only one type of data set,
		// ignores the specified dataSetType
		return new DataSetMetaData(this);
	}

	/*
	 * @see
	 * org.eclipse.datatools.connectivity.oda.IConnection#newQuery(java.lang
	 * .String)
	 */
	public IQuery newQuery(String dataSetType) throws OdaException {
		// assumes that this driver supports only one type of data set,
		// ignores the specified dataSetType
		return new Query(olapConnection);
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#getMaxQueries()
	 */
	public int getMaxQueries() throws OdaException {
		return 0; // no limit
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#commit()
	 */
	public void commit() throws OdaException {
		// do nothing; assumes no transaction support needed
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#rollback()
	 */
	public void rollback() throws OdaException {
		// do nothing; assumes no transaction support needed
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.datatools.connectivity.oda.IConnection#setLocale(com.ibm.
	 * icu.util.ULocale)
	 */
	public void setLocale(ULocale locale) throws OdaException {
		// do nothing; assumes no locale support
	}

}
