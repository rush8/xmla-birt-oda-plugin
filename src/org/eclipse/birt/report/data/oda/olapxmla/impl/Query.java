/*
 *************************************************************************
 * Copyright (c) 2012 Pulak Bose
 *  
 *************************************************************************
 */

package org.eclipse.birt.report.data.oda.olapxmla.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.birt.report.data.oda.olapxmla.util.Matrix;
import org.eclipse.birt.report.data.oda.olapxmla.util.OlapXmlaUtil;
import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.SortSpec;
import org.eclipse.datatools.connectivity.oda.spec.QuerySpecification;
import org.olap4j.CellSet;
import org.olap4j.OlapConnection;
import org.olap4j.OlapException;
import org.olap4j.OlapStatement;

/**
 * Implementation class of IQuery for an ODA runtime driver. <br>
 * For demo purpose, the auto-generated method stubs have hard-coded
 * implementation that returns a pre-defined set of meta-data and query results.
 * A custom ODA driver is expected to implement own data source specific
 * behavior in its place.
 */
public class Query implements IQuery {
	
	private static Logger logger = Logger.getLogger( Query.class.getName( ) );
	
	private int m_maxRows;
	private String m_preparedText;
	private OlapConnection olapConnection = null;
	private OlapStatement statement = null;
	private Matrix dataMatrix = null;

	public Query(OlapConnection pOlapConnection) {
		this.olapConnection = pOlapConnection;
	}

	/*
	 * @see
	 * org.eclipse.datatools.connectivity.oda.IQuery#prepare(java.lang.String)
	 */
	public void prepare(String queryText) throws OdaException {

		m_preparedText = queryText;

		try {

			//Create the metadata by querying
			//Don't use the WHERE clause if it contains parameters
			String metaDataQuery = null;
			String queryArray[] = queryText.split("WHERE");
			if (queryArray.length == 1 || !queryArray[1].contains("?"))
				metaDataQuery = queryText;
			else
				metaDataQuery = queryArray[0];			
			OlapStatement localStatement = olapConnection.createStatement();
			CellSet cellSet = localStatement.executeOlapQuery(metaDataQuery);
			dataMatrix = OlapXmlaUtil.convertMultiDimToTwoDim(cellSet);
			cellSet.close();
			localStatement.close();

		} catch (OlapException e) {
			logger.log(Level.SEVERE, "Error in Query" + e.getMessage());
			throw new OdaException(e.getMessage());
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error in Query" + e.getMessage());
			throw new OdaException(e.getMessage());
		}
	}

