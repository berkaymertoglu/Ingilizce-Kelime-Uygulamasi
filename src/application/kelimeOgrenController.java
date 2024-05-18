package application;

import com.mySQL.Util.VeritabaniUtil;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class kelimeOgrenController {
	
	private final Connection baglanti = VeritabaniUtil.Baglan();
	
	@FXML
	private Text txt_ingilizceKelime;

	@FXML
	private Text txt_turkceKelime;
	
	@FXML
    private Text txt_cumleİcindeKullanimi;

    @FXML
    private Text txt_cumleler;
    
    @FXML
    private Text txt_cumleler2;
    
    @FXML
    private Button btn_sonraki;
	 
	@FXML
	private ImageView imageView;
	
	public void initialize() {
		kelimeCek();
	}
	
	// Veritabanindan kelimeyi cek
	public void kelimeCek() {
	    String resimURL = null;
	    String ingilizceKelime = null;
	    String turkceKarsiligi = null;
	    String kelimeCumlesi = null;
	    int kelimeId = 0;
	    	  
	    String sql = """
	    	    SELECT kelimeID, ingilizce_kelime, turkce_karsiligi, resim_url, cumle_kullanimi
	    	    FROM kelimeler
	    	    WHERE status >= 1
	    	    ORDER BY RAND() LIMIT 1
	    	    """;
	     
	    try (PreparedStatement statement = baglanti.prepareStatement(sql)) {
	        ResultSet resultSet = statement.executeQuery();

	        if (resultSet.next()) {
	            kelimeId = resultSet.getInt("kelimeID");
	            ingilizceKelime = resultSet.getString("ingilizce_kelime");
	            turkceKarsiligi = resultSet.getString("turkce_karsiligi");
	            resimURL = resultSet.getString("resim_url");
	            kelimeCumlesi = resultSet.getString("cumle_kullanimi");

	            // FXML ekranina bilgileri yazdir
	            txt_ingilizceKelime.setText("İngilizce: " + ingilizceKelime);
	            txt_turkceKelime.setText("Türkçe: " + turkceKarsiligi);
	            txt_cumleler.setText(kelimeCumlesi);
	            // Resmi goster
	            if (resimURL != null) {
	                Image image = new Image(resimURL);
	                imageView.setImage(image);
	            }
	            
	            cumleleriGoster(kelimeId);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	// Veritabanindan cumleleri cek
	private void cumleleriGoster(int kelimeId) {
	    String cumleSql = "SELECT cumle FROM cumleler WHERE kelimeID = ?";
	    StringBuilder cumlelerText = new StringBuilder(); // Tum cumleleri birlestir

	    try (PreparedStatement cumleStatement = baglanti.prepareStatement(cumleSql)) {
	        cumleStatement.setInt(1, kelimeId);
	        try (ResultSet cumleResultSet = cumleStatement.executeQuery()) {
	            while (cumleResultSet.next()) {
	                String c = cumleResultSet.getString("cumle");
	                cumlelerText.append(c).append("\n"); 
	            }
	        }
	        
	        	txt_cumleler2.setText(cumlelerText.toString());
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	// Sonraki butonuna basildiginda kelime cek
	@FXML
	void btn_sonrakiClick(ActionEvent event) {
		
		kelimeCek();    
	}
}