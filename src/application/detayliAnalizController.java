package application;

import com.mySQL.Util.VeritabaniUtil;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.layout.AnchorPane;
import javafx.print.PageLayout;
import javafx.geometry.Bounds;
import javafx.print.Printer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.print.PrinterJob;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;

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
    
    // Cikti alma fonksiyonu
    @FXML
    void btn_ciktiAlClick(ActionEvent event) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(anchorPane.getScene().getWindow())) {
            PageLayout pageLayout = job.getPrinter().createPageLayout(javafx.print.Paper.A4, javafx.print.PageOrientation.PORTRAIT, Printer.MarginType.HARDWARE_MINIMUM);
            double scaleX = pageLayout.getPrintableWidth() / 685.0; // Belirtilen genislik
            double scaleY = pageLayout.getPrintableHeight() / 383.0; // Belirtilen yukseklik
            double scale = Math.min(scaleX, scaleY);

            anchorPane.setScaleX(scale);
            anchorPane.setScaleY(scale);

            boolean success = job.printPage(pageLayout, anchorPane);
            if (success) {
                job.endJob();
            }

            // Olceklendirmeyi geri al
            anchorPane.setScaleX(1.0);
            anchorPane.setScaleY(1.0);
        }
    }
    
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
                    
                // Tum ingilizce kelimelerini ve diger sutunlari alt alta yazdirma
                while (resultSet.next()) {
                    String ingilizceKelime = resultSet.getString("ingilizce_kelime");
                    int dogruCevapSayisi = resultSet.getInt("dogru_cevap_sayisi");
                    int yanlisCevapSayisi = resultSet.getInt("yanlis_cevap_sayisi");
                    double bilinmeYuzdesi = resultSet.getDouble("bilinme_yuzdesi");
                    String status = resultSet.getString("status");

                    
                    txt_kelimeDeger.setText(txt_kelimeDeger.getText() + ingilizceKelime + "\n");
                    txt_dogruCevapDeger.setText(txt_dogruCevapDeger.getText() + String.valueOf(dogruCevapSayisi) + "\n");
                    txt_yanlisCevapDeger.setText(txt_yanlisCevapDeger.getText() + String.valueOf(yanlisCevapSayisi) + "\n");
                    txt_bilinmeYuzdesiDeger.setText(txt_bilinmeYuzdesiDeger.getText() + String.valueOf(bilinmeYuzdesi) + "\n");
                    txt_statusDeger.setText(txt_statusDeger.getText() + status + "\n");
                }

                // PreparedStatement, ResultSet ve baglantiyi kapat
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