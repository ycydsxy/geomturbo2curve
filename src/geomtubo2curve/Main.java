package geomtubo2curve;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

	public static void run(File file) throws Exception {
		String content = Utils.readString(file);

		// 找hub和shroud
		System.out.println("===========READING ZR CURVES===========");

		String regx1 = "NI_BEGIN\\s+basic_curve([\\s\\S]*?)NI_END\\s+basic_curve";
		Pattern p1 = Pattern.compile(regx1);
		Matcher m1 = p1.matcher(content);
		while (m1.find()) {
			String curveContent = m1.group();

			System.out.println(curveContent);
			System.out.println("--------------------------------");
			String curveName = "";
			String regx2 = "NAME\\s+(.+)";
			Pattern p2 = Pattern.compile(regx2);
			Matcher m2 = p2.matcher(curveContent);
			if (m2.find()) {
				curveName = m2.group(1);
			} else {
				System.out
						.println("ERROR:NOT FOUND THE ZR CURVE NAME!CONTINUE...");
				continue;
			}
			List<Point> curvePoints = new ArrayList<>();
			String regx3 = "(-?.?\\d\\S*[ 　]+-?\\d\\S*)$";
			Pattern p3 = Pattern.compile(regx3, Pattern.MULTILINE);
			Matcher m3 = p3.matcher(curveContent);
			while (m3.find()) {
				curvePoints.add(new Point(m3.group(1)));
			}
			System.out.println("CurveName:" + curveName);
			System.out.println("PointsNum:" + curvePoints.size());
			StringBuffer buffer = new StringBuffer();
			for (Point point : curvePoints) {
				buffer.append(point.printPoint(true));
				buffer.append("\r\n");
			}
			Utils.writeString(new File(curveName + ".curve"), buffer.toString());
			System.out.println("================================");
		}
		
		System.out.println();
		
		// 找blade
		System.out.println("===========READING BLADE SECTIONS===========");

		String regx4 = "NI_BEGIN\\s+nibladegeometry([\\s\\S]*?)NI_END\\s+nibladegeometry";
		Pattern p4 = Pattern.compile(regx4);
		Matcher m4 = p4.matcher(content);
		int bladeIndex = 0;
		while (m4.find()) {// 多个叶片的层面
			String bladeContent = m4.group();
			List<List<Point>> bladeSections1 = new ArrayList<>();
			bladeIndex++;

			int periodicity = 0;
			String regx5 = "number_of_blades\\s+(\\d+)";
			Pattern p5 = Pattern.compile(regx5);
			Matcher m5 = p5.matcher(bladeContent);
			if (m5.find()) {
				periodicity = Integer.parseInt(m5.group(1));
			} else {
				System.out.println("ERROR:THE PERIODICITY OF BALDE #"
						+ bladeIndex + " IS NOT FOUND!CONTINUE...");
				continue;
			}

			String regx6 = "section\\s+([\\s\\S]*?)(NI_END|#)";
			Pattern p6 = Pattern.compile(regx6);
			Matcher m6 = p6.matcher(bladeContent);
			while (m6.find()) {// 每排叶片的层面
				String sectioncontent = m6.group(1);
				List<Point> sectionPoints = new ArrayList<>();
				String regx7 = "(-?.?\\d\\S*[ 　]+-?.?\\d\\S*[ 　]+-?\\d\\S*)$";
				Pattern p7 = Pattern.compile(regx7, Pattern.MULTILINE);
				Matcher m7 = p7.matcher(sectioncontent);
				while (m7.find()) {// 每个截面的层面
					String temp = m7.group(1);
					sectionPoints.add(new Point(temp));
				}
				bladeSections1.add(sectionPoints);
			}

			System.out.println("Periodicity:" + periodicity);
			int sectionSize_ori = bladeSections1.size();

			// 整理，将压力面、吸力面合成
			for (int i = 0; i < sectionSize_ori / 2; i++) {
				List<Point> section = bladeSections1.get(i);
				List<Point> pressureSection = bladeSections1.get(i
						+ sectionSize_ori / 2);
				Collections.reverse(pressureSection);
				section.addAll(pressureSection);
			}
			List<List<Point>> bladeSections = bladeSections1.subList(0,
					sectionSize_ori / 2);

			System.out.println("BladeSectionNum:" + bladeSections.size());
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < bladeSections.size(); i++) {
				List<Point> section = bladeSections.get(i);
				buffer.append("# Profile " + (i+1)).append("\r\n");
				for (Point point : section) {
					buffer.append(point.printPoint()).append("\r\n");
				}
			}
			Utils.writeString(new File("profile" + bladeIndex + ".curve"),
					buffer.toString());
			StringBuffer configuration = new StringBuffer();
			configuration.append("Axis of Rotation: Z").append("\r\n");
			configuration.append("Number of Blade Sets: ").append(periodicity)
					.append("\r\n");
			configuration.append("Geometry Units: M").append("\r\n");
			configuration.append("Hub Data File: hub.curve").append("\r\n");
			configuration.append("Shroud Data File: shroud.curve").append(
					"\r\n");
			configuration.append("Profile Data File:profile")
					.append(bladeIndex).append(".curve").append("\r\n");
			Utils.writeString(new File("BladeGen" + bladeIndex + ".inf"),
					configuration.toString());
			System.out.println("================================");
		}
	}
	
	public static void main(String args[]) throws Exception {
		String prompt="请输入需要转换的文件名:(如 's1.gemoturbo')\r\n";
		String filename = Utils.readDataFromConsole(prompt);
		run(new File(filename));
	}
}
