package com.book.ensureu.constant;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public enum PaperSubCategory {
	SSC_CGL_TIER1 {
		@Override
		public List<String> getPaperSubCategoryTypes() {
			List<String> paperSubCategoryTypes=new ArrayList<String>();
			paperSubCategoryTypes.add("SSC_CGL_TIER1-General Intelligence and Reasoning");
			paperSubCategoryTypes.add("SSC_CGL_TIER1-General Awareness");
			paperSubCategoryTypes.add("SSC_CGL_TIER1-Quantitative Aptitude");
			paperSubCategoryTypes.add("SSC_CGL_TIER1-English Comprehension");
			return paperSubCategoryTypes;
		}
	}, SSC_CGL_TIER2 {
		@Override
		public List<String> getPaperSubCategoryTypes() { 
			List<String> paperSubCategoryTypes=new ArrayList<String>();
			paperSubCategoryTypes.add("SSC_CGL_TIER2-Quantitative Ability");
			paperSubCategoryTypes.add("SSC_CGL_TIER2-Statistics");
			paperSubCategoryTypes.add("SSC_CGL_TIER2-General Studies (Finance & Economics)");
			paperSubCategoryTypes.add("SSC_CGL_TIER2-English Language & Comprehension");
			return paperSubCategoryTypes;
		}
	}, SSC_CHSL_TIER1 {
		@Override
		public List<String> getPaperSubCategoryTypes() {
			List<String> paperSubCategoryTypes=new ArrayList<String>();
			paperSubCategoryTypes.add("SSC_CHSL_TIER1-General Intelligence");
			paperSubCategoryTypes.add("SSC_CHSL_TIER1-General Awareness");
			paperSubCategoryTypes.add("SSC_CHSL_TIER1-Quantitative Aptitude");
			paperSubCategoryTypes.add("SSC_CHSL_TIER1-English Language");
			return paperSubCategoryTypes;
		}
	}, SSC_CHSL_TIER2 {
		@Override
		public List<String> getPaperSubCategoryTypes() {
			List<String> paperSubCategoryTypes=new ArrayList<String>();
			paperSubCategoryTypes.add("SSC_CHSL_TIER2-Mathematical Abilities");
			paperSubCategoryTypes.add("SSC_CHSL_TIER2-Reasoning and General Intelligence");
			paperSubCategoryTypes.add("SSC_CHSL_TIER2-English Language and Comprehension");
			paperSubCategoryTypes.add("SSC_CHSL_TIER2-General Awareness");
			paperSubCategoryTypes.add("SSC_CHSL_TIER2-Computer Knowledge");
			return paperSubCategoryTypes;
		}
	}, SSC_CPO_TIER1 {
		@Override
		public List<String> getPaperSubCategoryTypes() {
			List<String> paperSubCategoryTypes=new ArrayList<String>();
			paperSubCategoryTypes.add("SSC_CPO_TIER1-General Intelligence and Reasoning");
			paperSubCategoryTypes.add("SSC_CPO_TIER1-General Knowledge and General Awareness");
			paperSubCategoryTypes.add("SSC_CPO_TIER1-Quantitative Aptitude");
			paperSubCategoryTypes.add("SSC_CPO_TIER1-English Comprehension");
			return paperSubCategoryTypes;
		}
	},
	SSC_CPO_TIER2 {
		@Override
		public List<String> getPaperSubCategoryTypes() {
			List<String> paperSubCategoryTypes=new ArrayList<String>();
			paperSubCategoryTypes.add("SSC_CPO_TIER2-English Language and Comprehension");
			return paperSubCategoryTypes;
		}
	}, BANK_PO_PRE {
		@Override
		public List<String> getPaperSubCategoryTypes() {
			// TODO Auto-generated method stub
			return null;
		}
	}, BANK_PO_MAIN {
		@Override
		public List<String> getPaperSubCategoryTypes() {
			// TODO Auto-generated method stub
			return null;
		}
	};

	public String toString() {
		switch (this) {
		case SSC_CGL_TIER1:
			return "SSC CGL TIER-1";
		case SSC_CGL_TIER2:
			return "SSC CGL TIER-2";
		case SSC_CHSL_TIER1:
			return "SSC CHSL TIER-1";
		case SSC_CHSL_TIER2:
			return "SSC CHSL TIER-2";
		case SSC_CPO_TIER1:
			return "SSC CPO TIER-1";
		case SSC_CPO_TIER2:
			return "SSC CPO TIER-2";
		case BANK_PO_PRE:
			return "BANK PO PRE";
		case BANK_PO_MAIN:
			return "BANK PO MAIN";
		default:
			throw new IllegalArgumentException("Enum Parameter is not valid");

		}
	}

	public static PaperSubCategory valueOfByString(String value) {
		switch (value) {
		case "SSC CGL TIER-1":
			return PaperSubCategory.SSC_CGL_TIER1;
		case "SSC CGL TIER-2":
			return PaperSubCategory.SSC_CGL_TIER2;
		case "SSC CHSL TIER-1":
			return PaperSubCategory.SSC_CHSL_TIER1;
		case "SSC CHSL TIER-2":
			return PaperSubCategory.SSC_CHSL_TIER2;
		case "SSC CPO TIER-1":
			return PaperSubCategory.SSC_CPO_TIER1;
		case "SSC CPO TIER-2":
			return PaperSubCategory.SSC_CPO_TIER2;
		case "BANK PO PRE":
			return PaperSubCategory.BANK_PO_PRE;
		case "BANK PO MAIN":
			return PaperSubCategory.BANK_PO_MAIN;
		default:
			throw new IllegalArgumentException(" String Parameter is not valid");

		}
	}

	public static List<PaperSubCategory> getList() {
		List<PaperSubCategory> listOfSubCatogory = new LinkedList<>();
		listOfSubCatogory.add(SSC_CGL_TIER1);
		listOfSubCatogory.add(SSC_CGL_TIER2);
		listOfSubCatogory.add(SSC_CHSL_TIER1);
		listOfSubCatogory.add(SSC_CHSL_TIER2);
		listOfSubCatogory.add(SSC_CPO_TIER1);
		listOfSubCatogory.add(SSC_CPO_TIER2);
		listOfSubCatogory.add(BANK_PO_PRE);
		listOfSubCatogory.add(BANK_PO_MAIN);
		return listOfSubCatogory;
	}

	public static PaperCategory getParent(PaperSubCategory paperSubCategory) {
		switch(paperSubCategory) {
		case SSC_CGL_TIER1:
			return PaperCategory.SSC_CGL;
		case SSC_CGL_TIER2:
			return PaperCategory.SSC_CGL;
		case SSC_CHSL_TIER1:
			return PaperCategory.SSC_CHSL;
		case SSC_CHSL_TIER2:
			return PaperCategory.SSC_CHSL;
		case SSC_CPO_TIER1:
			return PaperCategory.SSC_CPO;
		case SSC_CPO_TIER2:
			return PaperCategory.SSC_CPO;
		case BANK_PO_PRE:
			return PaperCategory.BANK_PO;
		case BANK_PO_MAIN:
			return PaperCategory.BANK_PO;
 
		default :throw new IllegalArgumentException("paperSubCategory doesn't exist ");
			
		}
	}
	
	public abstract List<String> getPaperSubCategoryTypes();
}
