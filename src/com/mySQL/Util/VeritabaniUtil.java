package com.mySQL.Util;

import java.sql.*;

public class VeritabaniUtil {
	static Connection conn = null; 
	
	public static Connection Baglan() {
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/kelime_uygulamasi?serverTimezone=Europe/Istanbul", "root", "mysql");
			return conn;
		} catch (Exception e) {
			System.out.println(e.getMessage().toString());
			return null;	
		}
	}
}
