package com.book.ensureu.constant;

public enum Test {

	A("manish"),B("thakran"),C("singh"),
	SSC_CGL_TIER1("SSC CGL TIER-1");
	private String val;
	private Test(String val) {
		this.val = val;
	}
	
	public String getVal() {
		return this.val;
	}
	
	public Test getValueByString(String val) {
		switch (val) {
		case "thakran":
			return A;
		
		default : 
		return B;	
		
		}
		
	}
	
}
