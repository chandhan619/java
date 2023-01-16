package com.lotus.data;

import java.util.ArrayList;
import java.util.Iterator;

import com.lotus.MainApp;

public class Invoice {
	private String date;
	private String dealerName;
	private String commercialCenter;
	private ArrayList<Item> items;
	private double totalAmount;
	private String gstType;
	private int taxPercentage ;
	private String district;

	private String[] excemptedItems = {"CGST","SGST","Hamali","ROUNDING OFF","Special Discount"};
	
	public Invoice() {
		items = new ArrayList<Item>();
	}
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getDealerName() {
		return dealerName;
	}
	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}
	public String getCommercialCenter() {

		return commercialCenter;
	}
	public void setCommercialCenter(String commercialCenter) {
		
		commercialCenter = commercialCenter.trim();
		district = MainApp.districts.get(commercialCenter);
		this.commercialCenter = commercialCenter;
	}
	public ArrayList<Item> getItems() {
		return items;
	}
	public void setItems(ArrayList<Item> items) {
		this.items = items;
	}
	public double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getGstType() {
		return gstType;
	}
	public void setGstType(String gstType) {
		this.gstType = gstType;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public int getTaxPercentage() {
		return taxPercentage;
	}

	public void setTaxPercentage(int taxPercentage) {
		this.taxPercentage = taxPercentage;
	}

	public void filterUnWantedItems()
	{
		Iterator<Item> itr = items.iterator();
		
		while(itr.hasNext())
		{
			Item item = itr.next();
			if(hasItemInList(item.getName()))
			{
				itr.remove();
			}
		}
	}
	
	public void calculateTax()
	{
		if(getGstType()!=null && getGstType().equals("GST TAXABLE SALES"))
		{
			taxPercentage = 18;
		}
	}
	
	boolean hasItemInList(String itemName)
	{
		boolean  flag = false;
		
		for(String item:excemptedItems)
		{
			if(item.equals(itemName))
			{
				flag = true;
				break;
			}
		}
		
		return flag;
	}
}
