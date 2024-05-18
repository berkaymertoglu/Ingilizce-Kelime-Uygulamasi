package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mySQL.Util.VeritabaniUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class cumleEkleController {
	
	Connection baglanti = null;
	String sql;
	
	public cumleEkleController() {
		baglanti = VeritabaniUtil.Baglan();
				
	}
    @FXML
    private Button btn_cumleEkle;

    @FXML
    private Text lbl_sonuc;

    @FXML
    private Text txt_cumleEkle;

    @FXML
    private Text txt_cumleGir;

    @FXML
    private TextField txt_cumleGirField;

    @FXML
    private Text txt_kelimeID;

    @FXML
    private TextField txt_kelimeIDField;

    @FXML
    void btn_cumleEkleClick(ActionEvent event) {
        try {
            // Kullanıcının girdiği kelimeyi al
            String kelime = txt_kelimeIDField.getText();
            String cumle = txt_cumleGirField.getText();
            
            // Kelimeyi veritabanından ara ve ID'sini al
            int kelimeID = getKelimeID(kelime);
            
            // Eğer kelime bulunamazsa
            if (kelimeID == -1) {
                lbl_sonuc.setText("Kelime bulunamadı.");
                return; // Metottan çık
            }
            
            // SQL sorgusu oluştur
            String sql = "INSERT INTO cumleler (kelimeID, cumle) VALUES (?, ?)";
            
            // PreparedStatement oluştur
            PreparedStatement preparedStatement = baglanti.prepareStatement(sql);
            
            // Değişkenleri preparedStatement'e ekle
            preparedStatement.setInt(1, kelimeID);
            preparedStatement.setString(2, cumle);
            
            // Sorguyu çalıştır ve etkilenen satır sayısını kontrol et
            int etkilenenSatir = preparedStatement.executeUpdate();
            
            if (etkilenenSatir > 0) {
                lbl_sonuc.setText("Cümle başarıyla eklendi.");
            } else {
                lbl_sonuc.setText("Cümle eklenirken bir hata oluştu.");
            }
            
            // PreparedStatement'i kapat
            preparedStatement.close();
            
        } catch (SQLException e) {
            System.out.println("SQL Hatası: " + e.getMessage());
            lbl_sonuc.setText("Cümle eklenirken bir hata oluştu.");
        }
    }
    
    private int getKelimeID(String kelime) throws SQLException {
        String sql = "SELECT kelimeID FROM kelimeler WHERE ingilizce_kelime = ?";
        PreparedStatement preparedStatement = baglanti.prepareStatement(sql);
        preparedStatement.setString(1, kelime);
        ResultSet resultSet = preparedStatement.executeQuery();
        
        if (resultSet.next()) {
            int kelimeID = resultSet.getInt("kelimeID");
            preparedStatement.close();
            return kelimeID;
        } else {
            preparedStatement.close();
            return -1; // Kelime bulunamadı
        }
    }
}
