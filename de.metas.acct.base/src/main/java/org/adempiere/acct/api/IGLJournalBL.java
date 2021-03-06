package org.adempiere.acct.api;

import org.compiere.model.I_GL_Journal;

import de.metas.util.ISingletonService;

public interface IGLJournalBL extends ISingletonService
{

	/**
	 * Document Status is Complete or Closed
	 *
	 * @return true if CO, CL or RE
	 */
	boolean isComplete(I_GL_Journal glJournal);

	void unpost(I_GL_Journal glJournal);

}
