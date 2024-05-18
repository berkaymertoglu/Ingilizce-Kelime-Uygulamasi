package application;

import javafx.event.ActionEvent;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import java.sql.*;
import java.util.Optional;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import java.io.IOException;

import com.mySQL.Util.VeritabaniUtil;
import com.myUtils.SifrelemeUtils;

public class girisController {
	Connection baglanti = null;
	PreparedStatement sorguIfadesi = null;
	ResultSet getirilen = null;
	String sql;
	
	public girisController() {
		baglanti = VeritabaniUtil.Baglan();
				
	}

    @FXML
    private Text lbl_sonuc;

    @FXML
    private AnchorPane sample;

    @FXML
    private Button txt_girisbuton;

    @FXML
    private Text txt_kullaniciadi;

    @FXML
    private TextField txt_kullaniciadifield;

    @FXML
    private Text txt_sifre;

    @FXML
    private PasswordField txt_sifrefield;

    @FXML
    private Hyperlink txt_sifremiunuttum;

    @FXML
    private Text txt_uyedegilmisin;

    @FXML
    private Hyperlink txt_uyeol;
    
    @FXML
    void txt_uyeol_click(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("kayit2.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage(); // Yeni bir stage (pencere) oluşturun
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void initialize() {
        txt_uyeol.setOnAction(event -> txt_uyeol_click(event));
    }
    
    @FXML
    void txt_sifremiunuttum_click(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Şifremi Unuttum");
        dialog.setHeaderText("Lütfen e-posta adresinizi girin:");
        dialog.setContentText("E-posta:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(email -> {
            // E-posta girildiğinde yapılacak işlemler
            try {         
                String sorgu = "SELECT sifre FROM kullanicilar WHERE eposta = ?";
                PreparedStatement preparedStatement = baglanti.prepareStatement(sorgu);
                preparedStatement.setString(1, email);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    String sifre = resultSet.getString("sifre");
                    // E-posta adresine şifreyi gönder
                    // Bu adımda e-posta gönderme işlemi yapılmalı
                    // Örneğin: bir e-posta gönderme kütüphanesi kullanılabilir
                    // Şu anda bir e-posta gönderme kütüphanesi veya kodu yer almamaktadır.
                    // Sadece şifreyi konsola yazdıralım
                    System.out.println("E-posta adresinize şifreniz gönderildi: " + sifre);
                } else {
                    // E-postaya sahip kullanıcı bulunamadı
                    System.out.println("Bu e-posta adresine sahip bir kullanıcı bulunamadı.");
                }

                baglanti.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }      
    
        @FXML
        void btn_giris_click(ActionEvent event) {
            String kullaniciAdi = txt_kullaniciadifield.getText().trim();
            String girilenSifre = txt_sifrefield.getText().trim();
            
            // Kullanıcının girdiği şifreyi şifreleyin
            String sifrelemeSifresi = SifrelemeUtils.encrypt(girilenSifre);

            sql = "select * from kullanicilar where kullanici_adi = ? and sifre = ?";
            try {
                sorguIfadesi = baglanti.prepareStatement(sql);
                sorguIfadesi.setString(1, kullaniciAdi);
                sorguIfadesi.setString(2, sifrelemeSifresi);
                
                ResultSet getirilen = sorguIfadesi.executeQuery();
                
                if (!getirilen.next()) {
                    lbl_sonuc.setText("Kullanıcı adı veya şifre hatalı."); 
                    // Kullanıcıya hata mesajı gösterildikten sonra return ile metodun burada sonlandırılması
                    return;
                } else {
                    getirilen.getString(1);
                    System.out.println("kullaniciID: " + getirilen.getInt("kullaniciID"));
                    System.out.println("kullanici: " + getirilen.getString("kullanici_adi"));
                    System.out.println("eposta: " + getirilen.getString("eposta"));
                    System.out.println("şifre: " + getirilen.getString("sifre"));
                }
                
            } catch (Exception e) {
                lbl_sonuc.setText(e.getMessage().toString());
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("kullanici.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage(); // Yeni bir stage (pencere) oluşturun
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
       
