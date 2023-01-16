package com.lotus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.lotus.data.Invoice;
import com.lotus.data.Item;

public class MainApp {
	
	static String fileName = "DayBook (3).xlsx";
	
	public static HashMap<String, String> districts = new HashMap<String, String>();
	public static HashMap<String, String> category = new HashMap<String, String>();
	public static void main(String args[]) throws IOException {
		FileInputStream fis = new FileInputStream(new File(fileName));
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet sheet = wb.getSheetAt(0);
		FormulaEvaluator formulaEvaluator = wb.getCreationHelper().createFormulaEvaluator();
		
		
		//Read Dristricts
		FileInputStream fin = new FileInputStream("input/Districts");
		BufferedReader br = new BufferedReader(new InputStreamReader(fin));
		
		String data = "";
		while((data = br.readLine())!=null)
		{
			districts.put(data.split(",")[0], data.split(",")[1]);
		}
		
		//Read Category
		fin = new FileInputStream("input/Category");
		br = new BufferedReader(new InputStreamReader(fin));
		while((data = br.readLine())!=null)
		{
			category.put(data.split(",")[0], data.split(",")[1]);
		}
		
		fin.close();
		ArrayList<Invoice> invoices = new ArrayList<Invoice>();
		int rowNum = 0;
		int collNum = 0;
		int currentInvoice = -1;
		int currentItem = -1;
		for (Row row : sheet) 
		{
			rowNum++;
			collNum = 0;
			String cellValue = "";
			for (Cell cell : row) 
			{
				collNum++;
				cellValue = getCellValue(formulaEvaluator, cell);
				if(cellValue.endsWith(","))
				{
					cellValue = cellValue.substring(0, cellValue.length() - 1);
				}
		//		System.out.print(cellValue + "\t");
				if(collNum==1 && !cellValue.equals(""))
				{
					currentInvoice++;	
					currentItem = -1;
					rowNum = 1;
					invoices.add(new Invoice());
					invoices.get(currentInvoice).setDate(cellValue);
				
				}
				else if(rowNum==1 && collNum==2  && !cellValue.equals(""))
				{
					

					
					invoices.get(currentInvoice).setDealerName(cellValue.split(",")[0]);
					if(cellValue.contains(","))
					{
						invoices.get(currentInvoice).setCommercialCenter(cellValue.split(",")[1]);
					}
				}
				else if(rowNum==2 && collNum==2  && !cellValue.equals(""))
				{
					invoices.get(currentInvoice).setGstType(cellValue);
				}
				
				else if(rowNum>2 && collNum == 2)
				{
					currentItem++;
					invoices.get(currentInvoice).getItems().add(new Item(cellValue));
				}
				else if(currentItem>=0 && !cellValue.equals("") && rowNum>2 && collNum == 3)
				{
					invoices.get(currentInvoice).getItems().get(currentItem).setQuantity(Double.valueOf(cellValue));
				}
				else if(currentItem>=0 && !cellValue.equals("") && rowNum>2 && collNum == 4)
				{
					invoices.get(currentInvoice).getItems().get(currentItem).setRate(Double.valueOf(cellValue));
				}
				
				invoices.get(currentInvoice).filterUnWantedItems();
				invoices.get(currentInvoice).calculateTax();
				
			}
			
	//		System.out.println();
		}
		
		wb.close();
		
		
		FileOutputStream fout = new FileOutputStream(fileName+"_Result.csv");
		
		fout.write("Date,Dealer Name,Commercial Center,District,Item Name,Category,Quantity,Item Cost,Total Cost,Tax Percentage,Total GST,Grand Total\r\n".getBytes());
		for(Invoice invoice:invoices)
		{
			for(Item item:invoice.getItems())
			{
				double itemTotal = item.getQuantity()*item.getRate()+(item.getRate()* (invoice.getTaxPercentage()/100));
				double gstTotal = item.getQuantity()* (item.getRate()* ((double)invoice.getTaxPercentage()/100));
				double grandTotal = itemTotal+gstTotal;
				String entry = 	invoice.getDate()+","+
								invoice.getDealerName()+","+
								invoice.getCommercialCenter()+","+
								invoice.getDistrict()+","+
								item.getName()+","+
								item.getCategory()+","+
								item.getQuantity()+","+
								item.getRate()+","+
								itemTotal+","+
								invoice.getTaxPercentage()+","+
								gstTotal+","+
								grandTotal+"\r\n";
								
				fout.write(entry.getBytes());
			}
		}
		fout.close();
/*		
		System.out.println("Test Data");
		System.out.println("Number of invoices "+invoices.size());
		
		System.out.println();
		System.out.println();
		for(Invoice invoice : invoices)
		{
			System.out.println("Date "+invoice.getDate());
			System.out.println("Dealer Name "+invoice.getDealerName());
			System.out.println("Commercial Center "+invoice.getCommercialCenter());
			System.out.println("GST Percentage " +invoice.getTaxPercentage());
			System.out.println("Items " + invoice.getItems().size());
			for(Item item:invoice.getItems())
			{
				System.out.println("\t Item Name "+ item.getName() +"\t Quantity : "+item.getQuantity()+"\t Rate : "+item.getRate());
			}
			
			System.out.println();
			System.out.println();
			System.out.println();
		}
		
*/
	}
	
	
	
	@SuppressWarnings("deprecation")
	static String getCellValue(FormulaEvaluator formulaEvaluator,Cell cell)
	{
		String cellValue = "";
		
		switch (formulaEvaluator.evaluateInCell(cell).getCellType()) {
		case Cell.CELL_TYPE_NUMERIC: 
			cellValue =String.valueOf(cell.getNumericCellValue());
			break;
		case Cell.CELL_TYPE_STRING: 
			cellValue = cell.getStringCellValue();
			break;
		}
		
		return cellValue;
	}
}