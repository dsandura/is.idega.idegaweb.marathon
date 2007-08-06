package is.idega.idegaweb.marathon.data;


import java.util.Collection;

import javax.ejb.CreateException;
import com.idega.data.IDOHome;
import com.idega.data.IDORelationshipException;

import javax.ejb.FinderException;

public interface PledgeHome extends IDOHome {
	public Pledge create() throws CreateException;

	public Pledge findByPrimaryKey(Object pk) throws FinderException;
	
	public Collection findAllPledges() throws FinderException;
	
	public Collection findAllPledgesForUser(int userID) throws IDORelationshipException, FinderException;
}