package com.book.ensureu.constant;

public enum PaperCategory {

	SSC_CGL("SSC CGL"),
	SSC_CPO("SSC CPO"),
	SSC_CHSL("SSC CHSL"),
	BANK_PO("BANK PO");
	
	private String category;
	
	private PaperCategory(String category) {
		this.category=category;
	}
	
	public String getCategory() {
		return category;
	}
	
	public static PaperCategory valueOfByString(String value) {
		switch (value) {
		case "SSC CGL":
			return PaperCategory.SSC_CGL;
		case "SSC CPO":
			return PaperCategory.SSC_CPO;
		case "SSC CHSL":
			return PaperCategory.SSC_CHSL;
		case "BANK PO":
			return PaperCategory.BANK_PO;
		default:
			throw new IllegalArgumentException("String Parameter is not valid");

		}
	}

	public static PaperCategory valueOfByAnyString(String value) {
		if (value == null) {
			throw new IllegalArgumentException("String parameter cannot be null");
		}
		// Normalize: trim, replace spaces with underscores, and make uppercase
		String normalized = value.trim().replace(" ", "_").toUpperCase();
		switch (normalized) {
			case "SSC_CGL":
				return PaperCategory.SSC_CGL;
			case "SSC_CPO":
				return PaperCategory.SSC_CPO;
			case "SSC_CHSL":
				return PaperCategory.SSC_CHSL;
			case "BANK_PO":
				return PaperCategory.BANK_PO;
			default:
				throw new IllegalArgumentException("String parameter is not valid: " + value);
		}
	}

	public static PaperType getParent(PaperCategory paperCategory) {
		
		switch(paperCategory) {
		case SSC_CGL:
		  return PaperType.SSC;
		case SSC_CPO:
			  return PaperType.SSC;
		case SSC_CHSL:
			  return PaperType.SSC;
		case BANK_PO:
			  return PaperType.BANK;	  
		default:
			throw new IllegalArgumentException(" invalid paperCategory");
		}
	}
	
	@Override
	public String toString() {
		return category;
	}
	
	
	
}
