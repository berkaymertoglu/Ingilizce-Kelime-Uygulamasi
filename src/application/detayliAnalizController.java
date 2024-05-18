package application;

import com.mySQL.Util.VeritabaniUtil;



import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.layout.AnchorPane;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class detayliAnalizController {
	
	Connection baglanti = VeritabaniUtil.Baglan();

	@FXML
    private Text txt_bilinmeYuzdesi;
	
	@FXML
    private AnchorPane anchorPane;

    @FXML
    private Text txt_bilinmeYuzdesiDeger;

    @FXML
    private Text txt_dogruCevap;

    @FXML
    private Text txt_dogruCevapDeger;

    @FXML
    private Text txt_kelime;

    @FXML
    private Text txt_kelimeDeger;

    @FXML
    private Text txt_status;

    @FXML
    private Text txt_statusDeger;

    @FXML
    private Text txt_yanlisCevap;

    @FXML
    private Text txt_yanlisCevapDeger;
    
    @FXML
    private Button btn_ciktiAl;
    
    
    
    public void initialize(){
    	ingilizceKelimeleriCek();   	
    }
    
    
    public void ingilizceKelimeleriCek() {
        Connection baglanti = VeritabaniUtil.Baglan();
        if (baglanti != null) {
            try {
                String sql = "SELECT analiz.ingilizce_kelime, analiz.dogru_cevap_sayisi, analiz.yanlis_cevap_sayisi, analiz.bilinme_yuzdesi, bilinenler.status " +
                             "FROM analiz " +
                             "JOIN bilinenler ON analiz.kelimeID = bilinenler.kelimeID"; // İlgili tabloları birleştirerek status değerini alıyoruz
                PreparedStatement preparedStatement = baglanti.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery();
                    
                // Tüm ingilizce kelimelerini ve diğer sütunları alt alta yazdırma
                while (resultSet.next()) {
                    String ingilizceKelime = resultSet.getString("ingilizce_kelime");
                    int dogruCevapSayisi = resultSet.getInt("dogru_cevap_sayisi");
                    int yanlisCevapSayisi = resultSet.getInt("yanlis_cevap_sayisi");
                    double bilinmeYuzdesi = resultSet.getDouble("bilinme_yuzdesi");
                    String status = resultSet.getString("status");

                    // Verileri istediğiniz metin alanlarına yazdırın
                    txt_kelimeDeger.setText(txt_kelimeDeger.getText() + ingilizceKelime + "\n");
                    txt_dogruCevapDeger.setText(txt_dogruCevapDeger.getText() + String.valueOf(dogruCevapSayisi) + "\n");
                    txt_yanlisCevapDeger.setText(txt_yanlisCevapDeger.getText() + String.valueOf(yanlisCevapSayisi) + "\n");
                    txt_bilinmeYuzdesiDeger.setText(txt_bilinmeYuzdesiDeger.getText() + String.valueOf(bilinmeYuzdesi) + "\n");
                    txt_statusDeger.setText(txt_statusDeger.getText() + status + "\n");
                }

                // PreparedStatement, ResultSet ve bağlantıyı kapat
                preparedStatement.close();
                baglanti.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Veritabanı bağlantısı başarısız.");
        }
    }
}