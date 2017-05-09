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
	public static void main(String[]args) throws IOException{
		HashMap<String,String>branchmap = new HashMap<String,String>();
		//支店コード、支店名のマップ
		HashMap<String,Long>branchsalesmap = new HashMap<String,Long>();
		//支店コードと売上のマップ
		HashMap<String,String>commoditymap = new HashMap<String,String>();
		//商品コード、商品名のマップ
		HashMap<String,Long>commoditysalesmap = new HashMap<String,Long>();
		//商品コードと売上のマップ
		ArrayList<Integer> check = new ArrayList<Integer>();
		//連番チェック用ファイル
		ArrayList<String> bring = new ArrayList<String>();
		//売上ファイル抽出用ファイル
		BufferedReader br = null;

		if(args.length != 1){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
		//reader(args[0], "branch.lst", branchmap, branchsalesmap, "支店");
		if(!reader(args[0], "branch.lst" , branchmap,branchsalesmap, "支店" , "^\\d{3}")){
			return;
		}
		//reader(args[0] , "commodity.lst" ,commoditymap,commoditysalesmap,"商品");
		if(!reader(args[0] , "commodity.lst" , commoditymap , commoditysalesmap , "商品","\\w{8}")){
			return;
		}
		File dir = new File(args[0]);
		String[]fileList=dir.list();

		for(int i=0 ; i<fileList.length ; i++){
			if(new File(args[0] , fileList[i]).isFile() && fileList[i].matches("\\d{8}.rcd")){
				bring.add(fileList[i]);
				String[] con = fileList[i].split("\\.");
				check.add(Integer.parseInt(con[0]));
			}
		}
		Collections.sort(check);
		int first = (check.get(0));
		int last = (check.get(check.size()-1));
		if(check.size() != last-first+1){
			System.out.println("売上ファイル名が連番になっていません");
			return;
		}
		try{
			for(int i=0 ; i<bring.size() ; i++){
				File fl = new File(args[0] , bring.get(i));
				br = new BufferedReader(new FileReader(fl));
				ArrayList<String> sales = new ArrayList<String>();
				String s;
				while((s = br.readLine()) != null){
					sales.add(s);
				}
				if(sales.size() != 3){
					System.out.println(bring.get(i)+"のフォーマットが不正です");
					return;
				}
				if(!branchsalesmap.containsKey(sales.get(0))){
					System.out.println(bring.get(i)+"の支店コードが不正です");
					return;
				}
				if(!commoditysalesmap.containsKey(sales.get(1))){
					System.out.println(bring.get(i)+"の商品コードが不正です");
					return;
				}
				if(!sales.get(2).matches("\\d+")){
					System.out.println("予期せぬエラーが発生しました");
					return;
				}

				long branchvalue = branchsalesmap.get(sales.get(0)).longValue();
				long commodityvalue = commoditysalesmap.get(sales.get(1)).longValue();
				long salevalue = Long.parseLong(sales.get(2));
				branchvalue+=salevalue;
				commodityvalue+=salevalue;
				if(String.valueOf(branchvalue).length() > 10){
					System.out.println("合計金額が10桁を超えました");
					return;
				}

				if(String.valueOf(commodityvalue).length() > 10){
					System.out.println("合計金額が10桁を超えました");
					return;
				}

				branchsalesmap.put(sales.get(0),branchvalue);
				commoditysalesmap.put(sales.get(1),commodityvalue);


			}
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");

		}finally{
			br.close();
		}
		if(!salesOut(args[0] , "branch.out" , branchmap , branchsalesmap)){
			return;
		}

		if(!salesOut(args[0] , "commodity.out" , commoditymap , commoditysalesmap)){
			return;
		}
	}
	public static boolean reader(String dirPath,String fileName,HashMap<String,String>code,
		HashMap<String,Long>sales,String kind,String letter)throws IOException{
		BufferedReader br = null;
		try{
			File file = new File(dirPath,fileName);
			if(!file.exists()){
				System.out.println(kind + "定義ファイルが存在しません");
				return false;
			}
			br = new BufferedReader(new FileReader(file));
			String s;

			while((s = br.readLine()) != null){
				String[] name = s.split(",");
				if(name.length != 2	|| !name[0].matches(letter)){
					System.out.println(kind + "定義ファイルのフォーマットが不正です");
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
				br.close();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
			}
		}
			return true;
	}
	public static boolean salesOut(String dirPath,String fileName,HashMap<String,String> nameMap,HashMap<String,Long> salesMap){
		List<Entry<String, Long>> entries = new ArrayList<Entry<String, Long>>(salesMap.entrySet());
		Collections.sort(entries, new Comparator<Entry<String, Long>>() {
		   @Override
		   public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {
		    return o2.getValue().compareTo(o1.getValue());
		   }
		});
			BufferedWriter bw = null;
		try{

			File file = new File(dirPath,fileName);
			file.createNewFile();
			FileWriter fw = new  FileWriter(file);
			bw=new BufferedWriter(fw);
			for(Entry<String, Long> e : entries) {
				bw.write(e.getKey() + "," + nameMap.get(e.getKey()) + "," + e.getValue());
				bw.newLine();
			}
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
			return false;
		}finally{
			try {
				bw.close();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
			}
		}
			return true;
	}

}
