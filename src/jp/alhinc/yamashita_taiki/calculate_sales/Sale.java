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
		HashMap<String,String>branchmap=new HashMap<String,String>();
		//支店コード、支店名のマップ
		HashMap<String,Long>branchsalesmap=new HashMap<String,Long>();
		//支店コードと売上のマップ
		HashMap<String,String>commoditymap=new HashMap<String,String>();
		//商品コード、商品名のマップ
		HashMap<String,Long>commoditysalesmap=new HashMap<String,Long>();
		//商品コードと売上のマップ
		ArrayList<Integer> sum=new ArrayList<Integer>();
		//連番チェック用ファイル
		ArrayList<String> bring=new ArrayList<String>();
		//売上ファイル抽出用ファイル
		File file =null;
		BufferedReader br=null;

		if(args.length!=1){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
		//reader(args[0],"branch.lst",branchmap,branchsalesmap,"支店");
		if(!reader(args[0],"branch.lst",branchmap,branchsalesmap,"支店","^\\d{3}")){
			return;
		}
		//reader(args[0],"commodity.lst",commoditymap,commoditysalesmap,"商品");
		if(!reader(args[0],"commodity.lst",commoditymap,commoditysalesmap,"商品","\\w{8}")){
			return;
		}
		File dir =new File(args[0]);
		String[]fileList=dir.list();

		for(int i=0;i<fileList.length;i++){
			if(fileList[i].matches("\\d{8}.rcd")&&new File(args[0], fileList[i]).isFile()){
				bring.add(fileList[i]);
				String[] con = fileList[i].split("\\.");
				sum.add(Integer.parseInt(con[0]));
			}
		}
		Collections.sort(sum);
		int first=(sum.get(0));
		int last=(sum.get(sum.size()-1));
		if(sum.size()!=last-first+1){
			System.out.println("売上ファイル名が連番になっていません");
			return;

		}
		try{
			for(int i=0;i<bring.size();i++){
				File fl=new File(args[0],bring.get(i));
				FileReader fr=new FileReader(fl);
				br=new BufferedReader(fr);
				ArrayList<String> sales=new ArrayList<String>();
				String s;
				while((s=br.readLine())!=null){
					sales.add(s);
				}
				if(sales.size()!=3){
					System.out.println(bring.get(i)+"のフォーマットが不正です");
					return;
				}
				if(branchsalesmap.containsKey(sales.get(0))==false){
					System.out.println(bring.get(i)+"の支店コードが不正です");
					return;
				}
				if(commoditysalesmap.containsKey(sales.get(1))==false){
					System.out.println(bring.get(i)+"の商品コードが不正です");
					return;
				}
				if(!sales.get(2).matches("\\d+")){
					System.out.println("予期せぬエラーが発生しました");
					return;

				}

				long x=branchsalesmap.get(sales.get(0)).longValue();
				long y=commoditysalesmap.get(sales.get(1)).longValue();
				long z=Long.parseLong(sales.get(2));
				x+=z;
				y+=z;
				if(String.valueOf(x).length()>=10){
					System.out.println("合計金額が10桁を超えました");
					return;
				}

				if(String.valueOf(y).length()>=10){
					System.out.println("合計金額が10桁を超えました");
					return;
				}

				branchsalesmap.put(sales.get(0),x);
				commoditysalesmap.put(sales.get(1),y);


			}
		}catch(IOException e){
				System.out.println("予期せぬエラーが発生しました");

		}finally{
			br.close();
		}
		BufferedWriter bw=null;
		if(!salesout(args[0],"branch.out",branchmap,branchsalesmap)){
			return;
		}

		if(!salesout(args[0],"commodity.out",commoditymap,commoditysalesmap)){
			return;
		}
	}
	public static boolean reader(String dirPath,String fileName,HashMap<String,String>code,
			HashMap<String,Long>sales,String kind,String letter)throws IOException{
		BufferedReader br=null;
		try{
			File file =new File(dirPath,fileName);
			if(!file.exists()){
				System.out.println(kind+"定義ファイルが存在しません");
				return false;
			}
			br=new BufferedReader(new FileReader(file));
			String s;

			while((s=br.readLine()) !=null){
				String[] name = s.split(",");
					if(name[0].matches(letter)==false&&name.length!=2){
					System.out.println(kind+"定義ファイルのフォーマットが不正です");
					return false;
					}
				code.put(name[0],name[1]);
				//カンマで分けて支店定義マップに突っ込む
				sales.put(name[0], 0L);
				//支店コード、売上ファイルの初期値設定
			}

		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
			return false;

		}finally{
			br.close();
		}
			return true;
	}
	/*public static boolean search(String dirPath,ArrayList<Integer>sum,ArrayList<String>bring){

		File dir =new File(dirPath);
		String[]fileList=dir.list();

		for(int i=0;i<fileList.length;i++){
			if(fileList[i].matches("\\d{8}.rcd")&&new File(dirPath, fileList[i]).isFile()){
				bring.add(fileList[i]);
				String[] con = fileList[i].split("\\.");
				sum.add(Integer.parseInt(con[0]));
			}
		}
			Collections.sort(sum);
			int first=(sum.get(0));
			int last=(sum.get(sum.size()-1));
			if(sum.size()!=last-first+1){
			System.out.println("売上ファイル名が連番になっていません");
			return false;

			}
			return true;
	}*/
	public static boolean salesout(String dirPath,String fileName,HashMap<String,String> nameMap,HashMap<String,Long> salesmap){
		List<Entry<String, Long>> branchentries = new ArrayList<Entry<String, Long>>(salesmap.entrySet());
		Collections.sort(branchentries, new Comparator<Entry<String, Long>>() {
		   @Override
		   public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {
		    return o2.getValue().compareTo(o1.getValue());
		   }
		});
			BufferedWriter bw=null;
		try{

			File file =new File(dirPath,fileName);
			file.createNewFile();
			FileWriter fw=new  FileWriter(file);
			bw=new BufferedWriter(fw);
			for(Entry<String, Long> e : branchentries) {
				bw.write(e.getKey()+","+nameMap.get(e.getKey())+","+ e.getValue());
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
				e.printStackTrace();
			}
		}
			return true;
	}

}
