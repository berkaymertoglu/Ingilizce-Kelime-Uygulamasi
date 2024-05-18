package application;

import javafx.event.ActionEvent;
import javafx.scene.Node;



import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

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
    
    // Uye ol hyperlink tiklandiginda yeni pencere ac
    @FXML
    void txt_uyeol_click(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("kayit2.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage(); // Yeni bir stage (pencere) olustur
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
    
    // Sifremi unuttum hyperlink tiklandigi zaman girilen mail adresine kullanici sifresini e-posta olarak gonder
    @FXML
    void txt_sifremiunuttum_click(ActionEvent event) {
    	TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("E-Posta Adresi");
        dialog.setHeaderText(null);
        dialog.setContentText("Lütfen e-posta adresinizi girin:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String to = result.get(); // Alicinin e-posta adresi
            String subject = "Şifre Hatırlatma"; // E-posta konusu

            // Sifreyi cozmek için veritabanindan sorgu yap
            sql = "SELECT sifre FROM kullanicilar WHERE eposta = ?";
            try {
                sorguIfadesi = baglanti.prepareStatement(sql);
                sorguIfadesi.setString(1, to);
                ResultSet resultSet = sorguIfadesi.executeQuery();

                if (resultSet.next()) {
                    String encryptedPassword = resultSet.getString("sifre"); // Veritabanindan sifreyi al
                    String decryptedPassword = SifrelemeUtils.decrypt(encryptedPassword); // Sifreyi coz
                    

                    String body = "Şifreniz: " + decryptedPassword; // E-posta icerigi

                    // Gonderen e-posta bilgileri
                    final String from = "testveritabani@gmail.com";
                    final String username = "testveritabani@gmail.com";
                    final String password = "mzad mpgn qqmv cfrv";

                    // SMTP sunucu ayarlari
                    String host = "smtp.gmail.com"; // SMTP sunucu adresi

                    Properties props = new Properties();
                    props.put("mail.smtp.auth", "true");
                    props.put("mail.smtp.starttls.enable", "true");
                    props.put("mail.smtp.host", host);
                    props.put("mail.smtp.port", "587");

                    // Oturum olusturma
                    Session session = Session.getInstance(props,
                            new javax.mail.Authenticator() {
                                protected PasswordAuthentication getPasswordAuthentication() {
                                    return new PasswordAuthentication(username, password);
                                }
                            });

                    try {
                        // E-posta olusturma
                        Message message = new MimeMessage(session);
                        message.setFrom(new InternetAddress(from));
                        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
                        message.setSubject(subject);
                        message.setText(body);

                        // E-postayi gonderme
                        Transport.send(message);

                        System.out.println("E-posta başarıyla gönderildi.");

                    } catch (MessagingException e) {
                        System.out.println("E-posta gönderilirken hata oluştu: " + e.getMessage());
                    }
                } else {
                    System.out.println("E-posta adresi bulunamadı.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }		
    	// Giris yapildiginda veritabanindan kullanici kontrolu yapan ve eger kullaniciysa sifresini sifreleyen fonksiyon
        @FXML
        void btn_giris_click(ActionEvent event) {
            String kullaniciAdi = txt_kullaniciadifield.getText().trim();
            String girilenSifre = txt_sifrefield.getText().trim();
            
            // Kullanicinin girdigi sifreyi sifrele
            String sifrelemeSifresi = SifrelemeUtils.encrypt(girilenSifre);

            sql = "select * from kullanicilar where kullanici_adi = ? and sifre = ?";
            try {
                sorguIfadesi = baglanti.prepareStatement(sql);
                sorguIfadesi.setString(1, kullaniciAdi);
                sorguIfadesi.setString(2, sifrelemeSifresi);
                
                ResultSet getirilen = sorguIfadesi.executeQuery();
                
                if (!getirilen.next()) {
                	Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Giriş Hatası");
                    alert.setHeaderText(null);
                    alert.setContentText("Kullanıcı adı veya şifre hatalı.");
                    alert.showAndWait(); 
                    // Kullanıcıya hata mesajı gösterildikten sonra return ile metodun burada sonlandırılması
                    return;
                } else {
                    getirilen.getString(1);
                    System.out.println("kullaniciID: " + getirilen.getInt("kullaniciID"));
                    System.out.println("kullanici: " + getirilen.getString("kullanici_adi"));
                    System.out.println("eposta: " + getirilen.getString("eposta"));
                    
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
                
                Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                currentStage.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
       
