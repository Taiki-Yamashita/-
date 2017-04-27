package jp.alhinc.m.yamashita_taiki.calculate_sales;
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
			File file =null;
			BufferedReader br=null;
			try{
				file =new File(args[0],"branch.lst");
				if(!file.exists()){
						System.out.println("支店定義ファイルは存在しません");
						return;
				}
				br=new BufferedReader(new FileReader(file));
				String s;
				while((s=br.readLine()) !=null){
					String[] branch = s.split(",",2);
					branchmap.put(branch[0],branch[1]);
					//カンマで分けて支店定義マップに突っ込む
					branchsalesmap.put(branch[0], 0L);
					//支店コード、売上ファイルの初期値設定
					if(s.matches("^\\d{3},.+支店$")==false){
						System.out.println("支店定義ファイルのフォーマットが不正です");
						return;
					}
					if(branch.length!=2){
						System.out.println("支店定義ファイルのフォーマットが不正です");
						return;
					}
				}
			}catch(IOException e){
				System.out.println(e);
				System.out.println("予期せぬエラーが発生しました");
				return;
			}finally{
				br.close();
			}

			try{
				 file =new File(args[0],"commodity.lst");
				if(!file.exists()){
						System.out.println("商品定義ファイルは存在しません");
						return;
				}
				br=new BufferedReader(new FileReader(file));
				String s;

				while((s=br.readLine()) !=null){

					String[] commodity = s.split(",");
					if(!commodity[0].matches("\\w{8}")){
						System.out.println("商品定義ファイルのフォーマットが不正です");
						return;
					}
					if(commodity.length!=2){
						System.out.println("商品定義ファイルのフォーマットが不正です");
						return;
					}
					commoditymap.put(commodity[0],commodity[1]);
					commoditysalesmap.put(commodity[0], 0L);
				}
				br.close();
			}catch(IOException e){
				System.out.println(e);
				System.out.println("予期せぬエラーが発生しました");
				return;
			}finally{
				br.close();
			}
			ArrayList<Integer> sum=new ArrayList<Integer>();
		try{
			File dir =new File(args[0]);
			String[]fileList=dir.list();
			for(int i=0;i<fileList.length;i++){
					ArrayList<String> sales=new ArrayList<String>();
					if(fileList[i].matches("\\d{8}\\.rcd")){
						if(!fileList[i].matches("\\d{8}\\.rcd")){
							System.out.println("売上ファイルが連番になっていません");
							return;
						}
						File fl=new File(args[0],fileList[i]);
						FileReader fr=new FileReader(fl);
					 br=new BufferedReader(fr);
						String s;
						String[] con = fileList[i].split("\\.",0);
						sum.add(Integer.parseInt(con[0]));
						while((s=br.readLine())!=null){
							sales.add(s);
						}
						br.close();
						if(sales.size()!=3){
							System.out.println(fileList[i]+"のフォーマットが不正です");
							return;
						}
						if(branchsalesmap.containsKey(sales.get(0))==false){
							System.out.println(fileList[i]+"の支店コードが不正です");
							return;
						}
						if(commoditysalesmap.containsKey(sales.get(1))==false){
							System.out.println(fileList[i]+"の商品コードが不正です");
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
			}
				Collections.sort(sum);
				int y=(sum.get(0));
				int z=(sum.get(sum.size()-1));
			if(sum.size()!=z-y+1){
				System.out.println("売上ファイルが連番になっていません");
				return;
			}
		}catch(IOException e){
				System.out.println(e);
				System.out.println("予期せぬエラーが発生しました");
			}
			List<Entry<String, Long>> branchentries = new ArrayList<Entry<String, Long>>(branchsalesmap.entrySet());
			Collections.sort(branchentries, new Comparator<Entry<String, Long>>() {
			    @Override
			    public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {
			    	return o2.getValue().compareTo(o1.getValue());
			    }
			});
			BufferedWriter bw=null;
			try{
				 file =new File(args[0],"branch.out");
				//支店別集計ファイル
				file.createNewFile();
				if(!file.exists()){
					System.out.println("支店別集計ファイルは存在しません");
					return;
				}
				FileWriter fw=new  FileWriter(file);
				bw=new BufferedWriter(fw);

				for(Entry<String, Long> e : branchentries) {

					bw.write(e.getKey()+","+branchmap.get(e.getKey())+","+ e.getValue());
					bw.newLine();
				}

			}
			catch(IOException e){
				System.out.println(e);
				System.out.println("予期せぬエラーが発生しました");
				return;
			}finally{
				bw.close();
			}
			List<Entry<String, Long>> commodityentries = new ArrayList<Entry<String, Long>>
			(commoditysalesmap.entrySet());
			Collections.sort(commodityentries, new Comparator<Entry<String, Long>>() {
			   @Override
			    public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {
			    	return o2.getValue().compareTo(o1.getValue());
			    }
			});
			try{
				file =new File(args[0],"commodity.out");
				//商品別集計ファイル
				file.createNewFile();
				if(!file.exists()){
					System.out.println("商品別集計ファイルは存在しません");
					return;
				}
				FileWriter fw=new  FileWriter(file);
				bw=new BufferedWriter(fw);
				for(Entry<String, Long> e : commodityentries) {

					bw.write(e.getKey()+","+commoditymap.get(e.getKey())+","+ e.getValue());
					bw.newLine();
				}

			}
			catch(IOException e){
				System.out.println(e);
				System.out.println("予期せぬエラーが発生しました");
				return;
			}finally{
		bw.close();
			}
	}
}
