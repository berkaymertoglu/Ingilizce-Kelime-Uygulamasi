package application;

import java.io.IOException;

import javafx.scene.layout.VBox;
import java.io.ObjectInputFilter.Status;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

import com.mySQL.Util.VeritabaniUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class sinavModuluController {
	
    
    List<CheckBox> checkboxes = new ArrayList<>();
    private int toplamSoruSayisi;
    private int soruSayaci = 1;
    private final Connection baglanti = VeritabaniUtil.Baglan();
    private String sql;
    
    
    public LocalDate stringToLocalDate(String tarih) {
        if (tarih == null) {
            return null;
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(tarih, formatter);
    }
      
    // MYSQL Değişkenler 
    private String dogruCevap = null;
    private int status = 0;
    private String upTarih = null;
    private int kelimeId = -1;
    
    

    @FXML
    private CheckBox btn_checkbox1;

    @FXML
    private CheckBox btn_checkbox2;

    @FXML
    private CheckBox btn_checkbox3;

    @FXML
    private CheckBox btn_checkbox4;
    
    @FXML
    private Button btn_sonraki;

    @FXML
    private Text txt_kacSoru;

    @FXML
    private Text txt_sinavKelimesi;
    
    
  
    
    @FXML
    private Hyperlink txt_analizRaporu;
    
    @FXML
    private Text txt_soruCumlesi;
    
    @FXML
    private Text txt_dogruCevap;
    
    @FXML
    private Text txt_cumle1;

    @FXML
    private Text txt_cumle2;
    
    @FXML
    private ImageView imageView;
    
    @FXML
    private Text lbl_sonuc;
    
    @FXML
    void btn_analizRaporu(ActionEvent event) {
        try {
            // FXML dosyasını yükle
            Parent root = FXMLLoader.load(getClass().getResource("analiz_raporu.fxml"));
            Scene scene = new Scene(root);

            // Stage'i ayarla ve göster
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
            // Hata durumunda bir işlem yapılabilir, örneğin hata mesajı gösterilebilir
        }
    }
    
  
    public void setToplamSoruSayisi(int toplamSoruSayisi) {
    	this.toplamSoruSayisi += toplamSoruSayisi;
    }

    @FXML
    void initialize() {
        yenisoruGoster();
        txt_dogruCevap.setText("");
     // Checkbox'lar için tek bir event listener tanımlayın
        for (CheckBox checkbox : checkboxes) {
            checkbox.setOnAction(event -> {
                if (checkbox.isSelected()) {
                    kontrolEtDogruCevap();
                }
            });
        }
    }

    @FXML
    void btn_sonraki_click(ActionEvent event) {
        if (soruSayaci < toplamSoruSayisi) {
            soruSayaci++;
            yenisoruGoster();

            if (kullaniciHerhangiBirSecimYaptiMi()) {
                kontrolEtDogruCevap();
            }
        } else {
            // Hyperlink'i görünür hale getir
            txt_analizRaporu.setVisible(true);

            // lbl_sonuc metnini güncelle
            lbl_sonuc.setText("Quiz tamamlandı. Tebrikler!");
        }
    }
    private void yenisoruGoster() {
        String resimURL = kelimeyiCek();
        checkboxlaraYerlestir();
        resmiGoster(resimURL);
        btn_checkbox1.setSelected(false);
        btn_checkbox2.setSelected(false);
        btn_checkbox3.setSelected(false);
        btn_checkbox4.setSelected(false);
        txt_dogruCevap.setText("");
    }

    
 // oncekiKelimeID değişkenini sınıfın alanı olarak tanımlayın
    private int oncekiKelimeID = 0;
    // Tüm kayıtların sayısını saklayacak değişken
    private int toplamKayitSayisi = 0;
    // Gösterilen kayıtların sayısını saklayacak değişken
    private int gosterilenKayitSayisi = 0;

    private void kayitSayisiniHesapla() {
        // Toplam kayıt sayısını almak için bir sorgu çalıştırın
        String sql = "SELECT COUNT(*) AS toplamKayitSayisi FROM bilinenler WHERE status >= 1";
        try (PreparedStatement statement = baglanti.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                toplamKayitSayisi = resultSet.getInt("toplamKayitSayisi");
                if (toplamKayitSayisi > 1) { // Başlangıç değeri negatif değilse
                    setToplamSoruSayisi(toplamKayitSayisi);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    List<Integer> secilenKelimeIDleri = new ArrayList<>();

    private String kelimeyiCek() {
        String resimURL = null;
        String cumle;

        if (toplamKayitSayisi == 0) {
            kayitSayisiniHesapla();
        }

        String sql;
        if (gosterilenKayitSayisi >= toplamKayitSayisi) {
            // Tüm kayıtlar gösterildiyse kelimeler tablosundan yeni kayıtları alın
            sql = "SELECT ingilizce_kelime, turkce_karsiligi, resim_url, kelimeID, status, tarih FROM kelimeler WHERE status = 1 ORDER BY RAND() LIMIT 1";
        } else {
            // Bilinenler tablosundan bir kelime seç
            sql = "SELECT ingilizce_kelime, turkce_karsiligi, resim_url, kelimeID, status, tarih FROM bilinenler WHERE status >= 1 AND kelimeID NOT IN (?) ORDER BY RAND() LIMIT 1";
        }

        try (PreparedStatement statement = baglanti.prepareStatement(sql)) {
            if (gosterilenKayitSayisi < toplamKayitSayisi) {
                // Daha önce seçilen kelimeID'leri hariç tut
                StringJoiner joiner = new StringJoiner(",");
                for (Integer kelimeID : secilenKelimeIDleri) {
                    joiner.add(String.valueOf(kelimeID));
                }
                String kelimeIDler = joiner.toString();
                statement.setString(1, kelimeIDler);
            }

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String kelime = resultSet.getString("turkce_karsiligi");
                dogruCevap = resultSet.getString("ingilizce_kelime");
                resimURL = resultSet.getString("resim_url");
                kelimeId = resultSet.getInt("kelimeID");
                status = resultSet.getInt("status");
                upTarih = resultSet.getString("tarih");;
                txt_sinavKelimesi.setText(kelime);
                // txt_statusID.setText(String.valueOf(status));
                // txt_upTarihId.setText(upTarih);

                // Önceki kelime ID'sini güncelle
                secilenKelimeIDleri.add(kelimeId);
              
                // Gösterilen kayıtların sayısını arttır
                gosterilenKayitSayisi++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resimURL;
    }

    
    private void resmiGoster(String resimURL) {
        Image image = new Image(resimURL);
        imageView.setImage(image);
    }

    private void checkboxlaraYerlestir() {
        checkboxes.add(btn_checkbox1);
        checkboxes.add(btn_checkbox2);
        checkboxes.add(btn_checkbox3);
        checkboxes.add(btn_checkbox4);

        // Doğru cevabı dışarıda tutarak rastgele 3 seçenek getir
        String sorgu = "SELECT DISTINCT ingilizce_kelime FROM kelimeler WHERE ingilizce_kelime != ? ORDER BY RAND() LIMIT 3";
        try (PreparedStatement statement = baglanti.prepareStatement(sorgu)) {
            statement.setString(1, dogruCevap);
            ResultSet resultSet = statement.executeQuery();
            
            ArrayList<String> secenekler = new ArrayList<>();
            while (resultSet.next()) {
                String dil = resultSet.getString("ingilizce_kelime");
                secenekler.add(dil);
            }
            resultSet.close();
            
            // Doğru cevapı ve diğer seçenekleri karıştır
            secenekler.add(dogruCevap);
            Collections.shuffle(secenekler);
            
            // Seçenekleri checkboxlara yerleştir
            for (int i = 0; i < checkboxes.size() && i < secenekler.size(); i++) {
                checkboxes.get(i).setText(secenekler.get(i));
                checkboxes.get(i).setVisible(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void kontrolEtDogruCevap() {   
        LocalDate simdikiTarih = LocalDate.now();
        LocalDate sonCevapTarihi = stringToLocalDate(upTarih);
        // String testString = txt_upTarihId.getText();
        long fark = -1;
        
        if (sonCevapTarihi != null) {
            fark = ChronoUnit.DAYS.between(sonCevapTarihi, simdikiTarih);
        }
       
         //txt_upTarihId.setText(testString + "-" + simdikiTarih.toString() + "-" + sonCevapTarihi + " - " + String.valueOf(kelimeId) + " : " + fark);

        if (dogruCevap != null && kullaniciCevabiDogruMu(dogruCevap)) {
            txt_dogruCevap.setText("Doğru cevap! Tebrikler!");
            ekleBilinenlerTablosu(); // Bilinenler tablosuna ekle         
            guncelleAnalizTablosu(true);
            
        } else {
            txt_dogruCevap.setText("Yanlış cevap! Doğru cevap: " + dogruCevap);
            cikarBilinenlerTablosu();
            guncelleAnalizTablosu(false);
            // Yanlış cevap durumunda status değerini 1'e düşür
            if (status != 1) { // Eğer status zaten 1 ise tekrar düşürme
                statusSıfırla();
            }
            return;
        }
        
        if (status == 1 && sonCevapTarihi == null) {
            // Eğer status 1 ve tarih null ise, yeni bir tarih al ve veritabanını güncelle
            try {
                sql = "UPDATE kelimeler SET status = 2, tarih = CURDATE() WHERE kelimeID = ?";
                PreparedStatement updateStatement = baglanti.prepareStatement(sql);                           
                updateStatement.setInt(1, kelimeId);
                updateStatement.executeUpdate();             
            } catch (SQLException e) {
                e.printStackTrace();
            }               
        } else if (status == 2 && sonCevapTarihi != null && fark == 1)  {
            try {
                sql = "UPDATE kelimeler SET status = 3 WHERE kelimeID = ?";
                PreparedStatement updateStatement = baglanti.prepareStatement(sql);
                updateStatement.setInt(1, kelimeId);
                updateStatement.executeUpdate();
                return;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (status == 3 && sonCevapTarihi != null && fark == 7) {
            // Eğer status 3 ve tarih null değilse, bir hafta geçmiş mi kontrol et
            try {
                sql = "UPDATE kelimeler SET status = 4 WHERE kelimeID = ?";
                PreparedStatement updateStatement = baglanti.prepareStatement(sql);
                updateStatement.setInt(1, kelimeId);
                updateStatement.executeUpdate();
                return;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (status == 4 && sonCevapTarihi != null && fark == 30) {
            // Eğer status 4 ve tarih null değilse, bir ay geçmiş mi kontrol et
            try {
                sql = "UPDATE kelimeler SET status = 5 WHERE kelimeID = ?";
                PreparedStatement updateStatement = baglanti.prepareStatement(sql);
                updateStatement.setInt(1, kelimeId);
                updateStatement.executeUpdate();
                return;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (status == 5 && sonCevapTarihi != null && fark == 90) {
            // Eğer status 5 ve tarih null değilse, 3 ay geçmiş mi kontrol et
            try {
                sql = "UPDATE kelimeler SET status = 6 WHERE kelimeID = ?";
                PreparedStatement updateStatement = baglanti.prepareStatement(sql);
                updateStatement.setInt(1, kelimeId);
                updateStatement.executeUpdate();
                return;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (status == 6 && sonCevapTarihi != null && fark == 180) {
            // Eğer status 6 ve tarih null değilse, 6 ay geçmiş mi kontrol et
            try {
                sql = "UPDATE kelimeler SET status = 7 WHERE kelimeID = ?";
                PreparedStatement updateStatement = baglanti.prepareStatement(sql);
                updateStatement.setInt(1, kelimeId);
                updateStatement.executeUpdate();
                return;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (status == 7 && sonCevapTarihi != null && fark == 365) {
            // Eğer status 7 ve tarih null değilse, 1 yıl geçmiş mi kontrol et
            try {
                sql = "UPDATE kelimeler SET status = 8 WHERE kelimeID = ?";
                PreparedStatement updateStatement = baglanti.prepareStatement(sql);
                updateStatement.setInt(1, kelimeId);
                updateStatement.executeUpdate();
                return;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            // Belirlenen süreler geçmemişse uygun mesajı göster
            txt_dogruCevap.setText("Doğru cevap! Ancak belirli bir süre geçmeden tekrar cevapladınız.");
        }
    }
 
    private boolean kullaniciCevabiDogruMu(String dogruCevap) {
        // Kullanıcının seçtiği cevabı al
        checkboxes.add(btn_checkbox1);
        checkboxes.add(btn_checkbox2);
        checkboxes.add(btn_checkbox3);
        checkboxes.add(btn_checkbox4);
        String kullaniciCevabi = "";
        for (CheckBox checkbox : checkboxes) {
            if (checkbox.isSelected()) {
                kullaniciCevabi = checkbox.getText();
                break;
            }
        }
        // Doğru cevap ile kullanıcının cevabını karşılaştır
        return dogruCevap.equals(kullaniciCevabi);
    }
    
    private boolean kullaniciHerhangiBirSecimYaptiMi() {
        // Herhangi bir checkbox'un seçili olup olmadığını kontrol et
        return btn_checkbox1.isSelected() || btn_checkbox2.isSelected() || btn_checkbox3.isSelected() || btn_checkbox4.isSelected();
    }
       
    private void ekleBilinenlerTablosu() {
        String kontrolQuery = "SELECT kelimeID FROM bilinenler WHERE kelimeID = ?";
        try {
            PreparedStatement kontrolStatement = baglanti.prepareStatement(kontrolQuery);
            kontrolStatement.setInt(1, kelimeId);
            ResultSet resultSet = kontrolStatement.executeQuery();
            if (!resultSet.next()) { // Eğer kelimeId veritabanında yoksa ekle
                String ekleQuery = "INSERT INTO bilinenler (kelimeID, turkce_karsiligi, ingilizce_kelime, resim_url, status, tarih) " +
                                   "VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement ekleStatement = baglanti.prepareStatement(ekleQuery);
                ekleStatement.setInt(1, kelimeId);
                ekleStatement.setString(2, txt_sinavKelimesi.getText());
                ekleStatement.setString(3, dogruCevap);
                
                // Resim URL'sini kelimeler tablosundan al
                String resimUrlQuery = "SELECT resim_url, status FROM kelimeler WHERE kelimeID = ?";
                PreparedStatement resimUrlStatement = baglanti.prepareStatement(resimUrlQuery);
                resimUrlStatement.setInt(1, kelimeId);
                ResultSet resimUrlResultSet = resimUrlStatement.executeQuery();
                if (resimUrlResultSet.next()) {
                    String resimUrl = resimUrlResultSet.getString("resim_url");
                    int status = resimUrlResultSet.getInt("status");
                    ekleStatement.setString(4, resimUrl);
                    ekleStatement.setInt(5, status);
                    ekleStatement.setDate(6, java.sql.Date.valueOf(LocalDate.now())); 
                    ekleStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
  
    private void cikarBilinenlerTablosu() {
        String silQuery = "DELETE FROM bilinenler WHERE kelimeID = ?";
        try {
            PreparedStatement silStatement = baglanti.prepareStatement(silQuery);
            silStatement.setInt(1, kelimeId);
            silStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void statusSıfırla() {
        try {
            // Status değerini 1'e düşür
            sql = "UPDATE kelimeler SET status = 1, tarih = NULL WHERE kelimeID = ?";
            PreparedStatement updateStatement = baglanti.prepareStatement(sql);
            updateStatement.setInt(1, kelimeId);
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void guncelleAnalizTablosu(boolean dogruCevap) {
        String sql = "SELECT * FROM analiz WHERE kelimeID = ?";
        try {
            PreparedStatement statement = baglanti.prepareStatement(sql);
            statement.setInt(1, kelimeId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int dogruCevapSayisi = resultSet.getInt("dogru_cevap_sayisi");
                int yanlisCevapSayisi = resultSet.getInt("yanlis_cevap_sayisi");

                if (dogruCevap) {
                    dogruCevapSayisi++;
                } else {
                    yanlisCevapSayisi++;
                }

                // Analiz tablosunu güncelle
                sql = "UPDATE analiz SET dogru_cevap_sayisi = ?, yanlis_cevap_sayisi = ? WHERE kelimeID = ?";
                PreparedStatement updateStatement = baglanti.prepareStatement(sql);
                updateStatement.setInt(1, dogruCevapSayisi);
                updateStatement.setInt(2, yanlisCevapSayisi);
                updateStatement.setInt(3, kelimeId);
                updateStatement.executeUpdate();
            } else {
                // Analiz tablosunda kayıt yoksa yeni bir kayıt ekle
                if (dogruCevap) {
                    sql = "INSERT INTO analiz (kelimeID, ingilizce_kelime, dogru_cevap_sayisi, yanlis_cevap_sayisi) VALUES (?, ?, 1, 0)";
                } else {
                    sql = "INSERT INTO analiz (kelimeID, ingilizce_kelime, dogru_cevap_sayisi, yanlis_cevap_sayisi) VALUES (?, ?, 0, 1)";
                }
                PreparedStatement insertStatement = baglanti.prepareStatement(sql);
                insertStatement.setInt(1, kelimeId);
                insertStatement.setString(2, txt_sinavKelimesi.getText());
                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
      
   
}