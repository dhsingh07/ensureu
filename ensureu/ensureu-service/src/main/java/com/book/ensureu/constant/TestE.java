package com.book.ensureu.constant;

import java.util.Random;

public class TestE {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
/*String s="SSC CGL";
PaperCategory pc=PaperCategory.valueOf(s);
System.out.println(pc);*/
		//char[] atp=OTP(4);
		String atp=OTP(4);
		System.out.println(atp);
	}

	
	static String OTP(int len) 
    { 
        System.out.println("Generating OTP using random() : "); 
        System.out.print("You OTP is : "); 
  
        // Using numeric values 
        String numbers = "0123456789"; 
  
        // Using random method 
        Random rndm_method = new Random(); 
  
        char[] otp = new char[len]; 
  
        for (int i = 0; i < len; i++) 
        { 
            // Use of charAt() method : to get character value 
            // Use of nextInt() as it is scanning the value as int 
            otp[i] = 
             numbers.charAt(rndm_method.nextInt(numbers.length())); 
        } 
        return  String.valueOf(otp);
 
    }
	
}
