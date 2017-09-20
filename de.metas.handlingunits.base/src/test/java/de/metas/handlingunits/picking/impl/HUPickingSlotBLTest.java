package de.metas.handlingunits.picking.impl;

import static org.adempiere.model.InterfaceWrapperHelper.newInstance;
import static org.adempiere.model.InterfaceWrapperHelper.save;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.adempiere.ad.dao.ICompositeQueryFilter;
import org.adempiere.ad.wrapper.POJOWrapper;
import org.adempiere.test.AdempiereTestHelper;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.metas.adempiere.model.I_M_Product;
import de.metas.handlingunits.model.I_M_HU;
import de.metas.handlingunits.model.I_M_HU_Storage;
import de.metas.handlingunits.model.I_M_Locator;
import de.metas.handlingunits.model.I_M_ShipmentSchedule;
import de.metas.handlingunits.model.I_M_Source_HU;
import de.metas.handlingunits.model.I_M_Warehouse;
import de.metas.handlingunits.model.X_M_HU;
import de.metas.handlingunits.picking.IHUPickingSlotBL.RetrieveActiveSourceHusQuery;
import de.metas.handlingunits.picking.impl.HUPickingSlotBL;

/*
 * #%L
 * de.metas.handlingunits.base
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

public class HUPickingSlotBLTest
{
	private I_M_Warehouse wh;
	private I_M_Locator locator;
	private I_M_Product product;
	private I_M_Product otherProduct;
	private I_M_Locator otherLocator;
	private List<I_M_HU> hus;

	@Before
	public void init()
	{
		AdempiereTestHelper.get().init();

		wh = newInstance(I_M_Warehouse.class);
		save(wh);

		locator = newInstance(I_M_Locator.class);
		locator.setM_Warehouse(wh);
		save(locator);

		final I_M_Warehouse otherWh = newInstance(I_M_Warehouse.class);
		save(otherWh);

		otherLocator = newInstance(I_M_Locator.class);
		otherLocator.setM_Warehouse(otherWh);
		save(otherLocator);

		product = newInstance(I_M_Product.class);
		save(product);

		otherProduct = newInstance(I_M_Product.class);
		save(otherProduct);
		
		hus = ImmutableList.of(
				createHU("locator-product", locator, product),
				createHU("locator-product-nosourceHU", locator, product),
				createHU("locator-otherProduct", locator, otherProduct),
				createHU("otherLocator-product", otherLocator, product),
				createHU("otherLocator-otherProduct", otherLocator, otherProduct));
	}

	@Test
	public void testCreateHuFiltersForScheds()
	{
	
		final RetrieveActiveSourceHusQuery query = new RetrieveActiveSourceHusQuery(ImmutableSet.of(product.getM_Product_ID()), wh.getM_Warehouse_ID());
		final ICompositeQueryFilter<I_M_HU> huFilters = new HUPickingSlotBL().createHuFiltersForScheds(query);
		assertThat(huFilters.accept(hus.get(0))).isTrue();
		assertThat(huFilters.accept(hus.get(1))).isTrue();
		assertThat(huFilters.accept(hus.get(2))).isFalse();
		assertThat(huFilters.accept(hus.get(3))).isFalse();
		assertThat(huFilters.accept(hus.get(4))).isFalse();
	}
	
	@Test
	public void testRetrieveActiveSourceHUs()
	{
		final I_M_Source_HU sourceHu = newInstance(I_M_Source_HU.class);
		sourceHu.setM_HU(hus.get(0));
		save(sourceHu);
		
		final RetrieveActiveSourceHusQuery query = new RetrieveActiveSourceHusQuery(ImmutableSet.of(product.getM_Product_ID()), wh.getM_Warehouse_ID());
		final List<I_M_HU> result = new HUPickingSlotBL().retrieveActiveSourceHUs(query);
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getM_HU_ID()).isEqualTo(hus.get(0).getM_HU_ID());
	}

	I_M_HU createHU(
			final String instanceName,
			final I_M_Locator locator,
			final I_M_Product product)
	{
		final I_M_HU hu = newInstance(I_M_HU.class);
		hu.setHUStatus(X_M_HU.HUSTATUS_Active);
		hu.setM_Locator(locator);
		save(hu);
		POJOWrapper.setInstanceName(hu, instanceName);

		final I_M_HU_Storage hu_storage = newInstance(I_M_HU_Storage.class);
		hu_storage.setM_HU(hu);
		hu_storage.setM_Product(product);
		save(hu_storage);

		return hu;
	}

	@Test
	public void testRetrieveActiveSourceHusQuery_fromShipmentSchedules()
	{
		final I_M_ShipmentSchedule shipmentSchedule1 = newInstance(I_M_ShipmentSchedule.class);
		shipmentSchedule1.setM_Warehouse(wh);
		shipmentSchedule1.setM_Product(product);
		save(shipmentSchedule1);

		final I_M_ShipmentSchedule shipmentSchedule2 = newInstance(I_M_ShipmentSchedule.class);
		shipmentSchedule2.setM_Warehouse(wh);
		shipmentSchedule2.setM_Product(product);
		save(shipmentSchedule2);
		
		final RetrieveActiveSourceHusQuery query = RetrieveActiveSourceHusQuery.fromShipmentSchedules(ImmutableList.of(shipmentSchedule1, shipmentSchedule2));
		
		assertThat(query.getWarehouseId()).isEqualTo(wh.getM_Warehouse_ID());
		assertThat(query.getProductIds()).containsExactly(product.getM_Product_ID());
	}
	
	
}
