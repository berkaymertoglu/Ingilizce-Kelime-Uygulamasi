package application;

import com.mySQL.Util.VeritabaniUtil;
import com.myUtils.SifrelemeUtils;


import javafx.scene.control.PasswordField;
import javafx.event.ActionEvent;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

public class KayitController {
	private static final String SECRET_KEY = "1234567890123456";

    @FXML
    private Button btn_uyeolma;

    @FXML
    private Text lbl_sonuc;

    @FXML
    private TextField txt_kayitemailfield;

    @FXML
    private TextField txt_kayitkullaniciadifield;

    @FXML
    private PasswordField txt_kayitsifrefield;

    @FXML
    void btn_uyeolma_click(ActionEvent event) {
        String kullaniciAdi = txt_kayitkullaniciadifield.getText().trim();
        String girilenSifre = txt_kayitsifrefield.getText().trim();
        String email = txt_kayitemailfield.getText().trim();

        // Kullanıcının girdiği şifreyi şifreleyin
        String sifrelemeSifresi = SifrelemeUtils.encrypt(girilenSifre);

        String eklemeSorgusu = "INSERT INTO kullanicilar (kullanici_adi, sifre, eposta) VALUES (?, ?, ?)";
        String kontrolKullaniciAdiSorgusu = "SELECT * FROM kullanicilar WHERE kullanici_adi = ?";
        String kontrolEmailSorgusu = "SELECT * FROM kullanicilar WHERE eposta = ?";

        try {
            Connection baglanti = VeritabaniUtil.Baglan();

            // Kullanıcı adı kontrolü
            PreparedStatement kontrolKullaniciAdiIfadesi = baglanti.prepareStatement(kontrolKullaniciAdiSorgusu);
            kontrolKullaniciAdiIfadesi.setString(1, kullaniciAdi);
            ResultSet kullaniciAdiSonuc = kontrolKullaniciAdiIfadesi.executeQuery();

            if (kullaniciAdiSonuc.next()) {
                lbl_sonuc.setText("Bu kullanıcı adı zaten kullanılmış.");
                return;
            }

            // E-posta kontrolü
            PreparedStatement kontrolEmailIfadesi = baglanti.prepareStatement(kontrolEmailSorgusu);
            kontrolEmailIfadesi.setString(1, email);
            ResultSet emailSonuc = kontrolEmailIfadesi.executeQuery();

            if (emailSonuc.next()) {
                lbl_sonuc.setText("Bu e-posta adresi zaten kullanılmış.");
                return;
            }

            // Üyelik ekleme
            PreparedStatement eklemeIfadesi = baglanti.prepareStatement(eklemeSorgusu);
            eklemeIfadesi.setString(1, kullaniciAdi);
            eklemeIfadesi.setString(2, sifrelemeSifresi); // Şifrelenmiş şifreyi ekleyin
            eklemeIfadesi.setString(3, email);

            int etkilenenSatirSayisi = eklemeIfadesi.executeUpdate();

            if (etkilenenSatirSayisi > 0) {
                lbl_sonuc.setText("Üyelik başarıyla oluşturuldu.");
            } else {
                lbl_sonuc.setText("Üyelik oluşturulamadı.");
            }
        } catch (SQLException e) {
            lbl_sonuc.setText("Üyelik oluşturulurken bir hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
