package application;

import com.mySQL.Util.VeritabaniUtil;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.CategoryAxis;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class analizRaporuController {

    @FXML
    private Text txt_analizRaporu;

    @FXML
    private BarChart<String, Number> barChart;
    
    @FXML
    private CategoryAxis xAxis; 
    
    @FXML
    private Button btn_sonraki;
    
    @FXML
    void btn_sonrakiClick(ActionEvent event) {
    	try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("detayli_analiz.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage(); // Yeni bir stage (pencere) oluşturun
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void initialize() {
        statusSayilariniCek();
    }

    public void statusSayilariniCek() {
        Connection baglanti = VeritabaniUtil.Baglan();
        if (baglanti != null) {
            try {
                String sql = "SELECT status, COUNT(*) AS count FROM bilinenler GROUP BY status";
                PreparedStatement preparedStatement = baglanti.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery();

                // Grafik için veri listesi
                ObservableList<XYChart.Series<String, Number>> barChartData = FXCollections.observableArrayList();

                // Tüm status değerlerini alıp bar grafiğe ekle
                while (resultSet.next()) {
                    int status = resultSet.getInt("status");
                    int count = resultSet.getInt("count");
                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    series.getData().add(new XYChart.Data<>("Status " + status, count));
                    barChartData.add(series);
                }

                // Grafik verilerini ayarla
                barChart.setData(barChartData);
                barChart.setTitle("");
                xAxis.setLabel("Status");

                // Eğer tüm status değerlerini dinamik olarak eklemek istiyorsanız burada bir işlem yapmanız gerekir.

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
