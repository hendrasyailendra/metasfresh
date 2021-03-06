package org.adempiere.service.impl;

import static org.adempiere.model.InterfaceWrapperHelper.loadOutOfTrx;

/*
 * #%L
 * de.metas.adempiere.adempiere.base
 * %%
 * Copyright (C) 2015 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */


import java.util.List;
import java.util.Properties;

import org.adempiere.ad.dao.IQueryBL;
import org.adempiere.ad.dao.IQueryBuilder;
import org.adempiere.ad.trx.api.ITrx;
import org.adempiere.model.InterfaceWrapperHelper;
import org.adempiere.service.IClientDAO;
import org.adempiere.util.proxy.Cached;
import org.compiere.model.I_AD_Client;
import org.compiere.model.I_AD_ClientInfo;
import org.compiere.util.Env;

import de.metas.adempiere.util.CacheCtx;
import de.metas.util.Services;

public class ClientDAO implements IClientDAO
{
	@Override
	public I_AD_Client getById(final int adClientId)
	{
		return loadOutOfTrx(adClientId, I_AD_Client.class);
	}

	@Override
	public I_AD_Client retriveClient(Properties ctx, int adClientId)
	{
		// NOTE: we assume table level caching is configured for AD_Client table
		// see org.adempiere.model.validator.AdempiereBaseValidator.setupCaching(IModelCacheService)
		return InterfaceWrapperHelper.create(ctx, adClientId, I_AD_Client.class, ITrx.TRXNAME_None);
	}

	@Override
	public I_AD_Client retriveClient(final Properties ctx)
	{
		final int adClientId = Env.getAD_Client_ID(ctx);
		return retriveClient(ctx, adClientId);
	}

	@Override
	public List<I_AD_Client> retrieveAllClients(Properties ctx)
	{
		final IQueryBuilder<I_AD_Client> queryBuilder = Services.get(IQueryBL.class)
				.createQueryBuilder(I_AD_Client.class, ctx, ITrx.TRXNAME_None)
				.addOnlyActiveRecordsFilter() // metas-ts: only return active clients (note that we have an inactive "trash" client with AD_Client_ID=99)
		// .addOnlyContextClient() // NO! we want all of them
		;

		queryBuilder.orderBy()
				.addColumn(I_AD_Client.COLUMNNAME_AD_Client_ID) // to have a predictable order
		;

		return queryBuilder.create().list();
	}

	@Override
	@Cached(cacheName = I_AD_ClientInfo.Table_Name)
	public I_AD_ClientInfo retrieveClientInfo(@CacheCtx final Properties ctx, final int adClientId)
	{
		return Services.get(IQueryBL.class)
				.createQueryBuilder(I_AD_ClientInfo.class, ctx, ITrx.TRXNAME_None)
				.addEqualsFilter(I_AD_ClientInfo.COLUMN_AD_Client_ID, adClientId)
				.create()
				.firstOnlyNotNull(I_AD_ClientInfo.class);
	}	// get

	@Override
	public I_AD_ClientInfo retrieveClientInfo(final Properties ctx)
	{
		final int adClientId = Env.getAD_Client_ID(ctx);
		return retrieveClientInfo(ctx, adClientId);
	}	// get

}
