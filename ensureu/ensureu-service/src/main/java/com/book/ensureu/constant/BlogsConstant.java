package com.book.ensureu.constant;

public class BlogsConstant {

	public enum Direction {
		ASCENDING("ASC"), DESCENDING("DESC");
		private final String directionCode;

		private Direction(String direction) {
			this.directionCode = direction;
		}

		public String getDirectionCode() {
			return this.directionCode;
		}
	}

	public enum SortBy {
		PRIORITY("priority"), CREATEDDATE("createdDate");
		private String sortByCode;

		private SortBy(String sortByCode) {
			this.sortByCode = sortByCode;
		}

		public String getSortByCode() {
			return sortByCode;
		}

	}

}
