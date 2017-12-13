package geomtubo2curve;

public class Point {

	private double xIndex;
	private double yIndex;
	private double zIndex;
	
	public Point(double xIndex, double yIndex, double zIndex) {
		super();
		this.xIndex = xIndex;
		this.yIndex = yIndex;
		this.zIndex = zIndex;
	}
	
	public Point(String s) throws Exception{
		String[] ss = s.trim().split("\\s+");
		if(2==ss.length){
			this.xIndex=Double.parseDouble(ss[0]);
			this.yIndex=Double.parseDouble(ss[1]);
		}else if(3==ss.length){
			this.xIndex=Double.parseDouble(ss[0]);
			this.yIndex=Double.parseDouble(ss[1]);
			this.zIndex=Double.parseDouble(ss[2]);
		}else{
			throw new Exception("error parsing String to Point!");
		}
	}
		
	public String printPoint(){
		String s=this.xIndex+" "+this.yIndex+" "+this.zIndex;
		return s;
	}
	
	/**
	 * Êä³ö r 0 z µÄ×Ö·û´®
	 * @param flag
	 * @return
	 */
	public String printPoint(boolean flag){
		if(flag){
			String s=this.yIndex+" 0 "+this.xIndex;
			return s;
		}else{
			return this.printPoint();
		}
		
		
	}
		
}
