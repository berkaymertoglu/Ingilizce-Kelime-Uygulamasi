package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.mySQL.Util.VeritabaniUtil;
import javafx.scene.control.Hyperlink;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.SQLException;

public class kelimeEkleController {
	
	Connection baglanti = null;
	String sql;
	
	public kelimeEkleController() {
		baglanti = VeritabaniUtil.Baglan();
				
	}

    @FXML
    private Button btn_kelimeEkle;

    @FXML
    private Text txt_cumlesi;

    @FXML
    private TextField txt_cumlesiField;

    @FXML
    private Text txt_ingKelime;

    @FXML
    private TextField txt_ingKelimeField;

    @FXML
    private Text txt_kelimeEkle;

    @FXML
    private Text txt_resmi;

    @FXML
    private TextField txt_resmiField;

    @FXML
    private Text txt_turkceKarsiligi;

    @FXML
    private TextField txt_turkceKarsiligiField;
    
    @FXML
    private Text txt_label;
    
    @FXML
    private Hyperlink txt_dahafazlacumle;
    
    // Veritabanina daha fazla cumle eklemeye yarayan fonksiyon
    @FXML
    void txt_dahaFazlaCumleClick(ActionEvent event) {
    	try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("cumle_ekle.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage(); 
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Veritabanina kelime ekleyen fonksiyon
    @FXML
    void btn_kelimeEkleClick(ActionEvent event) {
        String ingilizceKelime = txt_ingKelimeField.getText();
        String turkceKarsiligi = txt_turkceKarsiligiField.getText();
        String cumleKullanimi = txt_cumlesiField.getText();
        String resimURL = txt_resmiField.getText();
        
        sql = "INSERT INTO kelimeler(ingilizce_kelime, turkce_karsiligi, cumle_kullanimi, resim_url) VALUES (?,?,?,?)";
        
        try {
            PreparedStatement statement = baglanti.prepareStatement(sql);
            statement.setString(1, ingilizceKelime);
            statement.setString(2, turkceKarsiligi);
            statement.setString(3, cumleKullanimi);
            statement.setString(4, resimURL);
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows > 0) {
            	txt_label.setText("Kelime başarıyla eklendi.");
            } else {
                System.err.println("Kelime eklenirken bir hata oluştu.");
            }
        } catch (SQLException e) {
            System.err.println("SQL hatası: " + e.getMessage());
        }
    }
}   


