package de.metas.purchasecandidate.interceptor;

import java.util.Set;

import org.adempiere.ad.modelvalidator.annotations.DocValidate;
import org.adempiere.ad.modelvalidator.annotations.Interceptor;
import org.compiere.model.I_C_Order;
import org.compiere.model.ModelValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.metas.order.OrderId;
import de.metas.purchasecandidate.PurchaseCandidateId;
import de.metas.purchasecandidate.PurchaseCandidateRepository;
import de.metas.purchasecandidate.async.C_PurchaseCandidates_GeneratePurchaseOrders;

/*
 * #%L
 * de.metas.purchasecandidate.base
 * %%
 * Copyright (C) 2017 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */
@Interceptor(I_C_Order.class)
@Component("de.metas.purchasecandidate.interceptor.C_Order")
public class C_Order
{
	@Autowired
	private PurchaseCandidateRepository purchaseCandidateRepo;

	@DocValidate(timings = ModelValidator.TIMING_AFTER_COMPLETE)
	public void scheduleCreatePurchaseOrderFromPurchaseCandidates(final I_C_Order order)
	{
		if (!order.isSOTrx())
		{
			return;
		}

		final OrderId salesOrderid = OrderId.ofRepoId(order.getC_Order_ID());
		final Set<PurchaseCandidateId> purchaseCandidateIds = purchaseCandidateRepo.retrieveManualPurchaseCandidateIdsBySalesOrderIdFilterQtyToPurchase(salesOrderid);
		if (purchaseCandidateIds.isEmpty())
		{
			return;
		}

		C_PurchaseCandidates_GeneratePurchaseOrders.enqueue(purchaseCandidateIds);
	}
}
