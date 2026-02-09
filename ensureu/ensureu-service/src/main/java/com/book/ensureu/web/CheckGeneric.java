package com.book.ensureu.web;

public class CheckGeneric {

	
	
	public static  <T,V> T check(T a,V b) {
		System.out.println(b);
		return a;
	}
	
	public static void main(String ... args) {
		System.out.println(check("manish",String.class));;
	}
	
}
