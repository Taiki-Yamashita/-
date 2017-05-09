package jp.alhinc.yamashita_taiki.calculate_sales;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
public class Sale {
	public static void main(String[] args) throws IOException{
		HashMap<String,String>defineBranchMap = new HashMap<String,String>();
		//支店コード、支店名のマップ
		HashMap<String,Long>branchSalesMap = new HashMap<String,Long>();
		//支店コードと売上のマップ
		HashMap<String,String>defineCommodityMap = new HashMap<>();
		//商品コード、商品名のマップ
		HashMap<String,Long>commoditySalesMap = new HashMap<String,Long>();
		//商品コードと売上のマップ
		ArrayList<Integer> continueCheckSalesFile = new ArrayList<Integer>();
		//連番チェック用ファイル
		ArrayList<String> extractSaleFile = new ArrayList<String>();
		//売上ファイル抽出用ファイル
		BufferedReader br = null;
		if(args.length != 1){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
		if(!readDefinitionFile(args[0], "branch.lst" , defineBranchMap,branchSalesMap, "支店" , "^\\d{3}")){
			return;
		}
		if(!readDefinitionFile(args[0] , "commodity.lst" , defineCommodityMap , commoditySalesMap , "商品","\\w{8}")){
			return;
		}
		File dir = new File(args[0]);
		String[] fileList = dir.list();
		for(int i=0 ; i<fileList.length ; i++){
			if(new File(args[0] , fileList[i]).isFile() && fileList[i].matches("\\d{8}.rcd")){
				extractSaleFile.add(fileList[i]);
				String[] con = fileList[i].split("\\.");
				continueCheckSalesFile.add(Integer.parseInt(con[0]));
			}
		}
		Collections.sort(continueCheckSalesFile);
		int first = continueCheckSalesFile.get(0);
		int last = continueCheckSalesFile.get(continueCheckSalesFile.size() - 1);
		if(continueCheckSalesFile.size() != last - first + 1){
			System.out.println("売上ファイル名が連番になっていません");
			return;
		}
		try{
			for(int i = 0 ; i < extractSaleFile.size() ; i++){
				File fl = new File(args[0] , extractSaleFile.get(i));
				br = new BufferedReader(new FileReader(fl));
				ArrayList<String> sales = new ArrayList<String>();
				String s;
				while((s = br.readLine()) != null){
					sales.add(s);
				}
				if(sales.size() != 3){
					System.out.println(extractSaleFile.get(i) + "のフォーマットが不正です");
					return;
				}
				if(!branchSalesMap.containsKey(sales.get(0))){
					System.out.println(extractSaleFile.get(i) + "の支店コードが不正です");
					return;
				}
				if(!commoditySalesMap.containsKey(sales.get(1))){
					System.out.println(extractSaleFile.get(i) + "の商品コードが不正です");
					return;
				}
				if(!sales.get(2).matches("\\d+")){
					System.out.println("予期せぬエラーが発生しました");
					return;
				}
				long branchvalue = branchSalesMap.get(sales.get(0)).longValue();
				long commodityvalue = commoditySalesMap.get(sales.get(1)).longValue();
				long salevalue = Long.parseLong(sales.get(2));
				branchvalue += salevalue;
				commodityvalue += salevalue;
				if(String.valueOf(branchvalue).length() > 10){
					System.out.println("合計金額が10桁を超えました");
					return;
				}
				if(String.valueOf(commodityvalue).length() > 10){
					System.out.println("合計金額が10桁を超えました");
					return;
				}
				branchSalesMap.put(sales.get(0) , branchvalue);
				commoditySalesMap.put(sales.get(1) , commodityvalue);
			}
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
		}finally{
			if(br != null){
				br.close();
			}
		}
		if(!writeSales(args[0] , "branch.out" , defineBranchMap , branchSalesMap)){
			return;
		}
		if(!writeSales(args[0] , "commodity.out" , defineCommodityMap , commoditySalesMap)){
			return;
		}
	}
	public static boolean readDefinitionFile(String dirPath , String fileName , HashMap<String , String>code ,
		HashMap<String , Long>sales , String codeKind , String letter)throws IOException{
		BufferedReader br = null;
		try{
			File file = new File(dirPath , fileName);
			if(!file.exists()){
				System.out.println(codeKind + "定義ファイルが存在しません");
				return false;
			}
			br = new BufferedReader(new FileReader(file));
			String s;
			while((s = br.readLine()) != null){
				String[] name = s.split(",");
				if(name.length != 2	|| !name[0].matches(letter)){
					System.out.println(codeKind + "定義ファイルのフォーマットが不正です");
					return false;
				}
				code.put(name[0] , name[1]);
				sales.put(name[0] , 0L);
			}
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
			return false;
		}finally{
			try {
				if(br != null){
					br.close();
				}
			}catch (IOException e) {
				System.out.println("予期せぬエラーが発生しました");
			}
		}
			return true;
	}
	public static boolean writeSales(String dirPath , String fileName , HashMap<String , String> nameMap , HashMap<String , Long> salesMap){
		List<Entry<String , Long>> entries = new ArrayList<Entry<String , Long>>(salesMap.entrySet());
		Collections.sort(entries , new Comparator<Entry<String, Long>>() {
		   @Override
		   public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {
		    return o2.getValue().compareTo(o1.getValue());
		   }
		});
			BufferedWriter bw = null;
		try{
			File file = new File(dirPath , fileName);
			file.createNewFile();
			FileWriter fw = new  FileWriter(file);
			bw = new BufferedWriter(fw);
			for(Entry<String, Long> e : entries) {
				bw.write(e.getKey() + "," + nameMap.get(e.getKey()) + "," + e.getValue());
				bw.newLine();
			}
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
			return false;
		}finally{
			try {
				if(bw != null){
					bw.close();
				}
			}catch (IOException e) {
				System.out.println("予期せぬエラーが発生しました");
			}
		}
			return true;
	}
}