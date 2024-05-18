package application;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

public class kacSoruController {

    @FXML
    private TextField brn_kacSoru;
    
    // KUllanicinin istedigi soru sayisini belirleyen fonksiyon
    @FXML
    void btn_kacSoru_action(ActionEvent event) {
        int toplamSoruSayisi = Integer.parseInt(brn_kacSoru.getText());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("sinav_modulu.fxml"));
            Parent root = loader.load();
            sinavModuluController controller = loader.getController();
            controller.setToplamSoruSayisi(toplamSoruSayisi);
            Stage stage = new Stage(); // Yeni bir stage olustur
            stage.setScene(new Scene(root));
            stage.show();
            ((Node)(event.getSource())).getScene().getWindow().hide();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }   
}
