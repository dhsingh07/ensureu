package com.book.ensureu.notification.constant;

public enum EventType {
	
	PROFILE,
	CREATE,
	VERIFICATION,
	UPDATE,
	
	DELETE{
		public boolean isDelete(){
			return true;
		}
	},
	PAPERREMINDER,
	PAPEROFFER,
	INVITE,
	OTHER;
	
	public boolean isDelete() {
		return false;
	}
}
