package de.metas.purchasecandidate;

import javax.annotation.Nullable;

import org.adempiere.mm.attributes.AttributeSetInstanceId;
import org.adempiere.service.OrgId;
import org.adempiere.warehouse.WarehouseId;

import de.metas.bpartner.BPartnerId;
import de.metas.order.OrderAndLineId;
import de.metas.product.ProductId;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/*
 * #%L
 * de.metas.purchasecandidate.base
 * %%
 * Copyright (C) 2018 metas GmbH
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

@Value
@Builder
class PurchaseCandidateImmutableFields
{
	@Nullable
	OrderAndLineId salesOrderAndLineIdOrNull;

	@NonNull
	DemandGroupReference groupReference;

	@NonNull
	BPartnerId vendorId;

	@NonNull
	OrgId orgId;

	@NonNull
	WarehouseId warehouseId;

	@NonNull
	ProductId productId;

	@NonNull
	AttributeSetInstanceId attributeSetInstanceId;

	String vendorProductNo;

	boolean aggregatePOs;
}
