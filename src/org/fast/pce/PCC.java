package org.fast.pce;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

public class PCC {
	
	public static List<String> convert2NodeNames(List<String> lines) {
		List<String> nodeNames = new LinkedList<String>();
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			//line := src dst
			String[] srcDst = line.split(" ");
			String srcName = srcDst[0];
			String dstName = srcDst[1];
			if (!nodeNames.contains(srcName)) {
				nodeNames.add(srcName);
			}
			
			if (!nodeNames.contains(dstName)) {
				nodeNames.add(dstName);
			}
		}
		return nodeNames;
	}
	
	public static int NumR = 1000;
	
	public static int MinBwR = 1000;
	
	public static int ScaleR = 2;
	
	public static int Priority = 16;

	public static void main(String[] args) {
		
		String fileName = "/Users/tony/PycharmProjects/topologyzoo/Abvt.gml.format";
		
		List<String> lines = new LinkedList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line = br.readLine();
			while (line != null)
			{
				lines.add(line);
			    line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			
		}
		
		List<String> nodeNames = PCC.convert2NodeNames(lines);
		
		long totalStartTime = System.currentTimeMillis();
		
		for (int i = 0; i < NumR; i++) {
			System.out.println("pcc: -------------------------------------------------" + i);
			int bw = (int )(Math.random() * MinBwR * (ScaleR - 1) + MinBwR);
			int priority = (int )(Math.random() * Priority + 1);
			int srcNodeId = (int )(Math.random() * nodeNames.size());
			int dstNodeId = (int )(Math.random() * nodeNames.size());
			if (srcNodeId == dstNodeId) continue;
			//long startTime = System.currentTimeMillis();
			PCRequest pcr = new PCRequest(i, bw, priority, srcNodeId, dstNodeId);
			pcr.submitFunction();
			//System.out.println("finish time: " + (System.currentTimeMillis() - startTime));
		}
		
		System.out.println("total finish time: " + (System.currentTimeMillis() - totalStartTime));
		
		
		/*PCRequest pcr1 = new PCRequest(1, 10, 1); //100
		pcr1.submitFunction();
		PCRequest pcr2 = new PCRequest(2, 10, 2);
		pcr2.submitFunction();
		PCRequest pcr3 = new PCRequest(3, 10, 3);
		pcr3.submitFunction();
		PCRequest pcr4 = new PCRequest(4, 30, 5);
		pcr4.submitFunction();
		PCRequest pcr5 = new PCRequest(5, 50, 6);
		pcr5.submitFunction();
		PCRequest pcr6 = new PCRequest(6, 50, 7);
		pcr6.submitFunction();
		PCRequest pcr7 = new PCRequest(7, 10, 4);
		pcr7.submitFunction();*/
	}

}
