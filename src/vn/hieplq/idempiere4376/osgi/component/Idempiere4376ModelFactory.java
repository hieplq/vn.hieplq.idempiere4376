package vn.hieplq.idempiere4376.osgi.component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.adempiere.base.IModelFactory;
import org.compiere.model.PO;
import org.compiere.util.AdempiereUserError;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.osgi.service.component.annotations.Component;

import vn.hieplq.idempiere4376.model.api.I_idempiere4376;
import vn.hieplq.idempiere4376.model.imp.MIdempiere4376;

@Component(
   property= {"service.ranking:Integer=2"},
   service = org.adempiere.base.IModelFactory.class
 )
public class Idempiere4376ModelFactory implements IModelFactory{
	private final static CLogger s_log = CLogger.getCLogger(Idempiere4376ModelFactory.class);
	
	@Override
	public Class<?> getClass(String tableName) {
		return null;
	}

	@Override
	public PO getPO(String tableName, int Record_ID, String trxName) {
		if (!I_idempiere4376.Table_Name.equals(tableName) || Record_ID == 0){
			return null;
		}
		
		PO po = null;
		StringBuilder sql = new StringBuilder("SELECT * FROM ")
			.append(tableName)
			.append(" WHERE ").append(tableName).append("_ID=?");
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql.toString(), trxName);
			pstmt.setInt (1, Record_ID);
			rs = pstmt.executeQuery ();
			if (rs.next ())
			{
				po = getPO(tableName, rs, trxName);
			}
			else{
				String msg = "Not Found: " + tableName + "_ID=" + Record_ID + " on table " +  tableName + ":SQL:" + sql.toString();
				s_log.log (Level.SEVERE, msg);
				throw new AdempiereUserError(msg);
			}
		}
		catch (SQLException e)
		{
			s_log.log (Level.SEVERE, sql.toString(), e);
			throw new AdempiereUserError(e.getMessage(), e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
		return po;
	}

	@Override
	public PO getPO(String tableName, ResultSet rs, String trxName) {
		try {
			if (I_idempiere4376.Table_Name.equals(tableName))
				return new MIdempiere4376(Env.getCtx(), rs, trxName);
			return null;
		} catch (Exception e) {
			s_log.log (Level.SEVERE, e.toString(), e);
			throw new AdempiereUserError(e.getMessage(), e);
		}
	}

}