	/*
	 * @see
	 * org.eclipse.datatools.connectivity.oda.IQuery#setAppContext(java.lang
	 * .Object)
	 */
	public void setAppContext(Object context) throws OdaException {
		// do nothing; assumes no support for pass-through context
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#close()
	 */
	public void close() throws OdaException {

		try {

			if (statement != null)
				statement.close();
		} catch (SQLException e) {
			throw new OdaException(e.getMessage());
		}
		m_preparedText = null;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#getMetaData()
	 */
	public IResultSetMetaData getMetaData() throws OdaException {
		/*
		 * TODO Auto-generated method stub Replace with implementation to return
		 * an instance based on this prepared query.
		 */
		return new ResultSetMetaData(this.dataMatrix);
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#executeQuery()
	 */
	public IResultSet executeQuery() throws OdaException {
		try {
			
			//Following code parses and validates the MDX
			//MdxParser parser = olapConnection.getParserFactory()
			//		.createMdxParser(olapConnection);
			//SelectNode selectNode = parser.parseSelect(m_preparedText);
			//olapConnection.getParserFactory()
			//		.createMdxValidator(olapConnection)
			//		.validateSelect(selectNode);
			
			statement = olapConnection.createStatement();
			CellSet cellSet = statement.executeOlapQuery(m_preparedText);
			dataMatrix = OlapXmlaUtil.convertMultiDimToTwoDim(cellSet);
			cellSet.close();

			IResultSet resultSet = new ResultSet(dataMatrix);
			resultSet.setMaxRows(getMaxRows());
			return resultSet;
			
		} catch (OlapException e) {
			logger.log(Level.SEVERE, "Error in Query" + e.getMessage());
			throw new OdaException(e.getMessage());
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error in Query" + e.getMessage());
			throw new OdaException(e.getMessage());
		}
	}

	/*
	 * @see
	 * org.eclipse.datatools.connectivity.oda.IQuery#setProperty(java.lang.String
	 * , java.lang.String)
	 */
	public void setProperty(String name, String value) throws OdaException {
		// do nothing; assumes no data set query property
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setMaxRows(int)
	 */
	public void setMaxRows(int max) throws OdaException {
		m_maxRows = max;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#getMaxRows()
	 */
	public int getMaxRows() throws OdaException {
		return m_maxRows;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#clearInParameters()
	 */
	public void clearInParameters() throws OdaException {
		// TODO Auto-generated method stub
		// only applies to input parameter
	}

	/*
	 * @see
	 * org.eclipse.datatools.connectivity.oda.IQuery#setInt(java.lang.String,
	 * int)
	 */
	public void setInt(String parameterName, int value) throws OdaException {
		// TODO Auto-generated method stub
		// only applies to named input parameter
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setInt(int, int)
	 */
	public void setInt(int parameterId, int value) throws OdaException {
		// TODO Auto-generated method stub
		// only applies to input parameter
	}

	/*
	 * @see
	 * org.eclipse.datatools.connectivity.oda.IQuery#setDouble(java.lang.String,
	 * double)
	 */
	public void setDouble(String parameterName, double value)
			throws OdaException {
		// TODO Auto-generated method stub
		// only applies to named input parameter
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setDouble(int, double)
	 */
	public void setDouble(int parameterId, double value) throws OdaException {
		// TODO Auto-generated method stub
		// only applies to input parameter
	}

	/*
	 * @see
	 * org.eclipse.datatools.connectivity.oda.IQuery#setBigDecimal(java.lang
	 * .String, java.math.BigDecimal)
	 */
	public void setBigDecimal(String parameterName, BigDecimal value)
			throws OdaException {
		// TODO Auto-generated method stub
		// only applies to named input parameter
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setBigDecimal(int,
	 * java.math.BigDecimal)
	 */
	public void setBigDecimal(int parameterId, BigDecimal value)
			throws OdaException {
		// TODO Auto-generated method stub
		// only applies to input parameter
	}

	/*
	 * @see
	 * org.eclipse.datatools.connectivity.oda.IQuery#setString(java.lang.String,
	 * java.lang.String)
	 */
	public void setString(String parameterName, String value)
			throws OdaException {
		// TODO Auto-generated method stub
		// only applies to named input parameter
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setString(int,
	 * java.lang.String)
	 */
	public void setString(int parameterId, String value) throws OdaException {

		//Sometimes numbers come as formatted since the metadata is hard-coded to CHAR for all parameters. 
		//E.g. 1997 comes as 1,997
		m_preparedText = m_preparedText.replaceFirst("[?]", value.replaceAll("[,]", ""));
		
	}

	/*
	 * @see
	 * org.eclipse.datatools.connectivity.oda.IQuery#setDate(java.lang.String,
	 * java.sql.Date)
	 */
	public void setDate(String parameterName, Date value) throws OdaException {
		// TODO Auto-generated method stub
		// only applies to named input parameter
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setDate(int,
	 * java.sql.Date)
	 */
	public void setDate(int parameterId, Date value) throws OdaException {
		// TODO Auto-generated method stub
		// only applies to input parameter
	}

	/*
	 * @see
	 * org.eclipse.datatools.connectivity.oda.IQuery#setTime(java.lang.String,
	 * java.sql.Time)
	 */
	public void setTime(String parameterName, Time value) throws OdaException {
		// TODO Auto-generated method stub
		// only applies to named input parameter
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setTime(int,
	 * java.sql.Time)
	 */
	public void setTime(int parameterId, Time value) throws OdaException {
		// TODO Auto-generated method stub
		// only applies to input parameter
	}

	/*
	 * @see
	 * org.eclipse.datatools.connectivity.oda.IQuery#setTimestamp(java.lang.
	 * String, java.sql.Timestamp)
	 */
	public void setTimestamp(String parameterName, Timestamp value)
			throws OdaException {
		// TODO Auto-generated method stub
		// only applies to named input parameter
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setTimestamp(int,
	 * java.sql.Timestamp)
	 */
	public void setTimestamp(int parameterId, Timestamp value)
			throws OdaException {
		// TODO Auto-generated method stub
		// only applies to input parameter
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.datatools.connectivity.oda.IQuery#setBoolean(java.lang.String
	 * , boolean)
	 */
	public void setBoolean(String parameterName, boolean value)
			throws OdaException {
		// TODO Auto-generated method stub
		// only applies to named input parameter
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setBoolean(int,
	 * boolean)
	 */
	public void setBoolean(int parameterId, boolean value) throws OdaException {
		// TODO Auto-generated method stub
		// only applies to input parameter
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.datatools.connectivity.oda.IQuery#setObject(java.lang.String,
	 * java.lang.Object)
	 */
	public void setObject(String parameterName, Object value)
			throws OdaException {
		// TODO Auto-generated method stub
		// only applies to named input parameter
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setObject(int,
	 * java.lang.Object)
	 */
	public void setObject(int parameterId, Object value) throws OdaException {
		// TODO Auto-generated method stub
		// only applies to input parameter
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.datatools.connectivity.oda.IQuery#setNull(java.lang.String)
	 */
	public void setNull(String parameterName) throws OdaException {
		// TODO Auto-generated method stub
		// only applies to named input parameter
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#setNull(int)
	 */
	public void setNull(int parameterId) throws OdaException {
		// TODO Auto-generated method stub
		// only applies to input parameter
	}

	/*
	 * @see
	 * org.eclipse.datatools.connectivity.oda.IQuery#findInParameter(java.lang
	 * .String)
	 */
	public int findInParameter(String parameterName) throws OdaException {
		// TODO Auto-generated method stub
		// only applies to named input parameter
		return 0;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#getParameterMetaData()
	 */
	public IParameterMetaData getParameterMetaData() throws OdaException {
		/*
		 * TODO Auto-generated method stub Replace with implementation to return
		 * an instance based on this prepared query.
		 */
		return new ParameterMetaData(m_preparedText);
	}

	/*
	 * @see
	 * org.eclipse.datatools.connectivity.oda.IQuery#setSortSpec(org.eclipse
	 * .datatools.connectivity.oda.SortSpec)
	 */
	public void setSortSpec(SortSpec sortBy) throws OdaException {
		// only applies to sorting, assumes not supported
		throw new UnsupportedOperationException();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#getSortSpec()
	 */
	public SortSpec getSortSpec() throws OdaException {
		// only applies to sorting
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.datatools.connectivity.oda.IQuery#setSpecification(org.eclipse
	 * .datatools.connectivity.oda.spec.QuerySpecification)
	 */
	public void setSpecification(QuerySpecification querySpec)
			throws OdaException, UnsupportedOperationException {
		// assumes no support
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#getSpecification()
	 */
	public QuerySpecification getSpecification() {
		// assumes no support
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.datatools.connectivity.oda.IQuery#getEffectiveQueryText()
	 */
	public String getEffectiveQueryText() {
		// TODO Auto-generated method stub
		return m_preparedText;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#cancel()
	 */
	public void cancel() throws OdaException, UnsupportedOperationException {
		// assumes unable to cancel while executing a query
		throw new UnsupportedOperationException();
	}

}
