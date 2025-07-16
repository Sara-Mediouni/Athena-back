package com.example.demo.Entite;

public class RefreshToken {
	
	
	
	private String Token;
    private long expirationDate;
    
    
    
	public RefreshToken(String token, long expirationDate) {
		super();
		Token = token;
		this.expirationDate = expirationDate;
	}



	public RefreshToken() {
		super();
	}



	public String getToken() {
		return Token;
	}



	public void setToken(String token) {
		Token = token;
	}



	public long getExpirationDate() {
		return expirationDate;
	}



	public void setExpirationDate(long expirationDate) {
		this.expirationDate = expirationDate;
	}
	
	
	
    
    
    
    

}
